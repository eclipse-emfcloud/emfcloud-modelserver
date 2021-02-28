/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.LIVE_VALIDATION;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.TIMEOUT;
import static org.eclipse.emfcloud.modelserver.emf.common.JsonResponse.dirtyState;
import static org.eclipse.emfcloud.modelserver.emf.common.JsonResponse.fullUpdate;
import static org.eclipse.emfcloud.modelserver.emf.common.JsonResponse.validationResult;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getBooleanParam;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getLongParam;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.isMessageType;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.dirtyState;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.keepAlive;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.success;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;

public class DefaultSessionController implements SessionController {

   protected static Logger LOG = Logger.getLogger(DefaultSessionController.class.getSimpleName());

   protected final Map<String, Set<WsContext>> modelUrisToClients = Maps.newConcurrentMap();

   @Inject
   protected ModelRepository modelRepository;

   @Inject
   protected CodecsManager encoder;

   @Inject
   protected ModelValidator modelValidator;

   @Override
   public boolean subscribe(final WsContext client, final String modeluri) {
      if (!this.modelRepository.hasModel(modeluri)) {
         return false;
      }
      Long timeout = getLongParam(client, TIMEOUT).orElse(SessionController.NO_TIMEOUT);
      modelUrisToClients.computeIfAbsent(modeluri, clients -> ConcurrentHashMap.newKeySet()).add(client);
      client.session.setIdleTimeout(timeout);
      success(client);
      dirtyState(client, modelRepository.getDirtyState(modeluri));
      return true;
   }

   @Override
   public boolean unsubscribe(final WsContext client) {
      if (!this.isClientSubscribed(client)) {
         return false;
      }

      Iterator<Map.Entry<String, Set<WsContext>>> modelUriToClients = modelUrisToClients.entrySet().iterator();
      while (modelUriToClients.hasNext()) {
         Set<WsContext> clients = modelUriToClients.next().getValue();
         clients.remove(client);
         if (clients.isEmpty()) {
            modelUriToClients.remove();
         }
      }
      return true;
   }

   @Override
   public boolean handleMessage(final WsMessageContext clientMessage) {
      if (!isClientSubscribed(clientMessage) || !isMessageType(clientMessage, JsonResponseType.KEEPALIVE)) {
         return false;
      }
      keepAlive(clientMessage);
      return true;
   }

   @Override
   public void modelCreated(final String modeluri) {
      Optional<EObject> root = modelRepository.getModel(modeluri);
      if (root.isEmpty()) {
         broadcastError(modeluri, "Could not load changed object");
         return;
      }
      broadcastFullUpdate(modeluri, root.get());
      broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
      broadcastValidation(modeluri);
   }

   @Override
   public void modelUpdated(final String modeluri) {
      Optional<EObject> root = modelRepository.getModel(modeluri);
      if (root.isEmpty()) {
         broadcastError(modeluri, "Could not load changed object");
         return;
      }
      broadcastFullUpdate(modeluri, root.get());
      broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
      broadcastValidation(modeluri);
   }

   @Override
   public void commandExecuted(final String modeluri, final CCommandExecutionResult execution) {
      Optional<EObject> root = modelRepository.getModel(modeluri);
      if (root.isEmpty()) {
         broadcastError(modeluri, "Could not load changed object");
         return;
      }
      broadcastIncrementalUpdates(modeluri, execution);
      broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
      broadcastValidation(modeluri);
   }

   @Override
   public void modelDeleted(final String modeluri) {
      broadcastFullUpdate(modeluri, null);
   }

   @Override
   public void modelSaved(final String modeluri) {
      broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
   }

   @Override
   public void allModelsSaved() {
      for (String modeluri : modelRepository.getAbsoluteModelUris()) {
         broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
      }
   }

   @Override
   public boolean hasSession(final String modeluri) {
      return modelUrisToClients.containsKey(modeluri);
   }

   protected void broadcastValidation(final String modeluri) {
      broadcastValidation(modeluri, modelValidator.validate(modeluri));
   }

   protected void broadcastFullUpdate(final String modeluri, @Nullable final EObject updatedModel) {
      if (modelUrisToClients.containsKey(modeluri)) {
         getOpenSessions(modeluri).forEach(session -> broadcastFullUpdate(modeluri, updatedModel, session));
      }
   }

   protected void broadcastFullUpdate(final String modeluri, final EObject updatedModel, final WsContext session) {
      try {
         if (updatedModel == null) {
            // model has been deleted
            session.send(fullUpdate(NullNode.getInstance()));
         } else {
            session.send(fullUpdate(encoder.encode(session, updatedModel)));
         }
      } catch (EncodingException e) {
         LOG.error("Broadcast full update of " + modeluri + " failed", e);
      }
   }

   protected void broadcastIncrementalUpdates(final String modeluri, final CCommandExecutionResult execution) {
      Map<String, JsonNode> updates = encodeIfPresent(modeluri, execution);
      getOpenSessions(modeluri).forEach(session -> broadcastIncrementalUpdate(session, updates));
   }

   private void broadcastIncrementalUpdate(final WsContext session, final Map<String, JsonNode> updates) {
      String sessionFormat = encoder.findFormat(session);
      JsonNode update = updates.get(sessionFormat);
      session.send(JsonResponse.incrementalUpdate(update));
   }

   protected void broadcastDirtyState(final String modeluri, final Boolean isDirty) {
      getOpenSessions(modeluri).forEach(session -> session.send(dirtyState(isDirty)));
   }

   protected void broadcastValidation(final String modeluri, final JsonNode newResult) {
      getOpenValidationSessions(modeluri).forEach(session -> session.send(validationResult(newResult)));
   }

   protected Stream<WsContext> getOpenSessions(final String modeluri) {
      return modelUrisToClients.getOrDefault(modeluri, Collections.emptySet()).stream()
         .filter(ctx -> ctx.session.isOpen());
   }

   protected Stream<WsContext> getOpenValidationSessions(final String modeluri) {
      return getOpenSessions(modeluri).filter(this::requiresLiveValidation);
   }

   protected void broadcastError(final String modeluri, final String errorMessage) {
      getOpenSessions(modeluri).forEach(session -> session.send(JsonResponse.error(errorMessage)));
   }

   protected boolean requiresLiveValidation(final WsContext client) {
      return getBooleanParam(client, LIVE_VALIDATION).orElse(false);
   }

   protected boolean isClientSubscribed(final WsContext ctx) {
      return modelUrisToClients.entrySet().stream().anyMatch(entry -> entry.getValue().contains(ctx));
   }

   protected Map<String, JsonNode> encodeIfPresent(final String modeluri, final EObject execution) {
      Map<String, JsonNode> encodings = new HashMap<>();
      if (hasSession(modeluri)) {
         try {
            encodings = encoder.encode(execution);
         } catch (EncodingException exception) {
            LOG.error("Pre encoding of undo/redo command for " + modeluri + " failed", exception);
         }
      }
      return encodings;
   }

}
