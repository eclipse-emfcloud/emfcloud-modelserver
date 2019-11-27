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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Manual Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask#getActor <em>Actor</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getManualTask()
 * @model
 * @generated
 */
public interface ManualTask extends Task {
   /**
    * Returns the value of the '<em><b>Actor</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Actor</em>' attribute.
    * @see #setActor(String)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getManualTask_Actor()
    * @model
    * @generated
    */
   String getActor();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ManualTask#getActor
    * <em>Actor</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Actor</em>' attribute.
    * @see #getActor()
    * @generated
    */
   void setActor(String value);

} // ManualTask
