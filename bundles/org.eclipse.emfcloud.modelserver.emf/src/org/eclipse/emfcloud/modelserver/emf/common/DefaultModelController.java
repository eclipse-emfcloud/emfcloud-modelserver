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

import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.accepted;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.conflict;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.decodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.encodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.internalError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.modelNotFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.notFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.response;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.success;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;

public class DefaultModelController implements ModelController {
   protected static Logger LOG = Logger.getLogger(DefaultModelController.class.getSimpleName());

   protected final ModelRepository modelRepository;
   protected final SessionController sessionController;
   protected final ServerConfiguration serverConfiguration;
   protected final CodecsManager codecs;
   protected final ModelValidator modelValidator;

   @Inject
   public DefaultModelController(final ModelRepository modelRepository, final SessionController sessionController,
      final ServerConfiguration serverConfiguration, final CodecsManager codecs, final ModelValidator modelValidator,
      final Provider<ObjectMapper> objectMapperProvider) {
      JavalinJackson.configure(objectMapperProvider.get());
      this.modelRepository = modelRepository;
      this.sessionController = sessionController;
      this.serverConfiguration = serverConfiguration;
      this.codecs = codecs;
      this.modelValidator = modelValidator;
   }

   @Override
   public void create(final Context ctx, final String modeluri) {
      Optional<EObject> root = readPayload(ctx);
      if (root.isEmpty()) {
         return;
      }
      if (this.modelRepository.hasModel(modeluri)) {
         conflict(ctx, "Model already exists.");
         return;
      }

      try {
         this.modelRepository.addModel(modeluri, root.get());
         final JsonNode encoded = codecs.encode(ctx, root.get());
         success(ctx, encoded);
         this.sessionController.modelCreated(modeluri);
      } catch (EncodingException ex) {
         encodingError(ctx, ex);
      } catch (IOException ex) {
         internalError(ctx, "Could not save resource", ex);
      }
   }

   @Override
   public void delete(final Context ctx, final String modeluri) {
      if (this.modelRepository.hasModel(modeluri)) {
         try {
            this.modelRepository.deleteModel(modeluri);
            success(ctx, "Model '%s' successfully deleted", modeluri);
            this.sessionController.modelDeleted(modeluri);
         } catch (IOException e) {
            internalError(ctx, e);
         }
      } else {
         ContextResponse.modelNotFound(ctx, modeluri);
      }
   }

   @Override
   public void getAll(final Context ctx) {
      try {
         final Map<URI, EObject> allModels = this.modelRepository.getAllModels();
         Map<URI, JsonNode> encodedEntries = Maps.newLinkedHashMap();
         for (Map.Entry<URI, EObject> entry : allModels.entrySet()) {
            final JsonNode encoded = codecs.encode(ctx, entry.getValue());
            encodedEntries.put(entry.getKey(), encoded);
         }
         success(ctx, JsonCodec.encode(encodedEntries));
      } catch (EncodingException exception) {
         encodingError(ctx, exception);
      } catch (IOException exception) {
         internalError(ctx, "Could not load all models");
      }
   }

   @Override
   public void getOne(final Context ctx, final String modeluri) {
      Optional<EObject> root = this.modelRepository.getModel(modeluri);
      if (root.isEmpty() || root.get() == null) {
         modelNotFound(ctx, modeluri);
         return;
      }
      try {
         success(ctx, JsonCodec.encode(codecs.encode(ctx, root.get())));
      } catch (EncodingException exception) {
         encodingError(ctx, exception);
      }
   }

   @Override
   public void getModelElementById(final Context ctx, final String modeluri, final String elementid) {
      Optional<EObject> element = this.modelRepository.getModelElementById(modeluri, elementid);
      if (element.isEmpty()) {
         notFound(ctx, "Element with id '" + elementid + "' of model '" + modeluri + "' not found!");
         return;
      }
      try {
         success(ctx, codecs.encode(ctx, element.get()));
      } catch (EncodingException exception) {
         encodingError(ctx, exception);
      }
   }

