/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.jackson.module.EMFModule;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;

public final class ProviderDefaults {
   protected static final Logger LOG = Logger.getLogger(ProviderDefaults.class.getSimpleName());

   private ProviderDefaults() {}

   public static AdapterFactory provideAdapterFactory() {
      return new ComposedAdapterFactory();
   }

   public static ObjectMapper provideObjectMapper() {
      return EMFModule.setupDefaultMapper();
   }

   public static Javalin provideJavalin() {
      return Javalin.create(config -> {
         config.enableCorsForAllOrigins();
         config.requestLogger((ctx, ms) -> {
            String requestPath = ctx.path() + (ctx.queryString() == null ? "" : "?" + ctx.queryString());
            LOG.info(ctx.method() + " " + requestPath + " -> Status: " + ctx.status() + " (took " + ms + " ms)");
         });
         config.asyncRequestTimeout = 5000L;
         config.wsLogger(ws -> {
            ws.onConnect(ctx -> LOG.info("WS Connected: " + ctx.getSessionId()));
            ws.onMessage(ctx -> LOG.info("WS Received: " + ctx.message() + " by " + ctx.getSessionId()));
            ws.onClose(ctx -> LOG.info("WS Closed: " + ctx.getSessionId()));
            ws.onError(ctx -> LOG.info("WS Errored: " + ctx.getSessionId()));
         });
      });
   }
}
