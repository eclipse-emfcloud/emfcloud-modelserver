/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Boolean Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue#isValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getBooleanValue()
 * @model
 * @generated
 */
public interface BooleanValue extends Value {
   /**
    * Returns the value of the '<em><b>Value</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Value</em>' attribute.
    * @see #setValue(boolean)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getBooleanValue_Value()
    * @model required="true"
    * @generated
    */
   boolean isValue();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue#isValue <em>Value</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Value</em>' attribute.
    * @see #isValue()
    * @generated
    */
   void setValue(boolean value);

} // BooleanValue
