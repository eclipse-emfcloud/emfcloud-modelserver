/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>String Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.StringValue#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getStringValue()
 * @model
 * @generated
 */
public interface StringValue extends Value {
   /**
    * Returns the value of the '<em><b>Value</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Value</em>' attribute.
    * @see #setValue(String)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getStringValue_Value()
    * @model required="true"
    * @generated
    */
   String getValue();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.StringValue#getValue <em>Value</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Value</em>' attribute.
    * @see #getValue()
    * @generated
    */
   void setValue(String value);

} // StringValue
