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

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;

import com.google.inject.Inject;

import io.javalin.Javalin;

public class ModelServerRoutingV1 extends AbstractModelServerRouting {
   protected static final Logger LOG = Logger.getLogger(ModelServerRoutingV1.class.getSimpleName());

   @Inject
   public ModelServerRoutingV1(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController, final SchemaController schemaController,
      final ServerController serverController, final SessionController sessionController) {

      super(javalin, resourceManager, modelController, schemaController, serverController, sessionController);
   }

   @Override
   protected void apiEndpoints() {
      super.apiEndpoints();

      patch(ModelServerPathsV1.MODEL_BASE_PATH, this::setModel);
      patch(ModelServerPathsV1.EDIT, this::executeCommand);
   }

   @Override
   protected String getBasePath() { return ModelServerPathsV1.BASE_PATH; }
}
