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
package org.eclipse.emfcloud.modelserver.example;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1.BASE_PATH;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getIntegerParam;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getParam;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class CoffeeRouting implements Routing {
   private final Javalin javalin;

   private final AtomicInteger counter = new AtomicInteger(0);

   @Inject
   public CoffeeRouting(final Javalin javalin) {
      this.javalin = javalin;
   }

   @Override
   public void bindRoutes() {
      javalin.routes(this::endpoints);
   }

   protected void endpoints() {
      path(BASE_PATH, this::coffeeEndpoints);
   }

   protected void coffeeEndpoints() {
      get("counter", this::handleCounter);
   }

   protected void handleCounter(final Context ctx) {
      Optional<String> operation = getParam(ctx, "operation");
      if (operation.isPresent() && List.of("add", "subtract").contains(operation.get())) {
         handleCustom(ctx, operation.get());
      } else {
         ContextResponse.error(ctx, HttpURLConnection.HTTP_BAD_REQUEST,
            "Missing parameter 'operation': Please specify 'add' or 'subtract'.");
      }
   }

   protected void handleCustom(final Context ctx, final String operation) {
      Integer delta = getIntegerParam(ctx, "delta").orElse(1);
      boolean shouldIncrease = operation.contentEquals("add");
      int newValue = shouldIncrease
         ? counter.addAndGet(delta)
         : counter.addAndGet(-delta);
      ObjectNode data = Json.object(
         Json.prop("time", Json.text(DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now()))),
         Json.prop("delta", IntNode.valueOf(delta)),
         Json.prop("counter", IntNode.valueOf(newValue)),
         Json.prop("increased", Json.bool(shouldIncrease)));
      ctx.json(JsonResponse.success(data));
   }

}
