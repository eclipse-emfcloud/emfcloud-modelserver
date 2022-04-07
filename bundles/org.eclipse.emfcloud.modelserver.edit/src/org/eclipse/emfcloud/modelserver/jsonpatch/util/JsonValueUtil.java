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

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;

public final class JsonValueUtil {

   /**
    * Not instantiable by clients.
    */
   private JsonValueUtil() {
      super();
   }

   /**
    * Query whether a double value actually is an integral number.
    *
    * @param doubleValue a double value
    * @return whether it is an integer, having no fractional part
    */
   public static boolean isInteger(final double doubleValue) {
      return !Double.isNaN(doubleValue) && Double.isFinite(doubleValue) && Math.rint(doubleValue) == doubleValue;
   }

   /**
    * Obtain the best fitting JSON node for a double value.
    *
    * @param doubleValue a double value
    *
    * @return the best fitting number node
    */
   public static JsonNode valueOf(final double doubleValue) {
      if (Double.isNaN(doubleValue)) {
         return NullNode.instance;
      }

      if (isInteger(doubleValue)) {
         if (doubleValue > Long.MAX_VALUE || doubleValue < Long.MIN_VALUE) {
            // Gotta go big
            return BigIntegerNode.valueOf(new BigDecimal(doubleValue).toBigInteger());
         }
         if (doubleValue > Integer.MAX_VALUE || doubleValue < Integer.MIN_VALUE) {
            return LongNode.valueOf((long) doubleValue);
         }
         return IntNode.valueOf((int) doubleValue);
      }

      return DoubleNode.valueOf(doubleValue);
   }

}
