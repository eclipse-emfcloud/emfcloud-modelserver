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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An EMF-style switch over the JSON representation of operation values in a JSON Patch..
 *
 * @param <T> the switch result type
 */
public class ValueSwitch<T> {

   public ValueSwitch() {
      super();
   }

   public T caseBoolean(final boolean value) {
      return null;
   }

   public T caseString(final String value) {
      return null;
   }

   public T caseNumber(final double value) {
      return null;
   }

   public T caseObject(final JsonNode value) {
      return null;
   }

   public T caseNull() {
      return null;
   }

   public T defaultCase(final JsonNode value) {
      return null;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   public T doSwitch(final JsonNode valueNode) {
      T result = null;

      if (valueNode == null) {
         result = caseNull();
      } else if (valueNode.isBoolean()) {
         result = caseBoolean(valueNode.booleanValue());
      } else if (valueNode.isTextual()) {
         result = caseString(valueNode.textValue());
      } else if (valueNode.isNumber()) {
         result = caseNumber(valueNode.doubleValue());
      } else if (valueNode.isNull()) {
         result = caseNull();
      } else if (valueNode.isObject()) {
         result = caseObject(valueNode);
      }

      if (result == null) {
         result = defaultCase(valueNode);
      }

      return result;
   }

}
