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
package org.eclipse.emfcloud.modelserver.common.codecs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.jackson.errors.JSONException;
import org.eclipse.emfcloud.jackson.resource.JsonResource;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultJsonCodec implements Codec {

   private final EMFJsonConverter emfJsonConverter = new EMFJsonConverter();

   @Override
   public JsonNode encode(final EObject obj) throws EncodingException {
      // Encapsulate the command in a resource before marshalling it so that internal
      // cross-references are serialized as IDREFs (e.g., "//@objectsToAdd.0") instead
      // of HREFs (e.g., "#//@objectsToAdd.0") which will not resolve in the Model
      // Server on account of the resource with URI "" not existing. And copy the
      // object to ensure isolation of the user's model
      JsonResource resource = new JsonResource(URI.createURI("$marshall.res"), getObjectMapper());
      resource.getContents().add(EcoreUtil.copy(obj));

      return encode(resource.getContents().get(0), getObjectMapper());
   }

   @Override
   public Optional<EObject> decode(final String payload) throws DecodingException {
      return decode(payload, null);
   }

   @Override
   public Optional<EObject> decode(final String payload, final URI workspaceURI) throws DecodingException {
      URI uri = URI.createURI("virtual.json");
      if (workspaceURI != null) {
         uri = uri.resolve(workspaceURI);
      }

      final JsonResource jsonResource = new JsonResource(uri, getObjectMapper());

      try (InputStream input = new ByteArrayInputStream(payload.getBytes())) {
         jsonResource.load(input, null);
      } catch (IOException e) {
         throw new DecodingException(new JSONException(e, JsonLocation.NA));
      }

      return Optional.of(jsonResource.getContents().remove(0));
   }

   public static JsonNode encode(final Object obj) throws EncodingException {
      try {
         ObjectMapper mapper = new ObjectMapper();
         return mapper.valueToTree(obj);
      } catch (IllegalArgumentException ex) {
         throw new EncodingException(ex);
      }
   }

   public static JsonNode encode(final Object obj, final ObjectMapper mapper) throws EncodingException {
      try {
         return mapper.valueToTree(obj);
      } catch (IllegalArgumentException ex) {
         throw new EncodingException(ex);
      }
   }

   protected ObjectMapper getObjectMapper() { return emfJsonConverter.getMapper(); }
}
