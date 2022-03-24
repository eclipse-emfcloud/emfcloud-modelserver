/**
 * Copyright (c) 2022 STMicroelectronics and others.\n\nThis program and the accompanying materials are made available
 * under the\nterms of the Eclipse Public License v. 2.0 which is available at\nhttps://www.eclipse.org/legal/epl-2.0,
 * or the MIT License which is\navailable at https://opensource.org/licenses/MIT.\n\nSPDX-License-Identifier: EPL-2.0 OR
 * MIT
 */
package org.eclipse.emfcloud.modelserver.jsonpatch;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
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
 * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchFactory
 * @model kind="package"
 * @generated
 */
public interface JsonPatchPackage extends EPackage {
   /**
    * The package name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNAME = "jsonpatch";

   /**
    * The package namespace URI.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_URI = "http://www.eclipse.org/emfcloud/modelserver/jsonpatch";

   /**
    * The package namespace name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   String eNS_PREFIX = "patch";

   /**
    * The singleton instance of the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   JsonPatchPackage eINSTANCE = org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl.init();

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchImpl <em>Json
    * Patch</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getJsonPatch()
    * @generated
    */
   int JSON_PATCH = 0;

   /**
    * The feature id for the '<em><b>Patch</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int JSON_PATCH__PATCH = 0;

   /**
    * The number of structural features of the '<em>Json Patch</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int JSON_PATCH_FEATURE_COUNT = 1;

   /**
    * The number of operations of the '<em>Json Patch</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int JSON_PATCH_OPERATION_COUNT = 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.OperationImpl
    * <em>Operation</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.OperationImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getOperation()
    * @generated
    */
   int OPERATION = 1;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OPERATION__OP = 0;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OPERATION__PATH = 1;

   /**
    * The number of structural features of the '<em>Operation</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OPERATION_FEATURE_COUNT = 2;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OPERATION___GET_OP = 0;

   /**
    * The number of operations of the '<em>Operation</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OPERATION_OPERATION_COUNT = 1;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.AddImpl <em>Add</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.AddImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getAdd()
    * @generated
    */
   int ADD = 2;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD__PATH = OPERATION__PATH;

   /**
    * The feature id for the '<em><b>Value</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD__VALUE = OPERATION_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Add</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Add</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int ADD_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.RemoveImpl <em>Remove</em>}'
    * class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.RemoveImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getRemove()
    * @generated
    */
   int REMOVE = 3;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REMOVE__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REMOVE__PATH = OPERATION__PATH;

   /**
    * The number of structural features of the '<em>Remove</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REMOVE_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 0;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REMOVE___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Remove</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REMOVE_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ReplaceImpl <em>Replace</em>}'
    * class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ReplaceImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getReplace()
    * @generated
    */
   int REPLACE = 4;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE__PATH = OPERATION__PATH;

   /**
    * The feature id for the '<em><b>Value</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE__VALUE = OPERATION_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Replace</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Replace</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int REPLACE_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.MoveImpl <em>Move</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.MoveImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getMove()
    * @generated
    */
   int MOVE = 5;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE__PATH = OPERATION__PATH;

   /**
    * The feature id for the '<em><b>From</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE__FROM = OPERATION_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Move</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Move</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int MOVE_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.CopyImpl <em>Copy</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.CopyImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getCopy()
    * @generated
    */
   int COPY = 6;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY__PATH = OPERATION__PATH;

   /**
    * The feature id for the '<em><b>From</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY__FROM = OPERATION_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Copy</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Copy</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int COPY_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.TestImpl <em>Test</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.TestImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getTest()
    * @generated
    */
   int TEST = 7;

   /**
    * The feature id for the '<em><b>Op</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST__OP = OPERATION__OP;

   /**
    * The feature id for the '<em><b>Path</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST__PATH = OPERATION__PATH;

   /**
    * The feature id for the '<em><b>Value</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST__VALUE = OPERATION_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Test</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST_FEATURE_COUNT = OPERATION_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Get Op</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST___GET_OP = OPERATION___GET_OP;

   /**
    * The number of operations of the '<em>Test</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int TEST_OPERATION_COUNT = OPERATION_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ValueImpl <em>Value</em>}'
    * class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ValueImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getValue()
    * @generated
    */
   int VALUE = 8;

