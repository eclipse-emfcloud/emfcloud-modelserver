/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

public interface JsonRequestType {

   /** Execute a command. */
   String EXECUTE = "execute";

   /** Close, for example a transaction. */
   String CLOSE = "close";

   /** Roll back a transaction. */
   String ROLL_BACK = "roll-back";

}
