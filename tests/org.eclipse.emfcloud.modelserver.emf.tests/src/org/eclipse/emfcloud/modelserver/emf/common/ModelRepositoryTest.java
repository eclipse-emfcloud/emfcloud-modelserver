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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.ResourceManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Unit tests for the {@link ModelRepository} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class ModelRepositoryTest {

   @Mock
   private ServerConfiguration serverConfig;
   @Mock
   private CommandCodec commandCodec;
   @Mock
   private Command command;

   private ModelRepository repository;

   public ModelRepositoryTest() {
      super();
   }

   @Test
   public void addModel() throws IOException {
      repository.addModel("SuperBrewer3000.json", EcoreFactory.eINSTANCE.createEClass());
      assertTrue(repository.hasModel("SuperBrewer3000.json"));
   }

   @Test
   public void updateModel() throws DecodingException {
      repository.updateModel("SuperBrewer3000.json", CCommandFactory.eINSTANCE.createCommand());
      verify(command).execute();
   }

   @Test
   public void removeModel() throws IOException {
      repository.addModel(getModelUri("Test2.json").toString(), EcoreFactory.eINSTANCE.createEClass());
      assertTrue(repository.hasModel(getModelUri("Test2.json").toString()));
      repository.removeModel(getModelUri("Test2.json").toString());
      assertFalse(repository.hasModel(getModelUri("Test2.json").toString()));
   }

   @Test
   public void hasModel() {
      assertTrue(repository.hasModel(getModelUri("Coffee.ecore").toString()));
      assertTrue(repository.hasModel(getModelUri("Coffee.json").toString()));
      assertTrue(repository.hasModel(getModelUri("Test1.ecore").toString()));
      assertTrue(repository.hasModel(getModelUri("Test1.json").toString()));
      assertFalse(repository.hasModel("SuperBrewer3000.json"));
   }

   @Test
   public void getModel() throws DecodingException, IOException {
      String modelUri = getModelUri("Test1.json").toString();
      assertTrue(repository.hasModel(modelUri));
      repository.getModel(modelUri).ifPresentOrElse(
         result -> {
            assertTrue(((EPackage) result).getName().equals("test1"));
            assertTrue(((EPackage) result).getEClassifiers().size() == 2);
         },
         () -> assertTrue("Model not found in repository", false));
   }

   @Test
   public void getModelElementById() throws DecodingException, IOException {
      String modelUri = getModelUri("Test1.json").toString();
      assertTrue(repository.hasModel(modelUri));
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
      String modelUri = getModelUri("Test1.json").toString();
      assertTrue(repository.hasModel(modelUri));
      repository.getModelElementByName(modelUri, "type").ifPresentOrElse(
         result -> {
            assertTrue(result.eClass().equals(EcorePackage.eINSTANCE.getEAttribute()));
            assertTrue(((EAttribute) result).getName().equals("type"));
            assertTrue(((EAttribute) result).getEType().equals(EcorePackage.eINSTANCE.getEString()));
         },
         () -> assertTrue("Model not found in repository", false));
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

      Set<String> resultSet = repository.getAllModelUris();
      resultSet.equals(expectedModelUriSet);
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
      Set<EPackageConfiguration> configurations = new LinkedHashSet<>();
      configurations.add(
         new EPackageConfiguration() {
            @Override
            public String getId() { return EcorePackage.eINSTANCE.getNsURI(); }

            @Override
            public Collection<String> getFileExtensions() { return Lists.newArrayList("coffee", "json"); }

            @Override
            public void registerEPackage() {
               EcorePackage.eINSTANCE.eClass();
            }
         });
      configurations.add(
         new EPackageConfiguration() {
            @Override
            public String getId() { return CCommandPackage.eINSTANCE.getNsURI(); }

            @Override
            public Collection<String> getFileExtensions() { return Collections.singletonList("command"); }

            @Override
            public void registerEPackage() {
               CCommandPackage.eINSTANCE.eClass();
            }
         });
      ResourceManager resourceManager = new ResourceManager(configurations);
      when(command.canExecute()).thenReturn(true);
      when(commandCodec.decode(any(), any())).thenReturn(command);
      when(serverConfig.getWorkspaceRootURI())
         .thenReturn(URI.createFileURI(getCWD().getAbsolutePath() + "/resources/"));
      repository = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(ResourceManager.class).toInstance(resourceManager);
            bind(CommandCodec.class).toInstance(commandCodec);
            bind(AdapterFactory.class).toInstance(new EcoreAdapterFactory());
         }
      }).getInstance(ModelRepository.class);
   }

   @After
   public void cleanUpResources() throws IOException {
      // Tests create this in the Git workspace, so delete it
      if (repository.hasModel("SuperBrewer3000.json")) {
         repository.getModel("SuperBrewer3000.json").get().eResource().delete(null);
      }
   }

}
