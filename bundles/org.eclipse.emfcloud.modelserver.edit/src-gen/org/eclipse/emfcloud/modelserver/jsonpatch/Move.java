/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Move</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.Move#getFrom <em>From</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getMove()
 * @model
 * @generated
 */
public interface Move extends Operation {
   /**
    * Returns the value of the '<em><b>From</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>From</em>' attribute.
    * @see #setFrom(String)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getMove_From()
    * @model dataType="org.eclipse.emfcloud.modelserver.jsonpatch.Path" required="true"
    * @generated
    */
   String getFrom();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Move#getFrom <em>From</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>From</em>' attribute.
    * @see #getFrom()
    * @generated
    */
   void setFrom(String value);

} // Move
