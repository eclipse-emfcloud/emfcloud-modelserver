/**
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.emf.tests.constrainttest;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSubClass()
 * @model
 * @generated
 */
public interface SubClass extends SuperClassWithConstraint {
   /**
    * Returns the value of the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Type</em>' attribute.
    * @see #setType(String)
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSubClass_Type()
    * @model
    * @generated
    */
   String getType();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass#getType
    * <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Type</em>' attribute.
    * @see #getType()
    * @generated
    */
   void setType(String value);

} // SubClass
