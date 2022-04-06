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
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Copy;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage;
import org.eclipse.emfcloud.modelserver.jsonpatch.Move;
import org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;
import org.eclipse.emfcloud.modelserver.jsonpatch.Replace;
import org.eclipse.emfcloud.modelserver.jsonpatch.StringValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Test;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage
 * @generated
 */
public class JsonPatchAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static JsonPatchPackage modelPackage;

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public JsonPatchAdapterFactory() {
      if (modelPackage == null) {
         modelPackage = JsonPatchPackage.eINSTANCE;
      }
   }

   /**
    * Returns whether this factory is applicable for the type of the object.
    * <!-- begin-user-doc -->
    * This implementation returns <code>true</code> if the object is either the model's package or is an instance object
    * of the model.
    * <!-- end-user-doc -->
    *
    * @return whether this factory is applicable for the type of the object.
    * @generated
    */
   @Override
   public boolean isFactoryForType(final Object object) {
      if (object == modelPackage) {
         return true;
      }
      if (object instanceof EObject) {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

   /**
    * The switch that delegates to the <code>createXXX</code> methods.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected JsonPatchSwitch<Adapter> modelSwitch = new JsonPatchSwitch<>() {
      @Override
      public Adapter caseJsonPatch(final JsonPatch object) {
         return createJsonPatchAdapter();
      }

      @Override
      public Adapter caseOperation(final Operation object) {
         return createOperationAdapter();
      }

      @Override
      public Adapter caseAdd(final Add object) {
         return createAddAdapter();
      }

      @Override
      public Adapter caseRemove(final Remove object) {
         return createRemoveAdapter();
      }

      @Override
      public Adapter caseReplace(final Replace object) {
         return createReplaceAdapter();
      }

      @Override
      public Adapter caseMove(final Move object) {
         return createMoveAdapter();
      }

      @Override
      public Adapter caseCopy(final Copy object) {
         return createCopyAdapter();
      }

      @Override
      public Adapter caseTest(final Test object) {
         return createTestAdapter();
      }

      @Override
      public Adapter caseValue(final Value object) {
         return createValueAdapter();
      }

      @Override
      public Adapter caseBooleanValue(final BooleanValue object) {
         return createBooleanValueAdapter();
      }

      @Override
      public Adapter caseStringValue(final StringValue object) {
         return createStringValueAdapter();
      }

      @Override
      public Adapter caseNumberValue(final NumberValue object) {
         return createNumberValueAdapter();
      }

      @Override
      public Adapter caseObjectValue(final ObjectValue object) {
         return createObjectValueAdapter();
      }

      @Override
      public Adapter defaultCase(final EObject object) {
         return createEObjectAdapter();
      }
   };

   /**
    * Creates an adapter for the <code>target</code>.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param target the object to adapt.
    * @return the adapter for the <code>target</code>.
    * @generated
    */
   @Override
   public Adapter createAdapter(final Notifier target) {
      return modelSwitch.doSwitch((EObject) target);
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch <em>Json
    * Patch</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch
    * @generated
    */
   public Adapter createJsonPatchAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation
    * <em>Operation</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Operation
    * @generated
    */
   public Adapter createOperationAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Add
    * <em>Add</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Add
    * @generated
    */
   public Adapter createAddAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Remove
    * <em>Remove</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Remove
    * @generated
    */
   public Adapter createRemoveAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Replace
    * <em>Replace</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Replace
    * @generated
    */
   public Adapter createReplaceAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Move
    * <em>Move</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Move
    * @generated
    */
   public Adapter createMoveAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Copy
    * <em>Copy</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Copy
    * @generated
    */
   public Adapter createCopyAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Test
    * <em>Test</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Test
    * @generated
    */
   public Adapter createTestAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Value
    * <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Value
    * @generated
    */
   public Adapter createValueAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue
    * <em>Boolean Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue
    * @generated
    */
   public Adapter createBooleanValueAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.StringValue
    * <em>String Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.StringValue
    * @generated
    */
   public Adapter createStringValueAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue
    * <em>Number Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue
    * @generated
    */
   public Adapter createNumberValueAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue
    * <em>Object Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue
    * @generated
    */
   public Adapter createObjectValueAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for the default case.
    * <!-- begin-user-doc -->
    * This default implementation returns null.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @generated
    */
   public Adapter createEObjectAdapter() {
      return null;
   }

} // JsonPatchAdapterFactory
