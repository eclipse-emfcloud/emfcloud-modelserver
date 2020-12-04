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
package org.eclipse.emfcloud.modelserver.emf.common;

public enum JsonResponseType {

   SUCCESS("success"),
   ERROR("error"),
   FULLUPDATE("fullUpdate"),
   INCREMENTALUPDATE("incrementalUpdate"),
   DIRTYSTATE("dirtyState"),
   KEEPALIVE("keepAlive");

   private String typeString;

   JsonResponseType(final String typeString) {
      this.typeString = typeString;
   }

   @Override
   public String toString() {
      return typeString;
   }

}
