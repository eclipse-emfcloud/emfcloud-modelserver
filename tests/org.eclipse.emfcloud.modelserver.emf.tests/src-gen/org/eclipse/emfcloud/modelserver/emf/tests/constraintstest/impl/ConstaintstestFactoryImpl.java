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
package org.eclipse.emfcloud.modelserver.emf.tests.constraintstest.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emfcloud.modelserver.emf.tests.constraintstest.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConstaintstestFactoryImpl extends EFactoryImpl implements ConstaintstestFactory {
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static ConstaintstestFactory init() {
      try {
         ConstaintstestFactory theConstaintstestFactory = (ConstaintstestFactory)EPackage.Registry.INSTANCE.getEFactory(ConstaintstestPackage.eNS_URI);
         if (theConstaintstestFactory != null) {
            return theConstaintstestFactory;
         }
      }
      catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new ConstaintstestFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public ConstaintstestFactoryImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public EObject create(EClass eClass) {
      switch (eClass.getClassifierID()) {
         case ConstaintstestPackage.NO_CONSTRAINTS_CLASS: return createNoConstraintsClass();
         case ConstaintstestPackage.SUPER_CLASS_WITH_CONSTRAINT: return createSuperClassWithConstraint();
         case ConstaintstestPackage.SUB_CLASS: return createSubClass();
         case ConstaintstestPackage.SUB_SUB_CLASS: return createSubSubClass();
         case ConstaintstestPackage.SUB_CLASS_WITH_CONSTRAINT: return createSubClassWithConstraint();
         default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object createFromString(EDataType eDataType, String initialValue) {
      switch (eDataType.getClassifierID()) {
         case ConstaintstestPackage.STRING_TYPE:
            return createStringTypeFromString(eDataType, initialValue);
         case ConstaintstestPackage.STRING_TYPE2:
            return createStringType2FromString(eDataType, initialValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String convertToString(EDataType eDataType, Object instanceValue) {
      switch (eDataType.getClassifierID()) {
         case ConstaintstestPackage.STRING_TYPE:
            return convertStringTypeToString(eDataType, instanceValue);
         case ConstaintstestPackage.STRING_TYPE2:
            return convertStringType2ToString(eDataType, instanceValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public NoConstraintsClass createNoConstraintsClass() {
      NoConstraintsClassImpl noConstraintsClass = new NoConstraintsClassImpl();
      return noConstraintsClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public SuperClassWithConstraint createSuperClassWithConstraint() {
      SuperClassWithConstraintImpl superClassWithConstraint = new SuperClassWithConstraintImpl();
      return superClassWithConstraint;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public SubClass createSubClass() {
      SubClassImpl subClass = new SubClassImpl();
      return subClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public SubSubClass createSubSubClass() {
      SubSubClassImpl subSubClass = new SubSubClassImpl();
      return subSubClass;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public SubClassWithConstraint createSubClassWithConstraint() {
      SubClassWithConstraintImpl subClassWithConstraint = new SubClassWithConstraintImpl();
      return subClassWithConstraint;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public String createStringTypeFromString(EDataType eDataType, String initialValue) {
      return (String)super.createFromString(eDataType, initialValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public String convertStringTypeToString(EDataType eDataType, Object instanceValue) {
      return super.convertToString(eDataType, instanceValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public String createStringType2FromString(EDataType eDataType, String initialValue) {
      return (String)super.createFromString(eDataType, initialValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public String convertStringType2ToString(EDataType eDataType, Object instanceValue) {
      return super.convertToString(eDataType, instanceValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public ConstaintstestPackage getConstaintstestPackage() {
      return (ConstaintstestPackage)getEPackage();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @deprecated
    * @generated
    */
   @Deprecated
   public static ConstaintstestPackage getPackage() {
      return ConstaintstestPackage.eINSTANCE;
   }

} //ConstaintstestFactoryImpl
