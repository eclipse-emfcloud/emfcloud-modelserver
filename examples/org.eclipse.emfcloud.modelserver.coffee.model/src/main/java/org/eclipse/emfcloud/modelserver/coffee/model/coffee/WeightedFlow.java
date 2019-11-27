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
 * A representation of the model object '<em><b>Weighted Flow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.WeightedFlow#getProbability
 * <em>Probability</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWeightedFlow()
 * @model
 * @generated
 */
public interface WeightedFlow extends Flow {
   /**
    * Returns the value of the '<em><b>Probability</b></em>' attribute.
    * The literals are from the enumeration {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.Probability}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Probability</em>' attribute.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Probability
    * @see #setProbability(Probability)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getWeightedFlow_Probability()
    * @model
    * @generated
    */
   Probability getProbability();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.WeightedFlow#getProbability
    * <em>Probability</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Probability</em>' attribute.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.Probability
    * @see #getProbability()
    * @generated
    */
   void setProbability(Probability value);

} // WeightedFlow
