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

import java.io.IOException;
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
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.Codecs;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;

public class ModelController {

   private static final Logger LOG = Logger.getLogger(ModelController.class.getSimpleName());

   private ModelRepository modelRepository;
   private final SessionController sessionController;
   private final ServerConfiguration serverConfiguration;
   private final Codecs codecs;

   @Inject
   public ModelController(final ModelRepository modelRepository, final SessionController sessionController,
      final ServerConfiguration serverConfiguration) {

      JavalinJackson.configure(EMFJsonConverter.setupDefaultMapper());
      codecs = new Codecs();
      this.modelRepository = modelRepository;
      this.sessionController = sessionController;
      this.serverConfiguration = serverConfiguration;
   }

   public void create(final Context ctx, final String modeluri) {
      readPayload(ctx).ifPresentOrElse(
         eObject -> {
            try {
               this.modelRepository.addModel(modeluri, eObject);
               final JsonNode encoded = codecs.encode(ctx, eObject);
               ctx.json(JsonResponse.success(encoded));
               this.sessionController.modelChanged(modeluri);
            } catch (EncodingException ex) {
               handleEncodingError(ctx, ex);
            } catch (IOException e) {
               handleError(ctx, 500, "Could not save resource");
            }
         },
         () -> handleError(ctx, 400, "Create new model failed"));
   }

   public void delete(final Context ctx, final String modeluri) {
      if (this.modelRepository.hasModel(modeluri)) {
         try {
            this.modelRepository.removeModel(modeluri);
            ctx.json(JsonResponse.success("Model '" + modeluri + "' successfully deleted"));
            this.sessionController.modelDeleted(modeluri);
         } catch (IOException e) {
            handleError(ctx, 404, "Model '" + modeluri + "' not found, cannot be deleted!");
         }
      } else {
         handleError(ctx, 404, "Model '" + modeluri + "' not found, cannot be deleted!");
      }
   }

   public void getAll(final Context ctx) {
      try {
         final Map<URI, EObject> allModels = this.modelRepository.getAllModels();
         Map<URI, JsonNode> encodedEntries = Maps.newLinkedHashMap();
         for (Map.Entry<URI, EObject> entry : allModels.entrySet()) {
            final JsonNode encoded = codecs.encode(ctx, entry.getValue());
            encodedEntries.put(entry.getKey(), encoded);
         }
         ctx.json(JsonResponse.success(JsonCodec.encode(encodedEntries)));
      } catch (EncodingException ex) {
         handleEncodingError(ctx, ex);
      } catch (IOException e) {
         handleError(ctx, 404, "Could not load all models");
      }
   }

   public void getOne(final Context ctx, final String modeluri) {
      this.modelRepository.getModel(modeluri).ifPresentOrElse(
         model -> {
            if (model == null) {
               ctx.json(JsonResponse.error(""));
            } else {
               try {
                  ctx.json(JsonResponse.success(codecs.encode(ctx, model)));
               } catch (EncodingException ex) {
                  handleEncodingError(ctx, ex);
               }
            }
         },
         () -> handleError(ctx, 404, "Model '" + modeluri + "' not found!"));
   }

   public void update(final Context ctx, final String modeluri) {
      readPayload(ctx).ifPresentOrElse(
         eObject -> modelRepository.updateModel(modeluri, eObject)
            .ifPresentOrElse(__ -> {
               try {
                  ctx.json(JsonResponse.fullUpdate(codecs.encode(ctx, eObject)));
               } catch (EncodingException e) {
                  handleEncodingError(ctx, e);
               }
               sessionController.modelChanged(modeluri);
            },
               () -> handleError(ctx, 404, "No such model resource to update")),
         () -> handleError(ctx, 400, "Update has no content"));
   }

   public void save(final Context ctx, final String modeluri) {
      if (this.modelRepository.saveModel(modeluri)) {
         ctx.json(JsonResponse.success("Model '" + modeluri + "' successfully saved"));
         sessionController.modelSaved(modeluri);
      } else {
         handleError(ctx, 500, "Saving model '" + modeluri + "' failed!");
      }
   }

   private final Handler modelUrisHandler = ctx -> ctx
      .json(JsonResponse.success(JsonCodec.encode(this.modelRepository.getAllModelUris())));

   public Handler getModelUrisHandler() { return modelUrisHandler; }

   // #FIXME Very ugly solution to prevent Eclipse from adding the final modifier. Look for a better solution!
   protected void preventFinal() {
      this.modelRepository = null;
   }

   private Optional<EObject> readPayload(final Context ctx) {
      try {
         JsonNode json = JavalinJackson.getObjectMapper().readTree(ctx.body());
         if (!json.has("data")) {
            handleError(ctx, 400, "Empty JSON");
            return Optional.empty();
         }
         JsonNode jsonDataNode = json.get("data");
         String jsonData = !jsonDataNode.asText().isEmpty() ? jsonDataNode.asText() : jsonDataNode.toString();
         if (jsonData.equals("{}")) {
            handleError(ctx, 400, "Empty JSON");
            return Optional.empty();
         }

         return codecs.decode(ctx, jsonData, serverConfiguration.getWorkspaceRootURI());
      } catch (DecodingException | IOException e) {
         handleError(ctx, 400, "Invalid JSON");
      }
      return Optional.empty();
   }

   public void executeCommand(final Context ctx, final String modelURI) {
      this.modelRepository.getModel(modelURI).ifPresentOrElse(
         model -> {
            if (model == null) {
               handleError(ctx, 404, String.format("Model '%s' not found!", modelURI));
            } else {
               readPayload(ctx).filter(CCommand.class::isInstance)//
                  .map(CCommand.class::cast) //
                  .ifPresent(cmd -> {
                     try {
                        // Create a temporary resource in the workspace
                        // so that we can resolve cross-references into
                        // the user model from the command(s)
                        URI uri = URI.createURI("$command.res")
                           .resolve(serverConfiguration.getWorkspaceRootURI());
                        Resource resource = new ResourceImpl(uri);
                        modelRepository.getResourceSet().getResources().add(resource);
                        resource.getContents().add(cmd);

                        try {
                           EcoreUtil.resolveAll(resource);

                           // Use an unique copy of the command for each operation
                           // here to ensure isolation in case of side-effects
                           modelRepository.updateModel(modelURI, EcoreUtil.copy(cmd));
                           sessionController.modelChanged(modelURI, EcoreUtil.copy(cmd));
                           ctx.json(JsonResponse.success());
                        } finally {
                           resource.unload();
                           modelRepository.getResourceSet().getResources().remove(resource);
                        }
                     } catch (DecodingException e) {
                        handleDecodingError(ctx, e);
                     }
                  });
               ctx.json(JsonResponse.success("Model '" + modelURI + "' successfully updated"));
            }
         },
         () -> handleError(ctx, 404, String.format("Model '%s' not found!", modelURI)));
   }

   private void handleEncodingError(final Context context, final EncodingException ex) {
      handleError(context, 500, "An error occurred during data encoding", ex);
   }

   private void handleDecodingError(final Context context, final DecodingException ex) {
      handleError(context, 500, "An error occurred during data decoding", ex);
   }

   private void handleError(final Context ctx, final int statusCode, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.status(statusCode).json(JsonResponse.error(errorMsg));
   }

   private void handleError(final Context ctx, final int statusCode, final String errorMsg, final Exception e) {
      LOG.error(errorMsg, e);
      ctx.status(statusCode).json(JsonResponse.error(errorMsg));
   }
}
