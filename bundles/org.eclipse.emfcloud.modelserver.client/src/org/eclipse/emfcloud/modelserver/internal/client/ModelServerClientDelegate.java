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
package org.eclipse.emfcloud.modelserver.internal.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.EditingContext;
import org.eclipse.emfcloud.modelserver.client.Model;
import org.eclipse.emfcloud.modelserver.client.ModelServerNotification;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.client.SubscriptionListener;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
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
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
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

/**
 * Implementation of behaviours common to multiple API versions of the client.
 */
public class ModelServerClientDelegate implements AutoCloseable {

   public static final String APPLICATION_JSON = "application/json";

   public static final Set<String> DEFAULT_SUPPORTED_FORMATS = ImmutableSet.of(ModelServerPathParameters.FORMAT_JSON,
      ModelServerPathParameters.FORMAT_XMI);

   public static final String PATCH = "PATCH";
   public static final String POST = "POST";

   protected static final Logger LOG = LogManager.getLogger(ModelServerClientDelegate.class);

   private static final TypeReference<Map<String, JsonNode>> MODEL_MAP_TYPE = new TypeReference<>() {};

   protected final OkHttpClient client;
   protected final String baseUrl;
   protected final String defaultFormat;
   protected final Map<String, WebSocket> openSockets = new LinkedHashMap<>();
   protected final Map<EditingContextImpl<?>, WebSocket> openEditingSockets = new LinkedHashMap<>();
   protected final EPackageConfiguration[] configurations;

   private final Map<String, Codec> supportedFormats;

   public ModelServerClientDelegate(final String baseUrl, final String defaultFormat,
      final Map<String, Codec> supportedFormats, final EPackageConfiguration... configurations)
      throws MalformedURLException {

      this(new OkHttpClient(), baseUrl, defaultFormat, supportedFormats, configurations);
   }

   public ModelServerClientDelegate(final OkHttpClient client, final String baseUrl, final String defaultFormat,
      final Map<String, Codec> supportedFormats, final EPackageConfiguration... configurations)
      throws MalformedURLException {

      super();

      this.client = client;
      this.baseUrl = new URL(baseUrl).toString();
      this.defaultFormat = defaultFormat;
      this.configurations = configurations;
      this.supportedFormats = initFormats(supportedFormats);

      this.init();
   }

