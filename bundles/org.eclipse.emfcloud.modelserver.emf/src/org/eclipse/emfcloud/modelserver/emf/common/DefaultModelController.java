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
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.badRequest;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.decodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.encodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.internalError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.modelNotFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.notFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.response;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.success;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.command.CCommand;
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
         badRequest(ctx, "Create new model failed.");
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
            this.modelRepository.removeModel(modeluri);
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
         notFound(ctx, "Update has no content");
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
      CCommand undoCommand = modelRepository.getUndoCommand(modeluri);
      if (undoCommand != null) {
         /*
          * In order to encode certain commands (e.g., commands that remove model elements) we need all element
          * information,
          * e.g., the info which element was removed before it is gone. Therefore we encode the command in all formats
          * and provide them to the model repository which will select the correct format for each subscribed client.
          * This means that we encode the command once for all formats beforehand. The alternative would be to encode it
          * once per subscribed client.
          * The same applies to the redo method below.
          */
         Map<String, JsonNode> encodings = getCommandEncodings(modeluri, undoCommand);
         boolean undoSuccess = modelRepository.undo(modeluri);
         if (undoSuccess) {
            success(ctx, "Successful undo.");
            sessionController.commandExecuted(modeluri, encodings);
            return;
         }
      }
      accepted(ctx, "Cannot undo");
   }

   @Override
   public void redo(final Context ctx, final String modeluri) {
      CCommand redoCommand = modelRepository.getRedoCommand(modeluri);
      if (redoCommand != null) {
         /* Please see comment in undo method */
         Map<String, JsonNode> encodings = getCommandEncodings(modeluri, redoCommand);
         boolean redoSuccess = modelRepository.redo(modeluri);
         if (redoSuccess) {
            success(ctx, "Successful redo.");
            sessionController.commandExecuted(modeluri, encodings);
            return;
         }
      }
      accepted(ctx, "Cannot redo");
   }

   protected Map<String, JsonNode> getCommandEncodings(final String modeluri, final CCommand command) {
      Map<String, JsonNode> encodings = new HashMap<>();
      if (sessionController.hasSession(modeluri)) {
         try {
            encodings = codecs.encode(command);
         } catch (EncodingException exception) {
            LOG.error("Pre encoding of undo/redo command for " + modeluri + " failed", exception);
         }
      }
      return encodings;
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
         badRequest(ctx, "Could not read command.");
         return;
      }
      doExecuteCommand(ctx, modelURI, command);
   }

   protected void doExecuteCommand(final Context ctx, final String modelURI, final Optional<CCommand> command) {
      try {
         // Create a temporary resource in the workspace
         // so that we can resolve cross-references into
         // the user model from the command(s)
         URI uri = URI.createURI("$command.res").resolve(serverConfiguration.getWorkspaceRootURI());
         Resource resource = new ResourceImpl(uri);
         modelRepository.addTemporaryCommandResource(modelURI, resource, command.get());

         try {
            EcoreUtil.resolveAll(resource);
            /*
             * In a similar way as for undo/redo we encode the command beforehand to ensure the encoded
             * command for notifying subscribed client contains all necessary element information,
             * e.g.the original owner in case of a SET command that changes the name and therefore its
             * semantic URI
             */
            Map<String, JsonNode> encodings = getCommandEncodings(modelURI, command.get());
            modelRepository.executeCommand(modelURI, command.get());
            sessionController.commandExecuted(modelURI, encodings);
            success(ctx, "Model '%s' successfully updated", modelURI);
         } finally {
            resource.unload();
            modelRepository.removeTemporaryCommandResource(modelURI, resource);
         }
      } catch (DecodingException exception) {
         decodingError(ctx, exception);
      }
   }
}
