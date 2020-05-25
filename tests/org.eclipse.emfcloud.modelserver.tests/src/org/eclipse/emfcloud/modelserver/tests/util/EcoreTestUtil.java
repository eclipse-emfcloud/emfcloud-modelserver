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

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
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

   public static EAttribute createEAttribute(final String name, final int lower, final int upper,
      final EClassifier eType) {
      EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
      eAttribute.setName(name);
      eAttribute.setLowerBound(lower);
      eAttribute.setUpperBound(upper);
      eAttribute.setEType(eType);
      return eAttribute;
   }

   public static EAttribute stringEAttribute(final String name, final int lower, final int upper) {
      return createEAttribute(name, lower, upper, EcorePackage.eINSTANCE.getEString());
   }

   public static EAttribute integerEAttribute(final String name, final int lower, final int upper) {
      return createEAttribute(name, lower, upper, EcorePackage.eINSTANCE.getEInt());
   }

   public static EAttribute booleanEAttribute(final String name, final int lower, final int upper) {
      return createEAttribute(name, lower, upper, EcorePackage.eINSTANCE.getEBoolean());
   }

   public static EEnum createEENum(final String eEnumName, final List<String> literals) {
      EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
      eEnum.setName(eEnumName);
      for (int i = 0; i < literals.size(); i++) {
         EEnumLiteral eEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
         eEnumLiteral.setValue(i);
         eEnumLiteral.setName(literals.get(i));
         eEnum.getELiterals().add(eEnumLiteral);
      }
      return eEnum;
   }

}
