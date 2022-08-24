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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class DICodecsManager implements CodecsManager {

   protected static final Logger LOG = LogManager.getLogger(DICodecsManager.class);
   /** The format we prefer to use. If {@code null}, then the preferred format is inferred from the API version. */
   private String preferredFormat;

   private final Set<CodecProvider> codecProviders = new LinkedHashSet<>();

   /** In support of the deprecated {@link #findCodec(String, Map)} method, only. */
   private final ThreadLocal<Context> currentContext = new ThreadLocal<>();

   @Inject
   private ModelURIConverter uriConverter;

   public DICodecsManager() {
      super();
   }

   @Inject
   private void addCodecProviders(final Set<CodecProvider> codecProviders) {
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
      HashSet<String> uniqueFormats = this.codecProviders.stream().collect(HashSet::new,
         (acc, cp) -> acc.addAll(cp.getAllFormats()), Set<String>::addAll);
      uniqueFormats.stream().forEach(f -> {
         CodecProvider.getCodec(codecProviders, modelUri, f).map(c -> {
            try {
               return c.encode(eObject);
            } catch (EncodingException e) {
               LOG.error(e.getMessage(), e);
            }
            return null;
         }).ifPresent(n -> encodings.put(f, n));
      });
      return encodings;
   }

   @Override
   public JsonNode encode(final String modelUri, final Context context, final EObject eObject)
      throws EncodingException {
      return findCodec(modelUri, context).encode(eObject);
   }

   @Override
   public JsonNode encode(final String modelUri, final WsContext context, final EObject eObject)
      throws EncodingException {
      return findCodec(modelUri, context).encode(eObject);
   }

   @Override
   public Optional<EObject> decode(final String modelUri, final Context context, final String payload)
      throws DecodingException {
      return findCodec(modelUri, context).decode(payload);
   }

   @Override
   public Optional<EObject> decode(final Context context, final String payload, final URI workspaceURI)
      throws DecodingException {

      URI modelUri = uriConverter.resolveModelURI(context).orElse(workspaceURI);
      return findCodec(modelUri.toString(), context).decode(payload, workspaceURI);
   }

   @Override
   public Optional<EObject> decode(final String modelUri, final WsContext context, final String payload)
      throws DecodingException {
      return findCodec(modelUri, context).decode(payload);
   }

   @Override
   public Optional<EObject> decode(final WsContext context, final String payload, final URI workspaceURI)
      throws DecodingException {

      URI modelUri = uriConverter.resolveModelURI(context).orElse(workspaceURI);
      return findCodec(modelUri.toString(), context).decode(payload, workspaceURI);
   }

   @Override
   public String findFormat(final WsContext context) {
      String format = context.queryParam(ModelServerPathParametersV1.FORMAT);
      if (format != null) {
         return format;
      }
      return ModelServerPathParametersV1.FORMAT_JSON;
   }

   public Codec findCodec(final String modelUri, final Context context) {
      // We used the deprecated method, so delegate to it still
      Context previous = currentContext.get();
      currentContext.set(context);
      try {
         return findCodec(modelUri, context.queryParamMap());
      } finally {
         currentContext.set(previous);
      }
   }

   @Override
   public Codec findCodec(final String modelUri, final WsContext context) {
      String format = findFormat(context);
      return getCodec(modelUri, format).orElseGet(JsonCodec::new);
   }

   /**
    * @deprecated Override the {@link #findCodec(String, Context)} method, instead.
    */
   @Deprecated
   protected Codec findCodec(final String modelUri, final Map<String, List<String>> queryParams) {
      String format = ContextRequest.getParam(queryParams, ModelServerPathParametersV1.FORMAT).orElseGet(
         () -> Optional.ofNullable(currentContext.get()).map(this::getPreferredFormat).orElse(preferredFormat));
      return getCodec(modelUri, format).orElseGet(JsonCodec::new);
   }

   private Optional<Codec> getCodec(final String modelUri, final String format) {
      return CodecProvider.getCodec(codecProviders, modelUri, format);
   }

   @Inject(optional = true)
   public void setPreferredFormat(@Named(PREFERRED_FORMAT) final String format) { this.preferredFormat = format; }
}
