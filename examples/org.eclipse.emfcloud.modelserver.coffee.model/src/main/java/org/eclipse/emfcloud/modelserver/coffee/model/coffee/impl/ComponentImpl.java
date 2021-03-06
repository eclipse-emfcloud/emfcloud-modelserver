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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.ComponentImpl#getChildren
 * <em>Children</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.ComponentImpl#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ComponentImpl extends MinimalEObjectImpl.Container implements Component {
   /**
    * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getChildren()
    * @generated
    * @ordered
    */
   protected EList<Component> children;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected ComponentImpl() {
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
      return CoffeePackage.Literals.COMPONENT;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<Component> getChildren() {
      if (children == null) {
         children = new EObjectContainmentWithInverseEList<>(Component.class, this,
            CoffeePackage.COMPONENT__CHILDREN, CoffeePackage.COMPONENT__PARENT);
      }
      return children;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Component getParent() {
      if (eContainerFeatureID() != CoffeePackage.COMPONENT__PARENT) {
         return null;
      }
      return (Component) eInternalContainer();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public NotificationChain basicSetParent(final Component newParent, NotificationChain msgs) {
      msgs = eBasicSetContainer((InternalEObject) newParent, CoffeePackage.COMPONENT__PARENT, msgs);
      return msgs;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setParent(final Component newParent) {
      if (newParent != eInternalContainer()
         || (eContainerFeatureID() != CoffeePackage.COMPONENT__PARENT && newParent != null)) {
         if (EcoreUtil.isAncestor(this, newParent)) {
            throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
         }
         NotificationChain msgs = null;
         if (eInternalContainer() != null) {
            msgs = eBasicRemoveFromContainer(msgs);
         }
         if (newParent != null) {
            msgs = ((InternalEObject) newParent).eInverseAdd(this, CoffeePackage.COMPONENT__CHILDREN, Component.class,
               msgs);
         }
         msgs = basicSetParent(newParent, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.COMPONENT__PARENT, newParent, newParent));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @SuppressWarnings("unchecked")
   @Override
   public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
      switch (featureID) {
         case CoffeePackage.COMPONENT__CHILDREN:
            return ((InternalEList<InternalEObject>) (InternalEList<?>) getChildren()).basicAdd(otherEnd, msgs);
         case CoffeePackage.COMPONENT__PARENT:
            if (eInternalContainer() != null) {
               msgs = eBasicRemoveFromContainer(msgs);
            }
            return basicSetParent((Component) otherEnd, msgs);
      }
      return super.eInverseAdd(otherEnd, featureID, msgs);
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
         case CoffeePackage.COMPONENT__CHILDREN:
            return ((InternalEList<?>) getChildren()).basicRemove(otherEnd, msgs);
         case CoffeePackage.COMPONENT__PARENT:
            return basicSetParent(null, msgs);
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
   public NotificationChain eBasicRemoveFromContainerFeature(final NotificationChain msgs) {
      switch (eContainerFeatureID()) {
         case CoffeePackage.COMPONENT__PARENT:
            return eInternalContainer().eInverseRemove(this, CoffeePackage.COMPONENT__CHILDREN, Component.class, msgs);
      }
      return super.eBasicRemoveFromContainerFeature(msgs);
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
         case CoffeePackage.COMPONENT__CHILDREN:
            return getChildren();
         case CoffeePackage.COMPONENT__PARENT:
            return getParent();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @SuppressWarnings("unchecked")
   @Override
   public void eSet(final int featureID, final Object newValue) {
      switch (featureID) {
         case CoffeePackage.COMPONENT__CHILDREN:
            getChildren().clear();
            getChildren().addAll((Collection<? extends Component>) newValue);
            return;
         case CoffeePackage.COMPONENT__PARENT:
            setParent((Component) newValue);
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
         case CoffeePackage.COMPONENT__CHILDREN:
            getChildren().clear();
            return;
         case CoffeePackage.COMPONENT__PARENT:
            setParent((Component) null);
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
         case CoffeePackage.COMPONENT__CHILDREN:
            return children != null && !children.isEmpty();
         case CoffeePackage.COMPONENT__PARENT:
            return getParent() != null;
      }
      return super.eIsSet(featureID);
   }

} // ComponentImpl
