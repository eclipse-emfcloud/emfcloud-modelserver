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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.emfjson.jackson.resource.JsonResource;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.emfcloud.modelserver.coffee.model.coffee.BrewingUnit;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeeFactory;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Display;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;

import okhttp3.OkHttpClient;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;
import okio.Buffer;

public class ModelServerClientTest {

   private static final String BASE_URL = "http://fake-url.com/api/v1/";

   private MockInterceptor interceptor;
   private Display display;
   private DefaultJsonCodec jsonCodec;

   @Before
   public void before() {
      interceptor = new MockInterceptor();
      jsonCodec = new DefaultJsonCodec();
      display = CoffeeFactory.eINSTANCE.createDisplay();
      display.setWidth(200);
      display.setHeight(200);
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void get() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(display);
      interceptor.addRule()
         .get()
         .url(BASE_URL + ModelServerPaths.MODEL_BASE_PATH + "?modeluri=" + "SuperBrewer3000.json")
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.get("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getXmi() throws ExecutionException, InterruptedException, EncodingException, MalformedURLException {
      final BrewingUnit brewingUnit = CoffeeFactory.eINSTANCE.createBrewingUnit();
      interceptor.addRule()
         .get()
         .url(BASE_URL + ModelServerPaths.MODEL_BASE_PATH + "?modeluri=SuperBrewer3000.json&format=xmi")
         .respond(JsonResponse.success(new XmiCodec().encode(brewingUnit)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.get("SuperBrewer3000.json", "xmi");

      assertTrue(EcoreUtil.equals(f.get().body(), brewingUnit));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getAll() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      interceptor.addRule()
         .get()
         .url(BASE_URL + ModelServerPaths.MODEL_URIS)
         .respond(
            JsonResponse.success(JsonCodec.encode(Collections.singletonList("http://fake-model.com"))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<List<String>>> f = client.getAll();
      assertThat(f.get().body(), equalTo(Collections.singletonList("http://fake-model.com")));
   }

   @Test
   public void delete() throws ExecutionException, InterruptedException, MalformedURLException {
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.MODEL_BASE_PATH + "?modeluri=" + "SuperBrewer3000.json")
         .delete()
         .respond(Json.object(Json.prop("type", Json.text("success"))).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.delete("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void update() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = jsonCodec.encode(display);
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.MODEL_BASE_PATH + "?modeluri=" + "SuperBrewer3000.json")
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
      final BrewingUnit expected = CoffeeFactory.eINSTANCE.createBrewingUnit();
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.MODEL_BASE_PATH + "?modeluri=" + "SuperBrewer3000.json" + "&format=xmi")
         .patch()
         .respond(JsonResponse.success(new XmiCodec().encode(expected)).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<EObject>> f = client.update(
         "SuperBrewer3000.json",
         CoffeeFactory.eINSTANCE.createBrewingUnit(),
         "xmi");

      assertTrue(EcoreUtil.equals(f.get().body(), expected));
   }

   @Test(expected = CancellationException.class)
   public void updateWithUnsupportedFormat() throws MalformedURLException {
      ModelServerClient client = createClient();

      client.update(
         "SuperBrewer3000.json",
         CoffeeFactory.eINSTANCE.createBrewingUnit(),
         "wut");
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void getSchema() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      final JsonNode expected = JsonCodec.encode(Json.object(Json.prop("type", Json.text("object"))));
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.SCHEMA + "?modeluri=" + "SuperBrewer3000.json")
         .get()
         .respond(JsonResponse.success(expected).toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<String>> f = client.getSchema("SuperBrewer3000.json");

      assertThat(f.get().body(), equalTo(expected.toString()));
   }

   @Test
   public void pingTrue() throws ExecutionException, InterruptedException, MalformedURLException {
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.SERVER_PING)
         .get()
         .respond(JsonResponse.success().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();
      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   public void pingFalse() throws ExecutionException, InterruptedException, MalformedURLException {
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.SERVER_PING)
         .get()
         .respond(JsonResponse.error().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.ping();

      assertThat(f.get().body(), equalTo(false));
   }

   @Test
   public void configure() throws MalformedURLException, ExecutionException, InterruptedException {
      interceptor.addRule()
         .url(BASE_URL + ModelServerPaths.SERVER_CONFIGURE)
         .put()
         .respond(JsonResponse.success().toString());
      ModelServerClient client = createClient();

      final CompletableFuture<Response<Boolean>> f = client.configure(() -> "/home/user/workspace");

      assertThat(f.get().body(), equalTo(true));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void edit() throws EncodingException, ExecutionException, InterruptedException, MalformedURLException {
      Workflow flow = CoffeeFactory.eINSTANCE.createWorkflow();
      ((InternalEObject) flow).eSetProxyURI(URI.createURI("SuperBrewer3000.json#//workflows.0"));
      CCommand add = CCommandFactory.eINSTANCE.createCommand();
      add.setType(CommandKind.ADD);
      add.setOwner(flow);
      add.setFeature("nodes");
      add.getObjectsToAdd().add(CoffeeFactory.eINSTANCE.createAutomaticTask());
      add.getObjectValues().addAll(add.getObjectsToAdd());
      JsonResource cmdRes = new JsonResource(URI.createURI("$command.json"));
      cmdRes.getContents().add(add);

      final JsonNode expected = jsonCodec.encode(add);
      cmdRes.getContents().clear(); // Don't unload because that creates proxies

      // Issue #115: Ensure correct JSON encoding
      assertThat(expected.toString(), containsString("\"type\":\"add\""));
      assertThat(expected.toString(), containsString("\"objectValues\":[{\"eClass\":"));

      interceptor.addRule().url(BASE_URL + ModelServerPaths.EDIT + "?modeluri=" + "SuperBrewer3000.json&format=json")
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
      Workflow flow = CoffeeFactory.eINSTANCE.createWorkflow();
      ((InternalEObject) flow).eSetProxyURI(URI.createURI("SuperBrewer3000.json#//workflows.0"));
      Command add = AddCommand.create(domain, flow, CoffeePackage.Literals.WORKFLOW__NODES,
         CoffeeFactory.eINSTANCE.createAutomaticTask());

      interceptor.addRule().url(BASE_URL + ModelServerPaths.EDIT + "?modeluri=" + "SuperBrewer3000.json&format=json")
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
