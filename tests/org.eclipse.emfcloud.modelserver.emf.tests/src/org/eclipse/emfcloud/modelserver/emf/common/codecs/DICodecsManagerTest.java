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
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

@RunWith(MockitoJUnitRunner.class)
public class DICodecsManagerTest {

   private static final URI WORKSPACE_URI = URI.createURI("bogus://my/workspace/directory/");
   private static final URI MODEL_URI = URI.createURI("model.xml").resolve(WORKSPACE_URI);

   private DICodecsManager codecsManager;

   @Mock
   private ModelURIConverter uriConverter;

   @Mock
   private Context requestCtx;

   @Mock
   private WsContext sessionCtx;

   private final Map<String, List<String>> queryParams = Map.of();
   private String modelUri;
   private String json;

   public DICodecsManagerTest() {
      super();
   }

   @SuppressWarnings("deprecation")
   @Test
   public void encode_context() throws EncodingException {
      codecsManager.encode(modelUri, requestCtx, EcorePackage.Literals.EANNOTATION__SOURCE);

      verify(codecsManager).findCodec(modelUri, queryParams);
   }

   @Test
   public void encode_wscontext() throws EncodingException {
      codecsManager.encode(modelUri, sessionCtx, EcorePackage.Literals.EANNOTATION__SOURCE);

      verify(codecsManager).findCodec(modelUri, sessionCtx);
   }

   @SuppressWarnings("deprecation")
   @Test
   public void decode_context() throws DecodingException {
      codecsManager.decode(modelUri, requestCtx, json);

      verify(codecsManager).findCodec(modelUri, queryParams);
   }

   @Test
   public void decode_wscontext() throws DecodingException {
      codecsManager.decode(modelUri, sessionCtx, json);

      verify(codecsManager).findCodec(modelUri, sessionCtx);
   }

   @SuppressWarnings("deprecation")
   @Test
   public void decode_contextWithWorkspaceURI() throws DecodingException {
      codecsManager.decode(requestCtx, json, WORKSPACE_URI);

      // We looked up the codec by Model URI, not workspace URI
      verify(codecsManager).findCodec(modelUri, queryParams);
   }

   @Test
   public void decode_wscontextWithWorkspaceURI() throws DecodingException {
      codecsManager.decode(sessionCtx, json, WORKSPACE_URI);

      // We looked up the codec by Model URI, not workspace URI
      verify(codecsManager).findCodec(modelUri, sessionCtx);
   }

   //
   // Test framework
   //

   @Before
   public void before() throws NoSuchFieldException, SecurityException {
      when(requestCtx.queryParamMap()).thenReturn(queryParams);
      when(uriConverter.resolveModelURI(requestCtx)).thenReturn(Optional.of(MODEL_URI));
      when(uriConverter.resolveModelURI(sessionCtx)).thenReturn(Optional.of(MODEL_URI));
      modelUri = MODEL_URI.toString();

      json = Json.object().set("eClass", Json.text(EcoreUtil.getURI(EcorePackage.Literals.EPACKAGE).toString()))
         .toString();

      when(requestCtx.matchedPath()).thenReturn("/api/v1"); // This is all we need of the path for these tests

      codecsManager = spy(Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            super.configure();

            bind(ModelURIConverter.class).toInstance(uriConverter);
            bind(new TypeLiteral<Set<CodecProvider>>() {}).toInstance(Set.of(new DefaultCodecsProvider()));
         }
      }).getInstance(DICodecsManager.class));
   }

}
