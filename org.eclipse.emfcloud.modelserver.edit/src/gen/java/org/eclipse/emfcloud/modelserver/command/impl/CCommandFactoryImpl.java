/**
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package org.eclipse.emfcloud.modelserver.command.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.command.CommandKind;

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
         case CCommandPackage.COMMAND_KIND:
            return createCommandKindFromString(eDataType, initialValue);
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
         case CCommandPackage.COMMAND_KIND:
            return convertCommandKindToString(eDataType, instanceValue);
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
   public CommandKind createCommandKindFromString(final EDataType eDataType, final String initialValue) {
      CommandKind result = CommandKind.get(initialValue);
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
   public String convertCommandKindToString(final EDataType eDataType, final Object instanceValue) {
      return instanceValue == null ? null : instanceValue.toString();
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
