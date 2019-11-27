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
 * A representation of the literals of the enumeration '<em><b>Ram Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getRamType()
 * @model
 * @generated
 */
public enum RamType implements Enumerator {
   /**
    * The '<em><b>SODIMM</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #SODIMM_VALUE
    * @generated
    * @ordered
    */
   SODIMM(0, "SODIMM", "SO-DIMM"), //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * The '<em><b>SIDIMM</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #SIDIMM_VALUE
    * @generated
    * @ordered
    */
   SIDIMM(0, "SIDIMM", "SI-DIMM"); //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * The '<em><b>SODIMM</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #SODIMM
    * @model literal="SO-DIMM"
    * @generated
    * @ordered
    */
   public static final int SODIMM_VALUE = 0;

   /**
    * The '<em><b>SIDIMM</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #SIDIMM
    * @model literal="SI-DIMM"
    * @generated
    * @ordered
    */
   public static final int SIDIMM_VALUE = 0;

   /**
    * An array of all the '<em><b>Ram Type</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static final RamType[] VALUES_ARRAY = new RamType[] {
      SODIMM,
      SIDIMM,
   };

   /**
    * A public read-only list of all the '<em><b>Ram Type</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static final List<RamType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Ram Type</b></em>' literal with the specified literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param literal the literal.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static RamType get(final String literal) {
      for (RamType result : VALUES_ARRAY) {
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Ram Type</b></em>' literal with the specified name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param name the name.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static RamType getByName(final String name) {
      for (RamType result : VALUES_ARRAY) {
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Ram Type</b></em>' literal with the specified integer value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the integer value.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static RamType get(final int value) {
      switch (value) {
         case SODIMM_VALUE:
            return SODIMM;
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
   private RamType(final int value, final String name, final String literal) {
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

} // RamType
