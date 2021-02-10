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

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class ServerController {
   private static final Logger LOG = Logger.getLogger(ServerController.class);
   @Inject
   private ServerConfiguration serverConfiguration;
   @Inject
   private ModelRepository modelRepository;

   protected void ping(final Context ctx) {
      ctx.json(JsonResponse.success());
   }

   public CompletableFuture<ObjectNode> configure(final Context ctx) {
      ServerConfiguration newConf = ctx.bodyAsClass(ServerConfiguration.class);
      CompletableFuture<ObjectNode> future = new CompletableFuture<>();
      try {
         if (updateServerConfiguration(newConf)) {
            modelRepository.initialize();
            return CompletableFuture.completedFuture(JsonResponse.success());
         }
      } catch (IllegalArgumentException exception) {
         handleError(ctx, 400, exception.getMessage());
      }
      return future;
   }

   protected boolean updateServerConfiguration(final ServerConfiguration newConfiguration) {
      URI workspaceRootUri = newConfiguration.getWorkspaceRootURI();
      if (workspaceRootUri == null) {
         return false;
      }
      String workspaceRoot = workspaceRootUri.toString();
      if (!ServerConfiguration.isValidFileURI(workspaceRoot)) {
         throw new IllegalArgumentException("The given workspaceRoot is not a valid path: " + workspaceRoot);
      }
      serverConfiguration.setWorkspaceRoot(workspaceRoot);
      if (newConfiguration.getUiSchemaFolderURI() != null
         && ServerConfiguration.isValidFileURI(newConfiguration.getUiSchemaFolderURI().toString())) {
         serverConfiguration.setUiSchemaFolder(newConfiguration.getUiSchemaFolderURI().toString());
      } else {
         serverConfiguration.setUiSchemaFolderURI(URI.createURI(""));
      }
      return true;
   }

   private void handleError(final Context ctx, final int statusCode, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.status(statusCode)
         .result(errorMsg)
         .json(JsonResponse.error());
   }

   public Handler getPingHandler() { return this::ping; }

}
