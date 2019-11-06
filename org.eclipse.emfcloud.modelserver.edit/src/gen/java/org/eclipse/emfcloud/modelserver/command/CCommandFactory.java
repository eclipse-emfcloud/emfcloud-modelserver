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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emfcloud.modelserver.command.CCommandPackage
 * @generated
 */
public interface CCommandFactory extends EFactory {
   /**
    * The singleton instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   CCommandFactory eINSTANCE = org.eclipse.emfcloud.modelserver.command.impl.CCommandFactoryImpl.init();

   /**
    * Returns a new object of class '<em>Command</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Command</em>'.
    * @generated
    */
   CCommand createCommand();

   /**
    * Returns a new object of class '<em>Compound Command</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return a new object of class '<em>Compound Command</em>'.
    * @generated
    */
   CCompoundCommand createCompoundCommand();

   /**
    * Returns the package supported by this factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @return the package supported by this factory.
    * @generated
    */
   CCommandPackage getCommandPackage();

} // CCommandFactory
