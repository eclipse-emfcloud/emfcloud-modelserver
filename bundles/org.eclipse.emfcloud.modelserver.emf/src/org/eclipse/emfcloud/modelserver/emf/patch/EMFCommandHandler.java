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
package org.eclipse.emfcloud.modelserver.emf.patch;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.patch.PatchCommand;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class EMFCommandHandler implements PatchCommandHandler {

   @Inject
   private CodecsManager codecs;

   @Override
   public boolean handles(final String type) {
      return ModelServerPathParametersV2.EMF_COMMAND.equals(type);
   }

   @Override
   public PatchCommand<CCommand> getPatchCommand(final Context ctx, final JsonNode payload) {
      return getPatchCommand(ctx, payload, codecs::decode);
   }

   @Override
   public PatchCommand<?> getPatchCommand(final WsContext ctx, final JsonNode payload) {
      return getPatchCommand(ctx, payload, codecs::decode);
   }

   private <C> PatchCommand<CCommand> getPatchCommand(final C ctx, final JsonNode payload, final Decoder<C> decoder) {
      try {
         Optional<EObject> decodedCCommand = decoder.decode(ctx, new ObjectMapper().writeValueAsString(payload));
         if (decodedCCommand.isPresent() && decodedCCommand.get() instanceof CCommand) {
            return new PatchCommand<>() {

               @Override
               public String getType() { return ModelServerPathParametersV2.EMF_COMMAND; }

               @Override
               public CCommand getData() { return (CCommand) decodedCCommand.get(); }

            };

         }
      } catch (JsonProcessingException | DecodingException e) {
         e.printStackTrace();
      }

      return null;
   }

   @FunctionalInterface
   private interface Decoder<C> {
      Optional<EObject> decode(C context, String payload) throws DecodingException;
   }

}
