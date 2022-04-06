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
 * A representation of the model object '<em><b>Object Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getObjectValue()
 * @model
 * @generated
 */
public interface ObjectValue extends Value {
   /**
    * Returns the value of the '<em><b>Value</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Value</em>' reference.
    * @see #setValue(EObject)
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getObjectValue_Value()
    * @model required="true"
    * @generated
    */
   EObject getValue();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue#getValue <em>Value</em>}'
    * reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Value</em>' reference.
    * @see #getValue()
    * @generated
    */
   void setValue(EObject value);

} // ObjectValue
