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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
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
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;
import org.eclipse.emfcloud.modelserver.jsonpatch.Replace;
import org.eclipse.emfcloud.modelserver.jsonpatch.StringValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Test;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class JsonPatchFactoryImpl extends EFactoryImpl implements JsonPatchFactory {
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static JsonPatchFactory init() {
      try {
         JsonPatchFactory theJsonPatchFactory = (JsonPatchFactory) EPackage.Registry.INSTANCE
            .getEFactory(JsonPatchPackage.eNS_URI);
         if (theJsonPatchFactory != null) {
            return theJsonPatchFactory;
         }
      } catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new JsonPatchFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public JsonPatchFactoryImpl() {
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
         case JsonPatchPackage.JSON_PATCH:
            return createJsonPatch();
         case JsonPatchPackage.ADD:
            return createAdd();
         case JsonPatchPackage.REMOVE:
            return createRemove();
         case JsonPatchPackage.REPLACE:
            return createReplace();
         case JsonPatchPackage.MOVE:
            return createMove();
         case JsonPatchPackage.COPY:
            return createCopy();
         case JsonPatchPackage.TEST:
            return createTest();
         case JsonPatchPackage.BOOLEAN_VALUE:
            return createBooleanValue();
         case JsonPatchPackage.STRING_VALUE:
            return createStringValue();
         case JsonPatchPackage.NUMBER_VALUE:
            return createNumberValue();
         case JsonPatchPackage.OBJECT_VALUE:
            return createObjectValue();
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
         case JsonPatchPackage.OP_KIND:
            return createOpKindFromString(eDataType, initialValue);
         case JsonPatchPackage.PATH:
            return createPathFromString(eDataType, initialValue);
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
         case JsonPatchPackage.OP_KIND:
            return convertOpKindToString(eDataType, instanceValue);
         case JsonPatchPackage.PATH:
            return convertPathToString(eDataType, instanceValue);
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
   public JsonPatch createJsonPatch() {
      JsonPatchImpl jsonPatch = new JsonPatchImpl();
      return jsonPatch;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Add createAdd() {
      AddImpl add = new AddImpl();
      return add;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Remove createRemove() {
      RemoveImpl remove = new RemoveImpl();
      return remove;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Replace createReplace() {
      ReplaceImpl replace = new ReplaceImpl();
      return replace;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Move createMove() {
      MoveImpl move = new MoveImpl();
      return move;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Copy createCopy() {
      CopyImpl copy = new CopyImpl();
      return copy;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Test createTest() {
      TestImpl test = new TestImpl();
      return test;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public BooleanValue createBooleanValue() {
      BooleanValueImpl booleanValue = new BooleanValueImpl();
      return booleanValue;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public StringValue createStringValue() {
      StringValueImpl stringValue = new StringValueImpl();
      return stringValue;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public NumberValue createNumberValue() {
      NumberValueImpl numberValue = new NumberValueImpl();
      return numberValue;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public ObjectValue createObjectValue() {
      ObjectValueImpl objectValue = new ObjectValueImpl();
      return objectValue;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public OpKind createOpKindFromString(final EDataType eDataType, final String initialValue) {
      OpKind result = OpKind.get(initialValue);
      if (result == null) {
         throw new IllegalArgumentException(
            "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      }
      return result;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String convertOpKindToString(final EDataType eDataType, final Object instanceValue) {
      return instanceValue == null ? null : instanceValue.toString();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String createPathFromString(final EDataType eDataType, final String initialValue) {
      return (String) super.createFromString(eDataType, initialValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public String convertPathToString(final EDataType eDataType, final Object instanceValue) {
      return super.convertToString(eDataType, instanceValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public JsonPatchPackage getJsonPatchPackage() { return (JsonPatchPackage) getEPackage(); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @deprecated
    * @generated
    */
   @Deprecated
   public static JsonPatchPackage getPackage() { return JsonPatchPackage.eINSTANCE; }

   @Override
   public BooleanValue createValue(final boolean value) {
      BooleanValue result = createBooleanValue();
      result.setValue(value);
      return result;
   }

   @Override
   public StringValue createValue(final String value) {
      StringValue result = createStringValue();
      result.setValue(value);
      return result;
   }

   @Override
   public NumberValue createValue(final double value) {
      NumberValue result = createNumberValue();
      result.setValue(value);
      return result;
   }

   @Override
   public ObjectValue createValue(final EObject value) {
      ObjectValue result = createObjectValue();
      result.setValue(value);
      return result;
   }

} // JsonPatchFactoryImpl
