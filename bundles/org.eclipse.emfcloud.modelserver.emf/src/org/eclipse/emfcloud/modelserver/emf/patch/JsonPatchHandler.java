/********************************************************************************
 * Copyright (C) 2022 STMicroelectronics
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.patch;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.patch.PatchCommand;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class JsonPatchHandler implements PatchCommandHandler {

   @Override
   public boolean handles(final String type) {
      return ModelServerPathParametersV2.JSON_PATCH.equals(type);
   }

   @Override
   public PatchCommand<ArrayNode> getPatchCommand(final Context ctx, final JsonNode payload) {
      return getPatchCommand(payload);
   }

   @Override
   public PatchCommand<ArrayNode> getPatchCommand(final WsContext ctx, final JsonNode payload) {
      return getPatchCommand(payload);
   }

   private PatchCommand<ArrayNode> getPatchCommand(final JsonNode payload) {
      return new PatchCommand<>() {

         @Override
         public String getType() { return ModelServerPathParametersV2.JSON_PATCH; }

         @Override
         public ArrayNode getData() {
            if (!payload.isArray()) {
               return Json.array(payload);
            }
            return (ArrayNode) payload;
         }
      };
   }
}
