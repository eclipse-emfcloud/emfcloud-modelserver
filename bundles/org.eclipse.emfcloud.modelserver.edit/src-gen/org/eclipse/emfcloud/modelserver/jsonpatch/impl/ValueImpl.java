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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class ValueImpl extends MinimalEObjectImpl.Container implements Value {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected ValueImpl() {
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
      return JsonPatchPackage.Literals.VALUE;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated NOT
    */
   @Override
   public String stringValue() {
      // Implemented by subclasses
      throw new UnsupportedOperationException();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Object eInvoke(final int operationID, final EList<?> arguments) throws InvocationTargetException {
      switch (operationID) {
         case JsonPatchPackage.VALUE___STRING_VALUE:
            return stringValue();
      }
      return super.eInvoke(operationID, arguments);
   }

} // ValueImpl
