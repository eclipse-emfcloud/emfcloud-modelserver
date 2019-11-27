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

import org.jetbrains.annotations.Nullable;

import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * Structure of JsonResponse
 * {
 * "type": "success" | "error" | "fullUpdate" | "incrementalUpdate" | "dirtyState"
 * "data": String | Boolean | EObject
 * }
 */

public final class JsonResponse {

   private JsonResponse() {}

   private static ObjectNode type(final JsonResponseType type) {
      return Json.object(Json.prop("type", Json.text(type.toString())));
   }

   private static JsonNode data(@Nullable final JsonNode jsonNode) {
      return Json.object(
         Json.prop("data", jsonNode == null ? NullNode.getInstance() : jsonNode));
   }

   private static JsonNode data(final String message) {
      return Json.object(
         Json.prop("data", Json.text(message)));
   }

   private static JsonNode data(final Boolean b) {
      return Json.object(
         Json.prop("data", Json.bool(b)));
   }

   public static ObjectNode success() {
      return type(JsonResponseType.SUCCESS);
   }

   public static JsonNode success(@Nullable final JsonNode jsonNode) {
      return Json.merge(success(), data(jsonNode));
   }

   public static JsonNode success(final String message) {
      return Json.merge(success(), data(message));
   }

   public static ObjectNode error() {
      return type(JsonResponseType.ERROR);
   }

   public static JsonNode error(final String message) {
      return Json.merge(error(), data(message));
   }

   public static JsonNode fullUpdate(@Nullable final JsonNode jsonNode) {
      return Json.merge(type(JsonResponseType.FULLUPDATE), data(jsonNode));
   }

   public static JsonNode incrementalUpdate(@Nullable final JsonNode jsonNode) {
      return Json.merge(type(JsonResponseType.INCREMENTALUPDATE), data(jsonNode));
   }

   public static JsonNode dirtyState(final Boolean isDirty) {
      return Json.merge(type(JsonResponseType.DIRTYSTATE), data(isDirty));
   }
}
