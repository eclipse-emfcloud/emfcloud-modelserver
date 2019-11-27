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
package org.eclipse.emfcloud.modelserver.coffee.model.coffee;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workflow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow#getNodes <em>Nodes</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow#getFlows <em>Flows</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWorkflow()
 * @model
 * @generated
 */
public interface Workflow extends EObject {
   /**
    * Returns the value of the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Name</em>' attribute.
    * @see #setName(String)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWorkflow_Name()
    * @model required="true"
    * @generated
    */
   String getName();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow#getName
    * <em>Name</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Name</em>' attribute.
    * @see #getName()
    * @generated
    */
   void setName(String value);

   /**
    * Returns the value of the '<em><b>Nodes</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Node}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Nodes</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWorkflow_Nodes()
    * @model containment="true"
    * @generated
    */
   EList<Node> getNodes();

   /**
    * Returns the value of the '<em><b>Flows</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Flow}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Flows</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWorkflow_Flows()
    * @model containment="true"
    * @generated
    */
   EList<Flow> getFlows();

} // Workflow
