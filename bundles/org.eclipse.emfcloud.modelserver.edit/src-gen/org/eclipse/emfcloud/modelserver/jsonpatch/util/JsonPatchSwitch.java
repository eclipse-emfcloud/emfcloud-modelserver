/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
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
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchPackage
 * @generated
 */
public class JsonPatchSwitch<T> extends Switch<T> {
   /**
    * The cached model package
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static JsonPatchPackage modelPackage;

   /**
    * Creates an instance of the switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public JsonPatchSwitch() {
      if (modelPackage == null) {
         modelPackage = JsonPatchPackage.eINSTANCE;
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
         case JsonPatchPackage.JSON_PATCH: {
            JsonPatch jsonPatch = (JsonPatch) theEObject;
            T result = caseJsonPatch(jsonPatch);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.OPERATION: {
            Operation operation = (Operation) theEObject;
            T result = caseOperation(operation);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.ADD: {
            Add add = (Add) theEObject;
            T result = caseAdd(add);
            if (result == null) {
               result = caseOperation(add);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.REMOVE: {
            Remove remove = (Remove) theEObject;
            T result = caseRemove(remove);
            if (result == null) {
               result = caseOperation(remove);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.REPLACE: {
            Replace replace = (Replace) theEObject;
            T result = caseReplace(replace);
            if (result == null) {
               result = caseOperation(replace);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.MOVE: {
            Move move = (Move) theEObject;
            T result = caseMove(move);
            if (result == null) {
               result = caseOperation(move);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.COPY: {
            Copy copy = (Copy) theEObject;
            T result = caseCopy(copy);
            if (result == null) {
               result = caseOperation(copy);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.TEST: {
            Test test = (Test) theEObject;
            T result = caseTest(test);
            if (result == null) {
               result = caseOperation(test);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.VALUE: {
            Value value = (Value) theEObject;
            T result = caseValue(value);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.BOOLEAN_VALUE: {
            BooleanValue booleanValue = (BooleanValue) theEObject;
            T result = caseBooleanValue(booleanValue);
            if (result == null) {
               result = caseValue(booleanValue);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.STRING_VALUE: {
            StringValue stringValue = (StringValue) theEObject;
            T result = caseStringValue(stringValue);
            if (result == null) {
               result = caseValue(stringValue);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.NUMBER_VALUE: {
            NumberValue numberValue = (NumberValue) theEObject;
            T result = caseNumberValue(numberValue);
            if (result == null) {
               result = caseValue(numberValue);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case JsonPatchPackage.OBJECT_VALUE: {
            ObjectValue objectValue = (ObjectValue) theEObject;
            T result = caseObjectValue(objectValue);
            if (result == null) {
               result = caseValue(objectValue);
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
    * Returns the result of interpreting the object as an instance of '<em>Json Patch</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Json Patch</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseJsonPatch(final JsonPatch object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Operation</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Operation</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseOperation(final Operation object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Add</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Add</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseAdd(final Add object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Remove</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Remove</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseRemove(final Remove object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Replace</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Replace</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseReplace(final Replace object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Move</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Move</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseMove(final Move object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Copy</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Copy</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseCopy(final Copy object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Test</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Test</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseTest(final Test object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Value</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Value</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseValue(final Value object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Boolean Value</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Boolean Value</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseBooleanValue(final BooleanValue object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>String Value</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>String Value</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseStringValue(final StringValue object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Number Value</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Number Value</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseNumberValue(final NumberValue object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Object Value</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Object Value</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseObjectValue(final ObjectValue object) {
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

} // JsonPatchSwitch
