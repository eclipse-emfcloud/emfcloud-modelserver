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

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import io.javalin.plugin.json.JavalinJackson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsHandler;
import io.javalin.websocket.WsMessageContext;

public class SessionController extends WsHandler {

   private static Logger LOG = Logger.getLogger(SessionController.class.getSimpleName());

   private final Map<String, Set<WsContext>> modelUrisToClients = Maps.newConcurrentMap();

   private final Map<String, Set<WsContext>> modelUrisToValidationClients = Maps.newConcurrentMap();

   @Inject
   private ModelRepository modelRepository;

   @Inject
   private CodecsManager encoder;

   @Inject
   private ModelValidator modelValidator;

   public boolean subscribe(final WsContext ctx, final String modeluri) {
      return this.subscribe(ctx, modeluri, -1); // Do not set an IdleTimeout, keep socket open until client disconnects
   }

   public boolean subscribe(final WsContext ctx, final String modeluri, final boolean liveValidation) {
      return this.subscribe(ctx, modeluri, -1, liveValidation); // Do not set an IdleTimeout, keep socket open until
                                                                // client disconnects
   }

   public boolean subscribe(final WsContext ctx, final String modeluri, final long timeout) {
      if (this.modelRepository.hasModel(modeluri)) {
         modelUrisToClients.computeIfAbsent(modeluri, clients -> ConcurrentHashMap.newKeySet()).add(ctx);
         ctx.session.setIdleTimeout(timeout);
         ctx.send(JsonResponse.success(ctx.getSessionId()));
         ctx.send(JsonResponse.dirtyState(modelRepository.getDirtyState(modeluri)));
         return true;
      }
      return false;
   }

   public boolean subscribe(final WsContext ctx, final String modeluri, final long timeout,
      final boolean liveValidation) {
      if (!liveValidation) {
         return subscribe(ctx, modeluri, timeout);
      }
      if (this.modelRepository.hasModel(modeluri)) {
         modelUrisToClients.computeIfAbsent(modeluri, clients -> ConcurrentHashMap.newKeySet()).add(ctx);
         modelUrisToValidationClients.computeIfAbsent(modeluri, clients -> ConcurrentHashMap.newKeySet()).add(ctx);
         ctx.session.setIdleTimeout(timeout);
         ctx.send(JsonResponse.success(ctx.getSessionId()));
         ctx.send(JsonResponse.dirtyState(modelRepository.getDirtyState(modeluri)));
         return true;
      }
      return false;
   }

   public boolean unsubscribe(final WsContext ctx) {
      if (!this.isClientSubscribed(ctx)) {
         return false;
      }

      Iterator<Map.Entry<String, Set<WsContext>>> it = modelUrisToClients.entrySet().iterator();

      while (it.hasNext()) {
         Map.Entry<String, Set<WsContext>> entry = it.next();
         Set<WsContext> clients = entry.getValue();
         clients.remove(ctx);
         if (clients.isEmpty()) {
            it.remove();
         }
      }

      return true;
   }

   public boolean handleMessage(final WsMessageContext ctx) {
      if (!this.isClientSubscribed(ctx)) {
         return false;
      }

      if (readMessageType(ctx).equals(JsonResponseType.KEEPALIVE)) {
         ctx.send(JsonResponse.keepAlive(ctx.getSessionId() + " stayin' alive!"));
         return true;
      }

      return false;
   }

   private String readMessageType(final WsMessageContext ctx) {
      try {
         JsonNode json = JavalinJackson.getObjectMapper().readTree(ctx.message());
         if (!json.has(JsonResponseMember.TYPE)) {
            handleError(ctx, "Empty JSON");
            return "";
         }
         JsonNode jsonTypeNode = json.get(JsonResponseMember.TYPE);
         String jsonType = !jsonTypeNode.asText().isEmpty() ? jsonTypeNode.asText() : jsonTypeNode.toString();
         if (jsonType.equals("{}")) {
            handleError(ctx, "Empty JSON");
            return "";
         }
         return jsonType;
      } catch (IOException e) {
         handleError(ctx, "Invalid JSON");
      }
      return "";
   }

