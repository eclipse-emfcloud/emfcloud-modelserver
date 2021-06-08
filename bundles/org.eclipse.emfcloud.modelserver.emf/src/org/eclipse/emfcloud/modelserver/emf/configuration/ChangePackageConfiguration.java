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

import org.eclipse.emf.ecore.change.ChangePackage;

public class ChangePackageConfiguration implements EPackageConfiguration {

   @Override
   public Collection<String> getFileExtensions() { return Collections.emptyList(); }

   @Override
   public void registerEPackage() {
      ChangePackage.eINSTANCE.eClass();
   }

   @Override
   public String getId() { return ChangePackage.eNS_URI; }

}
