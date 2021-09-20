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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPaths;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.emf.configuration.ChangePackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.CommandPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EcorePackageConfiguration;
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

public class ModelServerClient implements ModelServerClientApi<EObject>, ModelServerPaths, AutoCloseable {

   public static final Set<String> DEFAULT_SUPPORTED_FORMATS = ImmutableSet.of(ModelServerPathParameters.FORMAT_JSON,
      ModelServerPathParameters.FORMAT_XMI);
   public static final String PATCH = "PATCH";
   public static final String POST = "POST";

   protected static Logger LOG = Logger.getLogger(ModelServerClient.class.getSimpleName());

   protected final OkHttpClient client;
   protected final String baseUrl;
   protected final Map<String, WebSocket> openSockets = new LinkedHashMap<>();
   protected final Map<EditingContextImpl, WebSocket> openEditingSockets = new LinkedHashMap<>();
   protected final EPackageConfiguration[] configurations;

   public ModelServerClient(final String baseUrl, final EPackageConfiguration... configurations)
      throws MalformedURLException {
      this(new OkHttpClient(), baseUrl, configurations);
   }

   public ModelServerClient(final OkHttpClient client, final String baseUrl,
      final EPackageConfiguration... configurations) throws MalformedURLException {
      this.client = client;
      this.baseUrl = new URL(baseUrl).toString();
      this.configurations = configurations;
      this.init();
   }

   protected void init() {
      initEPackages();
   }

   private void initEPackages() {
      // default packages that should always be present
      EPackageConfiguration.setup(
         new EcorePackageConfiguration(),
         new CommandPackageConfiguration(),
         new ChangePackageConfiguration());
      // custom packages adding or overriding default configurations
      EPackageConfiguration.setup(this.configurations);
   }

   @Override
   public void close() {
      openSockets.keySet().forEach(this::unsubscribe);
      openEditingSockets.keySet().forEach(this::close);
      client.dispatcher().executorService().shutdown();
      client.connectionPool().evictAll();
   }

