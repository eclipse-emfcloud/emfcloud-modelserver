/********************************************************************************
 * Copyright (c) 2023 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.tests.util;

import java.lang.reflect.Field;

/**
 * Additional Mockito utils.
 */
public final class MockitoUtil {

   /**
    * Not instantiable by clients.
    */
   private MockitoUtil() {
      super();
   }

   /**
    * Replaces Mockito's removed internal 'FieldSetter' class.
    * Was formerly imported via 'import org.mockito.internal.util.reflection.FieldSetter;'
    */
   public static void setField(final Object object, final Field field, final Object value) {
      try {
         field.setAccessible(true);
         field.set(object, value);
      } catch (IllegalAccessException e) {
         throw new RuntimeException("Failed to set value for field '" + field.getName() + "' of object", e);
      }
   }

}
