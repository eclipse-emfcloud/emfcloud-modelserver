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
import static org.eclipse.emfcloud.modelserver.tests.util.EMFMatchers.eEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.DICodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.jackson.resource.JsonResource;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;

/**
 * Unit tests for the {@link ModelController} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultModelControllerTest {

   @Mock
   private ModelRepository modelRepository;
   @Mock
   private ModelResourceManager modelResourceManager;
   @Mock
   private Context context;
   @Mock
   private SessionController sessionController;
   @Mock
   private ServerConfiguration serverConfiguration;
   @InjectMocks
   private DefaultModelValidator modelValidator;

   private CodecsManager codecs;

   private ModelController modelController;

   @Before
   public void before() throws NoSuchFieldException, SecurityException {
      ResourceSet set = new ResourceSetImpl();
      set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
      URI uri = URI.createFileURI("resources/Test1.ecore");
      Optional<Resource> resource = Optional.of(set.getResource(uri, true));

      when(serverConfiguration.getWorkspaceRootURI()).thenReturn(URI.createFileURI("/home/modelserver/workspace/"));
      codecs = new DICodecsManager(Map.of(ModelServerPathParametersV1.FORMAT_XMI, new XmiCodec()));
      when(modelRepository.getModel(getModelUri("Test1.ecore").toString()))
         .thenReturn(Optional.of(resource.get().getContents().get(0)));
      when(modelRepository.loadResource(getModelUri("Test1.ecore").toString())).thenReturn(resource);
      modelValidator = new DefaultModelValidator(modelRepository, new DefaultFacetConfig(),
         EMFModule::setupDefaultMapper);
      modelController = new DefaultModelController(modelRepository, sessionController, serverConfiguration, codecs,
         modelValidator, EMFModule::setupDefaultMapper);
   }

   @Test
   public void getOneXmiFormat() throws EncodingException {
      final AtomicReference<JsonNode> response = new AtomicReference<>();
      final EClass brewingUnit = EcoreFactory.eINSTANCE.createEClass();
      Answer<Void> answer = invocation -> {
         response.set(invocation.getArgument(0));
         return null;
      };
      doAnswer(answer).when(context).json(any(JsonNode.class));
      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.FORMAT,
         Collections.singletonList(ModelServerPathParametersV1.FORMAT_XMI));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(modelRepository.getModel("test")).thenReturn(Optional.of(brewingUnit));

      modelController.getOne(context, "test");

      assertThat(response.get().get(JsonResponseMember.DATA), is(equalTo(new XmiCodec().encode(brewingUnit))));
   }

   @Test
   public void getAllXmiFormat() throws EncodingException, IOException {
      final AtomicReference<JsonNode> response = new AtomicReference<>();
      final EClass brewingUnit = EcoreFactory.eINSTANCE.createEClass();
      Answer<Void> answer = invocation -> {
         response.set(invocation.getArgument(0));
         return null;
      };
      doAnswer(answer).when(context).json(any(JsonNode.class));
      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.FORMAT,
         Collections.singletonList(ModelServerPathParametersV1.FORMAT_XMI));
      when(context.queryParamMap()).thenReturn(queryParams);
      final Map<URI, EObject> allModels = Collections.singletonMap(URI.createURI("test"), brewingUnit);
      when(modelRepository.getAllModels()).thenReturn(allModels);

      modelController.getAll(context);

      assertThat(response.get().get(JsonResponseMember.DATA), is(equalTo(
         Json.object(
            Json.prop("test", new XmiCodec().encode(brewingUnit))))));
   }

   @Test
   public void getOneJsonFormat() throws EncodingException {
      final AtomicReference<JsonNode> response = new AtomicReference<>();
      final EClass brewingUnit = EcoreFactory.eINSTANCE.createEClass();
      Answer<Void> answer = invocation -> {
         response.set(invocation.getArgument(0));
         return null;
      };
      doAnswer(answer).when(context).json(any(JsonNode.class));
      when(modelRepository.getModel("test")).thenReturn(Optional.of(brewingUnit));

      modelController.getOne(context, "test");

      assertThat(response.get().get(JsonResponseMember.DATA), is(equalTo(new JsonCodec().encode(brewingUnit))));
   }

   @Test
   public void updateXmi() throws EncodingException {
      final EClass brewingUnit = EcoreFactory.eINSTANCE.createEClass();
      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.FORMAT,
         Collections.singletonList(ModelServerPathParametersV1.FORMAT_XMI));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(context.body()).thenReturn(
         Json.object(Json.prop(JsonResponseMember.DATA, new XmiCodec().encode(brewingUnit))).toString());
      String modeluri = "SuperBrewer3000.json";
      when(modelRepository.updateModel(eq(modeluri), any(EClass.class))).thenReturn(Optional.of(new ResourceImpl()));
      modelController.update(context, modeluri);
      verify(modelRepository, times(1)).updateModel(eq(modeluri), any(EClass.class));
   }

   @Test
   public void executeCommand() throws EncodingException, DecodingException {
      ResourceSet rset = new ResourceSetImpl();
      String modeluri = "SuperBrewer3000.json";
      JsonResource res = new JsonResource(
         URI.createURI(modeluri).resolve(serverConfiguration.getWorkspaceRootURI()));
      rset.getResources().add(res);
      final EClass task = EcoreFactory.eINSTANCE.createEClass();
      res.getContents().add(task);
      CCommand setCommand = CCommandFactory.eINSTANCE.createCommand();
      setCommand.setType(CommandKind.SET);
      setCommand.setOwner(task);
      setCommand.setFeature("name");
      setCommand.getDataValues().add("Foo");
      JsonResource cmdRes = new JsonResource(URI.createURI("$command.json"));
      cmdRes.getContents().add(setCommand);

      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.MODEL_URI, Collections.singletonList(modeluri));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(context.body())
         .thenReturn(Json.object(Json.prop(JsonResponseMember.DATA, new JsonCodec().encode(setCommand))).toString());
      when(modelRepository.getModel(modeluri)).thenReturn(Optional.of(task));
      modelController.executeCommand(context, modeluri);

      // unload to proxify
      res.unload();
      verify(modelRepository).executeCommand(eq(modeluri), argThat(eEqualTo(setCommand)));

      // No subscribers registered for this incrementalUpdate, therefore no pre-encoded commands are created and the map
      // can remain empty for this test
      Map<String, JsonNode> encodings = new HashMap<>();
      verify(sessionController).commandExecuted(eq(modeluri), eq(encodings));
   }

   @Test
   public void addCommandNotification() throws EncodingException, DecodingException {
      ResourceSet rset = new ResourceSetImpl();
      String modeluri = "SuperBrewer3000.json";
      JsonResource res = new JsonResource(
         URI.createURI(modeluri).resolve(serverConfiguration.getWorkspaceRootURI()));
      rset.getResources().add(res);
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      res.getContents().add(eClass);

      final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
      CCommand addCommand = CCommandFactory.eINSTANCE.createCommand();
      addCommand.setType(CommandKind.ADD);
      addCommand.setOwner(eClass);
      addCommand.setFeature("eAttributes");
      addCommand.getObjectsToAdd().add(attribute);
      addCommand.getObjectValues().add(attribute);
      JsonResource cmdRes = new JsonResource(URI.createURI("$command.json"));
      cmdRes.getContents().add(addCommand);
      String commandAsString = Json
         .object(Json.prop(JsonResponseMember.DATA, Json.text(new JsonCodec().encode(addCommand).toString())))
         .toString();

      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.MODEL_URI, Collections.singletonList(modeluri));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(context.body()).thenReturn(commandAsString);
      when(modelRepository.getModel(modeluri)).thenReturn(Optional.of(attribute));
      modelController.executeCommand(context, modeluri);

      // unload to proxify
      res.unload();
      verify(modelRepository).executeCommand(eq(modeluri), argThat(eEqualTo(addCommand)));

      // No subscribers registered for this incrementalUpdate, therefore no pre-encoded commands are created and the map
      // can be remain for this test
      Map<String, JsonNode> encodings = new HashMap<>();
      verify(sessionController).commandExecuted(eq(modeluri), eq(encodings));
   }

   @Test
   public void getModelelementByIdJsonFormat() throws EncodingException {
      final EClass simpleWorkflow = EcoreFactory.eINSTANCE.createEClass();
      simpleWorkflow.setName("SimpleWorkflow");
      when(modelRepository.getModelElementById("test", "//@workflows.0")).thenReturn(Optional.of(simpleWorkflow));

      modelController.getModelElementById(context, "test", "//@workflows.0");

      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, Json.object(
            prop("eClass", Json.text("http://www.eclipse.org/emf/2002/Ecore#//EClass")),
            prop("name", Json.text("SimpleWorkflow")))));

      verify(context).json(expectedResponse);
   }

   @Test
   public void getModelelementByIdXmiFormat() throws EncodingException {
      final EClass simpleWorkflow = EcoreFactory.eINSTANCE.createEClass();
      simpleWorkflow.setName("SimpleWorkflow");
      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.FORMAT,
         Collections.singletonList(ModelServerPathParametersV1.FORMAT_XMI));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(modelRepository.getModelElementById("test", "//@workflows.0")).thenReturn(Optional.of(simpleWorkflow));

      modelController.getModelElementById(context, "test", "//@workflows.0");

      String expectedXmiSnippet = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n<ecore:EClass xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"SimpleWorkflow\"/>\n";
      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, Json.text(expectedXmiSnippet)));

      verify(context).json(expectedResponse);
   }

   @Test
   public void getModelelementByNameJsonFormat() throws EncodingException {
      final EClass preHeatTask = EcoreFactory.eINSTANCE.createEClass();
      preHeatTask.setName("PreHeat");
      when(modelRepository.getModelElementById("test", "PreHeat")).thenReturn(Optional.of(preHeatTask));

      modelController.getModelElementById(context, "test", "PreHeat");

      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, Json.object(
            prop("eClass", Json.text("http://www.eclipse.org/emf/2002/Ecore#//EClass")),
            prop("name", Json.text("PreHeat")))));

      verify(context).json(expectedResponse);
   }

   @Test
   public void getModelelementByNameXmiFormat() throws EncodingException {
      final EClass preHeatTask = EcoreFactory.eINSTANCE.createEClass();
      preHeatTask.setName("PreHeat");
      final LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
      queryParams.put(ModelServerPathParametersV1.FORMAT,
         Collections.singletonList(ModelServerPathParametersV1.FORMAT_XMI));
      when(context.queryParamMap()).thenReturn(queryParams);
      when(modelRepository.getModelElementById("test", "PreHeat")).thenReturn(Optional.of(preHeatTask));

      modelController.getModelElementById(context, "test", "PreHeat");

      String expectedXmiSnippet = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n<ecore:EClass xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"PreHeat\"/>\n";
      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, Json.text(expectedXmiSnippet)));

      verify(context).json(expectedResponse);
   }

   @Test
   public void validate() throws EncodingException, IOException {
      modelController.validate(context, getModelUri("Test1.ecore").toString());

      verify(context)
         .json(
            argThat(jsonNodeThat(
               containsRegex(".\"type\":\"validationResult\",\"data\":.*\"id\".*\"severity\".*\"children\".*"))));
   }

   @Test
   public void getValidationConstraints() throws EncodingException, IOException {
      modelController.getValidationConstraints(context, getModelUri("Test1.ecore").toString());

      verify(context)
         .json(argThat(jsonNodeThat(containsRegex(".\"type\":\"success\",\"data\":.*"))));
   }

   static File getCWD() { return new File(System.getProperty("user.dir")); }

   private URI getModelUri(final String modelFileName) {
      return URI.createFileURI(getCWD() + "/resources/" + modelFileName);
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
