/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.apache.log4j.Logger;

import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class ServerController {
   private static final Logger LOG = Logger.getLogger(ServerController.class);
   @Inject
   private ServerConfiguration serverConfiguration;
   @Inject
   private ModelRepository modelRepository;

   private final Handler pingHandler = ctx -> {
      ctx.json(JsonResponse.success());
   };

   private final Handler configureHandler = ctx -> {
      ServerConfiguration newConf = ctx.bodyAsClass(ServerConfiguration.class);
      String workspaceRoot = newConf.getWorkspaceRoot();
      if (workspaceRoot != null) {
         if (ServerConfiguration.isValidWorkspaceRoot(workspaceRoot)) {
            serverConfiguration.setWorkspaceRoot(workspaceRoot);
            modelRepository.initialize(workspaceRoot, true);
            ctx.json(JsonResponse.success());
         } else {
            handleError(ctx, 400, "The given workspaceRoot is not a valid path: " + workspaceRoot);
         }
      }
   };

   private void handleError(final Context ctx, final int statusCode, final String errorMsg) {
      LOG.error(errorMsg);
      ctx.status(statusCode)
         .result(errorMsg)
         .json(JsonResponse.error());
   }

   public Handler getPingHandler() { return pingHandler; }

   public Handler getConfigureHandler() { return configureHandler; }

}
