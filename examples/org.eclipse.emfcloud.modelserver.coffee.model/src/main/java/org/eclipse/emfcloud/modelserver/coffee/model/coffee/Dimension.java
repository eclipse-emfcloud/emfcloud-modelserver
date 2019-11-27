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
package org.eclipse.emfcloud.modelserver.coffee.model.coffee;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dimension</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getWidth <em>Width</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getHeight <em>Height</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getLength <em>Length</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getDimension()
 * @model
 * @generated
 */
public interface Dimension extends EObject {
   /**
    * Returns the value of the '<em><b>Width</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Width</em>' attribute.
    * @see #setWidth(int)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getDimension_Width()
    * @model
    * @generated
    */
   int getWidth();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getWidth
    * <em>Width</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Width</em>' attribute.
    * @see #getWidth()
    * @generated
    */
   void setWidth(int value);

   /**
    * Returns the value of the '<em><b>Height</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Height</em>' attribute.
    * @see #setHeight(int)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getDimension_Height()
    * @model
    * @generated
    */
   int getHeight();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getHeight
    * <em>Height</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Height</em>' attribute.
    * @see #getHeight()
    * @generated
    */
   void setHeight(int value);

   /**
    * Returns the value of the '<em><b>Length</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Length</em>' attribute.
    * @see #setLength(int)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getDimension_Length()
    * @model
    * @generated
    */
   int getLength();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension#getLength
    * <em>Length</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Length</em>' attribute.
    * @see #getLength()
    * @generated
    */
   void setLength(int value);

} // Dimension
