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
 * A representation of the model object '<em><b>Flow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow#getSource <em>Source</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow#getTarget <em>Target</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getFlow()
 * @model
 * @generated
 */
public interface Flow extends EObject {
   /**
    * Returns the value of the '<em><b>Source</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Source</em>' reference.
    * @see #setSource(Node)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getFlow_Source()
    * @model required="true"
    * @generated
    */
   Node getSource();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow#getSource
    * <em>Source</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Source</em>' reference.
    * @see #getSource()
    * @generated
    */
   void setSource(Node value);

   /**
    * Returns the value of the '<em><b>Target</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Target</em>' reference.
    * @see #setTarget(Node)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getFlow_Target()
    * @model required="true"
    * @generated
    */
   Node getTarget();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow#getTarget
    * <em>Target</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Target</em>' reference.
    * @see #getTarget()
    * @generated
    */
   void setTarget(Node value);

} // Flow
