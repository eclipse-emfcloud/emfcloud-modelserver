/**
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Op Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getOpKind()
 * @model
 * @generated
 */
public enum OpKind implements Enumerator {
   /**
    * The '<em><b>None</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NONE_VALUE
    * @generated
    * @ordered
    */
   NONE(0, "none", "none"),

   /**
    * The '<em><b>Add</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #ADD_VALUE
    * @generated
    * @ordered
    */
   ADD(1, "add", "add"),

   /**
    * The '<em><b>Remove</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REMOVE_VALUE
    * @generated
    * @ordered
    */
   REMOVE(2, "remove", "remove"),

   /**
    * The '<em><b>Replace</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REPLACE_VALUE
    * @generated
    * @ordered
    */
   REPLACE(3, "replace", "replace"),

   /**
    * The '<em><b>Move</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #MOVE_VALUE
    * @generated
    * @ordered
    */
   MOVE(4, "move", "move"),

   /**
    * The '<em><b>Copy</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #COPY_VALUE
    * @generated
    * @ordered
    */
   COPY(5, "copy", "copy"),

   /**
    * The '<em><b>Test</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #TEST_VALUE
    * @generated
    * @ordered
    */
   TEST(6, "test", "test");

   /**
    * The '<em><b>None</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #NONE
    * @model name="none"
    * @generated
    * @ordered
    */
   public static final int NONE_VALUE = 0;

   /**
    * The '<em><b>Add</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #ADD
    * @model name="add"
    * @generated
    * @ordered
    */
   public static final int ADD_VALUE = 1;

   /**
    * The '<em><b>Remove</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REMOVE
    * @model name="remove"
    * @generated
    * @ordered
    */
   public static final int REMOVE_VALUE = 2;

   /**
    * The '<em><b>Replace</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #REPLACE
    * @model name="replace"
    * @generated
    * @ordered
    */
   public static final int REPLACE_VALUE = 3;

   /**
    * The '<em><b>Move</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #MOVE
    * @model name="move"
    * @generated
    * @ordered
    */
   public static final int MOVE_VALUE = 4;

   /**
    * The '<em><b>Copy</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #COPY
    * @model name="copy"
    * @generated
    * @ordered
    */
   public static final int COPY_VALUE = 5;

   /**
    * The '<em><b>Test</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #TEST
    * @model name="test"
    * @generated
    * @ordered
    */
   public static final int TEST_VALUE = 6;

   /**
    * An array of all the '<em><b>Op Kind</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static final OpKind[] VALUES_ARRAY = new OpKind[] {
      NONE,
      ADD,
      REMOVE,
      REPLACE,
      MOVE,
      COPY,
      TEST,
   };

   /**
    * A public read-only list of all the '<em><b>Op Kind</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static final List<OpKind> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Op Kind</b></em>' literal with the specified literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param literal the literal.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static OpKind get(final String literal) {
      for (OpKind result : VALUES_ARRAY) {
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Op Kind</b></em>' literal with the specified name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param name the name.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static OpKind getByName(final String name) {
      for (OpKind result : VALUES_ARRAY) {
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Op Kind</b></em>' literal with the specified integer value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the integer value.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static OpKind get(final int value) {
      switch (value) {
         case NONE_VALUE:
            return NONE;
         case ADD_VALUE:
            return ADD;
         case REMOVE_VALUE:
            return REMOVE;
         case REPLACE_VALUE:
            return REPLACE;
         case MOVE_VALUE:
            return MOVE;
         case COPY_VALUE:
            return COPY;
         case TEST_VALUE:
            return TEST;
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
   OpKind(final int value, final String name, final String literal) {
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

} // OpKind
