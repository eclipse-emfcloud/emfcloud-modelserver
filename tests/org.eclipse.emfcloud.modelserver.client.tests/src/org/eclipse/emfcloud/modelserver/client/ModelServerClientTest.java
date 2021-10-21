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
import org.eclipse.emfcloud.jackson.resource.JsonResource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.command.AddCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
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

public class ModelServerClientTest {

   private static String BASE_URL = "http://fake-url.com/api/v1/";
   private Builder baseHttpUrlBuilder;

   private MockInterceptor interceptor;
   private EClass eClass;
   private DefaultJsonCodec jsonCodec;

   @Before
   public void before() {
      interceptor = new MockInterceptor();
      jsonCodec = new DefaultJsonCodec();

      baseHttpUrlBuilder = new HttpUrl.Builder()
         .scheme("http").host("fake-url.com").addPathSegment("api").addPathSegment("v1");

      eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName("AbstractTestClass");
      eClass.setAbstract(true);
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void get() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String getUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.get("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getXmi() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      String getXmiUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getXmiUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.get("SuperBrewer3000.json",
         ModelServerPathParametersV1.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getModelUris()
      throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String getModelUrisUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_URIS)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getModelUrisUrl)
         .respond(
            JsonResponse.success(JsonCodec.encode(Collections.singletonList("http://fake-model.com"))).toString());
      ModelServerClient client = createClient();

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
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getAllUrl)
         .respond(JsonResponse.success(allModels).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<List<Model<String>>>> f = client.getAll();
      assertThat(f.get().body(), equalTo(Collections.singletonList(new Model<>(modelUri, content.toString()))));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getAllXmi() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "http://fake-model.com";
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();

      String getAllXmiUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getAllXmiUrl)
         .respond(JsonResponse.success(JsonCodec
            .encode(Collections.singletonList(new Model<>(modelUri, new XmiCodec().encode(eClass)))))
            .toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<List<Model<EObject>>>> f = client.getAll(ModelServerPathParametersV1.FORMAT_XMI);
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
         .addPathSegment(ModelServerPathsV1.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, "//@workflows.0/@nodes.0")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByIdUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

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
         .addPathSegment(ModelServerPathsV1.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.ELEMENT_ID, "//@workflows.0")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByIdXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementById("SuperBrewer3000.json",
         "//@workflows.0", ModelServerPathParametersV1.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByName()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(EcoreFactory.eINSTANCE.createEClass());
      String getElementByNameUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, "PreHeat")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByNameUrl)
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.getModelElementByName("SuperBrewer3000.json", "PreHeat");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByNameXMI()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      String getElementByNameXMIUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_ELEMENT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.ELEMENT_NAME, "Simple Workflow")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByNameXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementByName("SuperBrewer3000.json",
         "Simple Workflow", ModelServerPathParametersV1.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   public void delete() throws ExecutionException, InterruptedException, MalformedURLException {
      String deleteUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .url(deleteUrl)
         .delete()
         .respond(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.delete("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void close() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String closeUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.CLOSE)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(closeUrl)
         .post()
         .respond(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.close(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void create() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String createUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON)
         .build().toString();
      interceptor.addRule()
         .url(createUrl)
         .post()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.create(
         "SuperBrewer3000.json",
         expected.toString());

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void createModel() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String createModelUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON)
         .build().toString();
      interceptor.addRule()
         .url(createModelUrl)
         .post()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      CompletableFuture<Response<EObject>> f = client.create(
         "SuperBrewer3000.json",
         eClass,
         ModelServerPathParametersV1.FORMAT_JSON);

      assertThat(jsonCodec.encode(f.get().body()), equalTo(expected));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void update() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String updateUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON)
         .build().toString();
      interceptor.addRule()
         .url(updateUrl)
         .patch()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

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
         .addPathSegment(ModelServerPathsV1.MODEL_BASE_PATH)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_XMI)
         .build().toString();
      interceptor.addRule()
         .url(updateWithFormatUrl)
         .patch()
         .respond(JsonResponse.success(new XmiCodec().encode(expected)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.update(
         "SuperBrewer3000.json",
         EcoreFactory.eINSTANCE.createEClass(),
         ModelServerPathParametersV1.FORMAT_XMI);

      assertTrue(EcoreUtil.equals(f.get().body(), expected));
   }

   @Test(expected = CancellationException.class)
   public void updateWithUnsupportedFormat() throws MalformedURLException {
      ModelServerClient client = createClient();

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
         .addPathSegment(ModelServerPathsV1.TYPE_SCHEMA)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .url(typeSchemaUrl)
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.getTypeSchema("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getUiSchema() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop(JsonResponseMember.TYPE, Json.text("object"))));
      String uiSchemaUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.UI_SCHEMA)
         .addQueryParameter(ModelServerPathParametersV1.SCHEMA_NAME, "controlunit")
         .build().toString();
      interceptor.addRule()
         .url(uiSchemaUrl)
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.getUiSchema("controlunit");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   public void pingTrue() throws ExecutionException, InterruptedException, MalformedURLException {
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV1.SERVER_PING).build().toString();
      interceptor.addRule()
         .url(pingUrl)
         .get()
         .respond(JsonResponse.success().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void pingFalse() throws ExecutionException, InterruptedException, MalformedURLException {
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV1.SERVER_PING).build().toString();
      interceptor.addRule()
         .url(pingUrl)
         .get()
         .respond(JsonResponse.error().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();

      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void configure() throws MalformedURLException, ExecutionException, InterruptedException {
      String configureUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPathsV1.SERVER_CONFIGURE).build().toString();
      interceptor.addRule()
         .url(configureUrl)
         .put()
         .respond(JsonResponse.success().toString());
      ModelServerClient client = createClient();

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
      add.setFeature("eAttributes");
      add.getObjectsToAdd().add(EcoreFactory.eINSTANCE.createEAttribute());
      add.getObjectValues().addAll(add.getObjectsToAdd());
      JsonResource cmdRes = new JsonResource(URI.createURI("$command.json"));
      cmdRes.getContents().add(add);

      final JsonNode expected = jsonCodec.encode(add);
      cmdRes.getContents().clear(); // Don't unload because that creates proxies

      // Issue #115: Ensure correct JSON encoding
      assertThat(expected.toString(), containsString("\"type\":\"add\""));
      assertThat(expected.toString(), containsString("\"objectValues\":[{\"eClass\":"));

      String editUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.EDIT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON)
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
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json", add,
         ModelServerPathParametersV1.FORMAT_JSON);

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
         .addPathSegment(ModelServerPathsV1.EDIT)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, "SuperBrewer3000.json")
         .addQueryParameter(ModelServerPathParametersV1.FORMAT, ModelServerPathParametersV1.FORMAT_JSON)
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
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json",
         AddCommandContribution.clientCommand((AddCommand) add),
         ModelServerPathParametersV1.FORMAT_JSON);

      assertThat(f.get().body(), is(true));
   }

   @Test
   public void undo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String undoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.UNDO)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(undoUrl)
         .get()
         .respond(JsonResponse.success("Successful undo.").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.undo(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void cannotUndo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String undoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.UNDO)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(undoUrl)
         .get()
         .respond(JsonResponse.warning("Cannot undo.").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.undo(modelUri);
      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void redo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.REDO)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("Successful redo.").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.redo(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void cannotRedo() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.REDO)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.warning("Cannot redo.").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.redo(modelUri);
      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void save() throws ExecutionException, InterruptedException, MalformedURLException {
      String modelUri = "SuperBrewer3000.coffee";
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.SAVE)
         .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("Model 'SuperBrewer3000.coffee' successfully saved").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.save(modelUri);
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void saveAll() throws ExecutionException, InterruptedException, MalformedURLException {
      String redoUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPathsV1.SAVE_ALL)
         .build().toString();

      interceptor.addRule()
         .url(redoUrl)
         .get()
         .respond(JsonResponse.success("All models successfully saved").toString());

      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.saveAll();
      assertThat(f.get().body(), equalTo(true));
   }

   private ModelServerClient createClient() throws MalformedURLException {
      return new ModelServerClient(
         new OkHttpClient.Builder().addInterceptor(interceptor).build(),
         BASE_URL);
   }
}
