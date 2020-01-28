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
package org.eclipse.emfcloud.modelserver.client;

import java.util.Optional;

public class ModelServerNotification {
   private final String type;
   private final Optional<String> data;

   public ModelServerNotification(final String type, final Optional<String> data) {
      super();
      this.type = type;
      this.data = data;
   }

   public String getType() { return type; }

   public Optional<String> getData() { return data; }

   @Override
   public String toString() {
      return super.toString() + "{ type = " + getType() + ", data = " + getData() + "}";
   }
}