   /**
    * The number of structural features of the '<em>Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int VALUE_FEATURE_COUNT = 0;

   /**
    * The operation id for the '<em>Value String</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int VALUE___VALUE_STRING = 0;

   /**
    * The number of operations of the '<em>Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int VALUE_OPERATION_COUNT = 1;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.BooleanValueImpl <em>Boolean
    * Value</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.BooleanValueImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getBooleanValue()
    * @generated
    */
   int BOOLEAN_VALUE = 9;

   /**
    * The feature id for the '<em><b>Value</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int BOOLEAN_VALUE__VALUE = VALUE_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Boolean Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int BOOLEAN_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Value String</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int BOOLEAN_VALUE___VALUE_STRING = VALUE___VALUE_STRING;

   /**
    * The number of operations of the '<em>Boolean Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int BOOLEAN_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.StringValueImpl <em>String
    * Value</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.StringValueImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getStringValue()
    * @generated
    */
   int STRING_VALUE = 10;

   /**
    * The feature id for the '<em><b>Value</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int STRING_VALUE__VALUE = VALUE_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>String Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int STRING_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Value String</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int STRING_VALUE___VALUE_STRING = VALUE___VALUE_STRING;

   /**
    * The number of operations of the '<em>String Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int STRING_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.NumberValueImpl <em>Number
    * Value</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.NumberValueImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getNumberValue()
    * @generated
    */
   int NUMBER_VALUE = 11;

   /**
    * The feature id for the '<em><b>Value</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NUMBER_VALUE__VALUE = VALUE_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Number Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NUMBER_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Value String</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NUMBER_VALUE___VALUE_STRING = VALUE___VALUE_STRING;

   /**
    * The number of operations of the '<em>Number Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int NUMBER_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ObjectValueImpl <em>Object
    * Value</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ObjectValueImpl
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getObjectValue()
    * @generated
    */
   int OBJECT_VALUE = 12;

   /**
    * The feature id for the '<em><b>Value</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OBJECT_VALUE__VALUE = VALUE_FEATURE_COUNT + 0;

   /**
    * The number of structural features of the '<em>Object Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OBJECT_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

   /**
    * The operation id for the '<em>Value String</em>' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OBJECT_VALUE___VALUE_STRING = VALUE___VALUE_STRING;

   /**
    * The number of operations of the '<em>Object Value</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    * @ordered
    */
   int OBJECT_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

   /**
    * The meta object id for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.OpKind <em>Op Kind</em>}' enum.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.OpKind
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getOpKind()
    * @generated
    */
   int OP_KIND = 13;

   /**
    * The meta object id for the '<em>Path</em>' data type.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see java.lang.String
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getPath()
    * @generated
    */
   int PATH = 14;

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch <em>Json
    * Patch</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Json Patch</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch
    * @generated
    */
   EClass getJsonPatch();

   /**
    * Returns the meta object for the containment reference list
    * '{@link org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch#getPatch <em>Patch</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference list '<em>Patch</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch#getPatch()
    * @see #getJsonPatch()
    * @generated
    */
   EReference getJsonPatch_Patch();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation
    * <em>Operation</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Operation</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Operation
    * @generated
    */
   EClass getOperation();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getOp
    * <em>Op</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Op</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getOp()
    * @see #getOperation()
    * @generated
    */
   EAttribute getOperation_Op();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getPath
    * <em>Path</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Path</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getPath()
    * @see #getOperation()
    * @generated
    */
   EAttribute getOperation_Path();

   /**
    * Returns the meta object for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getOp() <em>Get
    * Op</em>}' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the '<em>Get Op</em>' operation.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Operation#getOp()
    * @generated
    */
   EOperation getOperation__GetOp();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Add <em>Add</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Add</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Add
    * @generated
    */
   EClass getAdd();

