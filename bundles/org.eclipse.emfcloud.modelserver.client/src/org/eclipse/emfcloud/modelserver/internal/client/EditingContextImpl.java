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

package org.eclipse.emfcloud.modelserver.internal.client;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emfcloud.modelserver.client.EditingContext;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandCodec;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Implementation of the client's editing context.
 */
public class EditingContextImpl extends WebSocketListener implements EditingContext {

   private final ModelServerClient owner;
   private final CommandCodec codec = new EMFCommandCodec();
   private WebSocket socket;
   private int referenceCount = 1;

   /**
    * Initializes me.
    *
    * @param owner ModelServerClient which owns this EditingContext.
    */
   public EditingContextImpl(final ModelServerClient owner) {
      super();

      this.owner = owner;
   }

   @Override
   public boolean execute(final Command command) throws EncodingException {
      CCommand serializable = codec.serverToClient(command);
      String message = owner.encode(serializable);
      return execute(message);
   }

   @Override
   public boolean execute(final String command) {
      if (socket == null) {
         return false;
      }

      // Wrap the command in a message
      String message = String.format("{data:%s}", command);
      return socket.send(message);
   }

   //
   // WebSocket events
   //

   @Override
   public void onOpen(final WebSocket webSocket, final Response response) {
      this.socket = webSocket;
   }

   @Override
   public void onClosed(final WebSocket webSocket, final int code, final String reason) {
      if (webSocket == this.socket) {
         this.socket = null;
      }
   }

   //
   // Reference counting
   //

   public void retain() {
      referenceCount = referenceCount + 1;
   }

   public boolean release() {
      referenceCount = referenceCount - 1;
      return referenceCount <= 0;
   }

}
