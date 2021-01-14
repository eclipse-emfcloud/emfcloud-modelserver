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
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class DICodecsManager implements CodecsManager {

   /** The format we prefer to use. */
   @Inject(optional = true)
   @Named(PREFERRED_FORMAT)
   private final String preferredFormat = ModelServerPathParameters.FORMAT_JSON;

   private final Map<String, Codec> formatToCodec = new LinkedHashMap<>();

   /**
    * Get the map of known formats and their corresponding codecs.
    * This method can be overridden if necessary, or simply invoked in a subclass to update the map.
    *
    * @return map with formats and codecs
    */
   protected Map<String, Codec> getFormatToCodec() { return formatToCodec; }

   /**
    * Injected constructor.
    *
    * @param codecs the codecs per format
    */
   @Inject
   public DICodecsManager(final Map<String, Codec> codecs) {
      formatToCodec.putAll(codecs);
   }

   /**
    * Get the format we prefer to use.
    * This format should have a corresponding Codec in the manager, but it's not guaranteed when binding is overridden.
    *
    * @return preferred format value
    */
   @Override
   public String getPreferredFormat() { return preferredFormat; }

   @Override
   public Map<String, JsonNode> encode(final EObject eObject) throws EncodingException {
      Map<String, JsonNode> encodings = new LinkedHashMap<>();
      formatToCodec.forEach((key, codec) -> {
         try {
            encodings.put(key, codec.encode(eObject));
         } catch (EncodingException e) {
            e.printStackTrace();
         }
      });
      return encodings;
   }

   @Override
   public JsonNode encode(final Context context, final EObject eObject) throws EncodingException {
      return findFormat(context.queryParamMap()).encode(eObject);
   }

   @Override
   public JsonNode encode(final WsContext context, final EObject eObject) throws EncodingException {
      return findFormat(context.queryParamMap()).encode(eObject);
   }

   @Override
   public Optional<EObject> decode(final Context context, final String payload) throws DecodingException {
      return findFormat(context.queryParamMap()).decode(payload);
   }

   @Override
   public Optional<EObject> decode(final Context context, final String payload, final URI workspaceURI)
      throws DecodingException {
      return findFormat(context.queryParamMap()).decode(payload, workspaceURI);
   }

   @Override
   public String findFormat(final WsContext context) {
      return context.queryParam(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON);
   }

   protected Codec findFormat(final Map<String, List<String>> queryParams) {
      String format = ContextRequest.getParam(queryParams, ModelServerPathParametersV1.FORMAT).orElse(preferredFormat);
      return Optional.ofNullable(formatToCodec.get(format)).orElseGet(JsonCodec::new);
   }
}
