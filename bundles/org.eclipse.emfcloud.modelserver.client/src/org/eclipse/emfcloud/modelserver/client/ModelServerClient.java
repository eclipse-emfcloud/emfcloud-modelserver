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
package org.eclipse.emfcloud.modelserver.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPaths;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.DefaultCommandCodec;
import org.eclipse.emfcloud.modelserver.internal.client.EditingContextImpl;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ModelServerClient implements ModelServerClientApi<EObject>, ModelServerPaths {

   private static final Set<String> SUPPORTED_FORMATS = ImmutableSet.of("json", "xmi");
   private static final String PATCH = "PATCH";
   private static final String POST = "POST";

   private static Logger LOG = Logger.getLogger(ModelServerClient.class.getSimpleName());

   private final OkHttpClient client;
   private final String baseUrl;
   private final Map<String, WebSocket> openSockets = new LinkedHashMap<>();
   private final Map<EditingContextImpl, WebSocket> openEditingSockets = new LinkedHashMap<>();

   public ModelServerClient(final String baseUrl) throws MalformedURLException {
      this(new OkHttpClient(), baseUrl);
   }

   public ModelServerClient(final OkHttpClient client, final String baseUrl) throws MalformedURLException {
      this.client = client;
      this.baseUrl = new URL(baseUrl).toString();
   }

   public void close() {
      client.dispatcher().executorService().shutdown();
      client.connectionPool().evictAll();
   }

   @Override
   public CompletableFuture<Response<String>> get(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter("modeluri", modelUri)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<EObject>> get(final String modelUri, final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("format", checkedFormat)
               .build())
         .build();

      return call(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   private CompletableFuture<Response<Optional<String>>> call(final Request request) {
      return makeCall(request)
         .thenApply(response -> parseField(response, "data"));
   }

   @Override
   public CompletableFuture<Response<List<String>>> getAll() {
      final Request request = new Request.Builder()
         .url(makeUrl(MODEL_URIS))
         .build();

      return call(request)
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> {
            List<String> uris = new ArrayList<>();
            try {
               Json.parse(body).forEach(uri -> uris.add(uri.textValue()));
               return uris;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         }));
   }

   @Override
   public CompletableFuture<Response<String>> getModelElementById(final String modelUri, final String elementid) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("elementid", elementid)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementById(final String modelUri, final String elementid,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("elementid", elementid)
               .addQueryParameter("format", checkedFormat)
               .build())
         .build();

      return call(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<String>> getModelElementByName(final String modelUri, final String elementname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("elementname", elementname)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementByName(final String modelUri, final String elementname,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("elementname", elementname)
               .addQueryParameter("format", checkedFormat)
               .build())
         .build();

      return call(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<Boolean>> delete(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter("modeluri", modelUri)
               .build())
         .delete()
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "type"))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals("success")));
   }

   @Override
   public CompletableFuture<Response<String>> create(final String modelUri, final String createdModelAsJsonText) {
      TextNode dataNode = Json.text(createdModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, POST, "json", dataNode);

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<EObject>> create(final String modelUri, final EObject createdModel,
      final String format) {
      return createOrUpdateModel(modelUri, createdModel, format, POST);
   }

   private CompletableFuture<Response<EObject>> createOrUpdateModel(final String modelUri, final EObject model,
      final String format,
      final String httpMethod) {
      String checkedFormat = checkedFormat(format);
      TextNode dataNode = Json.text(encode(model, checkedFormat));
      final Request request = buildCreateOrUpdateRequest(modelUri, httpMethod, checkedFormat, dataNode);

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @NotNull
   private Request buildCreateOrUpdateRequest(final String modelUri, final String httpMethod,
      final String checkedFormat,
      final TextNode dataNode) {
      return new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("format", checkedFormat)
               .build())
         .method(httpMethod,
            RequestBody.create(
               Json.object(
                  Json.prop("data", dataNode)).toString(),
               MediaType.parse("application/json")))
         .build();
   }

   @Override
   public CompletableFuture<Response<String>> update(final String modelUri, final String updatedModelAsJsonText) {
      TextNode dataNode = Json.text(updatedModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, PATCH, "json", dataNode);

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<EObject>> update(final String modelUri, final EObject updatedModel,
      final String format) {
      return createOrUpdateModel(modelUri, updatedModel, format, PATCH);
   }

   private String checkedFormat(final String format) {
      if (Strings.isNullOrEmpty(format)) {
         return "json";
      }

      String result = format.toLowerCase();

      if (!isSupportedFormat(result)) {
         throw new CancellationException("Unsupported format " + format);
      }

      return result;
   }

   private boolean isSupportedFormat(final String format) {
      return SUPPORTED_FORMATS.contains(format);
   }

   @Override
   public CompletableFuture<Response<Boolean>> save(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(SAVE))
               .addQueryParameter("modeluri", modelUri)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "type"))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals("success")));
   }

   @Override
   public CompletableFuture<Response<String>> getTypeSchema(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(TYPE_SCHEMA))
               .addQueryParameter("modeluri", modelUri)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<String>> getUISchema(final String schemaname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(UI_SCHEMA))
               .addQueryParameter("schemaname", schemaname)
               .build())
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "data"))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<Boolean>> configure(final ServerConfiguration configuration) {

      ObjectNode config = Json.object(
         Json.prop("workspaceRoot", Json.text(configuration.getWorkspaceRoot())),
         Json.prop("uiSchemaFolder", Json.text(configuration.getUiSchemaFolder())));

      final Request request = new Request.Builder()
         .url(makeUrl(SERVER_CONFIGURE))
         .put(RequestBody.create(config.toString(), MediaType.parse("application/json")))
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "type"))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals("success")));
   }

   @Override
   public CompletableFuture<Response<Boolean>> ping() {
      final Request request = new Request.Builder()
         .url(makeUrl(SERVER_PING))
         .build();

      return makeCall(request)
         .thenApply(response -> parseField(response, "type"))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals("success")));
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final Command command) {
      return edit(modelUri, command, "json");
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final Command command, final String format) {
      CCommand encoded;
      try {
         encoded = new DefaultCommandCodec().encode(command);
      } catch (EncodingException e) {
         LOG.error("Encoding of " + command + " failed: " + e.getMessage());
         throw new IllegalArgumentException(e);
      }
      return edit(modelUri, encoded, format);
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final CCommand command,
      final String format) {
      String checkedFormat = checkedFormat(format);
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(EDIT))
               .addQueryParameter("modeluri", modelUri)
               .addQueryParameter("format", checkedFormat)
               .build())
         .patch(
            RequestBody.create(
               Json.object(
                  Json.prop("data", Json.text(encode(command, checkedFormat)))).toString(),
               MediaType.parse("application/json")))
         .build();
      return makeCall(request)
         .thenApply(response -> parseField(response, "type"))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals("success")));
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter("modeluri", modelUri)
                  .addQueryParameter("format", checkedFormat)
                  .build()
                  .toString()))
         .build();

      @SuppressWarnings({ "checkstyle:AnonInnerLength" })
      final WebSocket socket = client.newWebSocket(request, new WebSocketListener() {
         @Override
         public void onOpen(@NotNull final WebSocket webSocket, @NotNull final okhttp3.Response response) {
            subscriptionListener.onOpen(new Response<>(response,
               body -> require(Optional.ofNullable(body))));
         }

         @Override
         public void onMessage(@NotNull final WebSocket webSocket, @NotNull final String text) {
            Optional<String> type = ModelServerClient.this.parseJsonField(text, "type");
            Optional<String> data = ModelServerClient.this.parseJsonField(text, "data");
            subscriptionListener.onNotification(new ModelServerNotification(type.orElse("unknown"), data));
         }

         @Override
         public void onClosing(@NotNull final WebSocket webSocket, final int code, @NotNull final String reason) {
            subscriptionListener.onClosing(code, reason);
         }

         @Override
         public void onClosed(@NotNull final WebSocket webSocket, final int code, @NotNull final String reason) {
            subscriptionListener.onClosed(code, reason);
         }

         @Override
         public void onFailure(@NotNull final WebSocket webSocket, @NotNull final Throwable t,
            @Nullable final okhttp3.Response response) {
            if (response != null) {
               subscriptionListener.onFailure(t, new Response<>(response));
            } else {
               subscriptionListener.onFailure(t);
            }
         }
      });
      openSockets.put(modelUri, socket);
   }

   @Override
   public boolean unsubscribe(final String modelUri) {
      final WebSocket webSocket = openSockets.get(modelUri);
      if (webSocket != null) {
         return webSocket.close(1000, "Websocket closed by client.");
      }
      return false;
   }

   private String makeWsUrl(final String path) {
      return path.replaceFirst("http:", "ws:");
   }

   private String makeUrl(final String path) {
      return baseUrl + path;
   }

   private HttpUrl.Builder createHttpUrlBuilder(final String path) {
      return Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
   }

   private CompletableFuture<Response<String>> makeCall(final Request request) {
      CompletableFuture<Response<String>> future = new CompletableFuture<>();
      this.client.newCall(request).enqueue(new Callback() {
         @Override
         public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
            future.completeExceptionally(e);
         }

         @Override
         public void onResponse(@NotNull final Call call, @NotNull final okhttp3.Response response) {
            future.complete(new Response<>(response));
         }
      });

      return future;
   }

   private Response<Optional<String>> parseField(final Response<String> response, final String field) {
      return response.mapBody(body -> parseJsonField(body, field));
   }

   private Optional<String> parseJsonField(final String jsonAsString, final String field) {
      try {
         final JsonNode data = Json.parse(jsonAsString).get(field);
         if (data == null) {
            return Optional.empty();
         }
         if (data.isTextual()) {
            return Optional.of(data.textValue());
         }
         return Optional.of(data.toString());
      } catch (IOException e) {
         LOG.error("Could not parse JSON", e);
         return Optional.empty();
      }
   }

   private <A> Response<A> getBodyOrThrow(final Response<Optional<A>> response) {
      return response.mapBody(this::require);
   }

   private <A> A require(final Optional<A> value) {
      return value.orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
   }

   public String encode(final EObject eObject) {
      return encode(eObject, "json");
   }

   public String encode(final EObject eObject, final String format) {
      try {
         if (format.equals("xmi")) {
            return new XmiCodec().encode(eObject).asText();
         }
         return new DefaultJsonCodec().encode(eObject).toString();
      } catch (EncodingException e) {
         LOG.error("Encoding of " + eObject + " with " + format + " format failed");
         throw new RuntimeException(e);
      }
   }

   public Optional<EObject> decode(final String payload) {
      return decode(payload, "json");
   }

   public Optional<EObject> decode(final String payload, final String format) {
      try {
         if (format.equals("xmi")) {
            return new XmiCodec().decode(payload);
         }
         return new DefaultJsonCodec().decode(payload);
      } catch (DecodingException e) {
         LOG.error("Decoding of " + payload + " with " + format + " format failed");
      }
      return Optional.empty();
   }

   @Override
   public EditingContext edit() {
      EditingContextImpl result;

      if (!openEditingSockets.isEmpty()) {
         result = openEditingSockets.keySet().iterator().next();
         result.retain();
         return result;
      }

      Request request = new Request.Builder()
         .url(makeWsUrl(EDIT))
         .build();
      result = new EditingContextImpl(this);

      final WebSocket socket = client.newWebSocket(request, result);
      openEditingSockets.put(result, socket);

      return result;
   }

   @Override
   public boolean close(final EditingContext editingContext) {
      if (((EditingContextImpl) editingContext).release()) {
         final WebSocket webSocket = openEditingSockets.remove(editingContext);
         if (webSocket != null) {
            return webSocket.close(1000, "Websocket closed by client.");
         }
      }
      return false;
   }
}
