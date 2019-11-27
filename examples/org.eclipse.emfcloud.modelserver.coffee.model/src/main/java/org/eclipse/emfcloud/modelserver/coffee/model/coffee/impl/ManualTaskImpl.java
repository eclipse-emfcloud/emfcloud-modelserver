/**
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Manual Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.ManualTaskImpl#getActor <em>Actor</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ManualTaskImpl extends TaskImpl implements ManualTask {
   /**
    * The default value of the '{@link #getActor() <em>Actor</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getActor()
    * @generated
    * @ordered
    */
   protected static final String ACTOR_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getActor() <em>Actor</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getActor()
    * @generated
    * @ordered
    */
   protected String actor = ACTOR_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected ManualTaskImpl() {
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
      return CoffeePackage.Literals.MANUAL_TASK;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String getActor() { return actor; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setActor(final String newActor) {
      String oldActor = actor;
      actor = newActor;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.MANUAL_TASK__ACTOR, oldActor, actor));
      }
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
         case CoffeePackage.MANUAL_TASK__ACTOR:
            return getActor();
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
         case CoffeePackage.MANUAL_TASK__ACTOR:
            setActor((String) newValue);
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
         case CoffeePackage.MANUAL_TASK__ACTOR:
            setActor(ACTOR_EDEFAULT);
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
         case CoffeePackage.MANUAL_TASK__ACTOR:
            return ACTOR_EDEFAULT == null ? actor != null : !ACTOR_EDEFAULT.equals(actor);
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String toString() {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuilder result = new StringBuilder(super.toString());
      result.append(" (actor: "); //$NON-NLS-1$
      result.append(actor);
      result.append(')');
      return result.toString();
   }

} // ManualTaskImpl
