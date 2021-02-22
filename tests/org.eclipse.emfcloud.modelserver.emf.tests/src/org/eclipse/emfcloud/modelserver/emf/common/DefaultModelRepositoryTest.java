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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.AbstractResourceTest;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Unit tests for the {@link ModelRepository} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultModelRepositoryTest extends AbstractResourceTest {

   @Mock
   private ServerConfiguration serverConfig;
   @Mock
   private CommandCodec commandCodec;
   @Mock
   private Command command;
   @Mock
   private ModelResourceManager modelResourceManager;

   private DefaultModelRepository repository;

   public DefaultModelRepositoryTest() {
      super();
   }

   @Test
   public void getAllModels() throws DecodingException, IOException {
      Set<URI> expectedModelUriSet = new HashSet<>();
      expectedModelUriSet.add(getModelUri("Coffee.ecore"));
      expectedModelUriSet.add(getModelUri("Coffee.json"));
      expectedModelUriSet.add(getModelUri("Test1.ecore"));
      expectedModelUriSet.add(getModelUri("Test1.json"));

      Map<URI, EObject> resultMap = repository.getAllModels();
      resultMap.keySet().equals(expectedModelUriSet);
   }

   @Test
   public void getAllModelUris() throws DecodingException, IOException {
      Set<String> expectedModelUriSet = new HashSet<>();
      expectedModelUriSet.add(getModelUri("Coffee.ecore").toString());
      expectedModelUriSet.add(getModelUri("Coffee.json").toString());
      expectedModelUriSet.add(getModelUri("Test1.ecore").toString());
      expectedModelUriSet.add(getModelUri("Test1.json").toString());

      Set<String> resultSet = repository.getRelativeModelUris();
      resultSet.equals(expectedModelUriSet);
   }

   @Test
   public void getModelElementById() throws DecodingException, IOException {
      String modelUri = getModelUri("Test1.ecore").toString();
      Resource testResource = loadResource("Test1.ecore");
      when(modelResourceManager.loadResource(modelUri)).thenReturn(Optional.of(testResource));
      repository.getModelElementById(modelUri, "//@eClassifiers.0/@eStructuralFeatures.0").ifPresentOrElse(
         result -> {
            assertTrue(result.eClass().equals(EcorePackage.eINSTANCE.getEAttribute()));
            assertTrue(((EAttribute) result).getName().equals("Name"));
            assertTrue(((EAttribute) result).getEType().equals(EcorePackage.eINSTANCE.getEString()));
         },
         () -> assertTrue("Model not found in repository", false));
   }

   @Test
   public void getModelElementByName() throws DecodingException, IOException {
      String modelUri = getModelUri("Test1.ecore").toString();
      Resource testResource = loadResource("Test1.ecore");
      when(modelResourceManager.loadResource(modelUri)).thenReturn(Optional.of(testResource));
      repository.getModelElementByName(modelUri, JsonResponseMember.TYPE).ifPresentOrElse(
         result -> {
            assertTrue(result.eClass().equals(EcorePackage.eINSTANCE.getEAttribute()));
            assertTrue(((EAttribute) result).getName().equals(JsonResponseMember.TYPE));
            assertTrue(((EAttribute) result).getEType().equals(EcorePackage.eINSTANCE.getEString()));
         },
         () -> assertTrue("Model not found in repository", false));
   }

   //
   // Test framework
   //

   static File getCWD() { return new File(System.getProperty("user.dir")); }

   private URI getModelUri(final String modelFileName) {
      return URI.createFileURI(getCWD() + "/resources/" + modelFileName);
   }

   @Before
   public void createRepository() throws DecodingException {
      repository = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(ModelResourceManager.class).toInstance(modelResourceManager);
         }
      }).getInstance(DefaultModelRepository.class);
   }

}
