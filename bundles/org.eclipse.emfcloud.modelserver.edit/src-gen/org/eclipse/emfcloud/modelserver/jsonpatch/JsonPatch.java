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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Json Patch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch#getPatch <em>Patch</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getJsonPatch()
 * @model
 * @generated
 */
public interface JsonPatch extends EObject {
   /**
    * Returns the value of the '<em><b>Patch</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Patch</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getJsonPatch_Patch()
    * @model containment="true" required="true"
    * @generated
    */
   EList<Operation> getPatch();

} // JsonPatch
