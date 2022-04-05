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
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * A specialized codec for transforming JSON wire payloads to and from an {@link EObject} representation,
 * including object values in <tt>add</tt>, <tt>replace</tt>, and <tt>test</tt> operations.
 */
public interface JsonPatchCodec {

   /**
    * Decode a JSON patch as an EMF representation of the same. Any cross-references between
    * object values in the patch will be unresolved proxies, but of course they may be resolved
    * at any time later in the context of the appropriate resource set.
    *
    * @param patch a JSON patch incoming from the wire
    * @return the decoded EMF model of the {@code patch}, if possible
    *
    * @throws DecodingException on failure to deserialize any patch content to EMF objects
    */
   Optional<JsonPatch> decode(JsonNode patch) throws DecodingException;

   /**
    * Encode the EMF representation of a patch as JSON for delivery over the wire.
    *
    * @param patch an EMF model of a JSON Patch
    * @return the JSON encoding of the {@code patch}
    * @throws EncodingException on failure to serialize any patch content to JSON
    */
   ArrayNode encode(JsonPatch patch) throws EncodingException;

   //
   // Nested types
   //

   /**
    * Protocol for creation of JSON Patch codecs.
    */
   interface Factory {
      /** A factory of the default codec implementation. */
      Factory DEFAULT = new DefaultJsonPatchCodecFactory();

      /**
       * Create a patch codec that uses the given {@code codec} to decode object values.
       *
       * @param codec     the codec for decoding object values in add/replace/test operations
       * @param modelType a supplier of the type of the root model object, required for inference of missing type
       *                     properties in object values but which may not be known at the time of creation of
       *                     the codec
       * @return the patch codec
       */
      JsonPatchCodec createCodec(Codec codec, Supplier<? extends EClass> modelType);

   }

}
