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

import java.net.HttpURLConnection;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public final class ContextResponse {
   private static final Logger LOG = Logger.getLogger(ContextResponse.class.getSimpleName());

   private ContextResponse() {}

   public static void response(final Context context, final JsonNode response) {
      context.json(response);
   }

   public static void success(final Context context) {
      response(context, JsonResponse.success());
   }

   public static void success(final Context context, final JsonNode jsonNode) {
      response(context, JsonResponse.success(jsonNode));
   }

   public static void success(final Context context, final String message) {
      response(context, JsonResponse.success(message));
   }

   public static void success(final Context context, final String messageFormat, final Object... args) {
      success(context, String.format(messageFormat, args));
   }

   public static void error(final Context context, final int statusCode, final String errorMsgFormat,
      final Object... args) {
      error(context, statusCode, String.format(errorMsgFormat, args));
   }

   public static void error(final Context context, final int statusCode, final String errorMsg) {
      error(context, statusCode, errorMsg, (Exception) null);
   }

   public static void error(final Context context, final int statusCode, final Exception exception) {
      error(context, statusCode, exception.getMessage(), exception);
   }

   public static void error(final Context context, final int statusCode, final String errorMsg,
      final Exception exception) {
      if (exception != null) {
         LOG.error(errorMsg, exception);
      } else {
         LOG.error(errorMsg);
      }
      context.status(statusCode);
      context.json(JsonResponse.error(errorMsg));
   }

   public static void warn(final Context context, final int statusCode, final String warningMsg) {
      LOG.error(warningMsg);
      context.status(statusCode);
      context.json(JsonResponse.warning(warningMsg));
   }

   public static void accepted(final Context context, final String message) {
      warn(context, HttpURLConnection.HTTP_ACCEPTED, message);
   }

   public static void encodingError(final Context context, final EncodingException exception) {
      internalError(context, "An error occurred during data encoding", exception);
   }

   public static void decodingError(final Context context, final DecodingException exception) {
      internalError(context, "An error occurred during data decoding", exception);
   }

   public static void modelNotFound(final Context context, final String modelUri) {
      error(context, HttpURLConnection.HTTP_NOT_FOUND, "Model '%s' not found!", modelUri);
   }

   public static void notFound(final Context context, final String errorMessage) {
      error(context, HttpURLConnection.HTTP_NOT_FOUND, errorMessage);
   }

   public static void notFound(final Context context, final String errorMessageFormat, final Object... args) {
      notFound(context, String.format(errorMessageFormat, args));
   }

   public static void internalError(final Context context, final Exception exception) {
      error(context, HttpURLConnection.HTTP_INTERNAL_ERROR, exception);
   }

   public static void internalError(final Context context, final String errorMessage) {
      error(context, HttpURLConnection.HTTP_INTERNAL_ERROR, errorMessage);
   }

   public static void internalError(final Context context, final String errorMessageFormat, final Object... args) {
      error(context, HttpURLConnection.HTTP_INTERNAL_ERROR, errorMessageFormat, args);
   }

   public static void internalError(final Context context, final String errorMessage, final Exception exception) {
      error(context, HttpURLConnection.HTTP_INTERNAL_ERROR, errorMessage, exception);
   }

   public static void badRequest(final Context context, final Exception exception) {
      error(context, HttpURLConnection.HTTP_BAD_REQUEST, exception);
   }

   public static void badRequest(final Context context, final String errorMessage, final Exception exception) {
      error(context, HttpURLConnection.HTTP_BAD_REQUEST, exception);
   }

   public static void badRequest(final Context context, final String errorMessage) {
      error(context, HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);
   }

   public static void conflict(final Context context, final String errorMessage) {
      error(context, HttpURLConnection.HTTP_CONFLICT, errorMessage);
   }

   public static void missingParameter(final Context context, final String parameter) {
      error(context, HttpURLConnection.HTTP_BAD_REQUEST, "Missing parameter '%s'!", parameter);
   }

   public static void success(final WsContext context) {
      context.send(JsonResponse.success(context.getSessionId()));
   }

   public static void dirtyState(final WsContext context, final boolean dirty) {
      context.send(JsonResponse.dirtyState(dirty));
   }

   public static void missingParameter(final WsContext context, final String parameter) {
      error(context, "Missing parameter '%s'!", parameter);
   }

   public static void error(final WsContext context, final String errorMsgFormat, final Object... args) {
      error(context, String.format(errorMsgFormat, args));
   }

   public static void error(final WsContext context, final String errorMsg, final Exception exception) {
      if (exception != null) {
         LOG.error(errorMsg, exception);
      } else {
         LOG.error(errorMsg);
      }
      context.send(JsonResponse.error(errorMsg));
   }

   public static void error(final WsContext context, final String errorMsg) {
      error(context, errorMsg, (Exception) null);
   }

   public static void keepAlive(final WsContext context) {
      context.send(JsonResponse.keepAlive(context.getSessionId() + " stayin' alive!"));
   }
}
