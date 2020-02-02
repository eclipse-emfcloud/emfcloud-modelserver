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
package org.eclipse.emfcloud.modelserver.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandKind()
 * @model
 * @generated
 */
public enum CommandKind implements Enumerator {
   /**
    * The '<em><b>Compound</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #COMPOUND_VALUE
    * @generated
    * @ordered
    */
   COMPOUND(0, "compound", "compound"),

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
    * The '<em><b>Set</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #SET_VALUE
    * @generated
    * @ordered
    */
   SET(3, "set", "set"),

   /**
    * The '<em><b>Replace</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #REPLACE_VALUE
    * @generated
    * @ordered
    */
   REPLACE(4, "replace", "replace"),

   /**
    * The '<em><b>Move</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #MOVE_VALUE
    * @generated
    * @ordered
    */
   MOVE(5, "move", "move");

   /**
    * The '<em><b>Compound</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #COMPOUND
    * @model name="compound"
    * @generated
    * @ordered
    */
   public static final int COMPOUND_VALUE = 0;

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
    * The '<em><b>Set</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #SET
    * @model name="set"
    * @generated
    * @ordered
    */
   public static final int SET_VALUE = 3;

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
   public static final int REPLACE_VALUE = 4;

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
   public static final int MOVE_VALUE = 5;

   /**
    * An array of all the '<em><b>Kind</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   private static final CommandKind[] VALUES_ARRAY = new CommandKind[] {
      COMPOUND,
      ADD,
      REMOVE,
      SET,
      REPLACE,
      MOVE,
   };

   /**
    * A public read-only list of all the '<em><b>Kind</b></em>' enumerators.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static final List<CommandKind> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Kind</b></em>' literal with the specified literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param literal the literal.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static CommandKind get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         CommandKind result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Kind</b></em>' literal with the specified name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param name the name.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static CommandKind getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         CommandKind result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Kind</b></em>' literal with the specified integer value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param value the integer value.
    * @return the matching enumerator or <code>null</code>.
    * @generated
    */
   public static CommandKind get(int value) {
      switch (value) {
         case COMPOUND_VALUE:
            return COMPOUND;
         case ADD_VALUE:
            return ADD;
         case REMOVE_VALUE:
            return REMOVE;
         case SET_VALUE:
            return SET;
         case REPLACE_VALUE:
            return REPLACE;
         case MOVE_VALUE:
            return MOVE;
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
   private CommandKind(int value, String name, String literal) {
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

} // CommandKind
