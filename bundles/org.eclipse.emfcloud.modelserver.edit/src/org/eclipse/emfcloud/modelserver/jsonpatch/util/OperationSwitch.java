/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An EMF-style switch over the JSON representation of the operations in a JSON Patch.
 *
 * @param <T> the switch result type
 */
public class OperationSwitch<T> {

   public OperationSwitch() {
      super();
   }

   public T caseAdd(final JsonNode addOp) {
      return null;
   }

   public T caseRemove(final JsonNode removeOp) {
      return null;
   }

   public T caseReplace(final JsonNode replaceOp) {
      return null;
   }

   public T caseMove(final JsonNode moveOp) {
      return null;
   }

   public T caseCopy(final JsonNode copyOp) {
      return null;
   }

   public T caseTest(final JsonNode testOp) {
      return null;
   }

   public T caseOperation(final JsonNode op) {
      return null;
   }

   public T defaultCase(final JsonNode op) {
      return null;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   public T doSwitch(final JsonNode operation) {
      T result = null;

      JsonNode op = operation.get("op");
      if (op != null && op.isTextual()) {
         String opString = op.textValue().toLowerCase();
         switch (opString) {
            case "add":
               result = caseAdd(operation);
               break;
            case "remove":
               result = caseRemove(operation);
               break;
            case "replace":
               result = caseReplace(operation);
               break;
            case "move":
               result = caseMove(operation);
               break;
            case "copy":
               result = caseCopy(operation);
               break;
            case "test":
               result = caseTest(operation);
               break;
            default:
               // Some other unknown kind of operation?
               result = caseOperation(operation);
               break;
         }
      }

      if (result == null) {
         result = defaultCase(operation);
      }

      return result;
   }

   protected Optional<String> getPath(final JsonNode operation) {
      return getString(operation, "path");
   }

   protected Optional<String> getString(final JsonNode node, final String property) {
      return Optional.ofNullable(node.get(property)).filter(Objects::nonNull)
         .filter(JsonNode::isTextual).map(JsonNode::textValue);
   }

}
