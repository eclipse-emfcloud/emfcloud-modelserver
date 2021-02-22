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
package org.eclipse.emfcloud.modelserver.emf.launch;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;

import com.google.inject.Inject;

public class DefaultModelServerStartup implements ModelServerStartup {

   @Inject(optional = true)
   private final Map<EntryPointType, AppEntryPoint> entryPoints = Collections.emptyMap();

   @Override
   public void start(final EntryPointType entryPointType, final int port) {
      AppEntryPoint appEntryPoint = entryPoints.get(entryPointType);
      if (appEntryPoint == null) {
         new RuntimeException("EntryPoint " + entryPointType + " not defined");
      }
      appEntryPoint.boot(port);
   }
}
