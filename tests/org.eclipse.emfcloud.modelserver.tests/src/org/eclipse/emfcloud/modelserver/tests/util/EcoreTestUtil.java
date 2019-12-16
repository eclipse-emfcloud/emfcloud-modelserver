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
package org.eclipse.emfcloud.modelserver.tests.util;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

public final class EcoreTestUtil {

   private EcoreTestUtil() {}

   public static EReference eReference(final String name, final int lower, final int upper, final EClassifier type) {
      EReference eReference = EcoreFactory.eINSTANCE.createEReference();
      eReference.setName(name);
      eReference.setLowerBound(lower);
      eReference.setUpperBound(upper);
      eReference.setEType(type);
      return eReference;
   }

   public static EClass emptyEClass(final String name) {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName(name);
      return eClass;
   }

   public static EAttribute stringEAttribute(final String name, final int lower, final int upper) {
      EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
      eAttribute.setName(name);
      eAttribute.setLowerBound(lower);
      eAttribute.setUpperBound(upper);
      eAttribute.setEType(EcorePackage.eINSTANCE.getEString());
      return eAttribute;
   }

}