   @Override
   public CompletableFuture<Response<String>> get(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<EObject>> get(final String modelUri, final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<List<Model<String>>>> getAll() {
      final Request request = new Request.Builder()
         .url(makeUrl(MODEL_BASE_PATH))
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> {
            List<Model<String>> models = new ArrayList<>();
            try {
               for (JsonNode modelNode : Json.parse(body)) {
                  Optional<String> modelUri = getJsonField(modelNode, ModelServerPathParametersV1.MODEL_URI);
                  Optional<String> content = getJsonField(modelNode, "content");
                  if (modelUri.isPresent() && content.isPresent()) {
                     models.add(new Model<>(modelUri.get(), content.get()));
                  } else {
                     LOG.warn("Incomplete Model: " + modelNode);
                  }
               }
               return models;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         }));
   }

   @Override
   public CompletableFuture<Response<List<Model<EObject>>>> getAll(final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, format)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> {
            List<Model<EObject>> models = new ArrayList<>();
            try {
               for (JsonNode modelNode : Json.parse(body)) {
                  Optional<String> modelUri = getJsonField(modelNode, "modelUri");
                  Optional<EObject> content = getJsonField(modelNode, "content")
                     .flatMap(contentText -> decode(contentText, checkedFormat));
                  if (modelUri.isPresent() && content.isPresent()) {
                     models.add(new Model<>(modelUri.get(), content.get()));
                  } else {
                     LOG.warn("Incomplete Model: " + modelNode);
                  }
               }
               return models;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         }));
   }

   @Override
   public CompletableFuture<Response<List<String>>> getModelUris() {
      final Request request = new Request.Builder()
         .url(makeUrl(MODEL_URIS))
         .build();

      return makeCallAndGetDataBody(request)
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
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, elementid)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementById(final String modelUri, final String elementid,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, elementid)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<String>> getModelElementByName(final String modelUri, final String elementname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, elementname)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementByName(final String modelUri, final String elementname,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, elementname)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @Override
   public CompletableFuture<Response<Boolean>> delete(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .delete()
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> close(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(CLOSE))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .post(RequestBody.create(new byte[0]))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<String>> create(final String modelUri, final String createdModelAsJsonText) {
      TextNode dataNode = Json.text(createdModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, POST, ModelServerPathParametersV1.FORMAT_JSON,
         dataNode);

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<EObject>> create(final String modelUri, final EObject createdModel,
      final String format) {
      return createOrUpdateModel(modelUri, createdModel, format, POST);
   }

   protected CompletableFuture<Response<EObject>> createOrUpdateModel(final String modelUri, final EObject model,
      final String format,
      final String httpMethod) {
      String checkedFormat = checkedFormat(format);
      TextNode dataNode = Json.text(encode(model, checkedFormat));
      final Request request = buildCreateOrUpdateRequest(modelUri, httpMethod, checkedFormat, dataNode);

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   @NotNull
   protected Request buildCreateOrUpdateRequest(final String modelUri, final String httpMethod,
      final String checkedFormat,
      final TextNode dataNode) {
      return new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .method(httpMethod,
            RequestBody.create(
               Json.object(
                  Json.prop(JsonResponseMember.DATA, dataNode)).toString(),
               MediaType.parse("application/json")))
         .build();
   }

   @Override
   public CompletableFuture<Response<String>> update(final String modelUri, final String updatedModelAsJsonText) {
      TextNode dataNode = Json.text(updatedModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, PATCH, ModelServerPathParametersV1.FORMAT_JSON,
         dataNode);

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<EObject>> update(final String modelUri, final EObject updatedModel,
      final String format) {
      return createOrUpdateModel(modelUri, updatedModel, format, PATCH);
   }

   protected String checkedFormat(final String format) {
      if (Strings.isNullOrEmpty(format)) {
         return getDefaultFormat();
      }

      String result = format.toLowerCase();

      if (!isSupportedFormat(result)) {
         throw new CancellationException("Unsupported format " + format);
      }

      return result;
   }

   /**
    * Test whether format is supported. You may override this method if your model server has specific format and codecs
    * configuration.
    *
    * @param format the format to test.
    * @return true when supported.
    */
   protected boolean isSupportedFormat(final String format) {
      return DEFAULT_SUPPORTED_FORMATS.contains(format);
   }

   /**
    * Get the default format to use. You may override this method if your model server has specific format and codecs
    * configuration.
    *
    * @return default format
    */
   protected String getDefaultFormat() { return ModelServerPathParametersV1.FORMAT_JSON; }

   @Override
   public CompletableFuture<Response<Boolean>> save(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(SAVE))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> saveAll() {
      final Request request = new Request.Builder()
         .url(makeUrl(SAVE_ALL))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<String>> validate(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(VALIDATION))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCall(request);
   }

   @Override
   public CompletableFuture<Response<String>> getTypeSchema(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(TYPE_SCHEMA))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   @Override
   public CompletableFuture<Response<String>> getUiSchema(final String schemaname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(UI_SCHEMA))
               .addQueryParameter(ModelServerPathParametersV1.SCHEMA_NAME, schemaname)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
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

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> ping() {
      final Request request = new Request.Builder()
         .url(makeUrl(SERVER_PING))
         .build();

      return makeCallAndExpectSuccess(request);
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
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final long timeout) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format,
      final long timeout) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final long timeout) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format,
      final long timeout) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   protected void doSubscribe(final String modelUri, final SubscriptionListener subscriptionListener,
      final Request request) {
      @SuppressWarnings({ "checkstyle:AnonInnerLength" })
      final WebSocket socket = client.newWebSocket(request, new WebSocketListener() {
         @Override
         public void onOpen(@NotNull final WebSocket webSocket, @NotNull final okhttp3.Response response) {
            subscriptionListener.onOpen(new Response<>(response,
               body -> require(Optional.ofNullable(body))));
         }

         @Override
         public void onMessage(@NotNull final WebSocket webSocket, @NotNull final String text) {
            Optional<String> type = ModelServerClient.this.parseJsonField(text, JsonResponseMember.TYPE);
            Optional<String> data = ModelServerClient.this.parseJsonField(text, JsonResponseMember.DATA);
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
   public boolean send(final String modelUri, final String message) {
      final WebSocket webSocket = openSockets.get(modelUri);
      if (webSocket != null) {
         return webSocket.send(message);
      }
      return false;
   }

   @Override
   public boolean unsubscribe(final String modelUri) {
      final WebSocket webSocket = openSockets.get(modelUri);
      if (webSocket != null) {
         return webSocket.close(1000, "Websocket closed by client.");
      }
      return false;
   }

   protected String makeWsUrl(final String path) {
      return path.replaceFirst("http:", "ws:");
   }

   protected String makeUrl(final String path) {
      return baseUrl + path;
   }

   protected HttpUrl.Builder createHttpUrlBuilder(final String path) {
      return Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
   }

   protected CompletableFuture<Response<String>> makeCall(final Request request) {
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

   protected Response<Optional<String>> parseField(final Response<String> response, final String field) {
      return response.mapBody(body -> parseJsonField(body, field));
   }

   protected Optional<String> parseJsonField(final String jsonAsString, final String field) {
      try {
         final JsonNode node = Json.parse(jsonAsString);
         return getJsonField(node, field);
      } catch (IOException e) {
         LOG.error("Could not parse JSON", e);
         LOG.debug("Invalid JSON: " + jsonAsString);
         return Optional.empty();
      }
   }

   protected Optional<String> getJsonField(final JsonNode node, final String field) {
      JsonNode data = node.get(field);
      if (data == null) {
         return Optional.empty();
      }
      if (data.isTextual()) {
         return Optional.of(data.textValue());
      }
      return Optional.of(data.toString());
   }

   protected <A> Response<A> getBodyOrThrow(final Response<Optional<A>> response) {
      return response.mapBody(this::require);
   }

   protected <A> A require(final Optional<A> value) {
      return value.orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
   }

   public String encode(final EObject eObject) {
      return encode(eObject, ModelServerPathParametersV1.FORMAT_JSON);
   }

   public String encode(final EObject eObject, final String format) {
      try {
         if (format.equals(ModelServerPathParametersV1.FORMAT_XMI)) {
            return new XmiCodec().encode(eObject).asText();
         }
         return new DefaultJsonCodec().encode(eObject).toString();
      } catch (EncodingException e) {
         LOG.error("Encoding of " + eObject + " with " + format + " format failed");
         throw new RuntimeException(e);
      }
   }

   public Optional<EObject> decode(final String payload) {
      return decode(payload, ModelServerPathParametersV1.FORMAT_JSON);
   }

   public Optional<EObject> decode(final String payload, final String format) {
      try {
         if (format.equals(ModelServerPathParametersV1.FORMAT_XMI)) {
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

   @Override
   public CompletableFuture<Response<String>> getValidationConstraints(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(VALIDATION_CONSTRAINTS))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();
      return makeCall(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> undo(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(UNDO))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndExpectSuccess(request);
   }

   @Override
   public CompletableFuture<Response<Boolean>> redo(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(REDO))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndExpectSuccess(request);

   }

   protected CompletableFuture<Response<Boolean>> makeCallAndExpectSuccess(final Request request) {
      return makeCall(request)
         .thenApply(response -> parseField(response, JsonResponseMember.TYPE))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals(JsonResponseType.SUCCESS)));
   }

   protected CompletableFuture<Response<Optional<String>>> makeCallAndParseDataField(final Request request) {
      return makeCall(request)
         .thenApply(response -> parseField(response, JsonResponseMember.DATA));
   }

   protected CompletableFuture<Response<String>> makeCallAndGetDataBody(final Request request) {
      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow);
   }

}
