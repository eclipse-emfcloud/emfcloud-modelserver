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

import static org.eclipse.emfcloud.modelserver.jsonschema.Json.prop;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandExecutionType;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.eclipse.jetty.websocket.api.Session;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
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
public class DefaultSessionControllerTest {

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
   @Mock
   private ModelValidator modelValidator;

   private DefaultSessionController sessionController;

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testSubscribeToValidModelUri() throws NoSuchFieldException, SecurityException {
      // try to subscribe to a valid modeluri
      //
      String sessionId = UUID.randomUUID().toString();
      initializeValidClientContext(sessionId);

      assertTrue(
         sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex("\"type\":\"success\",\"data\":\"" + sessionId + "\""))));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"dirtyState\",\"data\":false."))));
   }

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testSubscribeToInvalidModelUri() throws NoSuchFieldException, SecurityException {
      // try to subscribe to an invalid modeluri
      //
      initializeInvalidClientContext();

      assertFalse(sessionController.subscribe(invalidClientCtx,
         invalidClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)));
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
         sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex("\"type\":\"success\",\"data\":\"" + sessionId + "\""))));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"dirtyState\",\"data\":false."))));

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

   @Test
   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   public void testCommandSubscription() throws NoSuchFieldException, SecurityException, EncodingException {
      String sessionId = UUID.randomUUID().toString();
      String modelUri = "fancytesturi";

      initializeValidClientContext(sessionId);
      when(validClientCtx.session.isOpen()).thenReturn(true);
      when(repository.getModel(modelUri)).thenReturn(Optional.of(EcoreFactory.eINSTANCE.createEClass()));

      sessionController.subscribe(validClientCtx, validClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"success\",\"data\":\"" + sessionId + "\"."))));
      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"dirtyState\",\"data\":false."))));

      CCommand command = CCommandFactory.eINSTANCE.createCommand();
      command.setType(EMFCommandType.SET);

      Map<String, JsonNode> encodings = new HashMap<>();
      JsonNode expectedCommand = Json.object(
         prop("type", Json.text("execute")),
         prop("source", Json.object(
            prop("eClass", Json.text(EcoreUtil.getURI(CCommandPackage.Literals.COMMAND).toString())),
            prop("type", Json.text(command.getType().toString())),
            prop("owner", Json.object(
               prop("eClass", Json.text("")),
               prop("$ref", Json.text("")))),
            prop("feature", Json.text("")),
            prop("dataValues", Json.array(Json.text(""))))),
         prop("details", Json.array(
            Json.object(Json.prop("myCustomDetail", Json.text("test"))),
            Json.object(Json.prop("fancyInfo", Json.text("more testing"))))));
      encodings.put(ModelServerPathParametersV1.FORMAT_JSON, expectedCommand);

      when(repository.getDirtyState(modelUri)).thenReturn(true);

      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();
      result.setSource(EcoreUtil.copy(command));
      result.setType(CommandExecutionType.EXECUTE);
      result.getDetails().put("myCustomDetail", "test");
      result.getDetails().put("fancyInfo", "more testing");

      when(codecs.encode(result)).thenReturn(encodings);
      sessionController.commandExecuted(modelUri, result);

      verify(validClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"incrementalUpdate\",\"data\":.*\"type\":\"execute\".*"))));
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
         messageClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)));

      verify(messageClientCtx)
         .send(argThat(jsonNodeThat(containsRegex(
            "(?i)\"type\":\"success\",\"data\":\"" + sessionId + "\""))));
      verify(messageClientCtx).send(argThat(jsonNodeThat(
         containsRegex(".\"type\":\"dirtyState\",\"data\":false."))));

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
      String modelUri = "fancytesturi";

      when(repository.getDirtyState(modelUri)).thenReturn(false);
      when(validClientCtx.getSessionId()).thenReturn(sessionId);
      when(validClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)).thenReturn(modelUri);
      Field sessionField = WsContext.class.getDeclaredField("session");
      FieldSetter.setField(validClientCtx, sessionField, session);

      when(codecs.findFormat(validClientCtx)).thenReturn(ModelServerPathParametersV1.FORMAT_JSON);

      when(repository.hasModel(modelUri)).thenReturn(true);
   }

   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   private void initializeWsMessageContext(final String sessionId) throws NoSuchFieldException, SecurityException {
      String modelUri = "fancytesturi";

      when(messageClientCtx.getSessionId()).thenReturn(sessionId);
      when(messageClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)).thenReturn(modelUri);
      when(repository.hasModel(modelUri)).thenReturn(true);

      Field sessionField = WsContext.class.getDeclaredField("session");
      FieldSetter.setField(messageClientCtx, sessionField, session);
   }

   @SuppressWarnings({ "checkstyle:ThrowsCount" })
   private void initializeInvalidClientContext() throws NoSuchFieldException, SecurityException {
      String modelUri = "tedioustesturi";

      when(invalidClientCtx.pathParam(ModelServerPathParametersV1.MODEL_URI)).thenReturn(modelUri);
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
            bind(ModelValidator.class).toInstance(modelValidator);
         }
      }).getInstance(DefaultSessionController.class);
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
