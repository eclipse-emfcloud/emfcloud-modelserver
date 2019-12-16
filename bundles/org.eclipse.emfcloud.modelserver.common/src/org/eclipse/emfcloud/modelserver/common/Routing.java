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
package org.eclipse.emfcloud.modelserver.common;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class Routing {
   @Inject
   private Injector injector;

   protected Routing() {}

   public abstract void bindRoutes();

   public <T> T getController(final Class<T> clazz) {
      return injector.getInstance(clazz);
   }
}
