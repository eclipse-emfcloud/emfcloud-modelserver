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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfigurationDTO;

import com.google.inject.Inject;

import io.javalin.http.Context;

public class DefaultServerController implements ServerController {
   private final ServerConfiguration serverConfiguration;
   private final ModelRepository modelRepository;

   @Inject
   public DefaultServerController(final ServerConfiguration serverConfiguration,
      final ModelRepository modelRepository) {
      this.serverConfiguration = serverConfiguration;
      this.modelRepository = modelRepository;
   }

   @Override
   public void ping(final Context ctx) {
      ContextResponse.success(ctx);
   }

   @Override
   public void configure(final Context ctx) {
      ServerConfigurationDTO newConfiguration = ctx.bodyAsClass(ServerConfigurationDTO.class);
      try {
         if (updateServerConfiguration(newConfiguration)) {
            modelRepository.initialize();
            ContextResponse.success(ctx);
         }
      } catch (IllegalArgumentException exception) {
         ContextResponse.badRequest(ctx, exception);
      }
   }

   protected boolean updateServerConfiguration(final ServerConfigurationDTO newConfiguration) {
      String workspaceRoot = newConfiguration.getWorkspaceRoot();
      if (workspaceRoot == null) {
         return false;
      }

      if (!serverConfiguration.setWorkspaceRoot(workspaceRoot)) {
         throw new IllegalArgumentException("The given workspaceRoot is not a valid path: " + workspaceRoot);
      }

      String uiSchemaFolder = newConfiguration.getUiSchemaFolder();
      if (uiSchemaFolder != null) {
         serverConfiguration.setUiSchemaFolder(uiSchemaFolder);
      } else {
         serverConfiguration.setUiSchemaFolderURI(URI.createURI(""));
      }
      return true;
   }

}
