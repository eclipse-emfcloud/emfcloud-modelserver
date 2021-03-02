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
package org.eclipse.emfcloud.modelserver.command;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Execution Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getSource <em>Source</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getAffectedObjects <em>Affected
 * Objects</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getChangeDescription <em>Change
 * Description</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getDetails <em>Details</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult()
 * @model
 * @generated
 */
public interface CCommandExecutionResult extends EObject {
   /**
    * Returns the value of the '<em><b>Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Type</em>' attribute.
    * @see #setType(String)
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult_Type()
    * @model required="true"
    * @generated
    */
   String getType();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getType
    * <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Type</em>' attribute.
    * @see #getType()
    * @generated
    */
   void setType(String value);

   /**
    * Returns the value of the '<em><b>Source</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Source</em>' containment reference.
    * @see #setSource(CCommand)
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult_Source()
    * @model containment="true" required="true"
    * @generated
    */
   CCommand getSource();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getSource
    * <em>Source</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Source</em>' containment reference.
    * @see #getSource()
    * @generated
    */
   void setSource(CCommand value);

   /**
    * Returns the value of the '<em><b>Affected Objects</b></em>' reference list.
    * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Affected Objects</em>' reference list.
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult_AffectedObjects()
    * @model
    * @generated
    */
   EList<EObject> getAffectedObjects();

   /**
    * Returns the value of the '<em><b>Change Description</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Change Description</em>' containment reference.
    * @see #setChangeDescription(EObject)
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult_ChangeDescription()
    * @model containment="true"
    * @generated
    */
   EObject getChangeDescription();

   /**
    * Sets the value of the
    * '{@link org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult#getChangeDescription <em>Change
    * Description</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Change Description</em>' containment reference.
    * @see #getChangeDescription()
    * @generated
    */
   void setChangeDescription(EObject value);

   /**
    * Returns the value of the '<em><b>Details</b></em>' map.
    * The key is of type {@link java.lang.String},
    * and the value is of type {@link java.lang.String},
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Details</em>' map.
    * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage#getCommandExecutionResult_Details()
    * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString,
    *        org.eclipse.emf.ecore.EString&gt;"
    * @generated
    */
   EMap<String, String> getDetails();

} // CCommandExecutionResult
