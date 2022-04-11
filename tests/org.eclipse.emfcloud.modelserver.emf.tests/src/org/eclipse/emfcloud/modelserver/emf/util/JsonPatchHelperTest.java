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
package org.eclipse.emfcloud.modelserver.emf.util;

import static org.eclipse.emfcloud.modelserver.tests.util.MoreMatchers.emptyOptional;
import static org.eclipse.emfcloud.modelserver.tests.util.MoreMatchers.presentValueThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.MultiBindingDefaults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.javalin.websocket.WsContext;

@RunWith(MockitoJUnitRunner.class)
public class JsonPatchHelperTest {

   private final URI workspaceURI;
   private final URI modelURI;

   private ResourceSet resourceSet;

   @Mock
   private ServerConfiguration serverConfiguration;

   @Mock
   private ModelResourceManager modelResourceManager;

   @Mock(answer = Answers.CALLS_REAL_METHODS)
   private ModelURIConverter modelURIConverter;

   @Mock
   private WsContext session;

   private JsonPatchHelper patchHelper;

   public JsonPatchHelperTest() {
      super();

      File file = new File("resources/Coffee.ecore");
      URI uri = URI.createFileURI(file.getAbsolutePath());
      modelURI = uri;
      workspaceURI = modelURI.trimSegments(1).appendSegment(""); // Ensure trailing separator
   }

   @Test
   public void toURIFragmentPath() {
      Optional<String> path = patchHelper.toURIFragmentPath(session,
         "/eClassifiers/2/eStructuralFeatures/3/name");
      // The Object URI part must include the workspace-relative resource URI
      assertThat(path, presentValueThat(is("Coffee.ecore#//ControlUnit/display/name")));
   }

   @Test
   public void toURIFragmentPath_unresolved() {
      Optional<String> path = patchHelper.toURIFragmentPath(session,
         "/eClassifiers/2/eBogusFeatures/3/name");
      assertThat(path, emptyOptional());
   }

   //
   // Test framework
   //

   @Before
   public void createModelFixture() {
      resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
      resourceSet.getResource(modelURI, true);
   }

   @Before
   public void configureMocks() {
      when(modelURIConverter.normalize(modelURI)).thenReturn(modelURI);
      when(modelURIConverter.resolveModelURI(session)).thenReturn(Optional.of(modelURI));
      when(modelURIConverter.deresolveModelURI(ArgumentMatchers.eq(session), ArgumentMatchers.any(URI.class))).then(
         invocation -> invocation.<URI> getArgument(1).deresolve(workspaceURI));
      when(modelResourceManager.loadResource(ArgumentMatchers.anyString())).then(
         invocation -> Optional.ofNullable(resourceSet.getResource(URI.createURI(invocation.getArgument(0)), true)));
      when(modelResourceManager.getResourceSet(modelURI.toString())).thenReturn(resourceSet);
   }

   @Before
   public void createTestSubject() {
      patchHelper = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfiguration);
            bind(ModelResourceManager.class).toInstance(modelResourceManager);
            bind(ModelURIConverter.class).toInstance(modelURIConverter);

            MapBinding<String, Codec> codecBinding = MapBinding.create(String.class, Codec.class);
            codecBinding.putAll(MultiBindingDefaults.DEFAULT_CODECS);
            codecBinding.applyBinding(binder());
         }
      }).getInstance(JsonPatchHelper.class);
   }
}
