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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getOp <em>Op</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getPath <em>Path</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getOperation()
 * @model abstract="true"
 * @generated
 */
public interface Operation extends EObject {
   /**
    * Returns the value of the '<em><b>Op</b></em>' attribute.
    * The literals are from the enumeration {@link org.eclipse.emfcloud.modelserver.jsonpatch.OpKind}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Op</em>' attribute.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.OpKind
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getOperation_Op()
    * @model required="true" changeable="false" volatile="true" derived="true"
    * @generated
    */
   OpKind getOp();

   /**
    * Returns the value of the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Path</em>' attribute.
    * @see #setPath(String)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getOperation_Path()
    * @model dataType="org.eclipse.emfcloud.modelserver.jsonpatch.Path" required="true"
    * @generated
    */
   String getPath();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getPath <em>Path</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Path</em>' attribute.
    * @see #getPath()
    * @generated
    */
   void setPath(String value);

} // Operation