   /**
    * Returns the meta object for the containment reference
    * '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Add#getValue <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Add#getValue()
    * @see #getAdd()
    * @generated
    */
   EReference getAdd_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Remove <em>Remove</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Remove</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Remove
    * @generated
    */
   EClass getRemove();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Replace <em>Replace</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Replace</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Replace
    * @generated
    */
   EClass getReplace();

   /**
    * Returns the meta object for the containment reference
    * '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Replace#getValue <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Replace#getValue()
    * @see #getReplace()
    * @generated
    */
   EReference getReplace_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Move <em>Move</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Move</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Move
    * @generated
    */
   EClass getMove();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Move#getFrom
    * <em>From</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>From</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Move#getFrom()
    * @see #getMove()
    * @generated
    */
   EAttribute getMove_From();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Copy <em>Copy</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Copy</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Copy
    * @generated
    */
   EClass getCopy();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Copy#getFrom
    * <em>From</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>From</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Copy#getFrom()
    * @see #getCopy()
    * @generated
    */
   EAttribute getCopy_From();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Test <em>Test</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Test</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Test
    * @generated
    */
   EClass getTest();

   /**
    * Returns the meta object for the containment reference
    * '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Test#getValue <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the containment reference '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Test#getValue()
    * @see #getTest()
    * @generated
    */
   EReference getTest_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Value <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Value
    * @generated
    */
   EClass getValue();

   /**
    * Returns the meta object for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.Value#stringValue() <em>Value
    * String</em>}' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the '<em>Value String</em>' operation.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.Value#stringValue()
    * @generated
    */
   EOperation getValue__ValueString();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue <em>Boolean
    * Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Boolean Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue
    * @generated
    */
   EClass getBooleanValue();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue#isValue
    * <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue#isValue()
    * @see #getBooleanValue()
    * @generated
    */
   EAttribute getBooleanValue_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.StringValue <em>String
    * Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>String Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.StringValue
    * @generated
    */
   EClass getStringValue();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.StringValue#getValue
    * <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.StringValue#getValue()
    * @see #getStringValue()
    * @generated
    */
   EAttribute getStringValue_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue <em>Number
    * Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Number Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue
    * @generated
    */
   EClass getNumberValue();

   /**
    * Returns the meta object for the attribute '{@link org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue#getValue
    * <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the attribute '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue#getValue()
    * @see #getNumberValue()
    * @generated
    */
   EAttribute getNumberValue_Value();

   /**
    * Returns the meta object for class '{@link org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue <em>Object
    * Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for class '<em>Object Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue
    * @generated
    */
   EClass getObjectValue();

   /**
    * Returns the meta object for the reference '{@link org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue#getValue
    * <em>Value</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for the reference '<em>Value</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue#getValue()
    * @see #getObjectValue()
    * @generated
    */
   EReference getObjectValue_Value();

   /**
    * Returns the meta object for enum '{@link org.eclipse.emfcloud.modelserver.jsonpatch.OpKind <em>Op Kind</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for enum '<em>Op Kind</em>'.
    * @see org.eclipse.emfcloud.modelserver.jsonpatch.OpKind
    * @generated
    */
   EEnum getOpKind();

   /**
    * Returns the meta object for data type '{@link java.lang.String <em>Path</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the meta object for data type '<em>Path</em>'.
    * @see java.lang.String
    * @model instanceClass="java.lang.String"
    * @generated
    */
   EDataType getPath();

   /**
    * Returns the factory that creates the instances of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the factory that creates the instances of the model.
    * @generated
    */
   JsonPatchFactory getJsonPatchFactory();

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
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchImpl <em>Json
       * Patch</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getJsonPatch()
       * @generated
       */
      EClass JSON_PATCH = eINSTANCE.getJsonPatch();

      /**
       * The meta object literal for the '<em><b>Patch</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference JSON_PATCH__PATCH = eINSTANCE.getJsonPatch_Patch();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.OperationImpl
       * <em>Operation</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.OperationImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getOperation()
       * @generated
       */
      EClass OPERATION = eINSTANCE.getOperation();

