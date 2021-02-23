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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class CCommandFactoryImpl extends EFactoryImpl implements CCommandFactory {
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public static CCommandFactory init() {
      try {
         CCommandFactory theCommandFactory = (CCommandFactory) EPackage.Registry.INSTANCE
            .getEFactory(CCommandPackage.eNS_URI);
         if (theCommandFactory != null) {
            return theCommandFactory;
         }
      } catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new CCommandFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public CCommandFactoryImpl() {
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
         case CCommandPackage.COMMAND:
            return createCommand();
         case CCommandPackage.COMPOUND_COMMAND:
            return createCompoundCommand();
         case CCommandPackage.COMMAND_EXECUTION_RESULT:
            return createCommandExecutionResult();
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
   public CCommand createCommand() {
      CCommandImpl command = new CCommandImpl();
      return command;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public CCompoundCommand createCompoundCommand() {
      CCompoundCommandImpl compoundCommand = new CCompoundCommandImpl();
      return compoundCommand;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public CCommandExecutionResult createCommandExecutionResult() {
      CCommandExecutionResultImpl commandExecutionResult = new CCommandExecutionResultImpl();
      return commandExecutionResult;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public CCommandPackage getCommandPackage() { return (CCommandPackage) getEPackage(); }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @deprecated
    * @generated
    */
   @Deprecated
   public static CCommandPackage getPackage() { return CCommandPackage.eINSTANCE; }

} // CCommandFactoryImpl
