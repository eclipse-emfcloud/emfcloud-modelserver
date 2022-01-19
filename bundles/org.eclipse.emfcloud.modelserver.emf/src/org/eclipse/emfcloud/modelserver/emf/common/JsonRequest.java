/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * Structure of JSON request message. Request message types are enumerated in the
 * {@link JsonRequestType} interface and request message members in the
 * {@link JsonRequestMember} interface.
 */
public final class JsonRequest {

   private JsonRequest() {}

   private static ObjectNode type(final String requestType) {
      return Json.object(Json.prop(JsonRequestMember.TYPE, Json.text(requestType)));
   }

   private static JsonNode data(@Nullable final JsonNode jsonNode) {
      return Json.object(
         Json.prop(JsonRequestMember.DATA, jsonNode == null ? NullNode.getInstance() : jsonNode));
   }

   public static JsonNode close() {
      return type(JsonRequestType.CLOSE);
   }

   public static JsonNode execute(@Nullable final JsonNode command) {
      return Json.merge(type(JsonRequestType.EXECUTE), data(command));
   }

   public static JsonNode rollback(@Nullable final JsonNode diagnostic) {
      return Json.merge(type(JsonRequestType.ROLL_BACK), data(diagnostic));
   }

}
