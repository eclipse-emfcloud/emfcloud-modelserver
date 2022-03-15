/********************************************************************************
 * Copyright (c) 2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

@RunWith(MockitoJUnitRunner.class)
public class ModelServerRoutingV2Test {

   private static final URI WORKSPACE_URI = URI.createURI("file:///Users/test/workspace");

   private final Map<String, Map<String, Handler>> apiHandlers = Maps.newHashMap();

   @Mock
   private ModelResourceManager resourceManager;

   @Mock
   private ModelController modelController;

   @Mock
   private SchemaController schemaController;

   @Mock
   private ServerController serverController;

   @Mock
   private SessionController sessionController;

   @Mock
   private TransactionController transactionController;

   @Mock
   private ServerConfiguration serverConfiguration;

   @Mock
   private ModelURIConverter uriConverter;

   @Mock
   private Context context;
   private int resultStatus;
   private String resultString;

   private ModelServerRoutingV2 routing;

   @Test
   public void testGetOne() {
      model("model.json");

      get("/models", "model.json");

      assertThat(resultStatus, is(200));
      assertThat(resultString, containsString("OK"));
   }

   @Test
   public void testGetOneRejectedModelURI() {
      model("model.json");

      // Try to access it via the absolute URI
      get("/models", "file:///Users/test/workspace/model.json");

      assertThat(resultStatus, is(404));
   }

   //
   // Test framework
   //

   @Before
   public void setUp() throws Exception {
      Javalin javalin = mock(Javalin.class);
      when(javalin.routes(ArgumentMatchers.any())).thenCallRealMethod();

      when(javalin.get(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then(invocation -> {
         setHandler("GET", invocation.getArgument(0), invocation.getArgument(1));
         return javalin;
      });

      when(context.result(ArgumentMatchers.anyString())).then(invocation -> {
         resultString = invocation.getArgument(0);
         return context;
      });
      when(context.status(ArgumentMatchers.anyInt())).then(invocation -> {
         resultStatus = invocation.getArgument(0);
         return context;
      });

      when(serverConfiguration.getWorkspaceRootURI()).thenReturn(WORKSPACE_URI);
      when(uriConverter.resolveModelURI(context, "modeluri")).then(invocation -> {
         return Optional.ofNullable(context.queryParam(invocation.getArgument(1)))
            .map(uriString -> URI.createURI(uriString, true))
            .flatMap(uri -> new DefaultModelURIConverter.APIV2Resolver(serverConfiguration).apply(uri));
      });
      when(uriConverter.resolveModelURI(context)).thenCallRealMethod();

      routing = new ModelServerRoutingV2(javalin, resourceManager, modelController, schemaController,
         serverController,
         sessionController, transactionController);
      routing.setModelURIConverter(uriConverter);

      routing.bindRoutes();
   }

   void setContext(final String path, final String modeluri) {
      when(context.queryParam("modeluri")).thenReturn(modeluri);
   }

   void setHandler(final String method, final String api, final Handler handler) {
      apiHandlers.computeIfAbsent(method, __ -> Maps.newHashMap()).put(api, handler);
   }

   Handler getHandler(final String method, final String api) {
      Handler result = apiHandlers.getOrDefault(method, emptyMap()).get(api);
      assertThat("No GET handler for " + api, result, notNullValue());
      return result;
   }

   @SuppressWarnings("checkstyle:IllegalCatch")
   void handle(final String method, final String api) {
      try {
         getHandler(method, api).handle(context);
      } catch (Exception e) {
         e.printStackTrace();
         fail("Request handler failed with exception: " + e.getMessage());
      }
   }

   void model(final String relativePath) {
      doAnswer(invocation -> {
         context.status(200);
         context.result("OK model");
         return null;
      }).when(modelController).getOne(context, "file:///Users/test/workspace/" + relativePath);
   }

   void get(final String endpoint, final String modeluri) {
      setContext(endpoint, modeluri);
      handle("GET", "/api/v2" + endpoint);
   }
}
