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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getComponent()
 * @model abstract="true"
 * @generated
 */
public interface Component extends EObject {
   /**
    * Returns the value of the '<em><b>Children</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component}.
    * It is bidirectional and its opposite is
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getParent <em>Parent</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Children</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getComponent_Children()
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getParent
    * @model opposite="parent" containment="true"
    * @generated
    */
   EList<Component> getChildren();

   /**
    * Returns the value of the '<em><b>Parent</b></em>' container reference.
    * It is bidirectional and its opposite is
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getChildren <em>Children</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Parent</em>' container reference.
    * @see #setParent(Component)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getComponent_Parent()
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getChildren
    * @model opposite="children" transient="false"
    * @generated
    */
   Component getParent();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component#getParent
    * <em>Parent</em>}' container reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Parent</em>' container reference.
    * @see #getParent()
    * @generated
    */
   void setParent(Component value);

} // Component
