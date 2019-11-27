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
 * A representation of the model object '<em><b>Control Unit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getProcessor <em>Processor</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getDimension <em>Dimension</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getRam <em>Ram</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getDisplay <em>Display</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getUserDescription <em>User
 * Description</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit()
 * @model
 * @generated
 */
public interface ControlUnit extends Component {
   /**
    * Returns the value of the '<em><b>Processor</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Processor</em>' containment reference.
    * @see #setProcessor(Processor)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit_Processor()
    * @model containment="true" required="true"
    * @generated
    */
   Processor getProcessor();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getProcessor
    * <em>Processor</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Processor</em>' containment reference.
    * @see #getProcessor()
    * @generated
    */
   void setProcessor(Processor value);

   /**
    * Returns the value of the '<em><b>Dimension</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Dimension</em>' containment reference.
    * @see #setDimension(Dimension)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit_Dimension()
    * @model containment="true" required="true"
    * @generated
    */
   Dimension getDimension();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getDimension
    * <em>Dimension</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Dimension</em>' containment reference.
    * @see #getDimension()
    * @generated
    */
   void setDimension(Dimension value);

   /**
    * Returns the value of the '<em><b>Ram</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.RAM}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Ram</em>' containment reference list.
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit_Ram()
    * @model containment="true" required="true"
    * @generated
    */
   EList<RAM> getRam();

   /**
    * Returns the value of the '<em><b>Display</b></em>' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Display</em>' containment reference.
    * @see #setDisplay(Display)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit_Display()
    * @model containment="true"
    * @generated
    */
   Display getDisplay();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getDisplay
    * <em>Display</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>Display</em>' containment reference.
    * @see #getDisplay()
    * @generated
    */
   void setDisplay(Display value);

   /**
    * Returns the value of the '<em><b>User Description</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>User Description</em>' attribute.
    * @see #setUserDescription(String)
    * @see org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage#getControlUnit_UserDescription()
    * @model
    * @generated
    */
   String getUserDescription();

   /**
    * Sets the value of the '{@link org.eclipse.emfcloud.modelserver.coffee.model.coffee.ControlUnit#getUserDescription
    * <em>User Description</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @param value the new value of the '<em>User Description</em>' attribute.
    * @see #getUserDescription()
    * @generated
    */
   void setUserDescription(String value);

} // ControlUnit
