/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;

/**
 * Unit tests for the {@link SchemaController} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSchemaControllerTest {

   private SchemaController schemaController;

   private Context context;
   private ModelRepository modelRepository;
   private SchemaRepository schemaRepository;
   private JsonSchemaConverter jsonSchemaConverter;

   @Before
   public void before() {
      context = mock(Context.class);
      modelRepository = mock(ModelRepository.class);
      schemaRepository = mock(SchemaRepository.class);
      jsonSchemaConverter = mock(JsonSchemaConverter.class);
      schemaController = new DefaultSchemaController(modelRepository, schemaRepository, jsonSchemaConverter);
   }

   @Test
   public void getTypeSchema_modelAvailable() {
      final EClass machine = EcoreFactory.eINSTANCE.createEClass();
      machine.setName("CoffeeMachine");

      JsonNode expectedMachineTypeSchema = Json.object(
         prop("$id", Json.text(machine.getName().trim().toLowerCase())),
         prop("title", Json.text(machine.getName())),
         prop("type", Json.text("object")),
         prop("properties", Json.object()),
         prop("additionalProperties", Json.bool(false)));

      when(modelRepository.getModel("SuperBrewer3000.json")).thenReturn(Optional.of(machine));
      when(jsonSchemaConverter.from(any(EObject.class))).thenReturn(expectedMachineTypeSchema);

      schemaController.getTypeSchema(context, "SuperBrewer3000.json");

      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, expectedMachineTypeSchema));

      verify(context).json(expectedResponse);
   }

   @Test
   public void getTypeSchema_modelUnavailable() {
      when(modelRepository.getModel("SuperBrewer3000.json")).thenReturn(Optional.empty());

      schemaController.getTypeSchema(context, "SuperBrewer3000.json");

      String expectedErrorMsg = "Type schema for 'SuperBrewer3000.json' not found!";
      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.ERROR)),
         prop(JsonResponseMember.DATA, Json.text(expectedErrorMsg)));

      verify(context).status(404);
      verify(context).json(expectedResponse);
   }

   @Test
   public void getUiSchema_schemaAvailable() {
      final JsonNode machineUiSchema = Json.object(
         prop("type", Json.text("VerticalLayout")),
         prop("elements", Json.array(
            Json.object(
               prop("type", Json.text("Label")),
               prop("text", Json.text("Machine"))),
            Json.object(
               prop("type", Json.text("Control")),
               prop("label", Json.text("Name")),
               prop("scope", Json.text("#/properties/name"))))));

      when(schemaRepository.loadUiSchema("machine")).thenReturn(Optional.of(machineUiSchema));

      schemaController.getUiSchema(context, "machine");

      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.SUCCESS)),
         prop(JsonResponseMember.DATA, machineUiSchema));

      verify(context).json(expectedResponse);
   }

   @Test
   public void getUiSchema_schemaUnavailable() {
      schemaController.getUiSchema(context, "brewing");

      String expectedErrorMsg = "UI schema for 'brewing' not found!";
      JsonNode expectedResponse = Json.object(
         prop(JsonResponseMember.TYPE, Json.text(JsonResponseType.ERROR)),
         prop(JsonResponseMember.DATA, Json.text(expectedErrorMsg)));

      verify(context).status(404);
      verify(context).json(expectedResponse);
   }
}