   private static ImmutableMap<String, Codec> initFormats(final Map<String, Codec> supportedFormats) {
      ImmutableMap.Builder<String, Codec> result = ImmutableMap.builder();

      // Support these at a minimum for backward compatibility
      if (!supportedFormats.containsKey(ModelServerPathParametersV1.FORMAT_XMI)) {
         result.put(ModelServerPathParametersV1.FORMAT_XMI, new XmiCodec());
      }
      if (!supportedFormats.containsKey(ModelServerPathParametersV1.FORMAT_JSON)) {
         result.put(ModelServerPathParametersV1.FORMAT_JSON, new DefaultJsonCodec());
      }

      result.putAll(supportedFormats);
      return result.build();
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

   public String getBaseUrl() { return this.baseUrl; }

   @Override
   public void close() {
      openSockets.keySet().forEach(this::unsubscribe);
      openSockets.clear();
      openEditingSockets.keySet().forEach(this::close);
      openEditingSockets.clear();
      client.dispatcher().executorService().shutdown();
      client.connectionPool().evictAll();
   }

   public CompletableFuture<Response<String>> get(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<EObject>> get(final String modelUri, final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   public CompletableFuture<Response<List<Model<String>>>> getAll() {
      final Request request = new Request.Builder()
         .url(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> {
            List<Model<String>> models = new ArrayList<>();
            try {
               Map<String, JsonNode> uriToContent = new ObjectMapper().readValue(body, MODEL_MAP_TYPE);
               uriToContent.forEach((uri, content) -> models.add(new Model<>(uri, content.toString())));
               return models;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         }));
   }

   public CompletableFuture<Response<List<Model<EObject>>>> getAll(final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, format)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> {
            List<Model<EObject>> models = new ArrayList<>();
            try {
               Map<String, JsonNode> uriToContent = new ObjectMapper().readValue(body, MODEL_MAP_TYPE);
               for (Entry<String, JsonNode> entry : uriToContent.entrySet()) {
                  JsonNode node = entry.getValue();
                  Optional<EObject> model = node.isTextual()
                     ? decode(node.textValue(), checkedFormat)
                     : decode(node.toString(), checkedFormat);
                  if (model.isPresent()) {
                     models.add(new Model<>(entry.getKey(), model.get()));
                  } else {
                     LOG.warn("Incomplete Model: " + entry.getKey());
                  }
               }
               return models;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         }));
   }

   public CompletableFuture<Response<List<String>>> getModelUris() {
      final Request request = new Request.Builder()
         .url(makeUrl(ModelServerPathsV1.MODEL_URIS))
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

   public CompletableFuture<Response<String>> getModelElementById(final String modelUri, final String elementid) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, elementid)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<EObject>> getModelElementById(final String modelUri, final String elementid,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, elementid)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   public CompletableFuture<Response<String>> getModelElementByName(final String modelUri, final String elementname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, elementname)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<EObject>> getModelElementByName(final String modelUri, final String elementname,
      final String format) {
      String checkedFormat = checkedFormat(format);

      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_ELEMENT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, elementname)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .build();

      return makeCallAndParseDataField(request)
         .thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat))))
         .thenApply(this::getBodyOrThrow);
   }

   public CompletableFuture<Response<Boolean>> delete(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .delete()
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public CompletableFuture<Response<Boolean>> close(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.CLOSE))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .post(RequestBody.create(new byte[0]))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public CompletableFuture<Response<String>> create(final String modelUri, final String createdModelAsJsonText) {
      TextNode dataNode = Json.text(createdModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, POST, getDefaultFormat(),
         dataNode);

      return makeCallAndGetDataBody(request);
   }

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
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.MODEL_BASE_PATH))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .method(httpMethod,
            RequestBody.create(
               Json.object(
                  Json.prop(JsonResponseMember.DATA, dataNode)).toString(),
               MediaType.parse(APPLICATION_JSON)))
         .build();
   }

   public CompletableFuture<Response<String>> update(final String modelUri, final String updatedModelAsJsonText) {
      TextNode dataNode = Json.text(updatedModelAsJsonText);
      final Request request = buildCreateOrUpdateRequest(modelUri, PATCH, getDefaultFormat(),
         dataNode);

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<EObject>> update(final String modelUri, final EObject updatedModel,
      final String format) {
      return createOrUpdateModel(modelUri, updatedModel, format, PATCH);
   }

   public String checkedFormat(final String format) {
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
    * Test whether format is supported.
    *
    * @param format the format to test.
    * @return true when supported.
    */
   public boolean isSupportedFormat(final String format) {
      return supportedFormats.containsKey(format);
   }

   /**
    * Get the default format to use. You may override this method if your model server has specific format and codecs
    * configuration.
    *
    * @return default format
    */
   protected String getDefaultFormat() { return defaultFormat; }

   public CompletableFuture<Response<Boolean>> save(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SAVE))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public CompletableFuture<Response<Boolean>> saveAll() {
      final Request request = new Request.Builder()
         .url(makeUrl(ModelServerPathsV1.SAVE_ALL))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public CompletableFuture<Response<String>> validate(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.VALIDATION))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCall(request);
   }

   public CompletableFuture<Response<String>> getTypeSchema(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.TYPE_SCHEMA))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<String>> getUiSchema(final String schemaname) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.UI_SCHEMA))
               .addQueryParameter(ModelServerPathParametersV1.SCHEMA_NAME, schemaname)
               .build())
         .build();

      return makeCallAndGetDataBody(request);
   }

   public CompletableFuture<Response<Boolean>> configure(final ServerConfiguration configuration) {

      ObjectNode config = Json.object(
         Json.prop("workspaceRoot", Json.text(configuration.getWorkspaceRoot())),
         Json.prop("uiSchemaFolder", Json.text(configuration.getUiSchemaFolder())));

      final Request request = new Request.Builder()
         .url(makeUrl(ModelServerPathsV1.SERVER_CONFIGURE))
         .put(RequestBody.create(config.toString(), MediaType.parse(APPLICATION_JSON)))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public CompletableFuture<Response<Boolean>> ping() {
      final Request request = new Request.Builder()
         .url(makeUrl(ModelServerPathsV1.SERVER_PING))
         .build();

      return makeCallAndExpectSuccess(request);
   }

   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final long timeout) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format,
      final long timeout) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final long timeout) {
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
                  .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
                  .addQueryParameter(ModelServerPathParametersV1.LIVE_VALIDATION, "true")
                  .addQueryParameter(ModelServerPathParametersV1.TIMEOUT, String.valueOf(timeout))
                  .build()
                  .toString()))
         .build();

      doSubscribe(modelUri, subscriptionListener, request);
   }

   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format,
      final long timeout) {
      String checkedFormat = checkedFormat(format);
      Request request = new Request.Builder()
         .url(
            makeWsUrl(
               createHttpUrlBuilder(makeUrl(ModelServerPathsV1.SUBSCRIPTION))
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
            Optional<String> type = ModelServerClientDelegate.this.parseJsonField(text, JsonResponseMember.TYPE);
            Optional<String> data = ModelServerClientDelegate.this.parseJsonField(text, JsonResponseMember.DATA);
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

   public boolean send(final String modelUri, final String message) {
      final WebSocket webSocket = openSockets.get(modelUri);
      if (webSocket != null) {
         return webSocket.send(message);
      }
      return false;
   }

   public boolean unsubscribe(final String modelUri) {
      final WebSocket webSocket = openSockets.get(modelUri);
      if (webSocket != null) {
         return webSocket.close(1000, "Websocket closed by client.");
      }
      return false;
   }

   public String makeWsUrl(final String path) {
      return path.replaceFirst("http:", "ws:");
   }

   public String makeUrl(final String path) {
      return baseUrl + path;
   }

   public HttpUrl.Builder createHttpUrlBuilder(final String path) {
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
      return encode(eObject, getDefaultFormat());
   }

   public String encode(final EObject eObject, final String format) {
      Codec codec = supportedFormats.get(format);
      if (codec == null) {
         throw new IllegalArgumentException("Unsupported format: " + format);
      }

      try {
         JsonNode json = codec.encode(eObject);
         if (json.isValueNode()) {
            return json.asText();
         }
         return json.toString();
      } catch (EncodingException e) {
         LOG.error("Encoding of " + eObject + " with " + format + " format failed");
         throw new RuntimeException(e);
      }
   }

   public Optional<EObject> decode(final String payload) {
      return decode(payload, getDefaultFormat());
   }

   public Optional<EObject> decode(final String payload, final String format) {
      Codec codec = supportedFormats.get(format);
      if (codec == null) {
         throw new IllegalArgumentException("Unsupported format: " + format);
      }

      try {
         return codec.decode(payload);
      } catch (DecodingException e) {
         LOG.error("Decoding of " + payload + " with " + format + " format failed");
      }
      return Optional.empty();
   }

   public boolean close(final EditingContext editingContext) {
      if (((EditingContextImpl<?>) editingContext).release()) {
         final WebSocket webSocket = openEditingSockets.remove(editingContext);
         if (webSocket != null) {
            return webSocket.close(1000, "Websocket closed by client.");
         }
      }
      return false;
   }

   public CompletableFuture<Response<String>> getValidationConstraints(final String modelUri) {
      final Request request = new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.VALIDATION_CONSTRAINTS))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();
      return makeCall(request);
   }

   public CompletableFuture<Response<Boolean>> undo(final String modelUri) {
      return makeCallAndExpectSuccess(requestUndo(modelUri));
   }

   public Request requestUndo(final String modelUri) {
      return new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.UNDO))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();
   }

   public CompletableFuture<Response<Boolean>> redo(final String modelUri) {
      return makeCallAndExpectSuccess(requestRedo(modelUri));
   }

   public Request requestRedo(final String modelUri) {
      return new Request.Builder()
         .url(
            createHttpUrlBuilder(makeUrl(ModelServerPathsV1.REDO))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .build())
         .build();
   }

   public CompletableFuture<Response<String>> undoV2(final String modelUri) {
      return makeCallAndRequireSuccessDataBody(requestUndo(modelUri));
   }

   public CompletableFuture<Response<String>> redoV2(final String modelUri) {
      return makeCallAndRequireSuccessDataBody(requestRedo(modelUri));
   }

   public CompletableFuture<Response<Boolean>> makeCallAndExpectSuccess(final Request request) {
      return makeCall(request)
         .thenApply(response -> parseField(response, JsonResponseMember.TYPE))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals(JsonResponseType.SUCCESS)));
   }

   protected CompletableFuture<Response<Optional<String>>> makeCallAndParseDataField(final Request request) {
      return makeCall(request)
         .thenApply(response -> parseField(response, JsonResponseMember.DATA));
   }

   public CompletableFuture<Response<String>> makeCallAndGetDataBody(final Request request) {
      return makeCallAndParseDataField(request)
         .thenApply(this::getBodyOrThrow);
   }

   public CompletableFuture<Response<String>> makeCallAndRequireSuccessDataBody(final Request request) {
      CompletableFuture<Response<String>> primary = makeCall(request);
      CompletableFuture<Response<Boolean>> success = primary
         .thenApply(response -> parseField(response, JsonResponseMember.TYPE))
         .thenApply(this::getBodyOrThrow)
         .thenApply(response -> response.mapBody(body -> body.equals(JsonResponseType.SUCCESS)));
      CompletableFuture<Response<String>> data = primary
         .thenApply(response -> parseField(response, JsonResponseMember.DATA))
         .thenApply(this::getBodyOrThrow);
      CompletableFuture<Response<String>> result = success.thenCombine(data, (_success, _data) -> {
         if (!_success.body()) {
            throw new RuntimeException(_data.body() != null ? _data.body() : "Request failed for an unknown reason.");
         }
         return _data;
      });
      return result;
   }

   protected static <A, B> BiFunction<A, B, B> takeB() {
      return (a, b) -> b;
   }

   /**
    * Delegator for the {@code edit} API endpoint's websocket editing context.
    *
    * @param owner    the client on which behalf the editing context will operate
    * @param endpoint the possibly version-specific endpoint name
    * @param encoder  the possibly version-specific edit message encoder
    *
    * @return the websocket editing context
    */
   public <CLIENT> EditingContext edit(final CLIENT owner, final String endpoint,
      final EditingContextImpl.MessageEncoder<? super CLIENT> encoder) {

      EditingContextImpl<?> result;

      if (!openEditingSockets.isEmpty()) {
         result = openEditingSockets.keySet().iterator().next();
         result.retain();
         return result;
      }

      Request request = new Request.Builder()
         .url(makeWsUrl(endpoint))
         .build();
      result = new EditingContextImpl<>(owner, encoder, getDefaultFormat());

      final WebSocket socket = client.newWebSocket(request, result);
      openEditingSockets.put(result, socket);

      return result;
   }

   public WebSocket createWebSocket(final Request request, final WebSocketListener listener) {
      return client.newWebSocket(request, listener);
   }

}
