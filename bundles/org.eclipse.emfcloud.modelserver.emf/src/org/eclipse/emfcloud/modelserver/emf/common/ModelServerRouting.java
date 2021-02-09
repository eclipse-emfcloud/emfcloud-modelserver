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

import static io.javalin.apibuilder.ApiBuilder.after;
import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import static io.javalin.apibuilder.ApiBuilder.ws;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.ModelServerPaths;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class ModelServerRouting extends Routing {

   private static final Logger LOG = Logger.getLogger(ModelServerRouting.class.getSimpleName());

   private final Javalin javalin;
   private final ServerConfiguration serverConfiguration;

   // Do not process certain requests while server is being configured.
   protected CompletableFuture<Void> onServerConfigured;

   @Inject
   public ModelServerRouting(final Javalin javalin, final ServerConfiguration serverConfiguration) {
      this.javalin = javalin;
      this.serverConfiguration = serverConfiguration;
      this.onServerConfigured = CompletableFuture.completedFuture(null);
   }

   @Override
   @SuppressWarnings({ "checkstyle:MethodLength", "JavaNCSS", "checkstyle:CyclomaticComplexity" })
   public void bindRoutes() {
      javalin.routes(() -> {
         path("api/v1", () -> {

            // Wait until all preconditions are fulfilled
            before(ctx -> {
               if (requiresConfiguredServer(ctx) && !this.onServerConfigured.isDone()) {
                  String requestPath = ctx.path() + (ctx.queryString() == null ? "" : "?" + ctx.queryString());
                  LOG.debug("Waiting for sever configuration to be completed: " + requestPath);
                  this.onServerConfigured.get();
               }
            });

            // CREATE
            post(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).create(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET ONE MODEL/GET ALL MODELS
            get(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).getOne(ctx, param),
                     () -> getController(ModelController.class).getAll(ctx));
            });

            // GET MODEL ELEMENT
            get(ModelServerPaths.MODEL_ELEMENT, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     modelUriParam -> {
                        getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.ELEMENT_ID).ifPresentOrElse(
                           elementIdParam -> getController(ModelController.class).getModelElementById(ctx,
                              modelUriParam,
                              elementIdParam),
                           () -> getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.ELEMENT_NAME)
                              .ifPresentOrElse(
                                 elementnameParam -> getController(ModelController.class).getModelElementByName(ctx,
                                    modelUriParam, elementnameParam),
                                 () -> handleHttpError(ctx, 400, "Missing parameter 'elementid' or 'elementname'")));
                     },
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // UPDATE
            patch(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).update(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // DELETE
            delete(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).delete(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // EDIT - execute commands
            patch(ModelServerPaths.EDIT, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).executeCommand(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // SAVE
            get(ModelServerPaths.SAVE, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).save(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // SAVE ALL
            get(ModelServerPaths.SAVE_ALL, ctx -> getController(ModelController.class).saveAll(ctx));

            // UNDO
            get(ModelServerPaths.UNDO, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).undo(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // REDO
            get(ModelServerPaths.REDO, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).redo(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET MODELURIS
            get(ModelServerPaths.MODEL_URIS, ctx -> getController(ModelController.class).getModelUris(ctx));

            // GET JSON TYPE SCHEMA
            get(ModelServerPaths.TYPE_SCHEMA, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(SchemaController.class).getTypeSchema(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET JSONFORMS UI SCHEMA
            get(ModelServerPaths.UI_SCHEMA, ctx -> {
               getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.SCHEMA_NAME)
                  .ifPresentOrElse(
                     param -> getController(SchemaController.class).getUiSchema(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'schemaname'!"));
            });

            // SERVER CONFIGURATION
            before(ModelServerPaths.SERVER_CONFIGURE, ctx -> {
               LOG.debug("SERVER_CONFIGURE started");
               this.onServerConfigured = new CompletableFuture<>();
            });

            put(ModelServerPaths.SERVER_CONFIGURE, ctx -> {
               ctx.result(getController(ServerController.class).configure(ctx));
            });

            after(ModelServerPaths.SERVER_CONFIGURE, ctx -> {
               LOG.debug("SERVER_CONFIGURE completed -> pending requests will be processed now");
               this.onServerConfigured.complete(null);
            });

            // PING SERVER
            get(ModelServerPaths.SERVER_PING, getController(ServerController.class).getPingHandler());

            // WEBSOCKET
            ws(ModelServerPaths.SUBSCRIPTION, wsHandler -> {
               wsHandler.onConnect(ctx -> {
                  getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.MODEL_URI)
                     .map(this::adaptModelUri)
                     .ifPresentOrElse(
                        modeluri -> {
                           getQueryParam(ctx.queryParamMap(), ModelServerPathParameters.TIMEOUT)
                              .ifPresentOrElse(
                                 timeout -> {
                                    if (!getController(SessionController.class).subscribe(ctx, modeluri,
                                       Long.parseLong(timeout))) {
                                       handleWsErrorAndCloseSession(ctx, String
                                          .format("Cannot subscribe to '%s': modeluri is not a valid model resource",
                                             modeluri));
                                    }
                                 },
                                 () -> {
                                    if (!getController(SessionController.class).subscribe(ctx, modeluri)) {
                                       handleWsErrorAndCloseSession(ctx, String
                                          .format("Cannot subscribe to '%s': modeluri is not a valid model resource",
                                             modeluri));
                                    }
                                 });
                        },
                        () -> handleWsErrorAndCloseSession(ctx, "Missing parameter 'modeluri'!"));
               });
               wsHandler.onClose(ctx -> {
                  if (!getController(SessionController.class).unsubscribe(ctx)) {
                     handleWsError(ctx, "Cannot unsubscribe: invalid session");
                  }
               });
               wsHandler.onError(ctx -> handleWsError(ctx, ctx.error().getMessage()));
               wsHandler.onMessage(ctx -> {
                  if (!getController(SessionController.class).handleMessage(ctx)) {
                     handleWsError(ctx, "Cannot handle message: " + ctx.message());
                  }
               });
            });

            // TODO: ws for the commands
         });
      });
   }

   protected boolean requiresConfiguredServer(final Context ctx) {
      return !ctx.path().contains(ModelServerPaths.SERVER_CONFIGURE)
         && !ctx.path().contains(ModelServerPaths.SERVER_PING);
   }

   private Optional<String> getQueryParam(final Map<String, List<String>> queryParams, final String paramKey) {
      if (queryParams.containsKey(paramKey)) {
         return Optional.of(queryParams.get(paramKey).get(0));
      }

      return Optional.empty();
   }

   /**
    * Adapt the model URI specified by the client to an absolute <tt>file</tt>
    * scheme URI.
    *
    * @param modelUri the client-supplied model URI
    * @return the absolute file URI
    */
   private String adaptModelUri(final String modelUri) {
      URI uri = URI.createURI(modelUri, true);
      if (uri.isRelative()) {
         if (serverConfiguration.getWorkspaceRootURI().isFile()) {
            return uri.resolve(serverConfiguration.getWorkspaceRootURI()).toString();
         }
         return URI.createFileURI(modelUri).toString();
      }
      // Create file URI from path if modelUri is already absolute path (file:/ or full path file:///)
      // to ensure consistent usage of org.eclipse.emf.common.util.URI
      return URI.createFileURI(uri.path()).toString();
   }

   private void handleHttpError(final Context ctx, final int statusCode, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.status(statusCode).json(JsonResponse.error(errorMsg));
   }

   private void handleWsError(final WsContext ctx, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.send(JsonResponse.error(errorMsg));
   }

   private void handleWsErrorAndCloseSession(final WsContext ctx, final String errorMsg) {
      handleWsError(ctx, errorMsg);
      ctx.session.close();
   }
}
