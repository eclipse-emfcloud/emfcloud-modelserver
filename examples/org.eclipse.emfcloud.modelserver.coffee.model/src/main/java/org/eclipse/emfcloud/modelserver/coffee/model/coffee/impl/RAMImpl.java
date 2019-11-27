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
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.RAM;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.RamType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>RAM</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.RAMImpl#getClockSpeed <em>Clock Speed</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.RAMImpl#getSize <em>Size</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.RAMImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RAMImpl extends MinimalEObjectImpl.Container implements RAM {
   /**
    * The default value of the '{@link #getClockSpeed() <em>Clock Speed</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getClockSpeed()
    * @generated
    * @ordered
    */
   protected static final int CLOCK_SPEED_EDEFAULT = 0;

   /**
    * The cached value of the '{@link #getClockSpeed() <em>Clock Speed</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getClockSpeed()
    * @generated
    * @ordered
    */
   protected int clockSpeed = CLOCK_SPEED_EDEFAULT;

   /**
    * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getSize()
    * @generated
    * @ordered
    */
   protected static final int SIZE_EDEFAULT = 0;

   /**
    * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getSize()
    * @generated
    * @ordered
    */
   protected int size = SIZE_EDEFAULT;

   /**
    * The default value of the '{@link #getType() <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getType()
    * @generated
    * @ordered
    */
   protected static final RamType TYPE_EDEFAULT = RamType.SODIMM;

   /**
    * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getType()
    * @generated
    * @ordered
    */
   protected RamType type = TYPE_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected RAMImpl() {
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
      return CoffeePackage.Literals.RAM;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getClockSpeed() { return clockSpeed; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setClockSpeed(final int newClockSpeed) {
      int oldClockSpeed = clockSpeed;
      clockSpeed = newClockSpeed;
      if (eNotificationRequired()) {
         eNotify(
            new ENotificationImpl(this, Notification.SET, CoffeePackage.RAM__CLOCK_SPEED, oldClockSpeed, clockSpeed));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getSize() { return size; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setSize(final int newSize) {
      int oldSize = size;
      size = newSize;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.RAM__SIZE, oldSize, size));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public RamType getType() { return type; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setType(final RamType newType) {
      RamType oldType = type;
      type = newType == null ? TYPE_EDEFAULT : newType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.RAM__TYPE, oldType, type));
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
         case CoffeePackage.RAM__CLOCK_SPEED:
            return getClockSpeed();
         case CoffeePackage.RAM__SIZE:
            return getSize();
         case CoffeePackage.RAM__TYPE:
            return getType();
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
         case CoffeePackage.RAM__CLOCK_SPEED:
            setClockSpeed((Integer) newValue);
            return;
         case CoffeePackage.RAM__SIZE:
            setSize((Integer) newValue);
            return;
         case CoffeePackage.RAM__TYPE:
            setType((RamType) newValue);
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
         case CoffeePackage.RAM__CLOCK_SPEED:
            setClockSpeed(CLOCK_SPEED_EDEFAULT);
            return;
         case CoffeePackage.RAM__SIZE:
            setSize(SIZE_EDEFAULT);
            return;
         case CoffeePackage.RAM__TYPE:
            setType(TYPE_EDEFAULT);
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
         case CoffeePackage.RAM__CLOCK_SPEED:
            return clockSpeed != CLOCK_SPEED_EDEFAULT;
         case CoffeePackage.RAM__SIZE:
            return size != SIZE_EDEFAULT;
         case CoffeePackage.RAM__TYPE:
            return type != TYPE_EDEFAULT;
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
      result.append(" (clockSpeed: "); //$NON-NLS-1$
      result.append(clockSpeed);
      result.append(", size: "); //$NON-NLS-1$
      result.append(size);
      result.append(", type: "); //$NON-NLS-1$
      result.append(type);
      result.append(')');
      return result.toString();
   }

} // RAMImpl
