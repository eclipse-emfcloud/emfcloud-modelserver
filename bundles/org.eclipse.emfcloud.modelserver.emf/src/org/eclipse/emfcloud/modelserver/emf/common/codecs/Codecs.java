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
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class Codecs {

   /** The constant to tell which is the preferred formats in codecs. */
   public static final String PREFERRED_FORMAT = "PreferredFormat";

   @Inject(optional = true)
   @Named(PREFERRED_FORMAT)
   private final String preferredFormat = ModelServerPathParameters.FORMAT_JSON;

   private final Map<String, Codec> formatToCodec = new LinkedHashMap<>();

   /**
    * Legacy constructor in case users were instanciating Codecs class manually.
    * Prefer injecting it with the correct guice bindings.
    *
    * @deprecated
    */
   @Deprecated
   public Codecs() {

      formatToCodec.put(ModelServerPathParameters.FORMAT_XMI, new XmiCodec());
      formatToCodec.put(ModelServerPathParameters.FORMAT_JSON, new JsonCodec());
   }

   /**
    * Injected constructor.
    *
    * @param emfCodecs the emf codecs per format
    */
   @Inject
   public Codecs(final Map<String, Codec> emfCodecs) {
      formatToCodec.putAll(emfCodecs);
   }

   public JsonNode encode(final Context context, final EObject eObject) throws EncodingException {
      return findFormat(context.queryParamMap()).encode(eObject);
   }

   public JsonNode encode(final WsContext context, final EObject eObject) throws EncodingException {
      return findFormat(context.queryParamMap()).encode(eObject);
   }

   public Optional<EObject> decode(final Context context, final String payload) throws DecodingException {
      return findFormat(context.queryParamMap()).decode(payload);
   }

   public Optional<EObject> decode(final Context context, final String payload, final URI workspaceURI)
      throws DecodingException {
      return findFormat(context.queryParamMap()).decode(payload, workspaceURI);
   }

   private Codec findFormat(final Map<String, List<String>> queryParams) {
      return Optional
         .ofNullable(queryParams.get(ModelServerPathParameters.FORMAT))
         .filter(list -> !list.isEmpty())
         .flatMap(f -> Optional.ofNullable(formatToCodec.get(f.get(0))))
         .orElseGet(() -> Optional.ofNullable(formatToCodec.get(preferredFormat)).orElseGet(() -> new JsonCodec()));
   }
}
