/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public class Codecs {

   private final Map<String, Codec> formatToCodec = new LinkedHashMap<>();

   public Codecs() {
      formatToCodec.put("xmi", new XmiCodec());
      formatToCodec.put("json", new JsonCodec());
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
         .ofNullable(queryParams.get("format"))
         .filter(list -> !list.isEmpty())
         .flatMap(f -> Optional.ofNullable(formatToCodec.get(f.get(0))))
         .orElse(new JsonCodec());
   }
}
