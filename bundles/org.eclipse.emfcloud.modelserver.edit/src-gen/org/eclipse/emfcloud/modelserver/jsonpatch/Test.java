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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.Test#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getTest()
 * @model
 * @generated
 */
public interface Test extends Operation {
   /**
    * Returns the value of the '<em><b>Value</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Value</em>' containment reference.
    * @see #setValue(Value)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getTest_Value()
    * @model containment="true" required="true"
    * @generated
    */
   Value getValue();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Test#getValue <em>Value</em>}'
    * containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Value</em>' containment reference.
    * @see #getValue()
    * @generated
    */
   void setValue(Value value);

} // Test
