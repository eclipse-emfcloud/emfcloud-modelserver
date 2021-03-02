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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage
 * @generated
 */
public class CommandAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static CCommandPackage modelPackage;

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public CommandAdapterFactory() {
      if (modelPackage == null) {
         modelPackage = CCommandPackage.eINSTANCE;
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
   protected CommandSwitch<Adapter> modelSwitch = new CommandSwitch<>() {
      @Override
      public Adapter caseCommand(final CCommand object) {
         return createCommandAdapter();
      }

      @Override
      public Adapter caseCompoundCommand(final CCompoundCommand object) {
         return createCompoundCommandAdapter();
      }

      @Override
      public Adapter caseCommandExecutionResult(final CCommandExecutionResult object) {
         return createCommandExecutionResultAdapter();
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
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.command.CCommand
    * <em>Command</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand
    * @generated
    */
   public Adapter createCommandAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.command.CCompoundCommand
    * <em>Compound Command</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.command.CCompoundCommand
    * @generated
    */
   public Adapter createCompoundCommandAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult <em>Execution Result</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult
    * @generated
    */
   public Adapter createCommandExecutionResultAdapter() {
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

} // CommandAdapterFactory
