/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

/**
 * Exception thrown when a JsonPatch object is invalid.
 */
public class JsonPatchException extends Exception {

   private static final long serialVersionUID = -701410021200759108L;

   public JsonPatchException() {
      super();
   }

   public JsonPatchException(final String message) {
      super(message);
   }

   public JsonPatchException(final String message, final Exception cause) {
      super(message, cause);
   }

}
