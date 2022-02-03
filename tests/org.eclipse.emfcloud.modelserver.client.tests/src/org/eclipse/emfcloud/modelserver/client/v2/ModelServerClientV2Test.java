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

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.modelserver.client.Model;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.command.AddCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.OkHttpClient;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;
import okio.Buffer;

public class ModelServerClientV2Test {

   private static String BASE_URL = "http://fake-url.com/api/v2/";
   private Builder baseHttpUrlBuilder;

   private MockInterceptor interceptor;
   private EClass eClass;
   private JsonCodecV2 jsonCodec;

   @Before
   public void before() {
      interceptor = new MockInterceptor();
      jsonCodec = new JsonCodecV2();

      baseHttpUrlBuilder = new HttpUrl.Builder()
         .scheme("http").host("fake-url.com").addPathSegment("api").addPathSegment("v2");

      eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName("AbstractTestClass");
      eClass.setAbstract(true);
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void get() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String getUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.get("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getXmi() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      String getXmiUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getXmiUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<EObject>> f = client.get("SuperBrewer3000.json",
         ModelServerPathParametersV2.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getModelUris()
      throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String getModelUrisUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_URIS)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getModelUrisUrl)
         .respond(
            JsonResponse.success(JsonCodec.encode(Collections.singletonList("http://fake-model.com"))).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<List<String>>> f = client.getModelUris();
      assertThat(f.get().body(), equalTo(Collections.singletonList("http://fake-model.com")));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getAll() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "http://fake-model.com";
      final JsonNode content = jsonCodec.encode(eClass);
      ObjectNode model = Json.object(Json.prop("modeluri", Json.text(modelUri)),
         Json.prop("content", content));
      ArrayNode allModels = Json.array(model);

      String getAllUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getAllUrl)
         .respond(JsonResponse.success(allModels).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<List<Model<String>>>> f = client.getAll();
      assertThat(f.get().body(), equalTo(Collections.singletonList(new Model<>(modelUri, content.toString()))));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getAllXmi() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "http://fake-model.com";
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();

      String getAllXmiUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getAllXmiUrl)
         .respond(JsonResponse.success(JsonCodec
            .encode(Collections.singletonList(new Model<>(modelUri, new XmiCodec().encode(eClass)))))
            .toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<List<Model<EObject>>>> f = client.getAll(ModelServerPathParametersV2.FORMAT_XMI);
      List<Model<EObject>> response = f.get().body();
      assertThat(response.size(), is(1));
      assertThat(response.get(0).getModelUri(), is(modelUri));
      assertTrue(EcoreUtil.equals(response.get(0).getContent(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementById()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String getElementByIdUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.ELEMENT_ID, "//@workflows.0/@nodes.0")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByIdUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.getModelElementById("SuperBrewer3000.json",
         "//@workflows.0/@nodes.0");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByIdXMI()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      String getElementByIdXMIUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.ELEMENT_ID, "//@workflows.0")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByIdXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementById("SuperBrewer3000.json",
         "//@workflows.0", ModelServerPathParametersV2.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByName()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(EcoreFactory.eINSTANCE.createEClass());
      String getElementByNameUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.ELEMENT_NAME, "PreHeat")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByNameUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.getModelElementByName("SuperBrewer3000.json", "PreHeat");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByNameXMI()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      String getElementByNameXMIUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.ELEMENT_NAME, "Simple Workflow")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByNameXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementByName("SuperBrewer3000.json",
         "Simple Workflow", ModelServerPathParametersV2.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   public void delete() throws ExecutionException, InterruptedException, MalformedURLException {
      String deleteUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .url(deleteUrl)
         .delete()
         .respond(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS))).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.delete("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void close() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String closeUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.CLOSE)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(closeUrl)
         .post()
         .respond(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS))).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.close(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void create() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String createUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_JSON_V2)
         .build().toString();
      interceptor.addRule()
         .url(createUrl)
         .post()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.create(
         "SuperBrewer3000.json",
         expected.toString());

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void createModel() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      DefaultJsonCodec json = new DefaultJsonCodec();
      final JsonNode expected = json.encode(eClass);
      String createModelUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_JSON)
         .build().toString();
      interceptor.addRule()
         .url(createModelUrl)
         .post()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      CompletableFuture<Response<EObject>> f = client.create(
         "SuperBrewer3000.json",
         eClass,
         ModelServerPathParametersV2.FORMAT_JSON);

      assertThat(json.encode(f.get().body()), equalTo(expected));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void update() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String updateUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_JSON_V2)
         .build().toString();
      interceptor.addRule()
         .url(updateUrl)
         .patch()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.update(
         "SuperBrewer3000.json",
         expected.toString());

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void updateWithFormat()
      throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final EClass expected = EcoreFactory.eINSTANCE.createEClass();
      String updateWithFormatUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .url(updateWithFormatUrl)
         .patch()
         .respond(JsonResponse.success(new XmiCodec().encode(expected)).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<EObject>> f = client.update(
         "SuperBrewer3000.json",
         EcoreFactory.eINSTANCE.createEClass(),
         ModelServerPathParametersV2.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), expected));
   }

   @Test(expected = CancellationException.class)
   public void updateWithUnsupportedFormat() throws MalformedURLException {
      ModelServerClientV2 client = createClient();

      client.update(
         "SuperBrewer3000.json",
         EcoreFactory.eINSTANCE.createEClass(),
         "wut");
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getTypeSchema()
      throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text("object"))));
      String typeSchemaUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.TYPE_SCHEMA)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .url(typeSchemaUrl)
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.getTypeSchema("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getUiSchema() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text("object"))));
      String uiSchemaUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.UI_SCHEMA)
         .addQueryParameter(ModelServerPathParametersV2.SCHEMA_NAME, "controlunit")
         .build().toString();
      interceptor.addRule()
         .url(uiSchemaUrl)
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<String>> f = client.getUiSchema("controlunit");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   public void pingTrue() throws ExecutionException, InterruptedException, MalformedURLException {
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV2.SERVER_PING).build().toString();
      interceptor.addRule()
         .url(pingUrl)
         .get()
         .respond(JsonResponse.success().toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void pingFalse() throws ExecutionException, InterruptedException, MalformedURLException {
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV2.SERVER_PING).build().toString();
      interceptor.addRule()
         .url(pingUrl)
         .get()
         .respond(JsonResponse.error().toString());
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();

      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void configure() throws MalformedURLException, ExecutionException, InterruptedException {
      String configureUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV2.SERVER_CONFIGURE).build().toString();
      interceptor.addRule()
         .url(configureUrl)
         .put()
         .respond(JsonResponse.success().toString());
      ModelServerClientV2 client = createClient();

      ServerConfiguration serverConfiguration = ServerConfiguration.create("/home/user/workspace",
         "/home/user/workspace/.ui-schemas");
      final CompletableFuture<Response<Boolean>> f = client.configure(serverConfiguration);

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void edit() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      ((InternalEObject) eClass).eSetProxyURI(URI.createURI("SuperBrewer3000.json#//workflows.0"));
      CCommand add = CCommandFactory.eINSTANCE.createCommand();
      add.setType(EMFCommandType.ADD);
      add.setOwner(eClass);
      add.setFeature("eStructuralFeatures");
      add.getObjectsToAdd().add(EcoreFactory.eINSTANCE.createEAttribute());
      add.getObjectValues().addAll(add.getObjectsToAdd());

      final JsonNode expected = jsonCodec.encode(add);

      // Issue #115: Ensure correct JSON encoding
      assertThat(expected.toString(), containsString("\"type\":\"add\""));
      assertThat(expected.toString(), containsString("\"objectValues\":[{\"$type\":"));

      String editUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_JSON_V2)
         .build().toString();

      interceptor.addRule().url(editUrl)
         .patch().answer(request -> {
            Buffer buffer = new Buffer();
            try {
               request.body().writeTo(buffer);
            } catch (IOException e) {
               e.printStackTrace();
               fail("Failed to capture request body content: " + e.getMessage());
            }

            // The resulting string is escaped as though for a Java string literal
            String body = buffer.readString(Charsets.UTF_8).replace("\\\\", "\\").replace("\\\"", "\"");

            // This is the test's assertion
            if (body.contains(expected.toString())) {
               return new Rule.Builder().respond(JsonResponse.success("confirmed").toString());
            }
            return new Rule.Builder().respond(JsonResponse.error().toString());
         });
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json", add,
         ModelServerPathParametersV2.FORMAT_JSON_V2);

      assertThat(f.get().body(), is(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void editNativeCommand()
      throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      AdapterFactoryEditingDomain domain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(),
         new BasicCommandStack());
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      ((InternalEObject) eClass).eSetProxyURI(URI.createURI("SuperBrewer3000.json#//workflows.0"));
      Command add = AddCommand.create(domain, eClass, EcorePackage.eINSTANCE.getEClass_EAttributes(),
         EcoreFactory.eINSTANCE.createEAttribute());

      String editUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV2.FORMAT, ModelServerPathParametersV2.FORMAT_JSON)
         .build().toString();

      interceptor.addRule().url(editUrl)
         .patch().answer(request -> {
            Buffer buffer = new Buffer();
            try {
               request.body().writeTo(buffer);
            } catch (IOException e) {
               e.printStackTrace();
               fail("Failed to capture request body content: " + e.getMessage());
            }

            // The resulting string is escaped as though for a Java string literal
            String body = buffer.readString(Charsets.UTF_8).replace("\\\\", "\\").replace("\\\"", "\"");

            // This is the test's assertion
            if (body.contains("\"type\":\"add\"") && body.contains("\"objectValues\":[{\"eClass\":")) {
               return new Rule.Builder().respond(JsonResponse.success("confirmed").toString());
            }
            return new Rule.Builder().respond(JsonResponse.error().toString());
         });
      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json",
         AddCommandContribution.clientCommand((AddCommand) add),
         ModelServerPathParametersV2.FORMAT_JSON);

      assertThat(f.get().body(), is(true));
   }

   @Test
   public void undo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String undoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.UNDO)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(undoUrl)
         .get()
         .respond(JsonResponse.success("Successful undo.").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.undo(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void cannotUndo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String undoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.UNDO)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(undoUrl)
         .get()
         .respond(JsonResponse.warning("Cannot undo.").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.undo(modelUri);
      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void redo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.REDO)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("Successful redo.").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.redo(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void cannotRedo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.REDO)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.warning("Cannot redo.").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.redo(modelUri);
      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void save() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.SAVE)
         .addQueryParameter(ModelServerPathParametersV2.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("Model 'SuperBrewer3000.coffee' successfully saved").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.save(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void saveAll() throws ExecutionException, InterruptedException, MalformedURLException {
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV2.SAVE_ALL)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("All models successfully saved").toString());

      ModelServerClientV2 client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.saveAll();
      assertThat(f.get().body(), equalTo(true));
   }

   private ModelServerClientV2 createClient() throws MalformedURLException {
      return new ModelServerClientV2(
         new OkHttpClient.Builder().addInterceptor(interceptor).build(),
         BASE_URL);
   }
}
