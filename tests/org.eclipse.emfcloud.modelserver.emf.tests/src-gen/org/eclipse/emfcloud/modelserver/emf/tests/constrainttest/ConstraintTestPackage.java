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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestFactory
 * @model kind="package"
 * @generated
 */
public interface ConstraintTestPackage extends EPackage {
   /**
    * The package name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNAME = "constrainttest";

   /**
    * The package namespace URI.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_URI = "http://www.eclipse.org/emfcloud/modelserver/test/constrainttest";

   /**
    * The package namespace name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_PREFIX = "constrainttest";

   /**
    * The singleton instance of the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   ConstraintTestPackage eINSTANCE = org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl
      .init();

   /**
    * The meta object id for the
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.NoConstraintsClassImpl <em>No Constraints
    * Class</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.NoConstraintsClassImpl
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getNoConstraintsClass()
    * @generated
    */
   int NO_CONSTRAINTS_CLASS = 0;

   /**
    * The number of structural features of the '<em>No Constraints Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NO_CONSTRAINTS_CLASS_FEATURE_COUNT = 0;

   /**
    * The number of operations of the '<em>No Constraints Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NO_CONSTRAINTS_CLASS_OPERATION_COUNT = 0;

   /**
    * The meta object id for the
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SuperClassWithConstraintImpl <em>Super
    * Class With Constraint</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SuperClassWithConstraintImpl
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSuperClassWithConstraint()
    * @generated
    */
   int SUPER_CLASS_WITH_CONSTRAINT = 1;

   /**
    * The feature id for the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUPER_CLASS_WITH_CONSTRAINT__NAME = 0;

   /**
    * The number of structural features of the '<em>Super Class With Constraint</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUPER_CLASS_WITH_CONSTRAINT_FEATURE_COUNT = 1;

   /**
    * The number of operations of the '<em>Super Class With Constraint</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUPER_CLASS_WITH_CONSTRAINT_OPERATION_COUNT = 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassImpl
    * <em>Sub Class</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassImpl
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubClass()
    * @generated
    */
   int SUB_CLASS = 2;

   /**
    * The feature id for the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS__NAME = SUPER_CLASS_WITH_CONSTRAINT__NAME;

   /**
    * The feature id for the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS__TYPE = SUPER_CLASS_WITH_CONSTRAINT_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Sub Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_FEATURE_COUNT = SUPER_CLASS_WITH_CONSTRAINT_FEATURE_COUNT + 1;

   /**
    * The number of operations of the '<em>Sub Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_OPERATION_COUNT = SUPER_CLASS_WITH_CONSTRAINT_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubSubClassImpl
    * <em>Sub Sub Class</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubSubClassImpl
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubSubClass()
    * @generated
    */
   int SUB_SUB_CLASS = 3;

   /**
    * The feature id for the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_SUB_CLASS__NAME = SUB_CLASS__NAME;

   /**
    * The feature id for the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_SUB_CLASS__TYPE = SUB_CLASS__TYPE;

   /**
    * The number of structural features of the '<em>Sub Sub Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_SUB_CLASS_FEATURE_COUNT = SUB_CLASS_FEATURE_COUNT + 0;

   /**
    * The number of operations of the '<em>Sub Sub Class</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_SUB_CLASS_OPERATION_COUNT = SUB_CLASS_OPERATION_COUNT + 0;

   /**
    * The meta object id for the
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassWithConstraintImpl <em>Sub Class
    * With Constraint</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassWithConstraintImpl
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubClassWithConstraint()
    * @generated
    */
   int SUB_CLASS_WITH_CONSTRAINT = 4;

   /**
    * The feature id for the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_WITH_CONSTRAINT__NAME = SUPER_CLASS_WITH_CONSTRAINT__NAME;

   /**
    * The feature id for the '<em><b>Id</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_WITH_CONSTRAINT__ID = SUPER_CLASS_WITH_CONSTRAINT_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Sub Class With Constraint</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_WITH_CONSTRAINT_FEATURE_COUNT = SUPER_CLASS_WITH_CONSTRAINT_FEATURE_COUNT + 1;

   /**
    * The number of operations of the '<em>Sub Class With Constraint</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int SUB_CLASS_WITH_CONSTRAINT_OPERATION_COUNT = SUPER_CLASS_WITH_CONSTRAINT_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '<em>String Type</em>' data type.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see java.lang.String
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getStringType()
    * @generated
    */
   int STRING_TYPE = 5;

   /**
    * The meta object id for the '<em>String Type2</em>' data type.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see java.lang.String
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getStringType2()
    * @generated
    */
   int STRING_TYPE2 = 6;

   /**
    * Returns the meta object for class
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass <em>No Constraints
    * Class</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>No Constraints Class</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass
    * @generated
    */
   EClass getNoConstraintsClass();