   @Override
   public void getModelElementByName(final Context ctx, final String modeluri, final String elementname) {
      Optional<EObject> element = this.modelRepository.getModelElementByName(modeluri, elementname);
      if (element.isEmpty()) {
         notFound(ctx, "Element with name '" + elementname + "' of model '" + modeluri + "' not found!");
         return;
      }
      try {
         success(ctx, codecs.encode(ctx, element.get()));
      } catch (EncodingException exception) {
         encodingError(ctx, exception);
      }
   }

   @Override
   public void update(final Context ctx, final String modeluri) {
      Optional<EObject> newRoot = readPayload(ctx);
      if (newRoot.isEmpty()) {
         return;
      }
      Optional<Resource> resource = modelRepository.updateModel(modeluri, newRoot.get());
      if (resource.isEmpty()) {
         notFound(ctx, "No such model resource to update");
         return;
      }
      try {
         response(ctx, JsonResponse.fullUpdate(codecs.encode(ctx, newRoot.get())));
      } catch (EncodingException exception) {
         encodingError(ctx, exception);
      }
      sessionController.modelUpdated(modeluri);
   }

   @Override
   public void save(final Context ctx, final String modeluri) {
      if (!this.modelRepository.hasModel(modeluri)) {
         notFound(ctx, "Model '%s' not found.", modeluri);
         return;
      }
      if (this.modelRepository.saveModel(modeluri)) {
         success(ctx, "Model '%s' successfully saved", modeluri);
         sessionController.modelSaved(modeluri);
      } else {
         internalError(ctx, "Saving model '%s' failed!", modeluri);
      }
   }

   @Override
   public void saveAll(final Context ctx) {
      if (this.modelRepository.saveAllModels()) {
         success(ctx, "All models successfully saved");
         sessionController.allModelsSaved();
      } else {
         internalError(ctx, "Saving all models failed!");
      }
   }

   @Override
   public void validate(final Context ctx, final String modeluri) {
      response(ctx, JsonResponse.validationResult(this.modelValidator.validate(modeluri)));
   }

   @Override
   public void getValidationConstraints(final Context ctx, final String modeluri) {
      success(ctx, this.modelValidator.getValidationConstraints(modeluri));
   }

   @Override
   public void undo(final Context ctx, final String modeluri) {
      Optional<CCommandExecutionResult> undoExecution = modelRepository.undo(modeluri);
      if (undoExecution.isPresent()) {
         success(ctx, "Successful undo.");
         sessionController.commandExecuted(modeluri, undoExecution.get());
         return;
      }
      accepted(ctx, "Cannot undo");
   }

   @Override
   public void redo(final Context ctx, final String modeluri) {
      Optional<CCommandExecutionResult> redoExecution = modelRepository.redo(modeluri);
      if (redoExecution.isPresent()) {
         success(ctx, "Successful redo.");
         sessionController.commandExecuted(modeluri, redoExecution.get());
         return;
      }
      accepted(ctx, "Cannot redo");
   }

   @Override
   public void getModelUris(final Context ctx) {
      try {
         ctx.json(JsonResponse.success(JsonCodec.encode(this.modelRepository.getRelativeModelUris())));
      } catch (EncodingException ex) {
         encodingError(ctx, ex);
      }
   }

   protected Optional<EObject> readPayload(final Context ctx) {
      Optional<String> data = ContextRequest.readData(ctx);
      if (data.isEmpty()) {
         return Optional.empty();
      }

      try {
         return codecs.decode(ctx, data.get(), serverConfiguration.getWorkspaceRootURI());
      } catch (DecodingException exception) {
         decodingError(ctx, exception);
      }
      return Optional.empty();
   }

   @Override
   public void executeCommand(final Context ctx, final String modelURI) {
      Optional<EObject> root = this.modelRepository.getModel(modelURI);
      if (root.isEmpty() || root.get() == null) {
         modelNotFound(ctx, modelURI);
         return;
      }
      Optional<CCommand> command = readPayload(ctx).filter(CCommand.class::isInstance).map(CCommand.class::cast);
      if (command.isEmpty()) {
         return;
      }
      try {
         CCommandExecutionResult execution = modelRepository.executeCommand(modelURI, command.get());
         sessionController.commandExecuted(modelURI, execution);
         success(ctx, "Model '%s' successfully updated", modelURI);
      } catch (DecodingException exception) {
         decodingError(ctx, exception);
      }
   }

}
