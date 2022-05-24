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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerEditingDomain;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.MultiBindingDefaults;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.javalin.websocket.WsContext;

@RunWith(MockitoJUnitRunner.class)
public class JsonPatchHelperTest {

   private final URI modelURI;

   private ResourceSet resourceSet;
   private EPackage coffeePackage;

   @Mock(answer = Answers.RETURNS_MOCKS)
   private ModelServerEditingDomain editingDomain;

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
   }

   @Test
   public void getObjectURIFunction_replace() {
      EClass componentEClass = (EClass) coffeePackage.getEClassifiers().get(0);
      // The model didn't have this, yet.
      componentEClass.setInstanceClassName("org.eclipse.emfcloud.coffee.model.Component");

      ArrayNode diff = diffModel(ePackage -> {
         componentEClass.setInstanceClassName("org.eclipse.emfcloud.mycoffee.Component");
      });

      Assume.assumeThat("Too many operations", diff.size(), is(1));

      JsonNode operation = diff.get(0);
      assertThat(operation.get("op").textValue(), is("replace"));

      String uri = uriOf(diff, operation);

      // Recall that Ecore resources use URI fragments that are path-structured already
      assertThat(uri, endsWith("Coffee.ecore#//Component/instanceClassName"));
   }

   @Test
   public void getObjectURIFunction_add() {
      ArrayNode diff = diffModel(ePackage -> {
         EClass componentEClass = (EClass) coffeePackage.getEClassifiers().get(0);
         EStructuralFeature newFeature = EcoreFactory.eINSTANCE.createEAttribute();
         newFeature.setName("dummy");
         newFeature.setEType(EcorePackage.Literals.ESTRING);
         componentEClass.getEStructuralFeatures().add(newFeature);
      });

      Assume.assumeThat("Too many operations", diff.size(), is(1));

      JsonNode operation = diff.get(0);
      assertThat(operation.get("op").textValue(), is("add"));

      String uri = uriOf(diff, operation);

      // Recall that Ecore resources use URI fragments that are path-structured already
      assertThat(uri, endsWith("Coffee.ecore#//Component/eStructuralFeatures/-"));
   }

   @Test
   public void getObjectURIFunction_remove() {
      ArrayNode diff = diffModel(ePackage -> {
         EClass componentEClass = (EClass) coffeePackage.getEClassifiers().get(0);
         EStructuralFeature activities = componentEClass.getEStructuralFeatures().get(2);

         EcoreUtil.remove(activities);
      });

      Assume.assumeThat("Too many operations", diff.size(), is(1));

      JsonNode operation = diff.get(0);
      assertThat(operation.get("op").textValue(), is("remove"));

      String uri = uriOf(diff, operation);

      // Recall that Ecore resources use URI fragments that are path-structured already
      assertThat(uri, endsWith("Coffee.ecore#//Component/eStructuralFeatures/2"));
   }

   //
   // Test framework
   //

   @Before
   public void createModelFixture() {
      resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
      Resource resource = resourceSet.getResource(modelURI, true);
      coffeePackage = (EPackage) resource.getContents().get(0);
   }

   @Before
   public void configureMocks() {
      when(modelURIConverter.normalize(modelURI)).thenReturn(modelURI);
      when(modelResourceManager.getEditingDomain(resourceSet)).thenReturn(editingDomain);
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
      patchHelper.setNeedObjectURIMappings(true);
   }

   ArrayNode diffModel(final Consumer<? super EPackage> modelEdit) {
      try {
         ChangeRecorder recorder = new ChangeRecorder(resourceSet);
         modelEdit.accept(coffeePackage);
         ChangeDescription change = recorder.endRecording();
         CCommandExecutionResult exec = CCommandFactory.eINSTANCE.createCommandExecutionResult();
         exec.setChangeDescription(change);

         Map<URI, JsonNode> patches = patchHelper.getJsonPatches(coffeePackage, exec);

         JsonNode result = patches.get(coffeePackage.eResource().getURI());
         assumeThat("No diffcalculated", result, notNullValue());
         assumeThat("Diff is not a patch", result.isArray(), is(true));
         return (ArrayNode) result;
      } catch (EncodingException e) {
         e.printStackTrace();

         fail("Failed to generate diff");

         return null; // Unreachable
      }
   }

   String uriOf(final JsonNode diff, final JsonNode operation) {
      Function<JsonNode, URI> uriFunction = patchHelper.getObjectURIFunction(diff);
      assertThat("No URI function", uriFunction, notNullValue());

      URI result = uriFunction.apply(operation);

      return result == null ? null : result.toString();
   }
}
