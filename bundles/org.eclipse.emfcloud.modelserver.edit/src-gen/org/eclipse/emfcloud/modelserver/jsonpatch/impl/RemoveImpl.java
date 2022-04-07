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
package org.eclipse.emfcloud.modelserver.jsonpatch.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage;
import org.eclipse.emfcloud.modelserver.jsonpatch.OpKind;
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Remove</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class RemoveImpl extends OperationImpl implements Remove {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected RemoveImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return JsonPatchPackage.Literals.REMOVE;
   }

   @Override
   public OpKind getOp() { return OpKind.REMOVE; }

} // RemoveImpl
