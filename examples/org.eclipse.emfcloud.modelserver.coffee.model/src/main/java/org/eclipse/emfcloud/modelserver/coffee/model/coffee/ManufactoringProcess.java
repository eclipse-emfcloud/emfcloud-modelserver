/**
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.coffee.model.coffee;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Manufactoring Process</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getManufactoringProcess()
 * @model
 * @generated
 */
public enum ManufactoringProcess implements Enumerator {
   /**
    * The '<em><b>Nm18</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NM18_VALUE
    * @generated
    * @ordered
    */
   NM18(0, "nm18", "18nm"), //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * The '<em><b>Nm25</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NM25_VALUE
    * @generated
    * @ordered
    */
   NM25(1, "nm25", "nm25"); //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * The '<em><b>Nm18</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NM18
    * @model name="nm18" literal="18nm"
    * @generated
    * @ordered
    */
   public static final int NM18_VALUE = 0;

   /**
    * The '<em><b>Nm25</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NM25
    * @model name="nm25"
    * @generated
    * @ordered
    */
   public static final int NM25_VALUE = 1;

   /**
    * An array of all the '<em><b>Manufactoring Process</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static final ManufactoringProcess[] VALUES_ARRAY = new ManufactoringProcess[] {
      NM18,
      NM25,
   };

   /**
    * A public read-only list of all the '<em><b>Manufactoring Process</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static final List<ManufactoringProcess> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Manufactoring Process</b></em>' literal with the specified literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param literal the literal.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ManufactoringProcess get(final String literal) {
      for (ManufactoringProcess result : VALUES_ARRAY) {
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Manufactoring Process</b></em>' literal with the specified name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param name the name.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ManufactoringProcess getByName(final String name) {
      for (ManufactoringProcess result : VALUES_ARRAY) {
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Manufactoring Process</b></em>' literal with the specified integer value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the integer value.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ManufactoringProcess get(final int value) {
      switch (value) {
         case NM18_VALUE:
            return NM18;
         case NM25_VALUE:
            return NM25;
      }
      return null;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private final int value;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private final String name;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private final String literal;

   /**
    * Only this class can construct instances.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private ManufactoringProcess(final int value, final String name, final String literal) {
      this.value = value;
      this.name = name;
      this.literal = literal;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getValue() { return value; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String getName() { return name; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String getLiteral() { return literal; }

   /**
    * Returns the literal value of the enumerator, which is its string representation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String toString() {
      return literal;
   }

} // ManufactoringProcess
