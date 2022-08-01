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

import static org.eclipse.emfcloud.modelserver.tests.util.EMFMatchers.eNamedElement;
import static org.eclipse.emfcloud.modelserver.tests.util.EMFMatchers.eObjectOfClass;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerEditingDomain;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecProvider;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.MultiBindingDefaults;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonConstants;
import org.hamcrest.Matcher;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

   @Captor
   private ArgumentCaptor<Class<? extends Command>> commandClassCaptor;

   @Captor
   private ArgumentCaptor<CommandParameter> commandParameterCaptor;

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

   @Test
   public void add_containmentReference() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), "path", Json.text("/eClassifiers/0/eStructuralFeatures")));
      EStructuralFeature attr = EcoreFactory.eINSTANCE.createEAttribute();
      attr.setName("newAttribute");
      addOp.set("value", encode(attr));

      verifyAddCommand(addOp,
         eNamedElement(EClass.class, "Component"),
         is(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES),
         eNamedElement(EAttribute.class, "newAttribute"),
         is(CommandParameter.NO_INDEX));
   }

   @Test
   public void add_containmentReference_explicitAppend() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), "path", Json.text("/eClassifiers/0/eStructuralFeatures/-")));
      EStructuralFeature attr = EcoreFactory.eINSTANCE.createEAttribute();
      attr.setName("newAttribute");
      addOp.set("value", encode(attr));

      verifyAddCommand(addOp,
         eNamedElement(EClass.class, "Component"),
         is(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES),
         eNamedElement(EAttribute.class, "newAttribute"),
         is(CommandParameter.NO_INDEX));
   }

   @Test
   public void add_containmentReference_withIndex() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), "path", Json.text("/eClassifiers/0/eStructuralFeatures/2")));
      EStructuralFeature attr = EcoreFactory.eINSTANCE.createEAttribute();
      attr.setName("newAttribute");
      addOp.set("value", encode(attr));

      verifyAddCommand(addOp,
         eNamedElement(EClass.class, "Component"),
         is(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES),
         eNamedElement(EAttribute.class, "newAttribute"),
         is(2));
   }

   @Test
   public void add_attribute() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), //
            "path", Json.text("/eAnnotations/0/contents/0/multiValuedAttr"), //
            "value", Json.text("d")));

      verifyAddCommand(addOp,
         eObjectOfClass("ConcreteClass1"),
         eNamedElement(EAttribute.class, "multiValuedAttr"),
         is("d"),
         is(CommandParameter.NO_INDEX));
   }

   @Test
   public void add_attribute_explicitAppend() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), //
            "path", Json.text("/eAnnotations/0/contents/0/multiValuedAttr/-"), //
            "value", Json.text("d")));

      verifyAddCommand(addOp,
         eObjectOfClass("ConcreteClass1"),
         eNamedElement(EAttribute.class, "multiValuedAttr"),
         is("d"),
         is(CommandParameter.NO_INDEX));
   }

   @Test
   public void add_attribute_withIndex() throws JsonPatchException, JsonPatchTestException {
      ObjectNode addOp = Json
         .object(Map.of("op", Json.text("add"), //
            "path", Json.text("/eAnnotations/0/contents/0/multiValuedAttr/2"), //
            "value", Json.text("d")));

      verifyAddCommand(addOp,
         eObjectOfClass("ConcreteClass1"),
         eNamedElement(EAttribute.class, "multiValuedAttr"),
         is("d"),
         is(2));
   }

   //
   // Test framework
   //

   @Before
   public void createModelFixture() {
      resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
      Resource resource = resourceSet.getResource(modelURI, true);
      coffeePackage = (EPackage) resource.getContents().get(0);

      // Mix in objects for testing other kinds of features not found in Ecore
      URI extraURI = URI.createURI("aConcreteClass1.xmi").resolve(modelURI);
      Resource extra = resourceSet.getResource(extraURI, true);

      EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
      annotation.setSource("testing");
      annotation.getContents().addAll(extra.getContents());
      coffeePackage.getEAnnotations().add(annotation);
      extra.unload();
      resourceSet.getResources().remove(extra);
   }

   @Before
   public void configureMocks() {
      when(modelURIConverter.normalize(modelURI)).thenReturn(modelURI);
      when(modelResourceManager.getEditingDomain(resourceSet)).thenReturn(editingDomain);
      when(modelResourceManager.loadResource(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
      when(modelResourceManager.loadResource(modelURI.toString())).thenReturn(Optional.of(coffeePackage.eResource()));
   }

   @Before
   public void createTestSubject() {
      patchHelper = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(ServerConfiguration.class).toInstance(serverConfiguration);
            bind(ModelResourceManager.class).toInstance(modelResourceManager);
            bind(ModelURIConverter.class).toInstance(modelURIConverter);

            MultiBinding<CodecProvider> codecBinding = MultiBinding.create(CodecProvider.class);
            codecBinding.addAll(MultiBindingDefaults.DEFAULT_CODECS);
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

   JsonNode encode(final EObject object) {
      JsonNode result;

      try {
         result = new JsonCodecV2().encode(object);

         if (result.isObject() && object.eResource() == null) {
            // Make sure there's no NullNode for the id
            ObjectNode objectNode = (ObjectNode) result;
            if (objectNode.has(JsonConstants.ID_ATTR)) {
               objectNode.remove(JsonConstants.ID_ATTR);
            }
         }
      } catch (EncodingException e) {
         throw new AssertionError("Failed to encode object", e);
      }

      return result;
   }

   void verifyAddCommand(final JsonNode addOp, final Matcher<Object> owner, final Matcher<Object> feature,
      final Matcher<Object> item, final Matcher<Integer> index) throws JsonPatchException, JsonPatchTestException {
      ArrayNode patch = Json.array(addOp);
      patchHelper.getCommand(modelURI.toString(), resourceSet, patch);

      verify(editingDomain).createCommand(commandClassCaptor.capture(), commandParameterCaptor.capture());
      assertThat(commandClassCaptor.getValue(), sameInstance(AddCommand.class));
      CommandParameter param = commandParameterCaptor.getValue();

      assertThat(param.getOwner(), owner);
      assertThat(param.getFeature(), feature);
      assertThat(param.getCollection(), hasItem(item));
      assertThat(param.getIndex(), index);
   }
}
