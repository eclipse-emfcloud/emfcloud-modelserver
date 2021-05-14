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
 * A representation of the model object '<em><b>Sub Class With Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSubClassWithConstraint()
 * @model
 * @generated
 */
public interface SubClassWithConstraint extends SuperClassWithConstraint {
   /**
    * Returns the value of the '<em><b>Id</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Id</em>' attribute.
    * @see #setId(String)
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSubClassWithConstraint_Id()
    * @model dataType="org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.StringType2"
    * @generated
    */
   String getId();

   /**
    * Sets the value of the
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint#getId <em>Id</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Id</em>' attribute.
    * @see #getId()
    * @generated
    */
   void setId(String value);

} // SubClassWithConstraint
