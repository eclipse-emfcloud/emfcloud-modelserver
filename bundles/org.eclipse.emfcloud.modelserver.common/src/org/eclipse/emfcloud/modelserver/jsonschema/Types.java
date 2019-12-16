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
package org.eclipse.emfcloud.modelserver.jsonschema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;

public final class Types {

   private Types() {}

   public static boolean isValid(final EClassifier eClassifier) {
      return isBoolean(eClassifier)
         || isString(eClassifier)
         || isNumber(eClassifier)
         || isInteger(eClassifier)
         || isDate(eClassifier)
         || isEnum(eClassifier);
   }

   public static boolean isBoolean(final EClassifier eType) {
      return Boolean.class.isAssignableFrom(eType.getInstanceClass())
         || eType.getInstanceClass() == boolean.class;
   }

   public static boolean isString(final EClassifier eType) {
      return String.class.isAssignableFrom(eType.getInstanceClass());
   }

   public static boolean isNumber(final EClassifier eClassifier) {
      return isFloat(eClassifier) || isDouble(eClassifier) || isBigDecimal(eClassifier);
   }

   public static boolean isInteger(final EClassifier eClassifier) {
      return isBigInteger(eClassifier)
         || isByte(eClassifier)
         || isChar(eClassifier)
         || isInt(eClassifier)
         || isLong(eClassifier)
         || isShort(eClassifier);
   }

   public static boolean isEnum(final EClassifier eClassifier) {
      return EcorePackage.eINSTANCE.getEEnum().isInstance(eClassifier);
   }

   public static boolean isDate(final EClassifier eClassifier) {
      return Date.class.isAssignableFrom(eClassifier.getInstanceClass())
         || XMLGregorianCalendar.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isFloat(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == float.class
         || Float.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isDouble(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == double.class
         || Double.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isBigDecimal(final EClassifier eClassifier) {
      return BigDecimal.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isBigInteger(final EClassifier eClassifier) {
      return BigInteger.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isByte(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == byte.class
         || Byte.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isChar(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == char.class
         || Character.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isInt(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == int.class
         || Integer.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isLong(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == long.class
         || Long.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   private static boolean isShort(final EClassifier eClassifier) {
      return eClassifier.getInstanceClass() == short.class
         || Short.class.isAssignableFrom(eClassifier.getInstanceClass());
   }

   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:BooleanExpressionComplexity" })
   public static boolean isUnsupportedType(
      final EClassifier eType) {
      return eType == EcorePackage.eINSTANCE.getEByteArray()
         || eType == EcorePackage.eINSTANCE.getEDiagnosticChain()
         || eType == EcorePackage.eINSTANCE.getEEList()
         || eType == EcorePackage.eINSTANCE.getEEnumerator()
         || eType == EcorePackage.eINSTANCE.getEFeatureMap()
         || eType == EcorePackage.eINSTANCE.getEFeatureMapEntry()
         || eType == EcorePackage.eINSTANCE.getEInvocationTargetException()
         || eType == EcorePackage.eINSTANCE.getEJavaClass()
         || eType == EcorePackage.eINSTANCE.getEJavaObject()
         || eType == EcorePackage.eINSTANCE.getEMap()
         || eType == EcorePackage.eINSTANCE.getEResource()
         || eType == EcorePackage.eINSTANCE.getEResourceSet()
         || eType == EcorePackage.eINSTANCE.getETreeIterator();
   }
}
