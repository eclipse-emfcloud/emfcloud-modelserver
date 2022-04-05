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

import static org.eclipse.emfcloud.modelserver.common.APIVersion.API_V2;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;
import org.eclipse.emfcloud.modelserver.jsonpatch.util.JsonPatchCodec;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * An implementation of a {@link Codec}-based subscription listener that provides message
 * payloads, whereapplicable, to subclass call-back hooks as {@link EObject}s.
 * </p>
 * <p>
 * For API version 2 or later, this includes support for parsing {@link JsonPatch}es that
 * are the descriptions of incremental model updates for edits, undos, and redos.
 * The parsing of JSON patches occasionally requires inference of the {@link EClass}
 * of {@link EObject} values where the type information is not explicit in the patch but
 * can be determined from the model root class and the {@link Operation#getPath() path}
 * of the operation.
 * </p>
 */
public class EObjectSubscriptionListener extends TypedSubscriptionListener<EObject> {
   private static Logger LOG = LogManager.getLogger(EObjectSubscriptionListener.class);

   private final ListenerState state;

   public EObjectSubscriptionListener(final Codec codec) {
      this(codec, JsonPatchCodec.Factory.DEFAULT);
   }

   public EObjectSubscriptionListener(final Codec codec, final JsonPatchCodec.Factory patchCodecFactory) {
      this(codec, patchCodecFactory, new ListenerState());
   }

   private EObjectSubscriptionListener(final Codec codec, final JsonPatchCodec.Factory patchCodecFactory,
      final ListenerState state) {

      super(decoder(codec, state, patchCodecFactory), BiFunction.class);

      this.state = state;
   }

   @Override
   public void onIncrementalUpdate(final EObject incrementalUpdate) {
      if (incrementalUpdate instanceof JsonPatch) {
         onIncrementalUpdate((JsonPatch) incrementalUpdate);
      } else if (incrementalUpdate instanceof CCommandExecutionResult) {
         onIncrementalUpdate((CCommandExecutionResult) incrementalUpdate);
      } else {
         String type = String.valueOf(incrementalUpdate == null || incrementalUpdate.eClass() == null ? null
            : EcoreUtil.getURI(incrementalUpdate.eClass()));
         throw new IllegalArgumentException("Unexpected incremental update type: " + type);
      }
   }

   public void onIncrementalUpdate(final JsonPatch patch) {}

   public void onIncrementalUpdate(final CCommandExecutionResult commandExecutionResult) {}

   /**
    * Set a supplier that can provide the model root object type when and if required.
    * <em>Not needed for API v1.</em>
    *
    * @param modelTypeSupplier the model type supplier
    */
   public final void setModelTypeSupplier(final Supplier<? extends EClass> modelTypeSupplier) {
      this.state.setModelTypeSupplier(modelTypeSupplier);
   }

   /**
    * Set the API version of the subscription to which this listener is attached.
    *
    * @param apiVersion the API version
    */
   public final void setAPIVersion(final APIVersion apiVersion) {
      this.state.setAPIVersion(apiVersion);
   }

   /**
    * An externalization of the state of the listener employed because the listener instance cannot pass
    * itself to a helper class in a call to its own constructor.
    */
   private static final class ListenerState {

      private Supplier<? extends EClass> modelTypeSupplier;

      private APIVersion apiVersion = APIVersion.ZERO;

      EClass getModelType() { return modelTypeSupplier == null ? null : modelTypeSupplier.get(); }

      void setModelTypeSupplier(final Supplier<? extends EClass> modelTypeSupplier) {
         this.modelTypeSupplier = modelTypeSupplier;
      }

      APIVersion getAPIVersion() { return apiVersion; }

      BooleanSupplier is(final APIVersionRange apiVersionRange) {
         return () -> apiVersionRange.includes(getAPIVersion());
      }

      void setAPIVersion(final APIVersion apiVersion) { this.apiVersion = apiVersion; }
   }

   private static BiFunction<String, String, Optional<? extends EObject>> decoder(final Codec codec,
      final ListenerState state, final JsonPatchCodec.Factory patchCodecFactory) {

      final JsonPatchCodec patchCodec = patchCodecFactory.createCodec(codec, state::getModelType);
      final BooleanSupplier isIncrementalUpdateAJsonPatch = state.is(API_V2.range());

      return (payload, notificationType) -> {
         try {
            switch (notificationType) {
               case JsonResponseType.INCREMENTALUPDATE:
                  if (isIncrementalUpdateAJsonPatch.getAsBoolean()) {
                     // It's a patch
                     try {
                        JsonNode patch = Json.parse(payload);
                        return patchCodec.decode(patch);
                     } catch (IOException e) {
                        throw new DecodingException(e);
                     }
                  }

                  // It's a CommandExecutionResult, requiring no special parsing
                  return codec.decode(payload);
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
