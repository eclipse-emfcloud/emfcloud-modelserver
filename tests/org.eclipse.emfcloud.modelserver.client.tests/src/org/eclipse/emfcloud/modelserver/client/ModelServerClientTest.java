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
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.common.ModelServerPaths;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.emfjson.jackson.resource.JsonResource;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
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
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
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
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "xmi")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getXmiUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.get("SuperBrewer3000.json", "xmi");

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getAll() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      String getAllUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_URIS)
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getAllUrl)
         .respond(
            JsonResponse.success(JsonCodec.encode(Collections.singletonList("http://fake-model.com"))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<List<String>>> f = client.getAll();
      assertThat(f.get().body(), equalTo(Collections.singletonList("http://fake-model.com")));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementById()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String getElementByIdUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_ELEMENT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("elementid", "//@workflows.0/@nodes.0")
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
         .addPathSegment(ModelServerPaths.MODEL_ELEMENT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("elementid", "//@workflows.0")
         .addQueryParameter("format", "xmi")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByIdXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementById("SuperBrewer3000.json",
         "//@workflows.0", "xmi");

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getElementByName()
      throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(EcoreFactory.eINSTANCE.createEClass());
      String getElementByNameUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_ELEMENT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("elementname", "PreHeat")
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
         .addPathSegment(ModelServerPaths.MODEL_ELEMENT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("elementname", "Simple Workflow")
         .addQueryParameter("format", "xmi")
         .build().toString();
      interceptor.addRule()
         .get()
         .url(getElementByNameXMIUrl)
         .respond(JsonResponse.success(new XmiCodec().encode(eClass)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.getModelElementByName("SuperBrewer3000.json",
         "Simple Workflow", "xmi");

      assertTrue(EcoreUtil.equals(f.get().body(), eClass));
   }

   @Test
   public void delete() throws ExecutionException, InterruptedException, MalformedURLException {
      String deleteUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .build().toString();
      interceptor.addRule()
         .url(deleteUrl)
         .delete()
         .respond(Json.object(Json.prop("type", Json.text("success"))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.delete("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void create() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String createUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "json")
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
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "json")
         .build().toString();
      interceptor.addRule()
         .url(createModelUrl)
         .post()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      CompletableFuture<Response<EObject>> f = client.create(
         "SuperBrewer3000.json",
         eClass,
         "json");

      assertThat(jsonCodec.encode(f.get().body()), equalTo(expected));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void update() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(eClass);
      String updateUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "json")
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
         .addPathSegment(ModelServerPaths.MODEL_BASE_PATH)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "xmi")
         .build().toString();
      interceptor.addRule()
         .url(updateWithFormatUrl)
         .patch()
         .respond(JsonResponse.success(new XmiCodec().encode(expected)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.update(
         "SuperBrewer3000.json",
         EcoreFactory.eINSTANCE.createEClass(),
         "xmi");

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
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop("type", Json.text("object"))));
      String typeSchemaUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.TYPE_SCHEMA)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
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
   public void getUISchema() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop("type", Json.text("object"))));
      String uiSchemaUrl = baseHttpUrlBuilder
         .addPathSegment(ModelServerPaths.UI_SCHEMA)
         .addQueryParameter("schemaname", "controlunit")
         .build().toString();
      interceptor.addRule()
         .url(uiSchemaUrl)
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.getUISchema("controlunit");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   public void pingTrue() throws ExecutionException, InterruptedException, MalformedURLException {
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPaths.SERVER_PING).build().toString();
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
      String pingUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPaths.SERVER_PING).build().toString();
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
      String configureUrl = baseHttpUrlBuilder.addPathSegments(ModelServerPaths.SERVER_CONFIGURE).build().toString();
      interceptor.addRule()
         .url(configureUrl)
         .put()
         .respond(JsonResponse.success().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.configure(() -> "/home/user/workspace");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void edit() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      ((InternalEObject) eClass).eSetProxyURI(URI.createURI("SuperBrewer3000.json#//workflows.0"));
      CCommand add = CCommandFactory.eINSTANCE.createCommand();
      add.setType(CommandKind.ADD);
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
         .addPathSegment(ModelServerPaths.EDIT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "json")
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

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json", add, "json");

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
         .addPathSegment(ModelServerPaths.EDIT)
         .addQueryParameter("modeluri", "SuperBrewer3000.json")
         .addQueryParameter("format", "json")
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

      final CompletableFuture<Response<Boolean>> f = client.edit("SuperBrewer3000.json", add, "json");

      assertThat(f.get().body(), is(true));
   }

   private ModelServerClient createClient() throws MalformedURLException {
      return new ModelServerClient(
         new OkHttpClient.Builder().addInterceptor(interceptor).build(),
         BASE_URL);
   }
}
