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
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.jetty.websocket.api.Session;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;

@RunWith(MockitoJUnitRunner.class)
public class SessionControllerTest {

   @Mock
   private ServerConfiguration serverConfig;
   @Mock
   private CommandCodec commandCodec;
   @Mock
   private WsContext validClientCtx;
   @Mock
   private WsContext invalidClientCtx;
   @Mock
   private WsMessageContext messageClientCtx;
   @Mock
   private Session session;
   @Mock
   private ModelRepository repository;
   @Mock
   private ModelResourceManager modelResourceManager;
   @Mock
   private CodecsManager codecs;

   private SessionController sessionController;

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testSubscribeToValidModelUri() throws NoSuchFieldException, SecurityException {
      // try to subscribe to a valid modeluri
      //
      String sessionId = UUID.randomUUID().toString();
      initializeValidClientContext(sessionId);

      assertTrue(
         sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex("\"type\":\"success\",\"data\":\"" + sessionId + "\""))));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testSubscribeToInvalidModelUri() throws NoSuchFieldException, SecurityException {
      // try to subscribe to an invalid modeluri
      //
      initializeInvalidClientContext();

      assertFalse(sessionController.subscribe(invalidClientCtx,
         invalidClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)));
      assertFalse(sessionController.isClientSubscribed(invalidClientCtx));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testUnsubscribeFromValidSession() throws NoSuchFieldException, SecurityException {
      // try to subscribe to a valid modeluri
      //
      String sessionId = UUID.randomUUID().toString();
      initializeValidClientContext(sessionId);
      assertTrue(
         sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex("\"type\":\"success\",\"data\":\"" + sessionId + "\""))));

      // try to unsubscribe from this valid session
      //
      assertTrue(sessionController.isClientSubscribed(validClientCtx));
      assertTrue(sessionController.unsubscribe(validClientCtx));
      assertFalse(sessionController.isClientSubscribed(validClientCtx));
   }

   @Test
   public void testUnsubscribeFromInvalidSession() {
      // try to unsubscribe from an invalid session
      //
      assertFalse(sessionController.isClientSubscribed(invalidClientCtx));
      assertFalse(sessionController.unsubscribe(invalidClientCtx));
   }

   @Ignore
   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testCommandSubscription() throws NoSuchFieldException, SecurityException {
      String sessionId = UUID.randomUUID().toString();
      initializeValidClientContext(sessionId);
      when(validClientCtx.session.isOpen()).thenReturn(true);
      when(repository.getModel("fancytesturi")).thenReturn(Optional.of(EcoreFactory.eINSTANCE.createEClass()));

      sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParameters.MODEL_URI));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"success\",\"data\":\"" + sessionId + "\"."))));

      CCommand command = CCommandFactory.eINSTANCE.createCommand();
      command.setType(CommandKind.SET);
      sessionController.modelChanged("fancytesturi", command);

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"incrementalUpdate\",\"data\":.\"type\":\"SET\".*"))));

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"dirtyState\",\"data\":true."))));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testSubscribeAndSendKeepAlive() throws NoSuchFieldException, SecurityException {
      // try to subscribe to a valid modeluri
      //
      String sessionId = UUID.randomUUID().toString();
      initializeWsMessageContext(sessionId);
      assertTrue(sessionController.subscribe(messageClientCtx,
         messageClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)));
      verify(messageClientCtx)
         .send(argThat(jsonNodeThat(containsRegex(
            "(?i)\"type\":\"success\",\"data\":\"" + sessionId + "\""))));
      assertTrue(sessionController.isClientSubscribed(messageClientCtx));
      // client sends keepAlive message
      when(messageClientCtx.message()).thenReturn("{\"type\":\"keepAlive\",\"data\":\"\"}");
      assertTrue(sessionController.handleMessage(messageClientCtx));
      verify(messageClientCtx)
         .send(argThat(jsonNodeThat(containsRegex(
            "(?i)\"type\":\"keepAlive\",\"data\":\"" + sessionId + " stayin' alive!\""))));
   }

   //
   // Test framework
   //
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   private void initializeValidClientContext(final String sessionId) throws NoSuchFieldException, SecurityException {
      when(validClientCtx.getSessionId()).thenReturn(sessionId);
      when(validClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)).thenReturn("fancytesturi");
      Field sessionField = WsContext.class.getDeclaredField("session");
      FieldSetter.setField(validClientCtx, sessionField, session);

      when(repository.hasModel("fancytesturi")).thenReturn(true);
   }

   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   private void initializeWsMessageContext(final String sessionId) throws NoSuchFieldException, SecurityException {
      when(messageClientCtx.getSessionId()).thenReturn(sessionId);
      when(messageClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)).thenReturn("fancytesturi");
      when(repository.hasModel("fancytesturi")).thenReturn(true);

      Field sessionField = WsContext.class.getDeclaredField("session");
      FieldSetter.setField(messageClientCtx, sessionField, session);
   }

   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   private void initializeInvalidClientContext() throws NoSuchFieldException, SecurityException {
      when(invalidClientCtx.pathParam(ModelServerPathParameters.MODEL_URI)).thenReturn("tedioustesturi");
      FieldSetter.setField(invalidClientCtx, WsContext.class.getDeclaredField("session"), session);
   }

   @Before
   public void createSessionController() {
      sessionController = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(CommandCodec.class).toInstance(commandCodec);
            bind(ModelRepository.class).toInstance(repository);
            bind(ModelResourceManager.class).toInstance(modelResourceManager);
            bind(CodecsManager.class).toInstance(codecs);
         }
      }).getInstance(SessionController.class);

   }

   Matcher<Object> jsonNodeThat(final Matcher<String> data) {
      return new TypeSafeDiagnosingMatcher<>() {
         @Override
         public void describeTo(final Description description) {
            description.appendText("JsonNode that ");
            description.appendDescriptionOf(data);
         }

         @Override
         protected boolean matchesSafely(final Object item, final Description mismatchDescription) {
            if (!(item instanceof JsonNode)) {
               return false;
            }
            JsonNode node = (JsonNode) item;
            String text = node.toString();
            if (!data.matches(text)) {
               data.describeMismatch(text, mismatchDescription);
               return false;
            }
            return true;
         }
      };
   }

   Matcher<String> containsRegex(final String pattern) {
      return new CustomTypeSafeMatcher<>("contains regex '" + pattern + "'") {
         @Override
         protected boolean matchesSafely(final String item) {
            java.util.regex.Matcher m = Pattern.compile(pattern).matcher(item);
            return m.find();
         }
      };
   }

}
