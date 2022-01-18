/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emf.ecore.change.ChangeDescription;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;

public interface SessionController extends ModelListener {
   long NO_TIMEOUT = -1;

   boolean subscribe(WsContext client, String modeluri);

   boolean unsubscribe(WsContext client);

   boolean handleMessage(WsMessageContext clientMessage);

   boolean hasSession(String modeluri);

   /**
    * Subscribes a client to model changes. In V2, model changes will
    * be described via the JsonPatch format, rather than EMF {@link ChangeDescription}.
    *
    * @param ctx
    *                    The Websocket Context representing the client connection.
    * @param modeluri
    *                    The URI of the model to subscribe to.
    * @return
    *         <code>true</code> if the subscription was successful, <code>false<code> otherwise.
    */
   default boolean subscribeV2(final WsContext ctx, final String modeluri) {
      throw new UnsupportedOperationException("V2 API is not supported by this implementation");
   }
}
