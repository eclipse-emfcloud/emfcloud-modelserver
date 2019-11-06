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
package org.eclipse.emfcloud.modelserver.command;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.command.CCommandFactory
 * @model kind="package"
 * @generated
 */
public interface CCommandPackage extends EPackage {
   /**
    * The package name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNAME = "command";

   /**
    * The package namespace URI.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_URI = "http://www.eclipsesource.com/schema/2019/modelserver/command";

   /**
    * The package namespace name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_PREFIX = "cmd";

   /**
    * The singleton instance of the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   CCommandPackage eINSTANCE = org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl.init();

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl <em>Command</em>}'
    * class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl
    * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCommand()
    * @generated
    */
   int COMMAND = 0;

   /**
    * The feature id for the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__TYPE = 0;

   /**
    * The feature id for the '<em><b>Owner</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__OWNER = 1;

   /**
    * The feature id for the '<em><b>Feature</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__FEATURE = 2;

   /**
    * The feature id for the '<em><b>Indices</b></em>' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__INDICES = 3;

   /**
    * The feature id for the '<em><b>Data Values</b></em>' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__DATA_VALUES = 4;

   /**
    * The feature id for the '<em><b>Object Values</b></em>' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__OBJECT_VALUES = 5;

   /**
    * The feature id for the '<em><b>Objects To Add</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND__OBJECTS_TO_ADD = 6;

   /**
    * The number of structural features of the '<em>Command</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND_FEATURE_COUNT = 7;

   /**
    * The number of operations of the '<em>Command</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMMAND_OPERATION_COUNT = 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.command.impl.CCompoundCommandImpl <em>Compound
    * Command</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.command.impl.CCompoundCommandImpl
    * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCompoundCommand()
    * @generated
    */
   int COMPOUND_COMMAND = 1;

   /**
    * The feature id for the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__TYPE = COMMAND__TYPE;

   /**
    * The feature id for the '<em><b>Owner</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__OWNER = COMMAND__OWNER;

   /**
    * The feature id for the '<em><b>Feature</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__FEATURE = COMMAND__FEATURE;

   /**
    * The feature id for the '<em><b>Indices</b></em>' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__INDICES = COMMAND__INDICES;

   /**
    * The feature id for the '<em><b>Data Values</b></em>' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__DATA_VALUES = COMMAND__DATA_VALUES;

   /**
    * The feature id for the '<em><b>Object Values</b></em>' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__OBJECT_VALUES = COMMAND__OBJECT_VALUES;

   /**
    * The feature id for the '<em><b>Objects To Add</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__OBJECTS_TO_ADD = COMMAND__OBJECTS_TO_ADD;

   /**
    * The feature id for the '<em><b>Commands</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND__COMMANDS = COMMAND_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Compound Command</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND_FEATURE_COUNT = COMMAND_FEATURE_COUNT + 1;

   /**
    * The number of operations of the '<em>Compound Command</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COMPOUND_COMMAND_OPERATION_COUNT = COMMAND_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.command.CommandKind <em>Kind</em>}' enum.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.command.CommandKind
    * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCommandKind()
    * @generated
    */
   int COMMAND_KIND = 2;

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.command.CCommand <em>Command</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Command</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand
    * @generated
    */
   EClass getCommand();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getType
    * <em>Type</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Type</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getType()
    * @see #getCommand()
    * @generated
    */
   EAttribute getCommand_Type();

   /**
    * Returns the meta object for the reference '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getOwner
    * <em>Owner</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the reference '<em>Owner</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getOwner()
    * @see #getCommand()
    * @generated
    */
   EReference getCommand_Owner();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getFeature
    * <em>Feature</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Feature</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getFeature()
    * @see #getCommand()
    * @generated
    */
   EAttribute getCommand_Feature();

   /**
    * Returns the meta object for the attribute list
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getIndices
    * <em>Indices</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute list '<em>Indices</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getIndices()
    * @see #getCommand()
    * @generated
    */
   EAttribute getCommand_Indices();

