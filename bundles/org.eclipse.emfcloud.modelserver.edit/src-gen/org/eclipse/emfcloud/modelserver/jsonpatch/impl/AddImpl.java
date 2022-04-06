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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage;
import org.eclipse.emfcloud.modelserver.jsonpatch.OpKind;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Add</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.AddImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AddImpl extends OperationImpl implements Add {
   /**
    * The cached value of the '{@link #getValue() <em>Value</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getValue()
    * @generated
    * @ordered
    */
   protected Value value;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected AddImpl() {
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
      return JsonPatchPackage.Literals.ADD;
   }

   @Override
   public OpKind getOp() { return OpKind.ADD; }
   
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Value getValue() { return value; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public NotificationChain basicSetValue(final Value newValue, NotificationChain msgs) {
      Value oldValue = value;
      value = newValue;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonPatchPackage.ADD__VALUE,
            oldValue, newValue);
         if (msgs == null) {
            msgs = notification;
         } else {
            msgs.add(notification);
         }
      }
      return msgs;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setValue(final Value newValue) {
      if (newValue != value) {
         NotificationChain msgs = null;
         if (value != null) {
            msgs = ((InternalEObject) value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonPatchPackage.ADD__VALUE,
               null, msgs);
         }
         if (newValue != null) {
            msgs = ((InternalEObject) newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonPatchPackage.ADD__VALUE,
               null, msgs);
         }
         msgs = basicSetValue(newValue, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, JsonPatchPackage.ADD__VALUE, newValue, newValue));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
      final NotificationChain msgs) {
      switch (featureID) {
         case JsonPatchPackage.ADD__VALUE:
            return basicSetValue(null, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
      switch (featureID) {
         case JsonPatchPackage.ADD__VALUE:
            return getValue();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void eSet(final int featureID, final Object newValue) {
      switch (featureID) {
         case JsonPatchPackage.ADD__VALUE:
            setValue((Value) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void eUnset(final int featureID) {
      switch (featureID) {
         case JsonPatchPackage.ADD__VALUE:
            setValue((Value) null);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public boolean eIsSet(final int featureID) {
      switch (featureID) {
         case JsonPatchPackage.ADD__VALUE:
            return value != null;
      }
      return super.eIsSet(featureID);
   }

} // AddImpl
