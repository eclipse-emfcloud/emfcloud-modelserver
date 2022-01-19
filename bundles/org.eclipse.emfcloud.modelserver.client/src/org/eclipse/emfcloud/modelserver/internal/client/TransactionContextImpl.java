/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.emfcloud.modelserver.internal.client;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emfcloud.modelserver.client.TransactionContext;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonRequest;

import com.fasterxml.jackson.databind.JsonNode;

import okhttp3.WebSocket;

/**
 * Implementation of the client's transaction context.
 */
public class TransactionContextImpl<CLIENT> extends EditingContextImpl<CLIENT> implements TransactionContext {

   protected static Logger LOG = Logger.getLogger(TransactionContextImpl.class.getSimpleName());

   private final String clientID;
   private final Consumer<? super TransactionContextImpl<? extends CLIENT>> onCloseCallback;

   /**
    * Initializes me.
    *
    * @param owner ModelServerClient which owns this EditingContext.
    */
   public TransactionContextImpl(final CLIENT owner, final String clientID,
      final BiFunction<? super CLIENT, ? super CCommand, String> encoder,
      final Consumer<? super TransactionContextImpl<? extends CLIENT>> onCloseCallback) {

      super(owner, encoder);

      this.clientID = clientID;
      this.onCloseCallback = onCloseCallback;
   }

   public String getClientID() { return clientID; }

   @Override
   public void close() {
      send(JsonRequest.close());
   }

   @Override
   public void rollback(final Diagnostic error) {
      try {
         JsonNode jsonData = DefaultJsonCodec.encode(error);
         send(JsonRequest.rollback(jsonData));
      } catch (EncodingException e) {
         LOG.error("Failed to send rollback message.", e);
      }
   }

   @Override
   protected void handleClosed(final WebSocket webSocket, final int code, final String reason) {
      super.handleClosed(webSocket, code, reason);

      onCloseCallback.accept(this);
   }

}
