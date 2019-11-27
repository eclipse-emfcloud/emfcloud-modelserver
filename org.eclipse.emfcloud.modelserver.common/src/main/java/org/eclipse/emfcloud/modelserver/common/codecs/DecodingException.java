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
package org.eclipse.emfcloud.modelserver.common.codecs;

public class DecodingException extends Exception {

   private static final long serialVersionUID = 1L;

   public DecodingException(final Exception underlyingCause) {
      super(underlyingCause);
   }

   public DecodingException(final String message) {
      super(message);
   }

   public DecodingException(final String message, final Throwable cause) {
      super(message, cause);
   }

}