      /**
       * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute OPERATION__OP = eINSTANCE.getOperation_Op();

      /**
       * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute OPERATION__PATH = eINSTANCE.getOperation_Path();

      /**
       * The meta object literal for the '<em><b>Get Op</b></em>' operation.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EOperation OPERATION___GET_OP = eINSTANCE.getOperation__GetOp();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.AddImpl <em>Add</em>}'
       * class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.AddImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getAdd()
       * @generated
       */
      EClass ADD = eINSTANCE.getAdd();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference ADD__VALUE = eINSTANCE.getAdd_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.RemoveImpl
       * <em>Remove</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.RemoveImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getRemove()
       * @generated
       */
      EClass REMOVE = eINSTANCE.getRemove();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ReplaceImpl
       * <em>Replace</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ReplaceImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getReplace()
       * @generated
       */
      EClass REPLACE = eINSTANCE.getReplace();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference REPLACE__VALUE = eINSTANCE.getReplace_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.MoveImpl
       * <em>Move</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.MoveImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getMove()
       * @generated
       */
      EClass MOVE = eINSTANCE.getMove();

      /**
       * The meta object literal for the '<em><b>From</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute MOVE__FROM = eINSTANCE.getMove_From();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.CopyImpl
       * <em>Copy</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.CopyImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getCopy()
       * @generated
       */
      EClass COPY = eINSTANCE.getCopy();

      /**
       * The meta object literal for the '<em><b>From</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute COPY__FROM = eINSTANCE.getCopy_From();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.TestImpl
       * <em>Test</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.TestImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getTest()
       * @generated
       */
      EClass TEST = eINSTANCE.getTest();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference TEST__VALUE = eINSTANCE.getTest_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ValueImpl
       * <em>Value</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ValueImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getValue()
       * @generated
       */
      EClass VALUE = eINSTANCE.getValue();

      /**
       * The meta object literal for the '<em><b>Value String</b></em>' operation.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EOperation VALUE___VALUE_STRING = eINSTANCE.getValue__ValueString();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.BooleanValueImpl
       * <em>Boolean Value</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.BooleanValueImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getBooleanValue()
       * @generated
       */
      EClass BOOLEAN_VALUE = eINSTANCE.getBooleanValue();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute BOOLEAN_VALUE__VALUE = eINSTANCE.getBooleanValue_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.StringValueImpl
       * <em>String Value</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.StringValueImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getStringValue()
       * @generated
       */
      EClass STRING_VALUE = eINSTANCE.getStringValue();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute STRING_VALUE__VALUE = eINSTANCE.getStringValue_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.NumberValueImpl
       * <em>Number Value</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.NumberValueImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getNumberValue()
       * @generated
       */
      EClass NUMBER_VALUE = eINSTANCE.getNumberValue();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EAttribute NUMBER_VALUE__VALUE = eINSTANCE.getNumberValue_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.impl.ObjectValueImpl
       * <em>Object Value</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.ObjectValueImpl
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getObjectValue()
       * @generated
       */
      EClass OBJECT_VALUE = eINSTANCE.getObjectValue();

      /**
       * The meta object literal for the '<em><b>Value</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @generated
       */
      EReference OBJECT_VALUE__VALUE = eINSTANCE.getObjectValue_Value();

      /**
       * The meta object literal for the '{@link org.eclipse.emfcloud.modelserver.jsonpatch.OpKind <em>Op Kind</em>}'
       * enum.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.OpKind
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getOpKind()
       * @generated
       */
      EEnum OP_KIND = eINSTANCE.getOpKind();

      /**
       * The meta object literal for the '<em>Path</em>' data type.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       *
       * @see java.lang.String
       * @see org.eclipse.emfcloud.modelserver.jsonpatch.impl.JsonPatchPackageImpl#getPath()
       * @generated
       */
      EDataType PATH = eINSTANCE.getPath();

   }

} // JsonPatchPackage
