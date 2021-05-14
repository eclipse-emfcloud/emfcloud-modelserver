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
package org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage
 * @generated
 */
public class ConstraintTestSwitch<T> extends Switch<T> {
   /**
    * The cached model package
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static ConstraintTestPackage modelPackage;

   /**
    * Creates an instance of the switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public ConstraintTestSwitch() {
      if (modelPackage == null) {
         modelPackage = ConstraintTestPackage.eINSTANCE;
      }
   }

   /**
    * Checks whether this is a switch for the given package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param ePackage the package in question.
    * @return whether this is a switch for the given package.
    * @generated
    */
   @Override
   protected boolean isSwitchFor(final EPackage ePackage) {
      return ePackage == modelPackage;
   }

   /**
    * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the first non-null result returned by a <code>caseXXX</code> call.
    * @generated
    */
   @Override
   protected T doSwitch(final int classifierID, final EObject theEObject) {
      switch (classifierID) {
         case ConstraintTestPackage.NO_CONSTRAINTS_CLASS: {
            NoConstraintsClass noConstraintsClass = (NoConstraintsClass) theEObject;
            T result = caseNoConstraintsClass(noConstraintsClass);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case ConstraintTestPackage.SUPER_CLASS_WITH_CONSTRAINT: {
            SuperClassWithConstraint superClassWithConstraint = (SuperClassWithConstraint) theEObject;
            T result = caseSuperClassWithConstraint(superClassWithConstraint);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case ConstraintTestPackage.SUB_CLASS: {
            SubClass subClass = (SubClass) theEObject;
            T result = caseSubClass(subClass);
            if (result == null) {
               result = caseSuperClassWithConstraint(subClass);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case ConstraintTestPackage.SUB_SUB_CLASS: {
            SubSubClass subSubClass = (SubSubClass) theEObject;
            T result = caseSubSubClass(subSubClass);
            if (result == null) {
               result = caseSubClass(subSubClass);
            }
            if (result == null) {
               result = caseSuperClassWithConstraint(subSubClass);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case ConstraintTestPackage.SUB_CLASS_WITH_CONSTRAINT: {
            SubClassWithConstraint subClassWithConstraint = (SubClassWithConstraint) theEObject;
            T result = caseSubClassWithConstraint(subClassWithConstraint);
            if (result == null) {
               result = caseSuperClassWithConstraint(subClassWithConstraint);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         default:
            return defaultCase(theEObject);
      }
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>No Constraints Class</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>No Constraints Class</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseNoConstraintsClass(final NoConstraintsClass object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Super Class With Constraint</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Super Class With Constraint</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseSuperClassWithConstraint(final SuperClassWithConstraint object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Sub Class</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Sub Class</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseSubClass(final SubClass object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Sub Sub Class</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Sub Sub Class</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseSubSubClass(final SubSubClass object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Sub Class With Constraint</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Sub Class With Constraint</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseSubClassWithConstraint(final SubClassWithConstraint object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch, but this is the last case anyway.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject)
    * @generated
    */
   @Override
   public T defaultCase(final EObject object) {
      return null;
   }

} // ConstraintTestSwitch
