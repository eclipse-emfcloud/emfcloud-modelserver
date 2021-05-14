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
package org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestFactory;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ConstraintTestFactoryImpl extends EFactoryImpl implements ConstraintTestFactory {
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static ConstraintTestFactory init() {
      try {
         ConstraintTestFactory theConstraintTestFactory = (ConstraintTestFactory) EPackage.Registry.INSTANCE
            .getEFactory(ConstraintTestPackage.eNS_URI);
         if (theConstraintTestFactory != null) {
            return theConstraintTestFactory;
         }
      } catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new ConstraintTestFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public ConstraintTestFactoryImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EObject create(final EClass eClass) {
      switch (eClass.getClassifierID()) {
         case ConstraintTestPackage.NO_CONSTRAINTS_CLASS:
            return createNoConstraintsClass();
         case ConstraintTestPackage.SUPER_CLASS_WITH_CONSTRAINT:
            return createSuperClassWithConstraint();
         case ConstraintTestPackage.SUB_CLASS:
            return createSubClass();
         case ConstraintTestPackage.SUB_SUB_CLASS:
            return createSubSubClass();
         case ConstraintTestPackage.SUB_CLASS_WITH_CONSTRAINT:
            return createSubClassWithConstraint();
         default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Object createFromString(final EDataType eDataType, final String initialValue) {
      switch (eDataType.getClassifierID()) {
         case ConstraintTestPackage.STRING_TYPE:
            return createStringTypeFromString(eDataType, initialValue);
         case ConstraintTestPackage.STRING_TYPE2:
            return createStringType2FromString(eDataType, initialValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String convertToString(final EDataType eDataType, final Object instanceValue) {
      switch (eDataType.getClassifierID()) {
         case ConstraintTestPackage.STRING_TYPE:
            return convertStringTypeToString(eDataType, instanceValue);
         case ConstraintTestPackage.STRING_TYPE2:
            return convertStringType2ToString(eDataType, instanceValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public NoConstraintsClass createNoConstraintsClass() {
      NoConstraintsClassImpl noConstraintsClass = new NoConstraintsClassImpl();
      return noConstraintsClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public SuperClassWithConstraint createSuperClassWithConstraint() {
      SuperClassWithConstraintImpl superClassWithConstraint = new SuperClassWithConstraintImpl();
      return superClassWithConstraint;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public SubClass createSubClass() {
      SubClassImpl subClass = new SubClassImpl();
      return subClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public SubSubClass createSubSubClass() {
      SubSubClassImpl subSubClass = new SubSubClassImpl();
      return subSubClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public SubClassWithConstraint createSubClassWithConstraint() {
      SubClassWithConstraintImpl subClassWithConstraint = new SubClassWithConstraintImpl();
      return subClassWithConstraint;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String createStringTypeFromString(final EDataType eDataType, final String initialValue) {
      return (String) super.createFromString(eDataType, initialValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String convertStringTypeToString(final EDataType eDataType, final Object instanceValue) {
      return super.convertToString(eDataType, instanceValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String createStringType2FromString(final EDataType eDataType, final String initialValue) {
      return (String) super.createFromString(eDataType, initialValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String convertStringType2ToString(final EDataType eDataType, final Object instanceValue) {
      return super.convertToString(eDataType, instanceValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public ConstraintTestPackage getConstraintTestPackage() { return (ConstraintTestPackage) getEPackage(); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @deprecated
    * @generated
    */
   @Deprecated
   public static ConstraintTestPackage getPackage() { return ConstraintTestPackage.eINSTANCE; }

} // ConstraintTestFactoryImpl
