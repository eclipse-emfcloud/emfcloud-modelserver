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
package org.eclipse.emfcloud.modelserver.client.v1;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.AbstractModelServerClient;
import org.eclipse.emfcloud.modelserver.client.EditingContext;
import org.eclipse.emfcloud.modelserver.client.ModelServerClientApiV1;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.internal.client.EditingContextImpl;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;

public class ModelServerClientV1 extends AbstractModelServerClient
   implements ModelServerClientApiV1<EObject>, ModelServerPathsV1 {

   public static final Set<String> DEFAULT_SUPPORTED_FORMATS = AbstractModelServerClient.DEFAULT_SUPPORTED_FORMATS;

   protected static final Logger LOG = LogManager.getLogger(ModelServerClientV1.class);

   public ModelServerClientV1(final String baseUrl, final EPackageConfiguration... configurations)
      throws MalformedURLException {

      super(baseUrl, ModelServerPathParametersV1.FORMAT_JSON, configurations);
   }

   public ModelServerClientV1(final OkHttpClient client, final String baseUrl,
      final EPackageConfiguration... configurations) throws MalformedURLException {

      super(client, baseUrl, ModelServerPathParametersV1.FORMAT_JSON, configurations);
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final CCommand command,
      final String format) {
      String checkedFormat = checkedFormat(format);
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(EDIT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
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
   public EditingContext edit() {
      EditingContextImpl<?> result;

      if (!openEditingSockets.isEmpty()) {
         result = openEditingSockets.keySet().iterator().next();
         result.retain();
         return result;
      }

      Request request = new Request.Builder()
         .url(makeWsUrl(EDIT))
         .build();
      result = new EditingContextImpl<>(this, ModelServerClientV1::encode);

      final WebSocket socket = client.newWebSocket(request, result);
      openEditingSockets.put(result, socket);

      return result;
   }

}
