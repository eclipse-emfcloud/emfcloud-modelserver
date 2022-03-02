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
package org.eclipse.emfcloud.modelserver.emf.common.util;

import java.net.HttpURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public final class ContextResponse {
   private static final Logger LOG = LogManager.getLogger(ContextResponse.class);

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

   /**
    * Send a 'success' response to the client, containing the specified formatted message and Json Patch.
    *
    * @param context
    *                         The Context representing the client connection.
    * @param patch
    *                         The Json Patch node, showing the diff between the previous state of the model,
    *                         and the new state (e.g. after a model operation, or undo/redo).
    * @param messageFormat
    *                         The message to be attached to the result. Follows {@link String#format(String, Object...)}
    *                         syntax.
    * @param args
    *                         The arguments for the formatted message.
    */
   public static void successPatch(final Context context, final JsonNode patch, final String messageFormat,
      final Object... args) {
      JsonNode patchAndMessage = Json.object(
         Json.prop("message", Json.text(String.format(messageFormat, args))),
         Json.prop("patch", patch));
      success(context, patchAndMessage);
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

   public static void encodingError(final WsContext context, final EncodingException exception) {
      error(context, "An error occurred during data encoding", exception);
   }

   public static void decodingError(final Context context, final DecodingException exception) {
      internalError(context, "An error occurred during data decoding", exception);
   }

   public static void decodingError(final WsContext context, final DecodingException exception) {
      error(context, "An error occurred during data decoding", exception);
   }

   public static void modelNotFound(final Context context, final String modelUri) {
      error(context, HttpURLConnection.HTTP_NOT_FOUND, "Model '%s' not found!", modelUri);
   }

   public static void modelNotFound(final WsContext context, final String modelUri) {
      error(context, "Model '%s' not found!", modelUri);
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

   public static void success(final WsContext context, final JsonNode response) {
      context.send(JsonResponse.success(response));
   }

   public static void success(final WsContext context, final String messageFormat, final Object... args) {
      success(context, String.format(messageFormat, args));
   }

   /**
    * Send a 'success' response to the client, containing the specified formatted message and Json Patch.
    *
    * @param context
    *                         The Context representing the client connection.
    * @param patch
    *                         The Json Patch node, showing the diff between the previous state of the model,
    *                         and the new state (e.g. after a model operation, or undo/redo).
    * @param messageFormat
    *                         The message to be attached to the result. Follows {@link String#format(String, Object...)}
    *                         syntax.
    * @param args
    *                         The arguments for the formatted message.
    */
   public static void successPatch(final WsContext context, final JsonNode patch, final String messageFormat,
      final Object... args) {
      JsonNode patchAndMessage = Json.object(
         Json.prop("message", Json.text(String.format(messageFormat, args))),
         Json.prop("patch", patch));
      success(context, patchAndMessage);
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
