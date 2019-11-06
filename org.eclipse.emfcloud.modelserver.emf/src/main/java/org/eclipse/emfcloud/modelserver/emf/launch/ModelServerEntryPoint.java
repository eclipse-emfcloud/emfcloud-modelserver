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
package org.eclipse.emfcloud.modelserver.emf.launch;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.Routing;
import com.google.inject.Inject;

import io.javalin.Javalin;

public class ModelServerEntryPoint implements AppEntryPoint {

   private final Javalin app;
   private static final Logger LOG = Logger.getLogger(ModelServerEntryPoint.class.getSimpleName());

   @Inject(optional = true)
   private final Set<Routing> routes = Collections.emptySet();

   @Inject
   public ModelServerEntryPoint(final Javalin app) {
      this.app = app;
   }

   @Override
   public void boot(final int port) {
      bindRoutes();

      app.events(event -> event.serverStartFailed(() -> LOG.error("SERVER START FAILED")))
         .start(port);
   }

   private void bindRoutes() {
      routes.forEach(Routing::bindRoutes);
   }
}
