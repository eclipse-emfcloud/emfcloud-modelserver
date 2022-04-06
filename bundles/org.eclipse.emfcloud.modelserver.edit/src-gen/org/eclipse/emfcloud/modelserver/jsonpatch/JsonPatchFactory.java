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

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage
 * @generated
 */
public interface JsonPatchFactory extends EFactory {
   /**
    * The singleton instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   JsonPatchFactory eINSTANCE = org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchFactoryImpl.init();

   /**
    * Returns a new object of class '<em>Json Patch</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Json Patch</em>'.
    * @generated
    */
   JsonPatch createJsonPatch();

   /**
    * Returns a new object of class '<em>Add</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Add</em>'.
    * @generated
    */
   Add createAdd();

   /**
    * Returns a new object of class '<em>Remove</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Remove</em>'.
    * @generated
    */
   Remove createRemove();

   /**
    * Returns a new object of class '<em>Replace</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Replace</em>'.
    * @generated
    */
   Replace createReplace();

   /**
    * Returns a new object of class '<em>Move</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Move</em>'.
    * @generated
    */
   Move createMove();

   /**
    * Returns a new object of class '<em>Copy</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Copy</em>'.
    * @generated
    */
   Copy createCopy();

   /**
    * Returns a new object of class '<em>Test</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Test</em>'.
    * @generated
    */
   Test createTest();

   /**
    * Returns a new object of class '<em>Boolean Value</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Boolean Value</em>'.
    * @generated
    */
   BooleanValue createBooleanValue();

   /**
    * Returns a new object of class '<em>String Value</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>String Value</em>'.
    * @generated
    */
   StringValue createStringValue();

   /**
    * Returns a new object of class '<em>Number Value</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Number Value</em>'.
    * @generated
    */
   NumberValue createNumberValue();

   /**
    * Returns a new object of class '<em>Object Value</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Object Value</em>'.
    * @generated
    */
   ObjectValue createObjectValue();

   /**
    * Returns the package supported by this factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the package supported by this factory.
    * @generated
    */
   JsonPatchPackage getJsonPatchPackage();

   BooleanValue createValue(boolean value);

   StringValue createValue(String value);

   NumberValue createValue(double value);

   default NumberValue createValue(final Number value) {
      return createValue(value.doubleValue());
   }

   ObjectValue createValue(EObject value);

} // JsonPatchFactory
