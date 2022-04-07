/**
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Copy;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchFactory;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage;
import org.eclipse.emfcloud.modelserver.jsonpatch.Move;
import org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.OpKind;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;
import org.eclipse.emfcloud.modelserver.jsonpatch.Replace;
import org.eclipse.emfcloud.modelserver.jsonpatch.StringValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Test;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class JsonPatchPackageImpl extends EPackageImpl implements JsonPatchPackage {
   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass jsonPatchEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass operationEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass addEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass removeEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass replaceEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass moveEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass copyEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass testEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass valueEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass booleanValueEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass stringValueEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass numberValueEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EClass objectValueEClass = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EEnum opKindEEnum = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   private EDataType pathEDataType = null;

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
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage#eNS_URI
    * @see #init()
    * @generated
    */
   private JsonPatchPackageImpl() {
      super(eNS_URI, JsonPatchFactory.eINSTANCE);
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
    * This method is used to initialize {@link JsonPatchPackage#eINSTANCE} when that field is accessed.
    * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #eNS_URI
    * @see #createPackageContents()
    * @see #initializePackageContents()
    * @generated
    */
   public static JsonPatchPackage init() {
      if (isInited) {
         return (JsonPatchPackage) EPackage.Registry.INSTANCE.getEPackage(JsonPatchPackage.eNS_URI);
      }

      // Obtain or create and register package
      Object registeredJsonPatchPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
      JsonPatchPackageImpl theJsonPatchPackage = registeredJsonPatchPackage instanceof JsonPatchPackageImpl
         ? (JsonPatchPackageImpl) registeredJsonPatchPackage
         : new JsonPatchPackageImpl();

      isInited = true;

      // Create package meta-data objects
      theJsonPatchPackage.createPackageContents();

      // Initialize created meta-data
      theJsonPatchPackage.initializePackageContents();

      // Mark meta-data to indicate it can't be changed
      theJsonPatchPackage.freeze();

      // Update the registry and return the package
      EPackage.Registry.INSTANCE.put(JsonPatchPackage.eNS_URI, theJsonPatchPackage);
      return theJsonPatchPackage;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getJsonPatch() { return jsonPatchEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getJsonPatch_Patch() { return (EReference) jsonPatchEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getOperation() { return operationEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getOperation_Op() { return (EAttribute) operationEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getOperation_Path() { return (EAttribute) operationEClass.getEStructuralFeatures().get(1); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EOperation getOperation__GetOp() { return operationEClass.getEOperations().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getAdd() { return addEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getAdd_Value() { return (EReference) addEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getRemove() { return removeEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getReplace() { return replaceEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getReplace_Value() { return (EReference) replaceEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getMove() { return moveEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getMove_From() { return (EAttribute) moveEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getCopy() { return copyEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getCopy_From() { return (EAttribute) copyEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getTest() { return testEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getTest_Value() { return (EReference) testEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getValue() { return valueEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EOperation getValue__StringValue() { return valueEClass.getEOperations().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getBooleanValue() { return booleanValueEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getBooleanValue_Value() { return (EAttribute) booleanValueEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getStringValue() { return stringValueEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getStringValue_Value() { return (EAttribute) stringValueEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getNumberValue() { return numberValueEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EAttribute getNumberValue_Value() { return (EAttribute) numberValueEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EClass getObjectValue() { return objectValueEClass; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EReference getObjectValue_Value() { return (EReference) objectValueEClass.getEStructuralFeatures().get(0); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EEnum getOpKind() { return opKindEEnum; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EDataType getPath() { return pathEDataType; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public JsonPatchFactory getJsonPatchFactory() { return (JsonPatchFactory) getEFactoryInstance(); }

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
      jsonPatchEClass = createEClass(JSON_PATCH);
      createEReference(jsonPatchEClass, JSON_PATCH__PATCH);

      operationEClass = createEClass(OPERATION);
      createEAttribute(operationEClass, OPERATION__OP);
      createEAttribute(operationEClass, OPERATION__PATH);
      createEOperation(operationEClass, OPERATION___GET_OP);

      addEClass = createEClass(ADD);
      createEReference(addEClass, ADD__VALUE);

      removeEClass = createEClass(REMOVE);

      replaceEClass = createEClass(REPLACE);
      createEReference(replaceEClass, REPLACE__VALUE);

      moveEClass = createEClass(MOVE);
      createEAttribute(moveEClass, MOVE__FROM);

      copyEClass = createEClass(COPY);
      createEAttribute(copyEClass, COPY__FROM);

      testEClass = createEClass(TEST);
      createEReference(testEClass, TEST__VALUE);

      valueEClass = createEClass(VALUE);
      createEOperation(valueEClass, VALUE___STRING_VALUE);

      booleanValueEClass = createEClass(BOOLEAN_VALUE);
      createEAttribute(booleanValueEClass, BOOLEAN_VALUE__VALUE);

      stringValueEClass = createEClass(STRING_VALUE);
      createEAttribute(stringValueEClass, STRING_VALUE__VALUE);

      numberValueEClass = createEClass(NUMBER_VALUE);
      createEAttribute(numberValueEClass, NUMBER_VALUE__VALUE);

      objectValueEClass = createEClass(OBJECT_VALUE);
      createEReference(objectValueEClass, OBJECT_VALUE__VALUE);

      // Create enums
      opKindEEnum = createEEnum(OP_KIND);

      // Create data types
      pathEDataType = createEDataType(PATH);
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
      addEClass.getESuperTypes().add(this.getOperation());
      removeEClass.getESuperTypes().add(this.getOperation());
      replaceEClass.getESuperTypes().add(this.getOperation());
      moveEClass.getESuperTypes().add(this.getOperation());
      copyEClass.getESuperTypes().add(this.getOperation());
      testEClass.getESuperTypes().add(this.getOperation());
      booleanValueEClass.getESuperTypes().add(this.getValue());
      stringValueEClass.getESuperTypes().add(this.getValue());
      numberValueEClass.getESuperTypes().add(this.getValue());
      objectValueEClass.getESuperTypes().add(this.getValue());

      // Initialize classes, features, and operations; add parameters
      initEClass(jsonPatchEClass, JsonPatch.class, "JsonPatch", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getJsonPatch_Patch(), this.getOperation(), null, "patch", null, 1, -1, JsonPatch.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(operationEClass, Operation.class, "Operation", IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOperation_Op(), this.getOpKind(), "op", null, 1, 1, Operation.class, !IS_TRANSIENT, IS_VOLATILE,
         !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
      initEAttribute(getOperation_Path(), this.getPath(), "path", null, 1, 1, Operation.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEOperation(getOperation__GetOp(), this.getOpKind(), "getOp", 1, 1, IS_UNIQUE, IS_ORDERED);

      initEClass(addEClass, Add.class, "Add", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getAdd_Value(), this.getValue(), null, "value", null, 1, 1, Add.class, !IS_TRANSIENT, !IS_VOLATILE,
         IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(removeEClass, Remove.class, "Remove", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(replaceEClass, Replace.class, "Replace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getReplace_Value(), this.getValue(), null, "value", null, 1, 1, Replace.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
         IS_ORDERED);

      initEClass(moveEClass, Move.class, "Move", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getMove_From(), this.getPath(), "from", null, 1, 1, Move.class, !IS_TRANSIENT, !IS_VOLATILE,
         IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(copyEClass, Copy.class, "Copy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getCopy_From(), this.getPath(), "from", null, 1, 1, Copy.class, !IS_TRANSIENT, !IS_VOLATILE,
         IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(testEClass, Test.class, "Test", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getTest_Value(), this.getValue(), null, "value", null, 1, 1, Test.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
         IS_ORDERED);

      initEClass(valueEClass, Value.class, "Value", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEOperation(getValue__StringValue(), ecorePackage.getEString(), "stringValue", 1, 1, IS_UNIQUE, IS_ORDERED);

      initEClass(booleanValueEClass, BooleanValue.class, "BooleanValue", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getBooleanValue_Value(), ecorePackage.getEBoolean(), "value", null, 1, 1, BooleanValue.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(stringValueEClass, StringValue.class, "StringValue", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getStringValue_Value(), ecorePackage.getEString(), "value", null, 1, 1, StringValue.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(numberValueEClass, NumberValue.class, "NumberValue", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getNumberValue_Value(), ecorePackage.getEDouble(), "value", null, 1, 1, NumberValue.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(objectValueEClass, ObjectValue.class, "ObjectValue", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getObjectValue_Value(), ecorePackage.getEObject(), null, "value", null, 1, 1, ObjectValue.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      // Initialize enums and add enum literals
      initEEnum(opKindEEnum, OpKind.class, "OpKind");
      addEEnumLiteral(opKindEEnum, OpKind.NONE);
      addEEnumLiteral(opKindEEnum, OpKind.ADD);
      addEEnumLiteral(opKindEEnum, OpKind.REMOVE);
      addEEnumLiteral(opKindEEnum, OpKind.REPLACE);
      addEEnumLiteral(opKindEEnum, OpKind.MOVE);
      addEEnumLiteral(opKindEEnum, OpKind.COPY);
      addEEnumLiteral(opKindEEnum, OpKind.TEST);

      // Initialize data types
      initEDataType(pathEDataType, String.class, "Path", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

      // Create resource
      createResource(eNS_URI);
   }

} // JsonPatchPackageImpl
