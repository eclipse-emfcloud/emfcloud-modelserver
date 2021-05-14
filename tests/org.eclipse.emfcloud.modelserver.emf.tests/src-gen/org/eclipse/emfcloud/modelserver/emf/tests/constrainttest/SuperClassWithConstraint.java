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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Super Class With Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint#getName
 * <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSuperClassWithConstraint()
 * @model
 * @generated
 */
public interface SuperClassWithConstraint extends EObject {
   /**
    * Returns the value of the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Name</em>' attribute.
    * @see #setName(String)
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#getSuperClassWithConstraint_Name()
    * @model dataType="org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.StringType"
    * @generated
    */
   String getName();

   /**
    * Sets the value of the
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint#getName <em>Name</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Name</em>' attribute.
    * @see #getName()
    * @generated
    */
   void setName(String value);

} // SuperClassWithConstraint
