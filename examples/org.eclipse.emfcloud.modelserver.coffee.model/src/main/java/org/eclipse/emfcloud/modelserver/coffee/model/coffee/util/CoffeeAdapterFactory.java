/**
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.coffee.model.coffee.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.AutomaticTask;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.BrewingUnit;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Decision;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.DipTray;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Display;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Fork;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Join;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Merge;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Node;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Processor;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.RAM;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Task;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.WaterTank;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.WeightedFlow;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage
 * @generated
 */
public class CoffeeAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected static CoffeePackage modelPackage;

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public CoffeeAdapterFactory() {
      if (modelPackage == null) {
         modelPackage = CoffeePackage.eINSTANCE;
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
   protected CoffeeSwitch<Adapter> modelSwitch = new CoffeeSwitch<>() {
      @Override
      public Adapter caseComponent(final Component object) {
         return createComponentAdapter();
      }

      @Override
      public Adapter caseMachine(final Machine object) {
         return createMachineAdapter();
      }

      @Override
      public Adapter caseControlUnit(final ControlUnit object) {
         return createControlUnitAdapter();
      }

      @Override
      public Adapter caseBrewingUnit(final BrewingUnit object) {
         return createBrewingUnitAdapter();
      }

      @Override
      public Adapter caseDipTray(final DipTray object) {
         return createDipTrayAdapter();
      }

      @Override
      public Adapter caseWaterTank(final WaterTank object) {
         return createWaterTankAdapter();
      }

      @Override
      public Adapter caseProcessor(final Processor object) {
         return createProcessorAdapter();
      }

      @Override
      public Adapter caseDimension(final Dimension object) {
         return createDimensionAdapter();
      }

      @Override
      public Adapter caseRAM(final RAM object) {
         return createRAMAdapter();
      }

      @Override
      public Adapter caseDisplay(final Display object) {
         return createDisplayAdapter();
      }

      @Override
      public Adapter caseWorkflow(final Workflow object) {
         return createWorkflowAdapter();
      }

      @Override
      public Adapter caseNode(final Node object) {
         return createNodeAdapter();
      }

      @Override
      public Adapter caseTask(final Task object) {
         return createTaskAdapter();
      }

      @Override
      public Adapter caseAutomaticTask(final AutomaticTask object) {
         return createAutomaticTaskAdapter();
      }

      @Override
      public Adapter caseManualTask(final ManualTask object) {
         return createManualTaskAdapter();
      }

      @Override
      public Adapter caseFork(final Fork object) {
         return createForkAdapter();
      }

      @Override
      public Adapter caseJoin(final Join object) {
         return createJoinAdapter();
      }

      @Override
      public Adapter caseDecision(final Decision object) {
         return createDecisionAdapter();
      }

      @Override
      public Adapter caseMerge(final Merge object) {
         return createMergeAdapter();
      }

      @Override
      public Adapter caseFlow(final Flow object) {
         return createFlowAdapter();
      }

      @Override
      public Adapter caseWeightedFlow(final WeightedFlow object) {
         return createWeightedFlowAdapter();
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
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component <em>Component</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Component
    * @generated
    */
   public Adapter createComponentAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine
    * <em>Machine</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine
    * @generated
    */
   public Adapter createMachineAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit <em>Control Unit</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit
    * @generated
    */
   public Adapter createControlUnitAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.BrewingUnit <em>Brewing Unit</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.BrewingUnit
    * @generated
    */
   public Adapter createBrewingUnitAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.DipTray
    * <em>Dip Tray</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.DipTray
    * @generated
    */
   public Adapter createDipTrayAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.WaterTank <em>Water Tank</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.WaterTank
    * @generated
    */
   public Adapter createWaterTankAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Processor <em>Processor</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Processor
    * @generated
    */
   public Adapter createProcessorAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension <em>Dimension</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Dimension
    * @generated
    */
   public Adapter createDimensionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.RAM
    * <em>RAM</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.RAM
    * @generated
    */
   public Adapter createRAMAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Display
    * <em>Display</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Display
    * @generated
    */
   public Adapter createDisplayAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow
    * <em>Workflow</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow
    * @generated
    */
   public Adapter createWorkflowAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Node
    * <em>Node</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Node
    * @generated
    */
   public Adapter createNodeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Task
    * <em>Task</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Task
    * @generated
    */
   public Adapter createTaskAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.AutomaticTask <em>Automatic Task</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.AutomaticTask
    * @generated
    */
   public Adapter createAutomaticTaskAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask <em>Manual Task</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask
    * @generated
    */
   public Adapter createManualTaskAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Fork
    * <em>Fork</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Fork
    * @generated
    */
   public Adapter createForkAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Join
    * <em>Join</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Join
    * @generated
    */
   public Adapter createJoinAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Decision
    * <em>Decision</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Decision
    * @generated
    */
   public Adapter createDecisionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Merge
    * <em>Merge</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Merge
    * @generated
    */
   public Adapter createMergeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow
    * <em>Flow</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow
    * @generated
    */
   public Adapter createFlowAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.WeightedFlow <em>Weighted Flow</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    *
    * @return the new adapter.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.WeightedFlow
    * @generated
    */
   public Adapter createWeightedFlowAdapter() {
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

} // CoffeeAdapterFactory
