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
package org.eclipse.emfcloud.modelserver.emf.configuration;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emfcloud.modelserver.command.CCommandPackage;

public class CommandPackageConfiguration implements EPackageConfiguration {

   @Override
   public Collection<String> getFileExtensions() { return Collections.singletonList("command"); }

   @Override
   public void registerEPackage() {
      CCommandPackage.eINSTANCE.eClass();
   }

   @Override
   public String getId() { return CCommandPackage.eNS_URI; }

}
