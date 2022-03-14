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

import static io.javalin.apibuilder.ApiBuilder.path;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.ELEMENT_ID;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.ELEMENT_NAME;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.MODEL_URI;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1.SCHEMA_NAME;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1.SERVER_CONFIGURE;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1.SERVER_PING;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getParam;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.error;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.missingParameter;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.modelNotFound;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;

import com.google.common.base.Strings;

import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

/**
 * Implementation of common routing for multiple API versions.
 */
class ModelServerRoutingDelegate {
   protected static final Logger LOG = LogManager.getLogger(ModelServerRoutingDelegate.class);

   protected final Javalin javalin;
   protected final ModelResourceManager resourceManager;
   protected final ModelController modelController;
   protected final SchemaController schemaController;
   protected final ServerController serverController;
   protected final SessionController sessionController;

   protected final String basePath;

   // Do not process certain requests while server is being configured.
   protected CompletableFuture<Void> onServerConfigured;

   protected BiConsumer<? super WsConnectContext, String> subscriptionConnectionHandler = this::connectSubscription;

   protected ModelURIConverter uriConverter;

   protected ModelServerRoutingDelegate(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController, final SchemaController schemaController,
      final ServerController serverController, final SessionController sessionController, final String basePath) {

      super();

      this.javalin = javalin;
      this.resourceManager = resourceManager;
      this.modelController = modelController;
      this.schemaController = schemaController;
      this.serverController = serverController;
      this.sessionController = sessionController;
      this.onServerConfigured = CompletableFuture.completedFuture(null);

      this.basePath = basePath;
   }

   public void setModelURIConverter(final ModelURIConverter uriConverter) { this.uriConverter = uriConverter; }

   public void bindRoutes(final EndpointGroup endpointGroup) {
      javalin.routes(() -> endpoints(endpointGroup));
   }

   private void endpoints(final EndpointGroup endpointGroup) {
      path(getBasePath(), endpointGroup::addEndpoints);
   }

   protected String getBasePath() { return basePath; }

   protected void waitForPrecondition(final Context ctx) throws InterruptedException, ExecutionException {
      if (requiresConfiguredServer(ctx) && !this.onServerConfigured.isDone()) {
         String requestPath = ctx.path() + (ctx.queryString() == null ? "" : "?" + ctx.queryString());
         LOG.debug("Waiting for sever configuration to be completed: " + requestPath);
         // wait until server configured is complete
         this.onServerConfigured.get();
      }
   }

   protected boolean requiresConfiguredServer(final Context ctx) {
      return !ctx.path().contains(SERVER_CONFIGURE)
         && !ctx.path().contains(SERVER_PING);
   }

   protected void validateModel(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.validate(ctx, param));
   }

   protected void getValidationConstraints(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.getValidationConstraints(ctx, param));
   }

   protected void serverPing(final Context ctx) {
      serverController.ping(ctx);
   }

   protected void beforeServerConfigure(final Context ctx) {
      LOG.debug("SERVER_CONFIGURE started");
      this.onServerConfigured = new CompletableFuture<>();
   }

   protected void serverConfigure(final Context ctx) {
      serverController.configure(ctx);
   }

   protected void afterServerConfigure(final Context ctx) {
      LOG.debug("SERVER_CONFIGURE completed -> pending requests will be processed now");
      this.onServerConfigured.complete(null);
   }

   protected void getUiSchema(final Context ctx) {
      getParam(ctx, SCHEMA_NAME).ifPresentOrElse(
         param -> schemaController.getUiSchema(ctx, param),
         () -> missingParameter(ctx, SCHEMA_NAME));
   }

   protected void getTypeSchema(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> schemaController.getTypeSchema(ctx, param));
   }

   protected void getModelUris(final Context ctx) {
      modelController.getModelUris(ctx);
   }

   protected void undoCommand(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.undo(ctx, param));
   }

   protected void redoCommand(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.redo(ctx, param));
   }

   protected void saveAllModels(final Context ctx) {
      modelController.saveAll(ctx);
   }

   protected void saveModel(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.save(ctx, param));
   }

   protected void executeCommand(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.executeCommand(ctx, param));
   }

   protected void deleteModel(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.delete(ctx, param));
   }

   protected void closeModel(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.close(ctx, param));
   }

   protected void setModel(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, param -> modelController.update(ctx, param));
   }

   protected void getModelElement(final Context ctx) {
      uriConverter.withResolvedModelURI(ctx, modelURI -> {
         Optional<String> elementId = getParam(ctx, ELEMENT_ID);
         if (elementId.isPresent()) {
            modelController.getModelElementById(ctx, modelURI, elementId.get());
            return;
         }

         Optional<String> elementName = getParam(ctx, ELEMENT_NAME);
         if (elementName.isPresent()) {
            modelController.getModelElementByName(ctx, modelURI, elementName.get());
            return;
         }

         missingParameter(ctx, ELEMENT_ID + "' or '" + ELEMENT_NAME);
      });
   }

   protected void getModel(final Context ctx) {
      uriConverter.resolveModelURI(ctx).map(URI::toString).ifPresentOrElse(
         param -> modelController.getOne(ctx, param),
         () -> {
            if (!Strings.isNullOrEmpty(ctx.queryParam(MODEL_URI))) {
               // Rejected input should 404
               modelNotFound(ctx, ctx.queryParam(MODEL_URI));
            } else {
               modelController.getAll(ctx);
            }
         });
   }

   protected void createModel(final Context ctx) {
      uriConverter.resolveModelURI(ctx).map(URI::toString).ifPresentOrElse(
         param -> modelController.create(ctx, param),
         () -> missingParameter(ctx, MODEL_URI));
   }

   protected void subscribe(final WsConfig wsConfig) {
      wsConfig.onConnect(this::onSubscriptionConnect);
      wsConfig.onClose(this::onSubscriptionClose);
      wsConfig.onError(this::onSubscriptionError);
      wsConfig.onMessage(this::onSubscriptionMessage);
   }

   protected void onSubscriptionConnect(final WsConnectContext ctx) {
      uriConverter.resolveModelURI(ctx).map(URI::toString).ifPresentOrElse(
         modelUri -> this.subscriptionConnectionHandler.accept(ctx, modelUri),
         () -> {
            missingParameter(ctx, MODEL_URI);
            ctx.session.close();
         });
   }

   protected void setSubscriptionConnectionHandler(
      final BiConsumer<? super WsConnectContext, String> subscriptionConnectionHandler) {

      // Default to our own implementation if null
      this.subscriptionConnectionHandler = subscriptionConnectionHandler == null ? this::connectSubscription
         : subscriptionConnectionHandler;
   }

   protected void connectSubscription(final WsConnectContext ctx, final String modelUri) {
      if (!sessionController.subscribe(ctx, modelUri)) {
         error(ctx, "Cannot subscribe to '%s': modeluri is not a valid model resource", modelUri);
         ctx.session.close();
      }
   }

   protected void onSubscriptionClose(final WsCloseContext ctx) {
      if (!sessionController.unsubscribe(ctx)) {
         error(ctx, "Cannot unsubscribe: invalid session");
      }
   }

   protected void onSubscriptionError(final WsErrorContext ctx) {
      String message = ctx.error().getMessage() == null ? "No message" : ctx.error().getMessage();
      error(ctx, message);
   }

   protected void onSubscriptionMessage(final WsMessageContext ctx) {
      if (!sessionController.handleMessage(ctx)) {
         error(ctx, "Cannot handle message: %s", ctx.message());
      }
   }
}
