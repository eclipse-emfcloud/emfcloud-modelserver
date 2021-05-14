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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestFactory;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.util.ConstraintTestValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ConstraintTestPackageImpl extends EPackageImpl implements ConstraintTestPackage {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass noConstraintsClassEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass superClassWithConstraintEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass subClassEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass subSubClassEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass subClassWithConstraintEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EDataType stringTypeEDataType = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EDataType stringType2EDataType = null;

   /**
    * Creates an instance of the model <b>Package</b>, registered with
    * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
    * package URI value.
    * <p>
    * Note: the correct way to create the package is via the static
    * factory method {@link #init init()}, which also performs
    * initialization of the package, or returns the registered package,
    * if one already exists.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emf.ecore.EPackage.Registry
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage#eNS_URI
    * @see #init()
    * @generated
    */
   private ConstraintTestPackageImpl() {
      super(eNS_URI, ConstraintTestFactory.eINSTANCE);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static boolean isInited = false;

   /**
    * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
    *
    * <p>
    * This method is used to initialize {@link ConstraintTestPackage#eINSTANCE} when that field is accessed.
    * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #eNS_URI
    * @see #createPackageContents()
    * @see #initializePackageContents()
    * @generated
    */
   public static ConstraintTestPackage init() {
      if (isInited) {
         return (ConstraintTestPackage) EPackage.Registry.INSTANCE.getEPackage(ConstraintTestPackage.eNS_URI);
      }

      // Obtain or create and register package
      Object registeredConstraintTestPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
      ConstraintTestPackageImpl theConstraintTestPackage = registeredConstraintTestPackage instanceof ConstraintTestPackageImpl
         ? (ConstraintTestPackageImpl) registeredConstraintTestPackage
         : new ConstraintTestPackageImpl();

      isInited = true;

      // Create package meta-data objects
      theConstraintTestPackage.createPackageContents();

      // Initialize created meta-data
      theConstraintTestPackage.initializePackageContents();

      // Register package validator
      EValidator.Registry.INSTANCE.put(theConstraintTestPackage,
         (org.eclipse.emf.ecore.EValidator.Descriptor) () -> ConstraintTestValidator.INSTANCE);

      // Mark meta-data to indicate it can't be changed
      theConstraintTestPackage.freeze();

      // Update the registry and return the package
      EPackage.Registry.INSTANCE.put(ConstraintTestPackage.eNS_URI, theConstraintTestPackage);
      return theConstraintTestPackage;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getNoConstraintsClass() { return noConstraintsClassEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getSuperClassWithConstraint() { return superClassWithConstraintEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getSuperClassWithConstraint_Name() {
      return (EAttribute) superClassWithConstraintEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getSubClass() { return subClassEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getSubClass_Type() { return (EAttribute) subClassEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getSubSubClass() { return subSubClassEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getSubClassWithConstraint() { return subClassWithConstraintEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getSubClassWithConstraint_Id() {
      return (EAttribute) subClassWithConstraintEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EDataType getStringType() { return stringTypeEDataType; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EDataType getStringType2() { return stringType2EDataType; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public ConstraintTestFactory getConstraintTestFactory() { return (ConstraintTestFactory) getEFactoryInstance(); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private boolean isCreated = false;

   /**
    * Creates the meta-model objects for the package. This method is
    * guarded to have no affect on any invocation but its first.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public void createPackageContents() {
      if (isCreated) {
         return;
      }
      isCreated = true;

      // Create classes and their features
      noConstraintsClassEClass = createEClass(NO_CONSTRAINTS_CLASS);

      superClassWithConstraintEClass = createEClass(SUPER_CLASS_WITH_CONSTRAINT);
      createEAttribute(superClassWithConstraintEClass, SUPER_CLASS_WITH_CONSTRAINT__NAME);

      subClassEClass = createEClass(SUB_CLASS);
      createEAttribute(subClassEClass, SUB_CLASS__TYPE);

      subSubClassEClass = createEClass(SUB_SUB_CLASS);

      subClassWithConstraintEClass = createEClass(SUB_CLASS_WITH_CONSTRAINT);
      createEAttribute(subClassWithConstraintEClass, SUB_CLASS_WITH_CONSTRAINT__ID);

      // Create data types
      stringTypeEDataType = createEDataType(STRING_TYPE);
      stringType2EDataType = createEDataType(STRING_TYPE2);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private boolean isInitialized = false;

   /**
    * Complete the initialization of the package and its meta-model. This
    * method is guarded to have no affect on any invocation but its first.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public void initializePackageContents() {
      if (isInitialized) {
         return;
      }
      isInitialized = true;

      // Initialize package
      setName(eNAME);
      setNsPrefix(eNS_PREFIX);
      setNsURI(eNS_URI);

      // Create type parameters

      // Set bounds for type parameters

      // Add supertypes to classes
      subClassEClass.getESuperTypes().add(this.getSuperClassWithConstraint());
      subSubClassEClass.getESuperTypes().add(this.getSubClass());
      subClassWithConstraintEClass.getESuperTypes().add(this.getSuperClassWithConstraint());

      // Initialize classes, features, and operations; add parameters
      initEClass(noConstraintsClassEClass, NoConstraintsClass.class, "NoConstraintsClass", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(superClassWithConstraintEClass, SuperClassWithConstraint.class, "SuperClassWithConstraint",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getSuperClassWithConstraint_Name(), this.getStringType(), "name", null, 0, 1,
         SuperClassWithConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(subClassEClass, SubClass.class, "SubClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getSubClass_Type(), ecorePackage.getEString(), "type", null, 0, 1, SubClass.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(subSubClassEClass, SubSubClass.class, "SubSubClass", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(subClassWithConstraintEClass, SubClassWithConstraint.class, "SubClassWithConstraint", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getSubClassWithConstraint_Id(), this.getStringType2(), "id", null, 0, 1,
         SubClassWithConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      // Initialize data types
      initEDataType(stringTypeEDataType, String.class, "StringType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
      initEDataType(stringType2EDataType, String.class, "StringType2", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

      // Create resource
      createResource(eNS_URI);

      // Create annotations
      // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
      createExtendedMetaDataAnnotations();
   }

   /**
    * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected void createExtendedMetaDataAnnotations() {
      String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
      addAnnotation(stringTypeEDataType,
         source,
         new String[] {
            "minLength", "5"
         });
      addAnnotation(stringType2EDataType,
         source,
         new String[] {
            "maxLength", "5"
         });
   }

} // ConstraintTestPackageImpl
