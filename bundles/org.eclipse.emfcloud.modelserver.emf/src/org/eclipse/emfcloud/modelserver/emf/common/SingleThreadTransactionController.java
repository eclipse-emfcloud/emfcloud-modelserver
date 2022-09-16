/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

/**
 * A {@link TransactionController} that executes all requests in the same thread, to
 * ensure we use a consistent resource set state.
 */
public class SingleThreadTransactionController implements TransactionController {
   /**
    * Dependency injection name for the actual Transaction Controller implementation,
    * to which the {@link SingleThreadTransactionController} will delegate calls
    * in serial order on a single thread.
    *
    * @see Named
    */
   public static final String TRANSACTION_CONTROLLER_DELEGATE = "TransactionControllerDelegate";

   protected final TransactionController delegate;

   protected final ModelSynchronizer synchronizer;

   @Inject
   public SingleThreadTransactionController(
      final @Named(TRANSACTION_CONTROLLER_DELEGATE) TransactionController delegate,
      final ModelSynchronizer synchronizer) {

      super();

      this.delegate = delegate;
      this.synchronizer = synchronizer;
   }

   //
   // Delegation
   //

   @Override
   public void create(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.create(ctx, modeluri));
   }

   @Override
   public void onOpen(final WsConnectContext ctx) {
      synchronizer.syncExec(() -> delegate.onOpen(ctx));
   }

   @Override
   public void onClose(final WsCloseContext ctx) {
      synchronizer.syncExec(() -> delegate.onClose(ctx));
   }

   @Override
   public void onError(final WsErrorContext ctx) {
      synchronizer.syncExec(() -> delegate.onError(ctx));
   }

   @Override
   public void onMessage(final WsMessageContext ctx) {
      synchronizer.syncExec(() -> delegate.onMessage(ctx));
   }

   @Override
   public Optional<URI> getModelURI(final WsContext ctx) {
      Optional<URI> result = synchronizer.syncCall(() -> delegate.getModelURI(ctx));
      return result == null ? Optional.empty() : result;
   }

}
