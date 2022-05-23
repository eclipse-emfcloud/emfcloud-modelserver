/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics..
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Utility methods for Json Patch objects.
 */
public final class PatchUtil {

   // Json Patch Operation Constants

   public static final String OP = "op";
   public static final String TEST = "test";
   public static final String MOVE = "move";
   public static final String REMOVE = "remove";
   public static final String ADD = "add";
   public static final String REPLACE = "replace";
   public static final String ANY_INDEX = "-";
   public static final String PATH = "path";
   public static final String VALUE = "value";

   // Model Patch Constants

   /**
    * The "modelUri" attribute, for ModelPatch elements.
    */
   public static final String MODEL_URI = "modelUri";

   /**
    * The "patch" attribute, for ModelPatch elements.
    */
   public static final String PATCH = "patch";

   /**
    * Test if the specified node is a Json Patch (an array of Operations).
    *
    * @param jsonNode
    *                    The node to test.
    * @return
    *         <code>true</code> if the jsonNode is a Json Patch, <code>false</code> otherwise.
    */
   public static boolean isPatch(final JsonNode jsonNode) {
      return jsonNode.isArray() && PatchUtil.isPatch((ArrayNode) jsonNode);
   }

   /**
    * Test if the specified array is a Json Patch (an array of Operations).
    *
    * @param arrayNode
    *                    The node to test.
    * @return
    *         <code>true</code> if the jsonNode is a Json Patch, <code>false</code> otherwise.
    */
   public static boolean isPatch(final ArrayNode arrayNode) {
      return StreamSupport.stream(arrayNode.spliterator(), false).allMatch(PatchUtil::isOperation);
   }

   /**
    * Test if the specified node is a Json Patch Operation.
    *
    * @param jsonNode
    *                    The node to test.
    * @return
    *         <code>true</code> if the jsonNode is a Json Patch Operation, <code>false</code> otherwise.
    */
   public static boolean isOperation(final JsonNode jsonNode) {
      return jsonNode.has(OP) && jsonNode.has(PATH);
   }

   /**
    * Test if the specified node is a Model Patch (a Json object with a modelUri and a corresponding
    * Json Patch).
    *
    * @param jsonNode
    *                    The node to test.
    * @return
    *         <code>true</code> if the jsonNode is a Model Patch, <code>false</code> otherwise.
    */
   public static boolean isModelPatch(final JsonNode jsonNode) {
      return jsonNode.has(MODEL_URI) && jsonNode.has(PATCH);
   }

   private PatchUtil() {
      // No instances
   }
}
