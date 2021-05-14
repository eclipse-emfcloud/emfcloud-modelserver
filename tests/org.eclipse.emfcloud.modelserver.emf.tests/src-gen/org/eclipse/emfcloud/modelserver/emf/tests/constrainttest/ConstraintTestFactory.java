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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage
 * @generated
 */
public interface ConstraintTestFactory extends EFactory {
   /**
    * The singleton instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   ConstraintTestFactory eINSTANCE = org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestFactoryImpl
      .init();

   /**
    * Returns a new object of class '<em>No Constraints Class</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>No Constraints Class</em>'.
    * @generated
    */
   NoConstraintsClass createNoConstraintsClass();

   /**
    * Returns a new object of class '<em>Super Class With Constraint</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Super Class With Constraint</em>'.
    * @generated
    */
   SuperClassWithConstraint createSuperClassWithConstraint();

   /**
    * Returns a new object of class '<em>Sub Class</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Sub Class</em>'.
    * @generated
    */
   SubClass createSubClass();

   /**
    * Returns a new object of class '<em>Sub Sub Class</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Sub Sub Class</em>'.
    * @generated
    */
   SubSubClass createSubSubClass();

   /**
    * Returns a new object of class '<em>Sub Class With Constraint</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Sub Class With Constraint</em>'.
    * @generated
    */
   SubClassWithConstraint createSubClassWithConstraint();

   /**
    * Returns the package supported by this factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the package supported by this factory.
    * @generated
    */
   ConstraintTestPackage getConstraintTestPackage();

} // ConstraintTestFactory
