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
package org.eclipse.emfcloud.modelserver.client.v2;

import java.net.MalformedURLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.AbstractModelServerClient;
import org.eclipse.emfcloud.modelserver.client.EditingContext;
import org.eclipse.emfcloud.modelserver.client.ModelServerClientApi;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.ModelServerPaths;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.internal.client.EditingContextImpl;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableSet;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;

public class ModelServerClientV2 extends AbstractModelServerClient
   implements ModelServerClientApi<EObject>, ModelServerPaths {

   public static final Set<String> DEFAULT_SUPPORTED_FORMATS = ImmutableSet.<String> builder()
      .addAll(AbstractModelServerClient.DEFAULT_SUPPORTED_FORMATS)
      .add(ModelServerPathParametersV2.FORMAT_JSON_V2)
      .build();

   public static final String PATCH = "PATCH";
   public static final String POST = "POST";

   protected static final Logger LOG = LogManager.getLogger(ModelServerClientV2.class);

   public ModelServerClientV2(final String baseUrl, final EPackageConfiguration... configurations)
      throws MalformedURLException {
      super(baseUrl, ModelServerPathParametersV2.FORMAT_JSON_V2, configurations);
   }

   public ModelServerClientV2(final OkHttpClient client, final String baseUrl,
      final EPackageConfiguration... configurations) throws MalformedURLException {

      super(client, baseUrl, ModelServerPathParametersV2.FORMAT_JSON_V2, configurations);
   }

   @Override
   protected boolean isSupportedFormat(final String format) {
      return DEFAULT_SUPPORTED_FORMATS.contains(format);
   }

   @Override
   public String encode(final EObject eObject, final String format) {
      try {
         if (format.equals(ModelServerPathParametersV2.FORMAT_JSON_V2)) {
            return new JsonCodecV2().encode(eObject).toString();
         }
         return super.encode(eObject, format);
      } catch (EncodingException e) {
         LOG.error("Encoding of " + eObject + " with " + format + " format failed");
         throw new RuntimeException(e);
      }
   }

   @Override
   public Optional<EObject> decode(final String payload, final String format) {
      try {
         if (format.equals(ModelServerPathParametersV2.FORMAT_JSON_V2)) {
            return new JsonCodecV2().decode(payload);
         }
         return super.decode(payload, format);
      } catch (DecodingException e) {
         LOG.error("Decoding of " + payload + " with " + format + " format failed");
      }
      return Optional.empty();
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final CCommand command,
      final String format) {
      String checkedFormat = checkedFormat(format);
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV2.FORMAT, checkedFormat)
               .build())
         .patch(
            RequestBody.create(
               Json.object(
                  Json.prop(JsonResponseMember.DATA, Json.text(encode(command, checkedFormat)))).toString(),
               MediaType.parse("application/json")))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final ArrayNode jsonPatch,
      final String format) {
      String checkedFormat = checkedFormat(format);
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV2.FORMAT, checkedFormat)
               .build())
         .patch(
            RequestBody.create(
               Json.object(
                  Json.prop(JsonResponseMember.DATA, Json.text(encode(jsonPatch)))).toString(),
               MediaType.parse("application/json")))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public EditingContext edit() {
      EditingContextImpl<?> result;

      if (!openEditingSockets.isEmpty()) {
         result = openEditingSockets.keySet().iterator().next();
         result.retain();
         return result;
      }

      Request request = new Request.Builder()
         .url(makeWsUrl(MODEL_BASE_PATH))
         .build();
      result = new EditingContextImpl<>(this, ModelServerClientV2::encode);

      final WebSocket socket = client.newWebSocket(request, result);
      openEditingSockets.put(result, socket);

      return result;
   }

   /**
    * Encode a Json Patch (ArrayNode), as a json string.
    *
    * @param node
    *                The Json Patch to encode
    * @return
    *         The encoded json string
    */
   public String encode(final ArrayNode node) {
      return node.toString();
   }

}
