/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.patch;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

@RunWith(MockitoJUnitRunner.class)
public class EMFCommandHandlerTest {

   private static final String MODEL_URI = "http://bogus/model.xmi";

   @Mock
   private Context requestContext;

   @Mock
   private WsContext socketContext;

   @Mock
   private CodecsManager codecs;

   @Mock
   private ModelURIConverter uriConverter;

   private EMFCommandHandler commandHandler;

   public EMFCommandHandlerTest() {
      super();
   }

   @Test
   public void getPatchCommand_request_modelURI() throws DecodingException {
      commandHandler.getPatchCommand(requestContext, Json.object());

      verify(codecs).decode(ArgumentMatchers.eq(MODEL_URI), ArgumentMatchers.same(requestContext),
         ArgumentMatchers.any());
   }

   @Test
   public void getPatchCommand_socket_modelURI() throws DecodingException {
      commandHandler.getPatchCommand(socketContext, Json.object());

      verify(codecs).decode(ArgumentMatchers.eq(MODEL_URI), ArgumentMatchers.same(socketContext),
         ArgumentMatchers.any());
   }

   //
   // Test framework
   //

   @Before
   public void configureMocks() throws DecodingException {
      when(uriConverter.resolveModelURI(requestContext))
         .thenReturn(Optional.of(URI.createURI(MODEL_URI)));
      when(uriConverter.resolveModelURI(socketContext))
         .thenReturn(Optional.of(URI.createURI(MODEL_URI)));
      when(codecs.decode(ArgumentMatchers.anyString(), ArgumentMatchers.any(Context.class), ArgumentMatchers.any()))
         .thenReturn(Optional.empty());
      when(codecs.decode(ArgumentMatchers.anyString(), ArgumentMatchers.any(WsContext.class), ArgumentMatchers.any()))
         .thenReturn(Optional.empty());
   }

   @Before
   public void createTestSubject() {
      this.commandHandler = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(CodecsManager.class).toInstance(codecs);
            bind(ModelURIConverter.class).toInstance(uriConverter);
         }
      }).getInstance(EMFCommandHandler.class);
   }

}
