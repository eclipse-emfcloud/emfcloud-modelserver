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
package org.eclipse.emfcloud.modelserver.command.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class CCommandPackageImpl extends EPackageImpl implements CCommandPackage {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass commandEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass compoundCommandEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass commandExecutionResultEClass = null;

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
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#eNS_URI
    * @see #init()
    * @generated
    */
   private CCommandPackageImpl() {
      super(eNS_URI, CCommandFactory.eINSTANCE);
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
    * This method is used to initialize {@link CCommandPackage#eINSTANCE} when that field is accessed.
    * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #eNS_URI
    * @see #createPackageContents()
    * @see #initializePackageContents()
    * @generated
    */
   public static CCommandPackage init() {
      if (isInited) {
         return (CCommandPackage) EPackage.Registry.INSTANCE.getEPackage(CCommandPackage.eNS_URI);
      }

      // Obtain or create and register package
      Object registeredCommandPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
      CCommandPackageImpl theCommandPackage = registeredCommandPackage instanceof CCommandPackageImpl
         ? (CCommandPackageImpl) registeredCommandPackage
         : new CCommandPackageImpl();

      isInited = true;

      // Initialize simple dependencies
      EcorePackage.eINSTANCE.eClass();

      // Create package meta-data objects
      theCommandPackage.createPackageContents();

      // Initialize created meta-data
      theCommandPackage.initializePackageContents();

      // Mark meta-data to indicate it can't be changed
      theCommandPackage.freeze();

      // Update the registry and return the package
      EPackage.Registry.INSTANCE.put(CCommandPackage.eNS_URI, theCommandPackage);
      return theCommandPackage;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getCommand() { return commandEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCommand_Type() { return (EAttribute) commandEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommand_Owner() { return (EReference) commandEClass.getEStructuralFeatures().get(1); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCommand_Feature() { return (EAttribute) commandEClass.getEStructuralFeatures().get(2); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCommand_Indices() { return (EAttribute) commandEClass.getEStructuralFeatures().get(3); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCommand_DataValues() { return (EAttribute) commandEClass.getEStructuralFeatures().get(4); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommand_ObjectValues() { return (EReference) commandEClass.getEStructuralFeatures().get(5); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommand_ObjectsToAdd() { return (EReference) commandEClass.getEStructuralFeatures().get(6); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommand_Properties() { return (EReference) commandEClass.getEStructuralFeatures().get(7); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getCompoundCommand() { return compoundCommandEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCompoundCommand_Commands() {
      return (EReference) compoundCommandEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getCommandExecutionResult() { return commandExecutionResultEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCommandExecutionResult_Type() {
      return (EAttribute) commandExecutionResultEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommandExecutionResult_Source() {
      return (EReference) commandExecutionResultEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommandExecutionResult_AffectedObjects() {
      return (EReference) commandExecutionResultEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommandExecutionResult_ChangeDescription() {
      return (EReference) commandExecutionResultEClass.getEStructuralFeatures().get(3);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getCommandExecutionResult_Details() {
      return (EReference) commandExecutionResultEClass.getEStructuralFeatures().get(4);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public CCommandFactory getCommandFactory() { return (CCommandFactory) getEFactoryInstance(); }

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
      commandEClass = createEClass(COMMAND);
      createEAttribute(commandEClass, COMMAND__TYPE);
      createEReference(commandEClass, COMMAND__OWNER);
      createEAttribute(commandEClass, COMMAND__FEATURE);
      createEAttribute(commandEClass, COMMAND__INDICES);
      createEAttribute(commandEClass, COMMAND__DATA_VALUES);
      createEReference(commandEClass, COMMAND__OBJECT_VALUES);
      createEReference(commandEClass, COMMAND__OBJECTS_TO_ADD);
      createEReference(commandEClass, COMMAND__PROPERTIES);

      compoundCommandEClass = createEClass(COMPOUND_COMMAND);
      createEReference(compoundCommandEClass, COMPOUND_COMMAND__COMMANDS);

      commandExecutionResultEClass = createEClass(COMMAND_EXECUTION_RESULT);
      createEAttribute(commandExecutionResultEClass, COMMAND_EXECUTION_RESULT__TYPE);
      createEReference(commandExecutionResultEClass, COMMAND_EXECUTION_RESULT__SOURCE);
      createEReference(commandExecutionResultEClass, COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS);
      createEReference(commandExecutionResultEClass, COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION);
      createEReference(commandExecutionResultEClass, COMMAND_EXECUTION_RESULT__DETAILS);
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

      // Obtain other dependent packages
      EcorePackage theEcorePackage = (EcorePackage) EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

      // Create type parameters

      // Set bounds for type parameters

      // Add supertypes to classes
      compoundCommandEClass.getESuperTypes().add(this.getCommand());

      // Initialize classes, features, and operations; add parameters
      initEClass(commandEClass, CCommand.class, "Command", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getCommand_Type(), ecorePackage.getEString(), "type", null, 1, 1, CCommand.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommand_Owner(), theEcorePackage.getEObject(), null, "owner", null, 0, 1, CCommand.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEAttribute(getCommand_Feature(), ecorePackage.getEString(), "feature", null, 0, 1, CCommand.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getCommand_Indices(), ecorePackage.getEInt(), "indices", null, 0, -1, CCommand.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getCommand_DataValues(), ecorePackage.getEString(), "dataValues", null, 0, -1, CCommand.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommand_ObjectValues(), theEcorePackage.getEObject(), null, "objectValues", null, 0, -1,
         CCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
         IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommand_ObjectsToAdd(), theEcorePackage.getEObject(), null, "objectsToAdd", null, 0, -1,
         CCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
         IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommand_Properties(), theEcorePackage.getEStringToStringMapEntry(), null, "properties", null, 0,
         -1, CCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(compoundCommandEClass, CCompoundCommand.class, "CompoundCommand", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getCompoundCommand_Commands(), this.getCommand(), null, "commands", null, 0, -1,
         CCompoundCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(commandExecutionResultEClass, CCommandExecutionResult.class, "CommandExecutionResult", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getCommandExecutionResult_Type(), theEcorePackage.getEString(), "type", null, 1, 1,
         CCommandExecutionResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getCommandExecutionResult_Source(), this.getCommand(), null, "source", null, 1, 1,
         CCommandExecutionResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommandExecutionResult_AffectedObjects(), theEcorePackage.getEObject(), null, "affectedObjects",
         null, 0, -1, CCommandExecutionResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
         IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommandExecutionResult_ChangeDescription(), theEcorePackage.getEObject(), null,
         "changeDescription", null, 0, 1, CCommandExecutionResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
         IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getCommandExecutionResult_Details(), theEcorePackage.getEStringToStringMapEntry(), null, "details",
         null, 0, -1, CCommandExecutionResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
         !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      // Create resource
      createResource(eNS_URI);
   }

} // CCommandPackageImpl
