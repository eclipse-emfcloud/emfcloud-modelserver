/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;

public interface ModelListener {
   void modelCreated(String modeluri);

   void modelUpdated(String modeluri);

   void commandExecuted(String modeluri, CCommandExecutionResult execution);

   void modelDeleted(String modeluri);

   /**
    * Notifies when a model resource is closed.
    *
    * @param modeluri the concerned model's URI
    */
   void modelClosed(String modeluri);

   void modelSaved(String modeluri);

   void allModelsSaved();
}
