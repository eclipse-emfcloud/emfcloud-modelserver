/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.jsonschema;

/**
 * Constants for Json Models.
 */
public final class JsonConstants {

   /**
    * The Json attribute representing the type of an element.
    */
   public static final String TYPE_ATTR = "$type";

   /**
    * The Json attribute representing the ID of an element.
    */
   public static final String ID_ATTR = "$id";

   /**
    * The Json attribute representing a reference to another element.
    */
   public static final String REF_ATTR = "$ref";

   /**
    * The prefix character for Json Metadata attributes.
    */
   public static final String METADATA_PREFIX = "$";

   private JsonConstants() {
      // No instances
   }
}
