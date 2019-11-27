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
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dimension</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.DimensionImpl#getWidth <em>Width</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.DimensionImpl#getHeight <em>Height</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.impl.DimensionImpl#getLength <em>Length</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DimensionImpl extends MinimalEObjectImpl.Container implements Dimension {
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
    * The default value of the '{@link #getLength() <em>Length</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getLength()
    * @generated
    * @ordered
    */
   protected static final int LENGTH_EDEFAULT = 0;

   /**
    * The cached value of the '{@link #getLength() <em>Length</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getLength()
    * @generated
    * @ordered
    */
   protected int length = LENGTH_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected DimensionImpl() {
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
      return CoffeePackage.Literals.DIMENSION;
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
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.DIMENSION__WIDTH, oldWidth, width));
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
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.DIMENSION__HEIGHT, oldHeight, height));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public int getLength() { return length; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setLength(final int newLength) {
      int oldLength = length;
      length = newLength;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CoffeePackage.DIMENSION__LENGTH, oldLength, length));
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
         case CoffeePackage.DIMENSION__WIDTH:
            return getWidth();
         case CoffeePackage.DIMENSION__HEIGHT:
            return getHeight();
         case CoffeePackage.DIMENSION__LENGTH:
            return getLength();
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
         case CoffeePackage.DIMENSION__WIDTH:
            setWidth((Integer) newValue);
            return;
         case CoffeePackage.DIMENSION__HEIGHT:
            setHeight((Integer) newValue);
            return;
         case CoffeePackage.DIMENSION__LENGTH:
            setLength((Integer) newValue);
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
         case CoffeePackage.DIMENSION__WIDTH:
            setWidth(WIDTH_EDEFAULT);
            return;
         case CoffeePackage.DIMENSION__HEIGHT:
            setHeight(HEIGHT_EDEFAULT);
            return;
         case CoffeePackage.DIMENSION__LENGTH:
            setLength(LENGTH_EDEFAULT);
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
         case CoffeePackage.DIMENSION__WIDTH:
            return width != WIDTH_EDEFAULT;
         case CoffeePackage.DIMENSION__HEIGHT:
            return height != HEIGHT_EDEFAULT;
         case CoffeePackage.DIMENSION__LENGTH:
            return length != LENGTH_EDEFAULT;
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
      result.append(", length: "); //$NON-NLS-1$
      result.append(length);
      result.append(')');
      return result.toString();
   }

} // DimensionImpl
