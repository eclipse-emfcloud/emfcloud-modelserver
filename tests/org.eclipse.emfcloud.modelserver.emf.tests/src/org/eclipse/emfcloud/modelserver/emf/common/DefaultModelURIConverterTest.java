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

import static org.eclipse.emfcloud.modelserver.common.APIVersion.API_V1;
import static org.eclipse.emfcloud.modelserver.common.APIVersion.API_V2;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

@RunWith(Enclosed.class)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DefaultModelURIConverterTest {

   // Note the Windows file URIs
   static final URI WORKSPACE_ROOT_URI = URI.createURI("file:/C:/Users/junit/workspace");
   static final String WORKSPACE_ROOT_PATH = "C:\\Users\\junit\\workspace\\";
   static final URI OUTSIDE_WORKSPACE_DIRECTORY_URI = URI.createURI("file:/C:/Users/someoneelse/test/place");
   static final String OUTSIDE_WORKSPACE_DIRECTORY_PATH = "C:\\Users\\someoneelse\\test\\place\\";

   @Mock
   ServerConfiguration serverConfiguration;

   @Mock
   ModelResourceManager resourceManager;

   @Mock
   Context requestCtx;

   @Mock
   WsContext socketCtx;

   DefaultModelURIConverter uriConverter;

   private final APIVersion apiVersion;

   DefaultModelURIConverterTest(final APIVersion apiVersion) {
      super();

      this.apiVersion = apiVersion;
   }

   @RunWith(MockitoJUnitRunner.class)
   public static class ApiV2 extends DefaultModelURIConverterTest {

      public ApiV2() {
         super(API_V2);
      }

      @Test
      public void testNormalizeURI() {
         URI outsideWorkspaceAbsolute = outsideWorkspace("model.xmi");
         URI uri = uriConverter.normalize(outsideWorkspaceAbsolute);
         assertThat(uri, is(outsideWorkspaceAbsolute));

         URI insideWorkspaceAbsolute = inWorkspace("model.xmi");
         uri = uriConverter.normalize(insideWorkspaceAbsolute);
         assertThat(uri, is(insideWorkspaceAbsolute));

         URI relative = URI.createURI("nested/model.xmi");
         URI expected = inWorkspace("nested", "model.xmi");
         uri = uriConverter.normalize(relative);
         assertThat(uri, is(expected));
      }

      @Test
      public void resolveModelURI_Context() {
         configureContexts();
         Optional<URI> uri = uriConverter.resolveModelURI(requestCtx);
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("Relative path not resolved in the workspace", uri, is(Optional.of(expected)));

         configureContexts(outsideWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(requestCtx);
         assertThat("Absolute path outside the workspace not rejected", uri, is(Optional.empty()));

         configureContexts(inWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(requestCtx);
         assertThat("Absolute path inside the workspace not rejected", uri, is(Optional.empty()));
      }

      @Test
      public void resolveModelURI_WsContext() {
         configureContexts();
         Optional<URI> uri = uriConverter.resolveModelURI(socketCtx);
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("Relative path not resolved in the workspace", uri, is(Optional.of(expected)));

         configureContexts(outsideWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(socketCtx);
         assertThat("Absolute path outside the workspace not rejected", uri, is(Optional.empty()));

         configureContexts(inWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(socketCtx);
         assertThat("Absolute path inside the workspace not rejected", uri, is(Optional.empty()));
      }

      @Test
      public void testDeresolveModelURIContextURI() {
         configureContexts();
         URI uri = uriConverter.deresolveModelURI(requestCtx, inWorkspace("nested", "model.xmi"));
         URI expected = URI.createURI("nested/model.xmi");
         assertThat("URI in the workspace not deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(requestCtx, outsideWorkspace("nested", "model.xmi"));
         // This is a weird consequence of deresolving URIs with Windows device parts
         expected = URI.createURI("/Users/someoneelse/test/place/nested/model.xmi");
         assertThat("URI outside the workspace not deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(requestCtx, URI.createURI("nested/model.xmi"));
         expected = URI.createURI("nested/model.xmi");
         assertThat("Relative URI modified by deresolution", uri, is(expected));
      }

      @Test
      public void testDeresolveModelURIWsContextURI() {
         configureContexts();
         URI uri = uriConverter.deresolveModelURI(socketCtx, inWorkspace("nested", "model.xmi"));
         URI expected = URI.createURI("nested/model.xmi");
         assertThat("URI in the workspace not deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(socketCtx, outsideWorkspace("nested", "model.xmi"));
         // This is a weird consequence of deresolving URIs with Windows device parts
         expected = URI.createURI("/Users/someoneelse/test/place/nested/model.xmi");
         assertThat("URI outside the workspace not deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(socketCtx, URI.createURI("nested/model.xmi"));
         expected = URI.createURI("nested/model.xmi");
         assertThat("Relative URI modified by deresolution", uri, is(expected));
      }
   }

   @RunWith(MockitoJUnitRunner.class)
   public static class ApiV1 extends DefaultModelURIConverterTest {

      public ApiV1() {
         super(API_V1);
      }

      @Test
      public void testNormalizeURI() {
         URI outsideWorkspaceAbsolute = outsideWorkspace("model.xmi");
         URI uri = uriConverter.normalize(outsideWorkspaceAbsolute);
         assertThat(uri, is(outsideWorkspaceAbsolute));

         URI insideWorkspaceAbsolute = inWorkspace("model.xmi");
         uri = uriConverter.normalize(insideWorkspaceAbsolute);
         assertThat(uri, is(insideWorkspaceAbsolute));

         URI relative = URI.createURI("nested/model.xmi");
         URI expected = inWorkspace("nested", "model.xmi");
         uri = uriConverter.normalize(relative);
         assertThat(uri, is(expected));
      }

      @Test
      public void testNormalizeFileURIVariants() {
         URI usualFileURI = inWorkspace("model.xmi");
         System.out.println("URI has authority: " + usualFileURI.authority());
         URI usualNormalized = uriConverter.normalize(usualFileURI);

         URI variantFileURI = URI.createURI(usualFileURI.toString().replace("file:/", "file:///"));
         URI variantNormalized = uriConverter.normalize(variantFileURI);

         assertThat(variantNormalized, is(usualNormalized));
      }

      @Test
      public void resolveModelURI_Context() {
         configureContexts();
         Optional<URI> uri = uriConverter.resolveModelURI(requestCtx);
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("Relative path not resolved in the workspace", uri, is(Optional.of(expected)));

         configureContexts(outsideWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(requestCtx);
         expected = outsideWorkspace("nested", "model.xmi");
         assertThat("Absolute path outside the workspace was rejected", uri, is(Optional.of(expected)));

         configureContexts(inWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(requestCtx);
         expected = inWorkspace("nested", "model.xmi");
         assertThat("Absolute path inside the workspace was rejected", uri, is(Optional.of(expected)));
      }

      @Test
      public void testResolveModelURI_Context_FileURIVariant() {
         configureContexts("file:///C:/Users/junit/workspace/nested/model.xmi");
         Optional<URI> uri = uriConverter.resolveModelURI(requestCtx);

         assertThat(uri, is(Optional.of(URI.createURI("file:/C:/Users/junit/workspace/nested/model.xmi"))));
      }

      @Test
      public void resolveModelURI_WsContext() {
         configureContexts();
         Optional<URI> uri = uriConverter.resolveModelURI(socketCtx);
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("Relative path not resolved in the workspace", uri, is(Optional.of(expected)));

         configureContexts(outsideWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(socketCtx);
         expected = outsideWorkspace("nested", "model.xmi");
         assertThat("Absolute path outside the workspace was rejected", uri, is(Optional.of(expected)));

         configureContexts(inWorkspacePath("nested", "model.xmi"));
         uri = uriConverter.resolveModelURI(socketCtx);
         expected = inWorkspace("nested", "model.xmi");
         assertThat("Absolute path inside the workspace was rejected", uri, is(Optional.of(expected)));
      }

      @Test
      public void testResolveModelURI_WsContext_FileURIVariant() {
         configureContexts("file:///C:/Users/junit/workspace/nested/model.xmi");
         Optional<URI> uri = uriConverter.resolveModelURI(socketCtx);

         assertThat(uri, is(Optional.of(URI.createURI("file:/C:/Users/junit/workspace/nested/model.xmi"))));
      }

      @Test
      public void testDeresolveModelURIContextURI() {
         configureContexts();
         URI uri = uriConverter.deresolveModelURI(requestCtx, inWorkspace("nested", "model.xmi"));
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("URI in the workspace was deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(requestCtx, outsideWorkspace("nested", "model.xmi"));
         expected = outsideWorkspace("nested", "model.xmi");
         assertThat("URI outside the workspace was deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(requestCtx, URI.createURI("nested/model.xmi"));
         expected = URI.createURI("nested/model.xmi");
         assertThat("Relative URI modified by deresolution", uri, is(expected));
      }

      @Test
      public void testDeresolveModelURIWsContextURI() {
         configureContexts();
         URI uri = uriConverter.deresolveModelURI(socketCtx, inWorkspace("nested", "model.xmi"));
         URI expected = inWorkspace("nested", "model.xmi");
         assertThat("URI in the workspace was deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(socketCtx, outsideWorkspace("nested", "model.xmi"));
         expected = outsideWorkspace("nested", "model.xmi");
         assertThat("URI outside the workspace was deresolved", uri, is(expected));

         uri = uriConverter.deresolveModelURI(socketCtx, URI.createURI("nested/model.xmi"));
         expected = URI.createURI("nested/model.xmi");
         assertThat("Relative URI modified by deresolution", uri, is(expected));
      }
   }

   //
   // Test framework
   //

   @Before
   public void createTestFixture() {
      @SuppressWarnings({ "deprecation", "checkstyle:AnonInnerLength" })
      Module testModule = new AbstractModule() {
         @Override
         protected void configure() {
            when(serverConfiguration.getWorkspaceRootURI()).thenReturn(WORKSPACE_ROOT_URI.appendSegment(""));
            binder().bind(ServerConfiguration.class).toInstance(serverConfiguration);

            // This is only needed for API v1 implementation
            if (apiVersion.major() < 2) {
               final DefaultModelResourceManager delegate = new DefaultModelResourceManager(Set.of(), null,
                  serverConfiguration, null, null);
               when(resourceManager.adaptModelUri(ArgumentMatchers.any(String.class))).then(invocation -> {
                  String modelUri = invocation.getArgument(0);
                  return delegate.adaptModelUri(modelUri);
               });
            }
            binder().bind(ModelResourceManager.class).toInstance(resourceManager);

            MapBinding<APIVersionRange, Function<? super URI, Optional<URI>>> resolverBindings = MapBinding
               .create(APIVersionRange.class, new TypeLiteral<Function<? super URI, Optional<URI>>>() {});
            resolverBindings.put(API_V2.range(), DefaultModelURIConverter.APIV2Resolver.class);
            resolverBindings.put(APIVersion.ZERO.range(API_V2), DefaultModelURIConverter.APIV1Resolver.class);
            resolverBindings.setAnnotationName(DefaultModelURIConverter.MODEL_URI_RESOLVERS);
            resolverBindings.applyBinding(binder());

            MapBinding<APIVersionRange, Function<? super URI, URI>> deresolverBindings = MapBinding.create(
               APIVersionRange.class,
               new TypeLiteral<Function<? super URI, URI>>() {});
            deresolverBindings.put(API_V2.range(), DefaultModelURIConverter.APIV2Deresolver.class);
            deresolverBindings.put(APIVersion.ZERO.range(API_V2), DefaultModelURIConverter.APIV1Deresolver.class);
            deresolverBindings.setAnnotationName(DefaultModelURIConverter.MODEL_URI_DERESOLVERS);
            deresolverBindings.applyBinding(binder());

         }
      };

      this.uriConverter = Guice.createInjector(testModule).getInstance(DefaultModelURIConverter.class);
   }

   void configureContexts() {
      // Note the windows separator
      configureContexts("nested\\model.xmi");
   }

   void configureContexts(final String filePath) {
      String apiPath = String.format("/api/v%d/models", apiVersion.major());

      when(requestCtx.matchedPath()).thenReturn(apiPath);
      when(requestCtx.queryParamMap()).thenReturn(Map.of("modeluri", List.of(filePath)));

      when(socketCtx.matchedPath()).thenReturn(apiPath);
      when(socketCtx.queryParamMap()).thenReturn(Map.of("modeluri", List.of(filePath)));
   }

   URI inWorkspace(final String... segments) {
      return WORKSPACE_ROOT_URI.appendSegments(segments);
   }

   URI outsideWorkspace(final String... segments) {
      return OUTSIDE_WORKSPACE_DIRECTORY_URI.appendSegments(segments);
   }

   String inWorkspacePath(final String... segments) {
      StringBuilder result = new StringBuilder(WORKSPACE_ROOT_PATH);
      for (String seg : segments) {
         result.append('\\').append(seg);
      }
      return result.toString();
   }

   String outsideWorkspacePath(final String... segments) {
      StringBuilder result = new StringBuilder(OUTSIDE_WORKSPACE_DIRECTORY_PATH);
      for (String seg : segments) {
         result.append('\\').append(seg);
      }
      return result.toString();
   }

}
