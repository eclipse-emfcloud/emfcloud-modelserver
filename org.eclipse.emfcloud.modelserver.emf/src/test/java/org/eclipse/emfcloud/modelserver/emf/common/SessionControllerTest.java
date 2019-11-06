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
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeeFactory;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.ResourceManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.javalin.websocket.WsContext;

@RunWith(MockitoJUnitRunner.class)
public class SessionControllerTest {

   @Mock
   private ServerConfiguration serverConfig;
   @Mock
   private ResourceManager resourceManager;
   @Mock
   private CommandCodec commandCodec;
   @Mock
   private WsContext validClientCtx;
   @Mock
   private WsContext invalidClientCtx;
   @Mock
   private ModelRepository repository;

   private SessionController sessionController;

   @Test
   public void testSubscribeToValidModelUri() {
      // try to subscribe to a valid modeluri
      //
      initializeValidClientContext();

      assertTrue(sessionController.subscribe(validClientCtx, validClientCtx.pathParam("modeluri")));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));
      verify(validClientCtx).send(argThat(jsonNodeThat(containsRegex("(?i)\"type\":\"success\""))));
   }

   @Test
   public void testSubscribeToInvalidModelUri() {
      // try to subscribe to an invalid modeluri
      //
      initializeInvalidClientContext();

      assertFalse(sessionController.subscribe(invalidClientCtx, invalidClientCtx.pathParam("modeluri")));
      assertFalse(sessionController.isClientSubscribed(invalidClientCtx));
   }

   @Test
   public void testUnsubscribeFromValidSession() {
      // try to subscribe to a valid modeluri
      //
      initializeValidClientContext();
      assertTrue(sessionController.subscribe(validClientCtx, validClientCtx.pathParam("modeluri")));
      assertTrue(sessionController.isClientSubscribed(validClientCtx));

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
   public void testCommandSubscription()
      throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

      initializeValidClientContext();
      when(repository.getModel("fancytesturi")).thenReturn(Optional.of(CoffeeFactory.eINSTANCE.createMachine()));

      sessionController.subscribe(validClientCtx, validClientCtx.pathParam("modeluri"));
      CCommand command = CCommandFactory.eINSTANCE.createCommand();
      command.setType(CommandKind.SET);
      sessionController.modelChanged("fancytesturi", command);

      verify(validClientCtx).send(argThat(jsonNodeThat(containsRegex("(?i)\"type\":\"set\""))));
   }

   //
   // Test framework
   //

   private void initializeValidClientContext() {
      when(validClientCtx.getSessionId()).thenReturn(UUID.randomUUID().toString());
      when(validClientCtx.pathParam("modeluri")).thenReturn("fancytesturi");
      when(repository.hasModel("fancytesturi")).thenReturn(true);
   }

   private void initializeInvalidClientContext() {
      when(invalidClientCtx.pathParam("modeluri")).thenReturn("tedioustesturi");
   }

   @Before
   public void createSessionController() {
      sessionController = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(ResourceManager.class).toInstance(resourceManager);
            bind(CommandCodec.class).toInstance(commandCodec);
            bind(ModelRepository.class).toInstance(repository);
         }
      }).getInstance(SessionController.class);

      // Mock sessions are always open
      sessionController.setIsOnlyPredicate(ctx -> true);
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
