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
package org.eclipse.emfcloud.modelserver.command.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;

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
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage
 * @generated
 */
public class CommandSwitch<T> extends Switch<T> {
   /**
    * The cached model package
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static CCommandPackage modelPackage;

   /**
    * Creates an instance of the switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public CommandSwitch() {
      if (modelPackage == null) {
         modelPackage = CCommandPackage.eINSTANCE;
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
         case CCommandPackage.COMMAND: {
            CCommand command = (CCommand) theEObject;
            T result = caseCommand(command);
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case CCommandPackage.COMPOUND_COMMAND: {
            CCompoundCommand compoundCommand = (CCompoundCommand) theEObject;
            T result = caseCompoundCommand(compoundCommand);
            if (result == null) {
               result = caseCommand(compoundCommand);
            }
            if (result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case CCommandPackage.COMMAND_EXECUTION_RESULT: {
            CCommandExecutionResult commandExecutionResult = (CCommandExecutionResult) theEObject;
            T result = caseCommandExecutionResult(commandExecutionResult);
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
    * Returns the result of interpreting the object as an instance of '<em>Command</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Command</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseCommand(final CCommand object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Compound Command</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Compound Command</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseCompoundCommand(final CCompoundCommand object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Execution Result</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    *
    * @param object the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Execution Result</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseCommandExecutionResult(final CCommandExecutionResult object) {
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

} // CommandSwitch
