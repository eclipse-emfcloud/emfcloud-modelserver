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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.AutomaticTask;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Automatic Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.AutomaticTaskImpl#getComponent
 * <em>Component</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AutomaticTaskImpl extends TaskImpl implements AutomaticTask {
   /**
    * The cached value of the '{@link #getComponent() <em>Component</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getComponent()
    * @generated
    * @ordered
    */
   protected Component component;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected AutomaticTaskImpl() {
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
      return CoffeePackage.Literals.AUTOMATIC_TASK;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Component getComponent() {
      if (component != null && component.eIsProxy()) {
         InternalEObject oldComponent = (InternalEObject) component;
         component = (Component) eResolveProxy(oldComponent);
         if (component != oldComponent) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE, CoffeePackage.AUTOMATIC_TASK__COMPONENT,
                  oldComponent, component));
            }
         }
      }
      return component;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public Component basicGetComponent() {
      return component;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setComponent(final Component newComponent) {
      Component oldComponent = component;
      component = newComponent;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.AUTOMATIC_TASK__COMPONENT, oldComponent,
            component));
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
         case CoffeePackage.AUTOMATIC_TASK__COMPONENT:
            if (resolve) {
               return getComponent();
            }
            return basicGetComponent();
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
         case CoffeePackage.AUTOMATIC_TASK__COMPONENT:
            setComponent((Component) newValue);
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
         case CoffeePackage.AUTOMATIC_TASK__COMPONENT:
            setComponent((Component) null);
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
         case CoffeePackage.AUTOMATIC_TASK__COMPONENT:
            return component != null;
      }
      return super.eIsSet(featureID);
   }

} // AutomaticTaskImpl
