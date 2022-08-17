/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.jackson.module.EMFModule;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public final class ProviderDefaults {
   protected static final Logger LOG = LogManager.getLogger(ProviderDefaults.class);

   private static boolean isDevLoggingEnabled;

   private ProviderDefaults() {}

   public static AdapterFactory provideAdapterFactory() {
      return new ComposedAdapterFactory();
   }

   public static ObjectMapper provideObjectMapper() {
      return EMFModule.setupDefaultMapper();
   }

   public static void enableDevLogging() {
      ProviderDefaults.isDevLoggingEnabled = true;
   }

   public static Javalin provideJavalin() {
      return Javalin.create(config -> {
         config.enableCorsForAllOrigins();
         config.requestLogger((ctx, ms) -> {
            String requestPath = ctx.path() + (ctx.queryString() == null ? "" : "?" + ctx.queryString());
            LOG.info(ctx.method() + " " + requestPath + " -> Status: " + ctx.status() + " (took " + ms + " ms)");
         });
         config.asyncRequestTimeout = 5000L;
         config.jsonMapper(new JavalinJackson(ProviderDefaults.provideObjectMapper()));
         config.wsLogger(ws -> {
            ws.onConnect(ctx -> LOG.info("WS Connected: " + ctx.getSessionId()));
            ws.onMessage(ctx -> LOG.info("WS Received: " + ctx.message() + " by " + ctx.getSessionId()));
            ws.onClose(ctx -> LOG.info("WS Closed: " + ctx.getSessionId()));
            ws.onError(ctx -> LOG.info("WS Errored: " + ctx.getSessionId()));
         });
         if (ProviderDefaults.isDevLoggingEnabled) {
            config.enableDevLogging();
         }
      });
   }
}
