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
package org.eclipse.emfcloud.modelserver.common.tests.jsonschema;

import static org.eclipse.emfcloud.modelserver.jsonschema.Json.prop;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emfcloud.modelserver.jsonschema.DefaultJsonSchemaConverter;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;
import org.eclipse.emfcloud.modelserver.tests.util.EcoreTestUtil;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for the {@link JsonSchemaConverter} class.
 */
public class DefaultJsonSchemaConverterEClassTest extends DefaultJsonSchemaConverterTestHelper {

   private static final String ECLASS_NAME = "TestEClass";
   private static final String REFECLASS_NAME = "TestEClass2";
   private static final String REFERENCE_NAME = "testReference";
   private static final String ATTRIBUTE_NAME = "testAttribute";

   private EClass eClass;
   private EClass refClass;
   private EReference eReference;
   private EReference eReference2;

   private JsonSchemaConverter jsonSchemaCreator;

   @Before
   public void before() {
      jsonSchemaCreator = new DefaultJsonSchemaConverter();

      eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName(ECLASS_NAME);

      refClass = EcoreFactory.eINSTANCE.createEClass();
      refClass.setName(REFECLASS_NAME);
   }

   @Test
   public void createJsonSchemaFromEClassWithOptionalSingleReference() {
      eReference = EcoreTestUtil.eReference(REFERENCE_NAME, -1, 1, refClass);
      eClass.getEStructuralFeatures().add(eReference);

      final JsonNode actual = jsonSchemaCreator.from(eClass);
      final ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties",
            Json.object(
               prop(REFERENCE_NAME,
                  Json.object(prop("$ref", Json.text(getRefHelper(refClass))))))),
         prop("additionalProperties", Json.bool(false)));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithMandatorySingleReference() {
      eReference = EcoreTestUtil.eReference(REFERENCE_NAME, 1, 1, refClass);
      eClass.getEStructuralFeatures().add(eReference);

      final JsonNode actual = jsonSchemaCreator.from(eClass);
      final ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties",
            Json.object(
               prop(REFERENCE_NAME, Json.object(
                  prop("$ref", Json.text(getRefHelper(refClass))))))),
         prop("additionalProperties", Json.bool(false)),
         prop("required", Json.array(Collections.singletonList(REFERENCE_NAME))));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithOptionalMultiReference() {
      eReference = EcoreTestUtil.eReference(REFERENCE_NAME, -1, 10, refClass);
      eClass.getEStructuralFeatures().add(eReference);

      final JsonNode actual = jsonSchemaCreator.from(eClass);

      final ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object(
            prop(REFERENCE_NAME, Json.object(
               prop("type", Json.text("array")),
               prop("items", Json.object(
                  prop("type", Json.text("object")),
                  prop("$id", Json.text(getIdHelper(refClass))),
                  prop("title", Json.text(refClass.getName())),
                  prop("properties", Json.object()),
                  prop("additionalProperties", Json.bool(false)))))))),
         prop("additionalProperties", Json.bool(false)));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithMandatoryMultiReference() {
      eReference = EcoreTestUtil.eReference(REFERENCE_NAME, 1, 10, refClass);
      eClass.getEStructuralFeatures().add(eReference);

      final JsonNode actual = jsonSchemaCreator.from(eClass);

      ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object(
            prop(REFERENCE_NAME, Json.object(
               prop("type", Json.text("array")),
               prop("items", Json.object(
                  prop("type", Json.text("object")),
                  prop("$id", Json.text(getIdHelper(refClass))),
                  prop("title", Json.text(refClass.getName())),
                  prop("properties", Json.object()),
                  prop("additionalProperties", Json.bool(false)))))))),
         prop("additionalProperties", Json.bool(false)),
         prop("required", Json.array(Collections.singletonList(REFERENCE_NAME))));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithCircularReference() {
      eReference = EcoreTestUtil.eReference(REFERENCE_NAME, -1, 1, refClass);
      eClass.getEStructuralFeatures().add(eReference);

      eReference2 = EcoreTestUtil.eReference(REFERENCE_NAME, -1, 1, eClass);
      refClass.getEStructuralFeatures().add(eReference2);

      final JsonNode actual = jsonSchemaCreator.from(eClass);

      ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object(
            prop(REFERENCE_NAME, Json.object(
               prop("$ref", Json.text(getRefHelper(refClass))))))),
         prop("additionalProperties", Json.bool(false)));

      // eReference2 should not exist in output
      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEmptyEClass() {
      final JsonNode actual = jsonSchemaCreator.from(eClass);
      ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object()),
         prop("additionalProperties", Json.bool(false)));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithOptionalStringAttribute() {
      EAttribute optionalStringEAttribute = EcoreTestUtil.stringEAttribute(ATTRIBUTE_NAME, 0, 1);
      eClass.getEStructuralFeatures().add(optionalStringEAttribute);

      final JsonNode actual = jsonSchemaCreator.from(eClass);
      final ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object(
            prop(ATTRIBUTE_NAME, Json.object(
               prop("type", Json.text("string")))))),
         prop("additionalProperties", Json.bool(false)));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEClassWithMandatoryStringAttribute() throws JsonProcessingException {
      EAttribute mandatoryStringEAttribute = EcoreTestUtil.stringEAttribute(ATTRIBUTE_NAME, 1, 1);
      eClass.getEStructuralFeatures().add(mandatoryStringEAttribute);

      final JsonNode actual = jsonSchemaCreator.from(eClass);
      final ObjectNode expected = Json.object(
         prop("$id", Json.text(getIdHelper(eClass))),
         prop("title", Json.text(ECLASS_NAME)),
         prop("type", Json.text("object")),
         prop("properties", Json.object(
            prop(ATTRIBUTE_NAME, Json.object(prop("type", Json.text("string")))))),
         prop("additionalProperties", Json.bool(false)),
         prop("required", Json.array(Collections.singletonList(ATTRIBUTE_NAME))));

      assertEquals(expected, actual);
   }

}
