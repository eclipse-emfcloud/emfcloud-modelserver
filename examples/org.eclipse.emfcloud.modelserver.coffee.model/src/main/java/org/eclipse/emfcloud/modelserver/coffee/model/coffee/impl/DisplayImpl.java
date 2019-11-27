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
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Display;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Display</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.DisplayImpl#getWidth <em>Width</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.DisplayImpl#getHeight <em>Height</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DisplayImpl extends MinimalEObjectImpl.Container implements Display {
   /**
    * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getWidth()
    * @generated
    * @ordered
    */
   protected static final int WIDTH_EDEFAULT = 0;

   /**
    * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getWidth()
    * @generated
    * @ordered
    */
   protected int width = WIDTH_EDEFAULT;

   /**
    * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getHeight()
    * @generated
    * @ordered
    */
   protected static final int HEIGHT_EDEFAULT = 0;

   /**
    * The cached value of the '{@link #getHeight() <em>Height</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getHeight()
    * @generated
    * @ordered
    */
   protected int height = HEIGHT_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected DisplayImpl() {
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
      return CoffeePackage.Literals.DISPLAY;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getWidth() { return width; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setWidth(final int newWidth) {
      int oldWidth = width;
      width = newWidth;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.DISPLAY__WIDTH, oldWidth, width));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getHeight() { return height; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setHeight(final int newHeight) {
      int oldHeight = height;
      height = newHeight;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.DISPLAY__HEIGHT, oldHeight, height));
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
         case CoffeePackage.DISPLAY__WIDTH:
            return getWidth();
         case CoffeePackage.DISPLAY__HEIGHT:
            return getHeight();
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
         case CoffeePackage.DISPLAY__WIDTH:
            setWidth((Integer) newValue);
            return;
         case CoffeePackage.DISPLAY__HEIGHT:
            setHeight((Integer) newValue);
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
         case CoffeePackage.DISPLAY__WIDTH:
            setWidth(WIDTH_EDEFAULT);
            return;
         case CoffeePackage.DISPLAY__HEIGHT:
            setHeight(HEIGHT_EDEFAULT);
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
         case CoffeePackage.DISPLAY__WIDTH:
            return width != WIDTH_EDEFAULT;
         case CoffeePackage.DISPLAY__HEIGHT:
            return height != HEIGHT_EDEFAULT;
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
      result.append(" (width: "); //$NON-NLS-1$
      result.append(width);
      result.append(", height: "); //$NON-NLS-1$
      result.append(height);
      result.append(')');
      return result.toString();
   }

} // DisplayImpl
