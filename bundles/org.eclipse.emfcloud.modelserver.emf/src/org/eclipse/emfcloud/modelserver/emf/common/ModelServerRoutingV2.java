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

import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import static io.javalin.apibuilder.ApiBuilder.ws;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.MODEL_URI;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.error;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.missingParameter;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;

public class ModelServerRoutingV2 extends AbstractModelServerRouting {
   protected static final Logger LOG = LogManager.getLogger(ModelServerRoutingV2.class);

   protected final TransactionController transactionController;

   @Inject
   public ModelServerRoutingV2(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController, final SchemaController schemaController,
      final ServerController serverController, final SessionController sessionController,
      final TransactionController transactionController) {

      super(javalin, resourceManager, modelController, schemaController, serverController, sessionController);

      this.transactionController = transactionController;
   }

   @Override
   protected String getBasePath() { return ModelServerPathsV2.BASE_PATH; }

   @Override
   protected void apiEndpoints() {
      super.apiEndpoints();

      put(ModelServerPathsV2.MODEL_BASE_PATH, this::setModel); // Was PATCH in V1

      patch(ModelServerPathsV2.MODEL_BASE_PATH, this::executeCommand); // Was PATCH /edit in V1

      post(ModelServerPathsV2.TRANSACTION, this::createTransaction);
      ws(ModelServerPathsV2.TRANSACTION + "/:id", this::openTransaction);
   }

   @Override
   protected void onSubscriptionConnect(final WsConnectContext ctx) {
      Optional<String> modelUri = getResolvedFileUri(ctx.queryParamMap(), MODEL_URI);
      if (modelUri.isEmpty()) {
         missingParameter(ctx, MODEL_URI);
         ctx.session.close();
         return;
      }

      if (!sessionController.subscribeV2(ctx, modelUri.get())) {
         error(ctx, "Cannot subscribe to '%s': modeluri is not a valid model resource", modelUri.get());
         ctx.session.close();
      }
   }

   protected void createTransaction(final Context ctx) {
      getResolvedFileUri(ctx, MODEL_URI).ifPresentOrElse(
         param -> transactionController.create(ctx, param),
         () -> missingParameter(ctx, MODEL_URI));
   }

   protected void openTransaction(final WsConfig wsConfig) {
      wsConfig.onConnect(transactionController::onOpen);
      wsConfig.onClose(transactionController::onClose);
      wsConfig.onError(transactionController::onError);
      wsConfig.onMessage(transactionController::onMessage);
   }

   @Override
   protected void executeCommand(final Context ctx) {
      getResolvedFileUri(ctx, MODEL_URI).ifPresentOrElse(
         param -> modelController.executeCommandV2(ctx, param),
         () -> missingParameter(ctx, MODEL_URI));
   }
}
