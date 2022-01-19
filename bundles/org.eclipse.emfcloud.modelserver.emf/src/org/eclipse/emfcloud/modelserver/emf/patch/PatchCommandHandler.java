/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.patch;

import java.util.Optional;
import java.util.Set;

import org.eclipse.emfcloud.modelserver.common.patch.PatchCommand;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public interface PatchCommandHandler {
   boolean handles(String type);

   PatchCommand<?> getPatchCommand(Context ctx, JsonNode payload);

   PatchCommand<?> getPatchCommand(WsContext ctx, JsonNode payload);

   @ImplementedBy(RegistryImpl.class)
   interface Registry {
      Optional<PatchCommandHandler> getPatchCommandHandler(Context ctx, String type);

      Optional<PatchCommandHandler> getPatchCommandHandler(WsContext ctx, String type);

      Optional<PatchCommand<?>> getPatchCommand(Context ctx, JsonNode json);

      Optional<PatchCommand<?>> getPatchCommand(WsContext ctx, JsonNode json);
   }

   class RegistryImpl implements Registry {

      private Set<PatchCommandHandler> handlers;

      @Inject(optional = true)
      public void setPatchCommandHandlers(final Set<PatchCommandHandler> handlers) { this.handlers = handlers; }

      @Override
      public Optional<PatchCommandHandler> getPatchCommandHandler(final Context ctx, final String type) {
         return handlers.stream().filter(handler -> handler.handles(type)).findAny();
      }

      @Override
      public Optional<PatchCommandHandler> getPatchCommandHandler(final WsContext ctx, final String type) {
         return handlers.stream().filter(handler -> handler.handles(type)).findAny();
      }

      @Override
      public Optional<PatchCommand<?>> getPatchCommand(final Context ctx, final JsonNode patch) {
         String patchType = patch.get("type").asText();
         Optional<PatchCommandHandler> patchCommandHandler = getPatchCommandHandler(ctx, patchType);
         Optional<PatchCommand<?>> patchCommand = patchCommandHandler
            .map(handler -> handler.getPatchCommand(ctx, patch.get("data")));
         return patchCommand;
      }

      @Override
      public Optional<PatchCommand<?>> getPatchCommand(final WsContext ctx, final JsonNode patch) {
         String patchType = patch.get("type").asText();
         Optional<PatchCommandHandler> patchCommandHandler = getPatchCommandHandler(ctx, patchType);
         Optional<PatchCommand<?>> patchCommand = patchCommandHandler
            .map(handler -> handler.getPatchCommand(ctx, patch.get("data")));
         return patchCommand;
      }

   }
}
