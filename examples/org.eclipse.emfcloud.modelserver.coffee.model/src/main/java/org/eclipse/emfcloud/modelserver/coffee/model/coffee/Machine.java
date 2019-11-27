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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Machine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine#getWorkflows <em>Workflows</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getMachine()
 * @model
 * @generated
 */
public interface Machine extends Component {
   /**
    * Returns the value of the '<em><b>Name</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Name</em>' attribute.
    * @see #setName(String)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getMachine_Name()
    * @model required="true"
    * @generated
    */
   String getName();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine#getName <em>Name</em>}'
    * attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Name</em>' attribute.
    * @see #getName()
    * @generated
    */
   void setName(String value);

   /**
    * Returns the value of the '<em><b>Workflows</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Workflows</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getMachine_Workflows()
    * @model containment="true"
    * @generated
    */
   EList<Workflow> getWorkflows();

} // Machine