   /**
    * Returns the meta object for class
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint <em>Super Class With
    * Constraint</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Super Class With Constraint</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint
    * @generated
    */
   EClass getSuperClassWithConstraint();

   /**
    * Returns the meta object for the attribute
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint#getName
    * <em>Name</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Name</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint#getName()
    * @see #getSuperClassWithConstraint()
    * @generated
    */
   EAttribute getSuperClassWithConstraint_Name();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass
    * <em>Sub Class</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Sub Class</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass
    * @generated
    */
   EClass getSubClass();

   /**
    * Returns the meta object for the attribute
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass#getType <em>Type</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Type</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass#getType()
    * @see #getSubClass()
    * @generated
    */
   EAttribute getSubClass_Type();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass
    * <em>Sub Sub Class</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Sub Sub Class</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass
    * @generated
    */
   EClass getSubSubClass();

   /**
    * Returns the meta object for class
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint <em>Sub Class With
    * Constraint</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Sub Class With Constraint</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint
    * @generated
    */
   EClass getSubClassWithConstraint();

   /**
    * Returns the meta object for the attribute
    * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint#getId <em>Id</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Id</em>'.
    * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint#getId()
    * @see #getSubClassWithConstraint()
    * @generated
    */
   EAttribute getSubClassWithConstraint_Id();

   /**
    * Returns the meta object for data type '{@link java.lang.String <em>String Type</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for data type '<em>String Type</em>'.
    * @see java.lang.String
    * @model instanceClass="java.lang.String"
    *        extendedMetaData="minLength='5'"
    * @generated
    */
   EDataType getStringType();

   /**
    * Returns the meta object for data type '{@link java.lang.String <em>String Type2</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for data type '<em>String Type2</em>'.
    * @see java.lang.String
    * @model instanceClass="java.lang.String"
    *        extendedMetaData="maxLength='5'"
    * @generated
    */
   EDataType getStringType2();

   /**
    * Returns the factory that creates the instances of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the factory that creates the instances of the model.
    * @generated
    */
   ConstraintTestFactory getConstraintTestFactory();

   /**
    * <!-- begin-user-doc -->
    * Defines literals for the meta objects that represent
    * <ul>
    * <li>each class,</li>
    * <li>each feature of each class,</li>
    * <li>each operation of each class,</li>
    * <li>each enum,</li>
    * <li>and each data type</li>
    * </ul>
    * <!-- end-user-doc -->
    *
    * @generated
    */
   interface Literals {
      /**
       * The meta object literal for the
       * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.NoConstraintsClassImpl <em>No
       * Constraints Class</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.NoConstraintsClassImpl
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getNoConstraintsClass()
       * @generated
       */
      EClass NO_CONSTRAINTS_CLASS = eINSTANCE.getNoConstraintsClass();

      /**
       * The meta object literal for the
       * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SuperClassWithConstraintImpl <em>Super
       * Class With Constraint</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SuperClassWithConstraintImpl
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSuperClassWithConstraint()
       * @generated
       */
      EClass SUPER_CLASS_WITH_CONSTRAINT = eINSTANCE.getSuperClassWithConstraint();

      /**
       * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute SUPER_CLASS_WITH_CONSTRAINT__NAME = eINSTANCE.getSuperClassWithConstraint_Name();

      /**
       * The meta object literal for the
       * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassImpl <em>Sub Class</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassImpl
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubClass()
       * @generated
       */
      EClass SUB_CLASS = eINSTANCE.getSubClass();

      /**
       * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute SUB_CLASS__TYPE = eINSTANCE.getSubClass_Type();

      /**
       * The meta object literal for the
       * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubSubClassImpl <em>Sub Sub Class</em>}'
       * class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubSubClassImpl
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubSubClass()
       * @generated
       */
      EClass SUB_SUB_CLASS = eINSTANCE.getSubSubClass();

      /**
       * The meta object literal for the
       * '{@link org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassWithConstraintImpl <em>Sub Class
       * With Constraint</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.SubClassWithConstraintImpl
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getSubClassWithConstraint()
       * @generated
       */
      EClass SUB_CLASS_WITH_CONSTRAINT = eINSTANCE.getSubClassWithConstraint();

      /**
       * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute SUB_CLASS_WITH_CONSTRAINT__ID = eINSTANCE.getSubClassWithConstraint_Id();

      /**
       * The meta object literal for the '<em>String Type</em>' data type.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see java.lang.String
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getStringType()
       * @generated
       */
      EDataType STRING_TYPE = eINSTANCE.getStringType();

      /**
       * The meta object literal for the '<em>String Type2</em>' data type.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see java.lang.String
       * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.impl.ConstraintTestPackageImpl#getStringType2()
       * @generated
       */
      EDataType STRING_TYPE2 = eINSTANCE.getStringType2();

   }

} // ConstraintTestPackage
