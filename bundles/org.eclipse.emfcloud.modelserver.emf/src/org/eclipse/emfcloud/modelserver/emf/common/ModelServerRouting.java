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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
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

   @Inject
   public ModelServerRouting(final Javalin javalin, final ServerConfiguration serverConfiguration) {
      this.javalin = javalin;
      this.serverConfiguration = serverConfiguration;
   }

   @Override
   public void bindRoutes() {
      javalin.routes(() -> {
         path("api/v1", () -> {
            // CREATE
            post(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).create(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET ONE/GET ALL
            get(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).getOne(ctx, param),
                     () -> getController(ModelController.class).getAll(ctx));
            });
            // UPDATE
            patch(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).update(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // DELETE
            delete(ModelServerPaths.MODEL_BASE_PATH, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).delete(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // Execute commands
            patch(ModelServerPaths.EDIT, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).executeCommand(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // SAVE
            get(ModelServerPaths.SAVE, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(ModelController.class).save(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET MODELURIS
            get(ModelServerPaths.MODEL_URIS, getController(ModelController.class).getModelUrisHandler());

            // GET JSON SCHEMA
            get(ModelServerPaths.SCHEMA, ctx -> {
               getQueryParam(ctx.queryParamMap(), "modeluri")
                  .map(this::adaptModelUri)
                  .ifPresentOrElse(
                     param -> getController(SchemaController.class).getSchema(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'modeluri'!"));
            });

            // GET JSONFORMS UI SCHEMA
            get(ModelServerPaths.UI_SCHEMA, ctx -> {
               getQueryParam(ctx.queryParamMap(), "schemaname")
                  .ifPresentOrElse(
                     param -> getController(SchemaController.class).getJsonFormsUISchema(ctx, param),
                     () -> handleHttpError(ctx, 400, "Missing parameter 'schemaname'!"));
            });

            put(ModelServerPaths.SERVER_CONFIGURE, getController(ServerController.class).getConfigureHandler());
            get(ModelServerPaths.SERVER_PING, getController(ServerController.class).getPingHandler());

            ws(ModelServerPaths.SUBSCRIPTION, wsHandler -> {
               wsHandler.onConnect(ctx -> {
                  getQueryParam(ctx.queryParamMap(), "modeluri")
                     .map(this::adaptModelUri)
                     .ifPresentOrElse(
                        modeluri -> {
                           if (!getController(SessionController.class).subscribe(ctx, modeluri)) {
                              handleWsErrorAndCloseSession(ctx, String
                                 .format("Cannot subscribe to '%s': modeluri is not a valid model resource", modeluri));
                           }
                        },
                        () -> handleWsErrorAndCloseSession(ctx, "Missing parameter 'modeluri'!"));
               });
               wsHandler.onClose(ctx -> {
                  if (!getController(SessionController.class).unsubscribe(ctx)) {
                     handleWsError(ctx, "Cannot unsubscribe: invalid session");
                  }
               });
               wsHandler.onError(ctx -> ctx.error());
               wsHandler.onMessage(ctx -> {}); // we do not handle messages from subscribers at the moment
            });

            // TODO: ws for the commands
         });
      });
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

      return uri.toString();
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
