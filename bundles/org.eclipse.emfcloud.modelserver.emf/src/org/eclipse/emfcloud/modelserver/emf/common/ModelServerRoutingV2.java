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

import static io.javalin.apibuilder.ApiBuilder.after;
import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import static io.javalin.apibuilder.ApiBuilder.ws;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.MODEL_URI;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.error;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.missingParameter;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;
import org.eclipse.emfcloud.modelserver.common.Routing;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;

public class ModelServerRoutingV2 implements Routing {
   /**
    * This is not declared in the {@link ModelServerPathsV2} namespace because it is an
    * internal endpoint intended for use only by other model server instances.
    */
   protected static final String TRANSACTION_ENDPOINT = "transaction";

   private final ModelServerRoutingDelegate delegate;

   protected final ModelController modelController;
   protected final SessionController sessionController;
   protected final TransactionController transactionController;

   @Inject
   public ModelServerRoutingV2(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController, final SchemaController schemaController,
      final ServerController serverController, final SessionController sessionController,
      final TransactionController transactionController) {

      super();

      this.delegate = new ModelServerRoutingDelegate(javalin, resourceManager, modelController, schemaController,
         serverController, sessionController, ModelServerPathsV2.BASE_PATH);
      delegate.setSubscriptionConnectionHandler(this::connectSubscription);

      this.modelController = modelController;
      this.sessionController = sessionController;
      this.transactionController = transactionController;
   }

   @Override
   public void bindRoutes() {
      bindRoutes(this::endpoints);
   }

   protected void bindRoutes(final EndpointGroup endpointGroup) {
      delegate.bindRoutes(endpointGroup);
   }

   private void endpoints() {
      before(delegate::waitForPrecondition);

      get(ModelServerPathsV2.MODEL_BASE_PATH, delegate::getModel);
      post(ModelServerPathsV2.MODEL_BASE_PATH, delegate::createModel);
      delete(ModelServerPathsV2.MODEL_BASE_PATH, delegate::deleteModel);

      post(ModelServerPathsV2.CLOSE, delegate::closeModel);

      get(ModelServerPathsV2.UNDO, delegate::undoCommand);
      get(ModelServerPathsV2.REDO, delegate::redoCommand);

      get(ModelServerPathsV2.MODEL_ELEMENT, delegate::getModelElement);

      get(ModelServerPathsV2.SAVE, delegate::saveModel);
      get(ModelServerPathsV2.SAVE_ALL, delegate::saveAllModels);

      get(ModelServerPathsV2.MODEL_URIS, delegate::getModelUris);

      get(ModelServerPathsV2.TYPE_SCHEMA, delegate::getTypeSchema);

      get(ModelServerPathsV2.UI_SCHEMA, delegate::getUiSchema);

      before(ModelServerPathsV2.SERVER_CONFIGURE, delegate::beforeServerConfigure);
      put(ModelServerPathsV2.SERVER_CONFIGURE, delegate::serverConfigure);
      after(ModelServerPathsV2.SERVER_CONFIGURE, delegate::afterServerConfigure);

      get(ModelServerPathsV2.SERVER_PING, delegate::serverPing);

      get(ModelServerPathsV2.VALIDATION, delegate::validateModel);
      get(ModelServerPathsV2.VALIDATION_CONSTRAINTS, delegate::getValidationConstraints);

      ws(ModelServerPathsV2.SUBSCRIPTION, delegate::subscribe);

      put(ModelServerPathsV2.MODEL_BASE_PATH, delegate::setModel); // Was PATCH in V1

      patch(ModelServerPathsV2.MODEL_BASE_PATH, this::executeCommand); // Was PATCH /edit in V1

      post(TRANSACTION_ENDPOINT, this::createTransaction);
      ws(TRANSACTION_ENDPOINT + "/{id}", this::openTransaction);
   }

   protected void connectSubscription(final WsConnectContext ctx, final String modelUri) {
      if (!sessionController.subscribeV2(ctx, modelUri)) {
         error(ctx, "Cannot subscribe to '%s': modeluri is not a valid model resource", modelUri);
         ctx.session.close();
      }
   }

   protected void createTransaction(final Context ctx) {
      delegate.getResolvedFileUri(ctx, MODEL_URI).ifPresentOrElse(
         param -> transactionController.create(ctx, param),
         () -> missingParameter(ctx, MODEL_URI));
   }

   protected void openTransaction(final WsConfig wsConfig) {
      wsConfig.onConnect(transactionController::onOpen);
      wsConfig.onClose(transactionController::onClose);
      wsConfig.onError(transactionController::onError);
      wsConfig.onMessage(transactionController::onMessage);
   }

   protected void executeCommand(final Context ctx) {
      delegate.getResolvedFileUri(ctx, MODEL_URI).ifPresentOrElse(
         param -> modelController.executeCommandV2(ctx, param),
         () -> missingParameter(ctx, MODEL_URI));
   }
}
