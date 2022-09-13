/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;

import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

/**
 * Protocol for a <em>Transaction Controller</em> service that maintains the transaction
 * state for transaction resources created on the {@code transaction} endpoint.
 * The controller handles messaging with the client for execution of commands, private
 * broadcast of incremental updates, and closing or rolling back completed transactions.
 */
public interface TransactionController {
   /**
    * Create a new transaction resource on the {@code transaction} endpoint.
    *
    * @param ctx      the server context
    * @param modeluri the model URI on which to open a transaction
    */
   void create(Context ctx, String modeluri);

   /**
    * Handle the opening of a web socket on a transaction resource URI.
    *
    * @param ctx the web socket connection context
    */
   void onOpen(WsConnectContext ctx);

   /**
    * Handle the closure of a web socket on a transaction resource URI.
    * The transaction resource is deleted permanently.
    *
    * @param ctx the web socket closure context
    */
   void onClose(WsCloseContext ctx);

   /**
    * Handle an error in communication on the web socket of a transaction.
    *
    * @param ctx the web socket error context
    */
   void onError(WsErrorContext ctx);

   /**
    * Handle receipt of a message on the web socket of a transaction.
    *
    * @param ctx the web socket message context
    */
   void onMessage(WsMessageContext ctx);

   /**
    * Look up the model URI that is being in a transaction's socket context.
    *
    * @param ctx the transaction's socket context
    * @return the model URI that it is editing, or empty if the context is not a transaction session
    *         or it is closed or otherwise invalid
    */
   Optional<URI> getModelURI(WsContext ctx);
}
