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
package org.eclipse.emfcloud.modelserver.emf.common.util;

import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.badRequest;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.error;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;

public final class ContextRequest {
   protected static final Logger LOG = Logger.getLogger(ContextRequest.class.getSimpleName());

   private ContextRequest() {}

   public static Optional<String> getParam(final Context context, final String paramKey) {
      return getParam(context.queryParamMap(), paramKey);
   }

   public static Optional<String> getParam(final Map<String, List<String>> queryParams, final String paramKey) {
      return queryParams.containsKey(paramKey)
         ? Optional.ofNullable(queryParams.get(paramKey).get(0))
         : Optional.empty();
   }

   public static Optional<Long> getLongParam(final Context context, final String paramKey) {
      return getLongParam(context.queryParamMap(), paramKey);
   }

   public static Optional<Long> getLongParam(final WsContext context, final String paramKey) {
      return getLongParam(context.queryParamMap(), paramKey);
   }

   public static Optional<Long> getLongParam(final Map<String, List<String>> queryParams, final String paramKey) {
      return getParam(queryParams, paramKey).flatMap(ContextRequest::safeParseLong);
   }

   public static Optional<Integer> getIntegerParam(final WsContext context, final String paramKey) {
      return getIntegerParam(context.queryParamMap(), paramKey);
   }

   public static Optional<Integer> getIntegerParam(final Context context, final String paramKey) {
      return getIntegerParam(context.queryParamMap(), paramKey);
   }

   public static Optional<Integer> getIntegerParam(final Map<String, List<String>> queryParams, final String paramKey) {
      return getParam(queryParams, paramKey).flatMap(ContextRequest::safeParseInteger);
   }

   public static Optional<Boolean> getBooleanParam(final Map<String, List<String>> queryParams,
      final String paramKey) {
      return getParam(queryParams, paramKey).map(Boolean::parseBoolean);
   }

   public static Optional<Boolean> getBooleanParam(final Context context, final String paramKey) {
      return getBooleanParam(context.queryParamMap(), paramKey);
   }

   public static Optional<Boolean> getBooleanParam(final WsContext context, final String paramKey) {
      return getBooleanParam(context.queryParamMap(), paramKey);
   }

   private static Optional<Long> safeParseLong(final String text) {
      try {
         return Optional.of(Long.parseLong(text));
      } catch (NumberFormatException exception) {
         LOG.error(exception);
      }
      return Optional.empty();
   }

   private static Optional<Integer> safeParseInteger(final String text) {
      try {
         return Optional.of(Integer.parseInt(text));
      } catch (NumberFormatException exception) {
         LOG.error(exception);
      }
      return Optional.empty();
   }

   public static Optional<String> readData(final Context ctx) {
      try {
         if (ctx.body() == null) {
            badRequest(ctx, "Empty Body");
            return Optional.empty();
         }
         JsonNode json = JavalinJackson.getObjectMapper().readTree(ctx.body());
         if (!json.has(JsonResponseMember.DATA)) {
            badRequest(ctx, "Empty JSON");
            return Optional.empty();
         }
         JsonNode jsonDataNode = json.get(JsonResponseMember.DATA);
         String jsonData = !jsonDataNode.asText().isEmpty() ? jsonDataNode.asText() : jsonDataNode.toString();
         if (jsonData.equals("{}")) {
            badRequest(ctx, "Empty JSON");
            return Optional.empty();
         }
         return Optional.of(jsonData);
      } catch (IOException exception) {
         badRequest(ctx, "Invalid JSON", exception);
      }
      return Optional.empty();
   }

   public static boolean isMessageType(final WsMessageContext ctx, final String type) {
      return readMessageType(ctx).orElse("").equals(type);
   }

   public static Optional<String> readMessageType(final WsMessageContext ctx) {
      try {
         JsonNode json = JavalinJackson.getObjectMapper().readTree(ctx.message());
         if (!json.has(JsonResponseMember.TYPE)) {
            error(ctx, "Empty JSON");
            return Optional.empty();
         }
         JsonNode jsonTypeNode = json.get(JsonResponseMember.TYPE);
         String jsonType = !jsonTypeNode.asText().isEmpty() ? jsonTypeNode.asText() : jsonTypeNode.toString();
         if (jsonType.equals("{}")) {
            error(ctx, "Empty JSON");
            return Optional.empty();
         }
         return Optional.of(jsonType);
      } catch (IOException exception) {
         error(ctx, "Invalid JSON", exception);
      }
      return Optional.empty();
   }
}