   public void modelChanged(final String modeluri) {
      modelRepository.getModel(modeluri).ifPresentOrElse(
         eObject -> {
            broadcastFullUpdate(modeluri, eObject);
            broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
            broadcastValidation(modeluri);
         },
         () -> broadcastError(modeluri, "Could not load changed object"));
   }

   public void modelChanged(final String modeluri, final Map<String, JsonNode> encodings) {
      modelRepository.getModel(modeluri).ifPresentOrElse(
         eObject -> {
            broadcastIncrementalUpdates(modeluri, encodings);
            broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
            broadcastValidation(modeluri);
         },
         () -> broadcastError(modeluri, "Could not load changed object"));
   }

   public void broadcastValidation(final String modeluri) {
      broadcastValidation(modeluri, modelValidator.validate(modeluri));
   }

   public void modelDeleted(final String modeluri) {
      broadcastFullUpdate(modeluri, null);
   }

   public void modelSaved(final String modeluri) {
      broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
   }

   public void allModelSaved() {
      for (String modeluri : modelRepository.getAbsoluteModelUris()) {
         broadcastDirtyState(modeluri, modelRepository.getDirtyState(modeluri));
      }
   }

   private Stream<WsContext> getOpenSessions(final String modeluri) {
      return modelUrisToClients.getOrDefault(modeluri, Collections.emptySet()).stream()
         .filter(ctx -> ctx.session.isOpen());
   }

   private Stream<WsContext> getOpenValidationSessions(final String modeluri) {
      return modelUrisToValidationClients.getOrDefault(modeluri, Collections.emptySet()).stream()
         .filter(ctx -> ctx.session.isOpen());
   }

   public Map<String, JsonNode> getCommandEncodings(final String modeluri, final CCommand command) {
      Map<String, JsonNode> encodings = null;
      if (modelUrisToClients.containsKey(modeluri)) {
         try {
            encodings = encoder.encode(command);
         } catch (EncodingException e) {
            LOG.error("Pre encoding of undo/redo command for " + modeluri + " failed", e);
         }
      }
      return encodings;
   }

   private void broadcastFullUpdate(final String modeluri, @Nullable final EObject updatedModel) {
      if (modelUrisToClients.containsKey(modeluri)) {
         getOpenSessions(modeluri)
            .forEach(session -> {
               try {
                  if (updatedModel == null) {
                     // model has been deleted
                     session.send(JsonResponse.fullUpdate(NullNode.getInstance()));
                  } else {
                     session.send(JsonResponse.fullUpdate(encoder.encode(session, updatedModel)));
                  }
               } catch (EncodingException e) {
                  LOG.error("Broadcast full update of " + modeluri + " failed", e);
               }
            });
      }
   }

   private void broadcastIncrementalUpdates(final String modeluri,
      final Map<String, JsonNode> encodedIncrementalUpdates) {
      if (modelUrisToClients.containsKey(modeluri)) {
         getOpenSessions(modeluri)
            .forEach(session -> {
               session.send(JsonResponse.incrementalUpdate(encodedIncrementalUpdates.get(encoder.findFormat(session))));
            });
      }
   }

   private void broadcastDirtyState(final String modeluri, final Boolean isDirty) {
      getOpenSessions(modeluri)
         .forEach(session -> session.send(JsonResponse.dirtyState(isDirty)));
   }

   private void broadcastValidation(final String modeluri, final JsonNode newResult) {
      if (modelUrisToValidationClients.containsKey(modeluri)) {
         getOpenValidationSessions(modeluri)
            .forEach(session -> {
               session.send(JsonResponse.validationResult(newResult));
            });
      }
   }

   private void broadcastError(final String modeluri, final String errorMessage) {
      getOpenSessions(modeluri)
         .forEach(session -> session.send(JsonResponse.error(errorMessage)));
   }

   boolean isClientSubscribed(final WsContext ctx) {
      return !modelUrisToClients.entrySet().stream().filter(entry -> entry.getValue().contains(ctx)).collect(toSet())
         .isEmpty();
   }

   private void handleError(final WsContext ctx, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.send(JsonResponse.error(errorMsg));
   }

}
