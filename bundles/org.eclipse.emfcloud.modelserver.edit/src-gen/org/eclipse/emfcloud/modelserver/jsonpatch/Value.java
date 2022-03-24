/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#getValue()
 * @model abstract="true"
 * @generated
 */
public interface Value extends EObject {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @model required="true"
    * @generated
    */
   String stringValue();

} // Value
