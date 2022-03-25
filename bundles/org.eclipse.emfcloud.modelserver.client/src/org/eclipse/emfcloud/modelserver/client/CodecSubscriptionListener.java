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
package org.eclipse.emfcloud.modelserver.client;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.jsonpatch.util.JsonPatchCodec;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class CodecSubscriptionListener extends TypedSubscriptionListener<EObject> {
   private static Logger LOG = LogManager.getLogger(CodecSubscriptionListener.class);

   public CodecSubscriptionListener(final Codec codec, final EClass modelType) {
      this(codec, modelType, JsonPatchCodec.Factory.DEFAULT);
   }

   public CodecSubscriptionListener(final Codec codec, final EClass modelType,
      final JsonPatchCodec.Factory patchCodecFactory) {
      super(decoder(codec, modelType, patchCodecFactory), BiFunction.class);
   }

   private static BiFunction<String, String, Optional<? extends EObject>> decoder(final Codec codec,
      final EClass modelType, final JsonPatchCodec.Factory patchCodecFactory) {

      final JsonPatchCodec patchCodec = patchCodecFactory.createCodec(codec, modelType);

      return (payload, notificationType) -> {
         try {
            switch (notificationType) {
               case JsonResponseType.INCREMENTALUPDATE:
                  // It's a patch
                  try {
                     JsonNode patch = Json.parse(payload);
                     return patchCodec.decode(patch);
                  } catch (IOException e) {
                     throw new DecodingException(e);
                  }
               default:
                  return codec.decode(payload);
            }
         } catch (DecodingException e) {
            LOG.error("Failed to decode notification", e);
            return Optional.empty();
         }
      };
   }
}
