/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.apache.log4j.Logger;
import org.eclipse.emf.transaction.impl.TransactionalCommandStackImpl;

public class ModelServerCommandStack extends TransactionalCommandStackImpl {

   protected static Logger LOG = Logger.getLogger(ModelServerCommandStack.class.getSimpleName());

   public ModelServerCommandStack() {
      setExceptionHandler(this::handleException);
   }

   protected void handleException(final Exception exception) {
      LOG.error("Error while executing command", exception);
   }

}
