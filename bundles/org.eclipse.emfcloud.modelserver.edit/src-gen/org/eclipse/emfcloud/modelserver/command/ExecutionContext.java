/**
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Execution Context</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getExecutionContext()
 * @model
 * @generated
 */
public enum ExecutionContext implements Enumerator {
   /**
    * The '<em><b>EXECUTE</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #EXECUTE_VALUE
    * @generated
    * @ordered
    */
   EXECUTE(0, "EXECUTE", "EXECUTE"),

   /**
    * The '<em><b>UNDO</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #UNDO_VALUE
    * @generated
    * @ordered
    */
   UNDO(1, "UNDO", "UNDO"),

   /**
    * The '<em><b>REDO</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REDO_VALUE
    * @generated
    * @ordered
    */
   REDO(2, "REDO", "REDO");

   /**
    * The '<em><b>EXECUTE</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #EXECUTE
    * @model
    * @generated
    * @ordered
    */
   public static final int EXECUTE_VALUE = 0;

   /**
    * The '<em><b>UNDO</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #UNDO
    * @model
    * @generated
    * @ordered
    */
   public static final int UNDO_VALUE = 1;

   /**
    * The '<em><b>REDO</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REDO
    * @model
    * @generated
    * @ordered
    */
   public static final int REDO_VALUE = 2;

   /**
    * An array of all the '<em><b>Execution Context</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static final ExecutionContext[] VALUES_ARRAY = new ExecutionContext[] {
      EXECUTE,
      UNDO,
      REDO,
   };

   /**
    * A public read-only list of all the '<em><b>Execution Context</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static final List<ExecutionContext> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Execution Context</b></em>' literal with the specified literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param literal the literal.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ExecutionContext get(final String literal) {
      for (ExecutionContext result : VALUES_ARRAY) {
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Execution Context</b></em>' literal with the specified name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param name the name.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ExecutionContext getByName(final String name) {
      for (ExecutionContext result : VALUES_ARRAY) {
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Execution Context</b></em>' literal with the specified integer value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the integer value.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static ExecutionContext get(final int value) {
      switch (value) {
         case EXECUTE_VALUE:
            return EXECUTE;
         case UNDO_VALUE:
            return UNDO;
         case REDO_VALUE:
            return REDO;
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
   private ExecutionContext(final int value, final String name, final String literal) {
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

} // ExecutionContext
