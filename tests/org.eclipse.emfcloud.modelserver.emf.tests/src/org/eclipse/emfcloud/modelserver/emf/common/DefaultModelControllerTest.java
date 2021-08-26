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
import static org.eclipse.emfcloud.modelserver.tests.util.OSUtil.osLineSeparator;
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
import static org.mockito.hamcrest.MockitoHamcrest.intThat;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.module.EMFModule;
import org.eclipse.emfcloud.jackson.resource.JsonResource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandExecutionType;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.DICodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

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
      URI uri = URI.createFileURI("resources/TestError.ecore");
      Optional<Resource> resource = Optional.of(set.getResource(uri, true));

      when(serverConfiguration.getWorkspaceRootURI()).thenReturn(URI.createFileURI(System.getProperty("user.home")));
      codecs = new DICodecsManager(Map.of(ModelServerPathParametersV1.FORMAT_XMI, new XmiCodec()));
      when(modelRepository.getModel(getModelUri("TestError.ecore").toString()))
         .thenReturn(Optional.of(resource.get().getContents().get(0)));
      when(modelRepository.loadResource(getModelUri("TestError.ecore").toString())).thenReturn(resource);
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
      JsonResource res = createJsonResource(modeluri);
      rset.getResources().add(res);
      final EClass task = EcoreFactory.eINSTANCE.createEClass();
      res.getContents().add(task);
      CCommand setCommand = CCommandFactory.eINSTANCE.createCommand();
      setCommand.setType(EMFCommandType.SET);
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

      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();
      result.setSource(EcoreUtil.copy(setCommand));
      result.setType(CommandExecutionType.EXECUTE);

      when(modelRepository.executeCommand(eq(modeluri), any())).thenReturn(result);

      modelController.executeCommand(context, modeluri);

      // unload to proxify
      res.unload();
      verify(modelRepository).executeCommand(eq(modeluri), argThat(eEqualTo(setCommand)));

      // No subscribers registered for this incrementalUpdate, therefore no pre-encoded commands are created and the map
      // can remain empty for this test
      verify(sessionController).commandExecuted(eq(modeluri), eq(result));
   }

   @Test
   public void addCommandNotification() throws EncodingException, DecodingException {
      ResourceSet rset = new ResourceSetImpl();
      String modeluri = "SuperBrewer3000.json";
      JsonResource res = createJsonResource(modeluri);
      rset.getResources().add(res);
      final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      res.getContents().add(eClass);

      final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
      CCommand addCommand = CCommandFactory.eINSTANCE.createCommand();
      addCommand.setType(EMFCommandType.ADD);
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

      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();
      result.setSource(EcoreUtil.copy(addCommand));
      result.setType(CommandExecutionType.EXECUTE);
      when(modelRepository.executeCommand(eq(modeluri), any())).thenReturn(result);

      modelController.executeCommand(context, modeluri);

      // unload to proxify
      res.unload();
      verify(modelRepository).executeCommand(eq(modeluri), argThat(eEqualTo(addCommand)));

      // No subscribers registered for this incrementalUpdate, therefore no pre-encoded commands are created and the map
      // can be remain for this test

      verify(sessionController).commandExecuted(eq(modeluri), eq(result));
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

      String expectedXmiSnippet = osLineSeparator(
         "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n<ecore:EClass xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"SimpleWorkflow\"/>\n");
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

      String expectedXmiSnippet = osLineSeparator(
         "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n<ecore:EClass xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"PreHeat\"/>\n");
      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, Json.text(expectedXmiSnippet)));

      verify(context).json(expectedResponse);
   }

   @Test
   public void validate() throws EncodingException, IOException {
      modelController.validate(context, getModelUri("TestError.ecore").toString());

      verify(context)
         .json(
            argThat(jsonNodeStringThat(
               containsRegex(".\"type\":\"validationResult\",\"data\":.*\"id\".*\"severity\".*\"children\".*"))));
   }

   @Test
   public void getValidationConstraints() throws EncodingException, IOException {
      modelController.getValidationConstraints(context, getModelUri("TestError.ecore").toString());
      verify(context)
         .json(argThat(jsonNodeStringThat(containsRegex(".\"type\":\"success\",\"data\":.*"))));
   }

   @Test
   public void createNewModelWithoutPayload() throws EncodingException, IOException {
      modelController.create(context, getModelUri("NewModel.ecore").toString());
      verify(context).status(intThat(equalTo((HttpURLConnection.HTTP_BAD_REQUEST))));
      verify(context).json(argThat(hasProperties(prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.ERROR)))));
   }

   @Test
   public void createNewModel() throws EncodingException, IOException {
      final EClass root = EcoreFactory.eINSTANCE.createEClass();
      String requestBody = Json
         .object(Json.prop(JsonResponseMember.DATA, Json.text(new JsonCodec().encode(root).toString())))
         .toString();
      when(context.body()).thenReturn(requestBody);
      modelController.create(context, getModelUri("NewModel.ecore").toString());
      verify(context).json(argThat(hasProperties(prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)))));
   }

   @Test
   public void createExistingModel() throws EncodingException, IOException {
      String modelUri = getModelUri("NewModel.ecore").toString();

      final EClass root = EcoreFactory.eINSTANCE.createEClass();
      when(modelRepository.hasModel(modelUri)).thenReturn(true);

      String requestBody = Json
         .object(Json.prop(JsonResponseMember.DATA, Json.text(new JsonCodec().encode(root).toString())))
         .toString();
      when(context.body()).thenReturn(requestBody);
      modelController.create(context, modelUri);
      verify(context).status(intThat(equalTo((HttpURLConnection.HTTP_CONFLICT))));
      verify(context).json(argThat(hasProperties(prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.ERROR)))));
   }

   @Test
   public void saveModel() throws EncodingException, IOException {
      String modelUri = getModelUri("NewModel.ecore").toString();
      when(modelRepository.hasModel(modelUri)).thenReturn(true);
      when(modelRepository.saveModel(modelUri)).thenReturn(true);

      modelController.save(context, modelUri);
      verify(context).json(argThat(hasProperties(prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)))));
   }

   @Test
   public void saveNonExistingModel() throws EncodingException, IOException {
      String modelUri = getModelUri("NewModel.ecore").toString();
      when(modelRepository.hasModel(modelUri)).thenReturn(false);

      modelController.save(context, modelUri);
      verify(context).status(intThat(equalTo((HttpURLConnection.HTTP_NOT_FOUND))));
      verify(context).json(argThat(hasProperties(prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.ERROR)))));
   }

   static File getCWD() { return new File(System.getProperty("user.dir")); }

   private URI getModelUri(final String modelFileName) {
      return URI.createFileURI(getCWD() + "/resources/" + modelFileName);
   }

   Matcher<Object> jsonNodeStringThat(final Matcher<String> data) {
      return new TypeSafeDiagnosingMatcher<>() {
         @Override
         public void describeTo(final Description description) {
            description.appendText("JsonNodeString that ");
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

   Matcher<Object> jsonNodeThat(final Matcher<JsonNode> data) {
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
            if (!data.matches(node)) {
               data.describeMismatch(node, mismatchDescription);
               return false;
            }
            return true;
         }
      };
   }

   @SafeVarargs
   final Matcher<ObjectNode> hasProperties(final Map.Entry<String, JsonNode>... properties) {
      String description = Arrays.stream(properties)
         .map(property -> "\"" + property.getKey() + "\":" + property.getValue().toString())
         .collect(Collectors.joining(",", "{", "}"));

      return new CustomTypeSafeMatcher<>(description) {
         @Override
         protected boolean matchesSafely(final ObjectNode item) {
            for (Entry<String, JsonNode> property : properties) {
               if (!item.get(property.getKey()).equals(property.getValue())) {
                  return false;
               }
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

   private JsonResource createJsonResource(final String modeluri) {
      return new JsonResource(
         URI.createHierarchicalURI(new String[] { modeluri }, null, null)
            .resolve(serverConfiguration.getWorkspaceRootURI()));
   }
}
