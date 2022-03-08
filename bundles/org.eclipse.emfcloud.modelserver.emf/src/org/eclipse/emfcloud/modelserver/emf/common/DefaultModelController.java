/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
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
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.successPatch;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;
import org.eclipse.emfcloud.modelserver.common.patch.PatchCommand;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.patch.PatchCommandHandler;
import org.eclipse.emfcloud.modelserver.emf.util.JsonPatchHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import io.javalin.http.Context;

public class DefaultModelController implements ModelController {
   protected static final Logger LOG = LogManager.getLogger(DefaultModelController.class);

   protected final ModelRepository modelRepository;
   protected final SessionController sessionController;
   protected final ServerConfiguration serverConfiguration;
   protected final CodecsManager codecs;
   protected final ModelValidator modelValidator;
   protected final PatchCommandHandler.Registry commandHandlerRegistry;
   protected final ModelURIConverter uriConverter;
   protected final JsonPatchHelper jsonPatchHelper;

   @Inject
   @SuppressWarnings("checkstyle:ParameterNumber")
   public DefaultModelController(final ModelRepository modelRepository, final SessionController sessionController,
      final ServerConfiguration serverConfiguration, final CodecsManager codecs, final ModelValidator modelValidator,
      final PatchCommandHandler.Registry commandHandlerRegistry, final ModelResourceManager resourceManager,
      final ModelURIConverter uriConverter, final JsonPatchHelper jsonPatchHelper) {
      this.modelRepository = modelRepository;
      this.sessionController = sessionController;
      this.serverConfiguration = serverConfiguration;
      this.codecs = codecs;
      this.modelValidator = modelValidator;
      this.commandHandlerRegistry = commandHandlerRegistry;
      this.uriConverter = uriConverter;
      this.jsonPatchHelper = jsonPatchHelper;
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
            success(ctx, "Model '%s' successfully deleted", uriConverter.deresolveModelURI(ctx, modeluri));
            this.sessionController.modelDeleted(modeluri);
         } catch (IOException e) {
            internalError(ctx, e);
         }
      } else {
         ContextResponse.modelNotFound(ctx, modeluri);
      }
   }

   @Override
   public void close(final Context ctx, final String modeluri) {
      if (this.modelRepository.hasModel(modeluri)) {
         this.modelRepository.closeModel(modeluri);
         success(ctx, "Model '%s' successfully closed", uriConverter.deresolveModelURI(ctx, modeluri));
         this.sessionController.modelClosed(modeluri);
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
            encodedEntries.put(uriConverter.deresolveModelURI(ctx, entry.getKey()), encoded);
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
         notFound(ctx, "Element with id '" + elementid + "' of model '" + uriConverter.deresolveModelURI(ctx, modeluri)
            + "' not found!");
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
         notFound(ctx, "Element with name '" + elementname + "' of model '"
            + uriConverter.deresolveModelURI(ctx, modeluri) + "' not found!");
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
      String model = uriConverter.deresolveModelURI(ctx, modeluri);
      if (!this.modelRepository.hasModel(modeluri)) {
         notFound(ctx, "Model '%s' not found.", model);
         return;
      }
      if (this.modelRepository.saveModel(modeluri)) {
         success(ctx, "Model '%s' successfully saved", model);
         sessionController.modelSaved(modeluri);
      } else {
         internalError(ctx, "Saving model '%s' failed!", model);
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
      withModel(ctx, modeluri, root -> {
         modelRepository.undo(modeluri).ifPresentOrElse(undoExecution -> {
            final String message = "Successful undo.";
            Supplier<JsonNode> patchResponse = Suppliers
               .memoize(() -> getJSONPatchUpdate(ctx, modeluri, root, undoExecution));

            if (isV1API(ctx)) {
               // Don't give V1 API clients the patch result
               success(ctx, message);
            } else {
               successPatch(ctx, patchResponse.get(), message);
            }

            sessionController.commandExecuted(modeluri, Suppliers.ofInstance(undoExecution), patchResponse);
         }, () -> accepted(ctx, "Cannot undo"));
      });
   }

   protected boolean isV1API(final Context ctx) {
      return ctx.matchedPath().startsWith("/" + ModelServerPathsV1.BASE_PATH + "/");
   }

   @Override
   public void redo(final Context ctx, final String modeluri) {
      withModel(ctx, modeluri, root -> {
         modelRepository.redo(modeluri).ifPresentOrElse(redoExecution -> {
            final String message = "Successful redo.";
            Supplier<JsonNode> patchResponse = Suppliers
               .memoize(() -> getJSONPatchUpdate(ctx, modeluri, root, redoExecution));

            if (isV1API(ctx)) {
               // Don't give V1 API clients the patch result
               success(ctx, message);
            } else {
               successPatch(ctx, patchResponse.get(), message);
            }

            sessionController.commandExecuted(modeluri, Suppliers.ofInstance(redoExecution), patchResponse);
         }, () -> accepted(ctx, "Cannot redo"));
      });
   }

   @Override
   public void getModelUris(final Context ctx) {
      try {
         List<String> uris;

         if (ContextRequest.getAPIVersion(ctx).major() < 2) {
            // Compatibility for API v1
            uris = getModelURIsV1();
         } else {
            uris = this.modelRepository.getAbsoluteModelUris().stream()
               .map(uri -> uriConverter.deresolveModelURI(ctx, uri))
               .collect(Collectors.toList());
         }

         ctx.json(JsonResponse.success(JsonCodec.encode(uris)));
      } catch (EncodingException ex) {
         encodingError(ctx, ex);
      }
   }

   @SuppressWarnings("deprecation")
   private List<String> getModelURIsV1() { return List.copyOf(this.modelRepository.getRelativeModelUris()); }

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
      withModel(ctx, modelURI, root -> {
         Optional<CCommand> command = readPayload(ctx).filter(CCommand.class::isInstance).map(CCommand.class::cast);
         if (command.isEmpty()) {
            return;
         }
         try {
            CCommandExecutionResult execution = modelRepository.executeCommand(modelURI, command.get());

            success(ctx, "Model '%s' successfully updated", uriConverter.deresolveModelURI(ctx, modelURI));

            sessionController.commandExecuted(modelURI, Suppliers.ofInstance(execution),
               Suppliers.memoize(() -> getJSONPatchUpdate(ctx, modelURI, root, execution)));
         } catch (DecodingException exception) {
            decodingError(ctx, exception);
         }
      });
   }

   @Override
   public void executeCommandV2(final Context ctx, final String modelURI) {
      withModel(ctx, modelURI, root -> {
         Optional<PatchCommand<?>> command = readPatchCommand(ctx);
         command.ifPresent(pCommand -> executePatchCommand(ctx, modelURI, root, pCommand));
      });
   }

   protected void executePatchCommand(final Context ctx, final String modelURI, final EObject root,
      final PatchCommand<?> pCommand) {

      CCommandExecutionResult result;
      if (isCCommand(pCommand)) {
         try {
            result = this.modelRepository.executeCommand(modelURI, getCCommand(pCommand));
         } catch (DecodingException ex) {
            decodingError(ctx, ex);
            return;
         }
      } else if (isJsonPatch(pCommand)) {
         ArrayNode jsonPatch = getJsonPatch(pCommand);
         try {
            result = this.modelRepository.executeCommand(modelURI, jsonPatch);
         } catch (JsonPatchTestException | JsonPatchException ex) {
            LOG.error(ex.getMessage(), ex);
            return;
         }
      } else {
         // TODO Handle unsupported Patch/Command type
         return;
      }

      String model = uriConverter.deresolveModelURI(ctx, modelURI);
      JsonNode patchResult = getJSONPatchUpdate(ctx, modelURI, root, result);
      if (patchResult != null) {
         successPatch(ctx, patchResult, "Model '%s' successfully updated", model);

         sessionController.commandExecuted(modelURI, Suppliers.ofInstance(result),
            Suppliers.ofInstance(patchResult));
      } else {
         success(ctx, "Model '%s' successfully updated", model);
      }
   }

   /**
    * Perform the given action on the indicated model, if present, or else return a 404 to the client.
    *
    * @param ctx         the client request context
    * @param modelURI    the model on which to operation
    * @param modelAction the operation to perform on the model
    */
   private void withModel(final Context ctx, final String modelURI, final Consumer<? super EObject> modelAction) {
      Optional<EObject> root = this.modelRepository.getModel(modelURI);
      root.ifPresentOrElse(modelAction, () -> modelNotFound(ctx, modelURI));
   }

   private JsonNode getJSONPatchUpdate(final Context ctx, final String modelURI, final EObject root,
      final CCommandExecutionResult executionResult) {

      try {
         return jsonPatchHelper.getJsonPatch(root, executionResult);
      } catch (EncodingException ex) {
         LOG.error(ex.getMessage(), ex);
         return null;
      }
   }

   private CCommand getCCommand(final PatchCommand<?> pCommand) {
      Object data = pCommand.getData();
      if (data instanceof CCommand) {
         CCommand cCommand = (CCommand) data;
         // resolveWorkspaceURIs(cCommand);
         return cCommand;
      }
      return null;
   }

   private ArrayNode getJsonPatch(final PatchCommand<?> pCommand) {
      Object data = pCommand.getData();
      if (data instanceof ArrayNode) {
         return (ArrayNode) data;
      }
      return null;
   }

   private boolean isJsonPatch(final PatchCommand<?> pCommand) {
      return ModelServerPathParametersV2.JSON_PATCH.equals(pCommand.getType());
   }

   private boolean isCCommand(final PatchCommand<?> pCommand) {
      return ModelServerPathParametersV2.EMF_COMMAND.equals(pCommand.getType());
   }

   private Optional<PatchCommand<?>> readPatchCommand(final Context ctx) {
      Optional<String> data = ContextRequest.readData(ctx);
      if (data.isEmpty()) {
         return Optional.empty();
      }

      // In V1, Data is always encoded as Json (Although the actual Command might be encoded as XMI)
      ObjectMapper mapper = new ObjectMapper();
      try {
         JsonNode patch = mapper.readTree(data.get());
         return commandHandlerRegistry.getPatchCommand(ctx, patch);
      } catch (JsonMappingException ex) {
         LOG.error(ex.getMessage(), ex);
      } catch (JsonProcessingException ex) {
         LOG.error(ex.getMessage(), ex);
      }
      return Optional.empty();
   }

}