   /**
    * Returns the meta object for the attribute list
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getDataValues <em>Data Values</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute list '<em>Data Values</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getDataValues()
    * @see #getCommand()
    * @generated
    */
   EAttribute getCommand_DataValues();

   /**
    * Returns the meta object for the reference list
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getObjectValues <em>Object Values</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the reference list '<em>Object Values</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getObjectValues()
    * @see #getCommand()
    * @generated
    */
   EReference getCommand_ObjectValues();

   /**
    * Returns the meta object for the containment reference list
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommand#getObjectsToAdd <em>Objects To Add</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference list '<em>Objects To Add</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCommand#getObjectsToAdd()
    * @see #getCommand()
    * @generated
    */
   EReference getCommand_ObjectsToAdd();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.command.CCompoundCommand <em>Compound
    * Command</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Compound Command</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCompoundCommand
    * @generated
    */
   EClass getCompoundCommand();

   /**
    * Returns the meta object for the containment reference list
    * '{@link org.eclipse.emfcloud.modelserver.command.CCompoundCommand#getCommands <em>Commands</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference list '<em>Commands</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CCompoundCommand#getCommands()
    * @see #getCompoundCommand()
    * @generated
    */
   EReference getCompoundCommand_Commands();

   /**
    * Returns the meta object for enum '{@link org.eclipse.emfcloud.modelserver.command.CommandKind <em>Kind</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for enum '<em>Kind</em>'.
    * @see org.eclipse.emfcloud.modelserver.command.CommandKind
    * @generated
    */
   EEnum getCommandKind();

   /**
    * Returns the factory that creates the instances of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the factory that creates the instances of the model.
    * @generated
    */
   CCommandFactory getCommandFactory();

   /**
    * <!-- begin-user-doc -->
    * Defines literals for the meta objects that represent
    * <ul>
    * <li>each class,</li>
    * <li>each feature of each class,</li>
    * <li>each operation of each class,</li>
    * <li>each enum,</li>
    * <li>and each data type</li>
    * </ul>
    * <!-- end-user-doc -->
    *
    * @generated
    */
   interface Literals {
      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl
       * <em>Command</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl
       * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCommand()
       * @generated
       */
      EClass COMMAND = eINSTANCE.getCommand();

      /**
       * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute COMMAND__TYPE = eINSTANCE.getCommand_Type();

      /**
       * The meta object literal for the '<em><b>Owner</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference COMMAND__OWNER = eINSTANCE.getCommand_Owner();

      /**
       * The meta object literal for the '<em><b>Feature</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute COMMAND__FEATURE = eINSTANCE.getCommand_Feature();

      /**
       * The meta object literal for the '<em><b>Indices</b></em>' attribute list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute COMMAND__INDICES = eINSTANCE.getCommand_Indices();

      /**
       * The meta object literal for the '<em><b>Data Values</b></em>' attribute list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute COMMAND__DATA_VALUES = eINSTANCE.getCommand_DataValues();

      /**
       * The meta object literal for the '<em><b>Object Values</b></em>' reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference COMMAND__OBJECT_VALUES = eINSTANCE.getCommand_ObjectValues();

      /**
       * The meta object literal for the '<em><b>Objects To Add</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference COMMAND__OBJECTS_TO_ADD = eINSTANCE.getCommand_ObjectsToAdd();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.command.impl.CCompoundCommandImpl
       * <em>Compound Command</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.command.impl.CCompoundCommandImpl
       * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCompoundCommand()
       * @generated
       */
      EClass COMPOUND_COMMAND = eINSTANCE.getCompoundCommand();

      /**
       * The meta object literal for the '<em><b>Commands</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference COMPOUND_COMMAND__COMMANDS = eINSTANCE.getCompoundCommand_Commands();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.command.CommandKind <em>Kind</em>}'
       * enum.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.command.CommandKind
       * @see org.eclipse.emfcloud.modelserver.command.impl.CCommandPackageImpl#getCommandKind()
       * @generated
       */
      EEnum COMMAND_KIND = eINSTANCE.getCommandKind();

   }

} // CCommandPackage
