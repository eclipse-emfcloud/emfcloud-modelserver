/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Add</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.Add#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getAdd()
 * @model
 * @generated
 */
public interface Add extends Operation {
   /**
    * Returns the value of the '<em><b>Value</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Value</em>' containment reference.
    * @see #setValue(Value)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getAdd_Value()
    * @model containment="true" required="true"
    * @generated
    */
   Value getValue();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Add#getValue <em>Value</em>}' containment
    * reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Value</em>' containment reference.
    * @see #getValue()
    * @generated
    */
   void setValue(Value value);

} // Add
