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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.Routing;

import com.google.inject.Inject;

import io.javalin.Javalin;

public class ModelServerRoutingV1 implements Routing {
   protected static final Logger LOG = LogManager.getLogger(ModelServerRoutingV1.class);

   protected final ModelServerRoutingDelegate delegate;

   @Inject
   public ModelServerRoutingV1(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController, final SchemaController schemaController,
      final ServerController serverController, final SessionController sessionController) {

      super();

      this.delegate = new ModelServerRoutingDelegate(javalin, resourceManager, modelController, schemaController,
         serverController, sessionController, ModelServerPathsV1.BASE_PATH);
   }

   @Override
   public void bindRoutes() {
      delegate.bindRoutes(this::endpoints);
   }

   private void endpoints() {
      before(delegate::waitForPrecondition);

      get(ModelServerPathsV1.MODEL_BASE_PATH, delegate::getModel);
      post(ModelServerPathsV1.MODEL_BASE_PATH, delegate::createModel);
      delete(ModelServerPathsV1.MODEL_BASE_PATH, delegate::deleteModel);

      post(ModelServerPathsV1.CLOSE, delegate::closeModel);

      get(ModelServerPathsV1.UNDO, delegate::undoCommand);
      get(ModelServerPathsV1.REDO, delegate::redoCommand);

      get(ModelServerPathsV1.MODEL_ELEMENT, delegate::getModelElement);

      get(ModelServerPathsV1.SAVE, delegate::saveModel);
      get(ModelServerPathsV1.SAVE_ALL, delegate::saveAllModels);

      get(ModelServerPathsV1.MODEL_URIS, delegate::getModelUris);

      get(ModelServerPathsV1.TYPE_SCHEMA, delegate::getTypeSchema);

      get(ModelServerPathsV1.UI_SCHEMA, delegate::getUiSchema);

      before(ModelServerPathsV1.SERVER_CONFIGURE, delegate::beforeServerConfigure);
      put(ModelServerPathsV1.SERVER_CONFIGURE, delegate::serverConfigure);
      after(ModelServerPathsV1.SERVER_CONFIGURE, delegate::afterServerConfigure);

      get(ModelServerPathsV1.SERVER_PING, delegate::serverPing);

      get(ModelServerPathsV1.VALIDATION, delegate::validateModel);
      get(ModelServerPathsV1.VALIDATION_CONSTRAINTS, delegate::getValidationConstraints);

      patch(ModelServerPathsV1.MODEL_BASE_PATH, delegate::setModel);
      patch(ModelServerPathsV1.EDIT, delegate::executeCommand);

      ws(ModelServerPathsV1.SUBSCRIPTION, delegate::subscribe);

      // TODO: ws for the commands
   }
}
