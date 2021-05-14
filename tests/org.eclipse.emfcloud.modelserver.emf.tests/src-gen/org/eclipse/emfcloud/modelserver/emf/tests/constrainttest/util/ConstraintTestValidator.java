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

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.NoConstraintsClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubClassWithConstraint;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SubSubClass;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.SuperClassWithConstraint;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage
 * @generated
 */
public class ConstraintTestValidator extends EObjectValidator {
   /**
    * The cached model package
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static final ConstraintTestValidator INSTANCE = new ConstraintTestValidator();

   /**
    * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic
    * {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emf.common.util.Diagnostic#getSource()
    * @see org.eclipse.emf.common.util.Diagnostic#getCode()
    * @generated
    */
   public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emfcloud.modelserver.emf.tests.constrainttest";

   /**
    * A constant with a fixed name that can be used as the base value for additional hand written constants.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

   /**
    * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived
    * class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

   /**
    * Creates an instance of the switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public ConstraintTestValidator() {
      super();
   }

   /**
    * Returns the package of this validator switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   protected EPackage getEPackage() { return ConstraintTestPackage.eINSTANCE; }

   /**
    * Calls <code>validateXXX</code> for the corresponding classifier of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   protected boolean validate(final int classifierID, final Object value, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      switch (classifierID) {
         case ConstraintTestPackage.NO_CONSTRAINTS_CLASS:
            return validateNoConstraintsClass((NoConstraintsClass) value, diagnostics, context);
         case ConstraintTestPackage.SUPER_CLASS_WITH_CONSTRAINT:
            return validateSuperClassWithConstraint((SuperClassWithConstraint) value, diagnostics, context);
         case ConstraintTestPackage.SUB_CLASS:
            return validateSubClass((SubClass) value, diagnostics, context);
         case ConstraintTestPackage.SUB_SUB_CLASS:
            return validateSubSubClass((SubSubClass) value, diagnostics, context);
         case ConstraintTestPackage.SUB_CLASS_WITH_CONSTRAINT:
            return validateSubClassWithConstraint((SubClassWithConstraint) value, diagnostics, context);
         case ConstraintTestPackage.STRING_TYPE:
            return validateStringType((String) value, diagnostics, context);
         case ConstraintTestPackage.STRING_TYPE2:
            return validateStringType2((String) value, diagnostics, context);
         default:
            return true;
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateNoConstraintsClass(final NoConstraintsClass noConstraintsClass,
      final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      return validate_EveryDefaultConstraint(noConstraintsClass, diagnostics, context);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateSuperClassWithConstraint(final SuperClassWithConstraint superClassWithConstraint,
      final DiagnosticChain diagnostics, final Map<Object, Object> context) {
      return validate_EveryDefaultConstraint(superClassWithConstraint, diagnostics, context);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateSubClass(final SubClass subClass, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      return validate_EveryDefaultConstraint(subClass, diagnostics, context);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateSubSubClass(final SubSubClass subSubClass, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      return validate_EveryDefaultConstraint(subSubClass, diagnostics, context);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateSubClassWithConstraint(final SubClassWithConstraint subClassWithConstraint,
      final DiagnosticChain diagnostics, final Map<Object, Object> context) {
      return validate_EveryDefaultConstraint(subClassWithConstraint, diagnostics, context);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateStringType(final String stringType, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      boolean result = validateStringType_MinLength(stringType, diagnostics, context);
      return result;
   }

   /**
    * Validates the MinLength constraint of '<em>String Type</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateStringType_MinLength(final String stringType, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      int length = stringType.length();
      boolean result = length >= 5;
      if (!result && diagnostics != null) {
         reportMinLengthViolation(ConstraintTestPackage.Literals.STRING_TYPE, stringType, length, 5, diagnostics,
            context);
      }
      return result;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateStringType2(final String stringType2, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      boolean result = validateStringType2_MaxLength(stringType2, diagnostics, context);
      return result;
   }

   /**
    * Validates the MaxLength constraint of '<em>String Type2</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public boolean validateStringType2_MaxLength(final String stringType2, final DiagnosticChain diagnostics,
      final Map<Object, Object> context) {
      int length = stringType2.length();
      boolean result = length <= 5;
      if (!result && diagnostics != null) {
         reportMaxLengthViolation(ConstraintTestPackage.Literals.STRING_TYPE2, stringType2, length, 5, diagnostics,
            context);
      }
      return result;
   }

   /**
    * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public ResourceLocator getResourceLocator() {
      // TODO
      // Specialize this to return a resource locator for messages specific to this validator.
      // Ensure that you remove @generated or mark it @generated NOT
      return super.getResourceLocator();
   }

} // ConstraintTestValidator
