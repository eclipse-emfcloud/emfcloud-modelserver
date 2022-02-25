/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatchersManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.CommandPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EcorePackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.util.JsonPatchHelper;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

@RunWith(MockitoJUnitRunner.class)
public class DefaultModelResourceManagerTest extends AbstractResourceTest {

   private static ModelResourceManager modelResourceManager;

   @Mock
   private ModelWatchersManager watchersManager;

   @Mock
   private CommandCodec commandCodec;

   @Mock
   private ServerConfiguration serverConfig;

   @Mock(answer = Answers.RETURNS_MOCKS)
   private JsonPatchHelper jsonPatchHelper;

   public DefaultModelResourceManagerTest() {
      super();
   }

   @Before
   public void beforeTests() throws DecodingException {
      when(serverConfig.getWorkspaceRootURI())
         .thenReturn(URI.createFileURI(getCWD().getAbsolutePath() + "/" + RESOURCE_PATH));
      modelResourceManager = Guice.createInjector(new AbstractModule() {

         private Multibinder<EPackageConfiguration> ePackageConfigurationBinder;
         private ArrayList<Class<? extends EPackageConfiguration>> ePackageConfigurations;

         @Override
         protected void configure() {
            ePackageConfigurations = Lists.newArrayList(
               EcorePackageConfiguration.class,
               CommandPackageConfiguration.class);
            ePackageConfigurationBinder = Multibinder.newSetBinder(binder(), EPackageConfiguration.class);
            ePackageConfigurations.forEach(c -> ePackageConfigurationBinder.addBinding().to(c));

            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(CommandCodec.class).toInstance(commandCodec);
            bind(ModelWatchersManager.class).toInstance(watchersManager);
            bind(AdapterFactory.class).toInstance(new EcoreAdapterFactory());
            bind(ModelRepository.class).to(DefaultModelRepository.class).in(Scopes.SINGLETON);
            bind(ModelResourceManager.class).to(DefaultModelResourceManager.class).in(Scopes.SINGLETON);
            bind(JsonPatchHelper.class).toInstance(jsonPatchHelper);
         }
      }).getInstance(DefaultModelResourceManager.class);
   }

   @Test
   public void testLoadModelFromJson() throws IOException {
      Resource expectedResource = loadResource("Test1.ecore");
      Optional<Resource> result = modelResourceManager.loadResource(adaptModelUri("Test1.json"));
      assertTrue(result.isPresent());
      assertTrue(EcoreUtil.equals(expectedResource.getContents(), result.get().getContents()));
   }

   @Test
   public void testLoadModelFromEcore() throws IOException {
      Resource expectedResource = loadResource("Coffee.ecore");
      Optional<Resource> result = modelResourceManager.loadResource(adaptModelUri("Coffee.ecore"));
      assertTrue(result.isPresent());
      assertTrue(EcoreUtil.equals(expectedResource.getContents(), result.get().getContents()));
   }

   @Test
   public void testLoadModelFromInvalidModelUri() throws IOException {
      assertTrue(modelResourceManager.loadResource(adaptModelUri("Test2.ecore")).isEmpty());
   }

   @Test
   public void testLoadModelCastToExactType() {
      Optional<EPackage> result = modelResourceManager.loadModel(adaptModelUri("Test1.ecore"), EPackage.class);
      assertNotNull(result);
      assertTrue(result.isPresent());
      assertEquals("test1", result.get().getName());
   }

   @Test
   public void testLoadModelCastToSupertype() {
      Optional<EObject> result = modelResourceManager.loadModel(adaptModelUri("Test1.ecore"), EObject.class);
      assertNotNull(result);
      assertTrue(result.isPresent());
   }

   @Test
   public void testLoadModelInvalidCast() {
      Optional<EClass> result = modelResourceManager.loadModel(adaptModelUri("Test1.ecore"), EClass.class);
      assertNotNull(result);
      assertFalse(result.isPresent());
   }

   @Test
   public void addResource() throws IOException {
      String modelUri = "SuperBrewer3000.json";
      modelResourceManager.addResource(modelUri, EcoreFactory.eINSTANCE.createEClass());
      assertTrue(modelResourceManager.isResourceLoaded(modelUri));

      cleanUpResource(modelUri);
   }

   @Test
   public void deleteResource() throws IOException {
      modelResourceManager.addResource(adaptModelUri("Test2.json").toString(), EcoreFactory.eINSTANCE.createEClass());
      assertTrue(modelResourceManager.isResourceLoaded(adaptModelUri("Test2.json").toString()));
      modelResourceManager.deleteResource(adaptModelUri("Test2.json").toString());
      assertFalse(modelResourceManager.isResourceLoaded(adaptModelUri("Test2.json").toString()));
   }

   @Test
   public void hasResource() {
      assertTrue(modelResourceManager.isResourceLoaded(adaptModelUri("Coffee.ecore").toString()));
      assertTrue(modelResourceManager.isResourceLoaded(adaptModelUri("Coffee.json").toString()));
      assertTrue(modelResourceManager.isResourceLoaded(adaptModelUri("Test1.ecore").toString()));
      assertTrue(modelResourceManager.isResourceLoaded(adaptModelUri("Test1.json").toString()));
   }

   @Test
   public void saveResource() {
      assertTrue(modelResourceManager.save(adaptModelUri("Coffee.ecore").toString()));
   }

   @Test
   public void saveNonExistingResource() {
      assertFalse(modelResourceManager.save(adaptModelUri("NotExisting.ecore").toString()));
   }

   @Test
   public void hasResourceNot() {
      assertFalse(modelResourceManager.isResourceLoaded("SuperBrewer3000.json"));
   }

   @Test
   public void loadModel() throws DecodingException, IOException {
      String modelUri = adaptModelUri("Test1.json").toString();
      assertTrue(modelResourceManager.isResourceLoaded(modelUri));
      modelResourceManager.loadModel(modelUri, EObject.class).ifPresentOrElse(
         result -> {
            assertTrue(((EPackage) result).getName().equals("test1"));
            assertTrue(((EPackage) result).getEClassifiers().size() == 2);
         },
         () -> assertTrue("Model not found in repository", false));
   }

   // The JsonPatchHelper is injected via a Provider because the base implementation
   // has a dependency on the ModelResourceManager (mutually dependent)
   @Test
   public void jsonPatchHelperInjection() throws IOException, JsonPatchException, JsonPatchTestException {
      String modelURI = adaptModelUri("Coffee.ecore");
      modelResourceManager.loadResource(modelURI);
      ArrayNode jsonPatch = Json.array(Json.object(Map.of(
         "op", Json.text("replace"),
         "path", Json.text("/name"),
         "value", Json.text("joe"))));
      modelResourceManager.execute(modelURI, jsonPatch);

      verify(jsonPatchHelper).getCommand(eq(modelURI), any(ResourceSet.class), eq(jsonPatch));
   }

   // Test framework
   private static File getCWD() { return new File(System.getProperty("user.dir")); }

   private static String adaptModelUri(final String modelUri) {
      return URI.createFileURI(getCWD().getAbsolutePath() + "/" + RESOURCE_PATH + modelUri).toString();
   }

   private void cleanUpResource(final String modelUri) {
      if (modelResourceManager.isResourceLoaded(modelUri)) {
         modelResourceManager.loadModel(modelUri, EObject.class).ifPresent(r -> {
            try {
               r.eResource().delete(null);
            } catch (IOException e) {
               e.printStackTrace();
            }
         });
      }
   }
}
