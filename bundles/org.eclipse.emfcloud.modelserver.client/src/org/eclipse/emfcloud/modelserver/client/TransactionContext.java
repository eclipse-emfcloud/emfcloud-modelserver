/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.client;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * Client interface for transactions on managed models.
 * Commands executed within this context are executed but not put onto the stack.
 * Instead, they are collected into a single compound that is put on the stack when the transaction is closed.
 * The transaction may be rolled back, undoing all commands executed so far and closing the transactions.
 * The transaction may be closed, which puts the final state of the compound command on the stack.
 * If the client goes away, causing the websocket to be dropped, then the transaction is implicitly rolled back by the
 * server.
 */
public interface TransactionContext extends EditingContext {

   /**
    * Close the transaction.
    * The final state of the compound command collected in this context is put on the stack.
    */
   void close();

   /**
    * Roll back all commands executed so far in the context of the transaction and close it without putting a compound
    * on the stack.
    *
    * @param error
    */
   void rollback(Diagnostic error);

   // TODO: Call-back for receipt of the CommandExecutionResult

}
