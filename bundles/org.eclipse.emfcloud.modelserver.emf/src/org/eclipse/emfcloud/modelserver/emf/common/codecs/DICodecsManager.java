/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
   private String preferredFormat = ModelServerPathParameters.FORMAT_JSON;

   private final Set<CodecProvider> codecProviders = new LinkedHashSet<>();

   /**
    * Injected constructor.
    *
    * @param codecProviders the CodecProvider
    */
   @Inject
   public DICodecsManager(final Set<CodecProvider> codecProviders) {
      this.codecProviders.addAll(codecProviders);
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
   public Map<String, JsonNode> encode(final String modelUri, final EObject eObject) throws EncodingException {
      Map<String, JsonNode> encodings = new LinkedHashMap<>();
      this.codecProviders.forEach(cp -> {
         cp.getAllFormats().forEach(f -> {
            cp.getCodec(modelUri, f).ifPresent(c -> {
               try {
                  encodings.put(f, c.encode(eObject));
               } catch (EncodingException e) {
                  e.printStackTrace();
               }
            });
         });
      });
      return encodings;
   }

   @Override
   public JsonNode encode(final String modelUri, final Context context, final EObject eObject)
      throws EncodingException {
      return findFormat(modelUri, context.queryParamMap()).encode(eObject);
   }

   @Override
   public JsonNode encode(final String modelUri, final WsContext context, final EObject eObject)
      throws EncodingException {
      return findFormat(modelUri, context.queryParamMap()).encode(eObject);
   }

   @Override
   public Optional<EObject> decode(final String modelUri, final Context context, final String payload)
      throws DecodingException {
      return findFormat(modelUri, context.queryParamMap()).decode(payload);
   }

   @Override
   public Optional<EObject> decode(final Context context, final String payload, final URI workspaceURI)
      throws DecodingException {
      return findFormat(workspaceURI.toString(), context.queryParamMap()).decode(payload, workspaceURI);
   }

   @Override
   public Optional<EObject> decode(final String modelUri, final WsContext context, final String payload)
      throws DecodingException {
      return findCodec(modelUri, context).decode(payload);
   }

   @Override
   public Optional<EObject> decode(final WsContext context, final String payload, final URI workspaceURI)
      throws DecodingException {
      return findCodec(workspaceURI.toString(), context).decode(payload, workspaceURI);
   }

   @Override
   public String findFormat(final String modelUri, final WsContext context) {
      String format = context.queryParam(ModelServerPathParametersV1.FORMAT);
      if (format != null) {
         return format;
      }
      return ModelServerPathParametersV1.FORMAT_JSON;
   }

   @Override
   public Codec findCodec(final String modelUri, final WsContext context) {
      String format = findFormat(modelUri, context);
      // return Optional.ofNullable(formatToCodec.get(format)).orElseGet(JsonCodec::new);
      return getCodec(modelUri, format).orElseGet(JsonCodec::new);
   }

   protected Codec findFormat(final String modelUri, final Map<String, List<String>> queryParams) {
      String format = ContextRequest.getParam(queryParams, ModelServerPathParametersV1.FORMAT).orElse(preferredFormat);
      return getCodec(modelUri, format).orElseGet(JsonCodec::new);
   }

   private Optional<Codec> getCodec(final String modelUri, final String format) {
      Optional<CodecProvider> first = this.codecProviders.stream()
         .sorted((cp1, cp2) -> cp2.getPriority(modelUri, format) - cp1.getPriority(modelUri, format)).findFirst();
      if (first.isEmpty()) {
         return Optional.empty();
      }
      return first.get().getCodec(modelUri, format);

   }

   @Inject(optional = true)
   public void setPreferredFormat(@Named(PREFERRED_FORMAT) final String format) { this.preferredFormat = format; }
}
