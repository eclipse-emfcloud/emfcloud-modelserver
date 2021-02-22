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

import java.util.Arrays;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
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
public class DefaultJsonSchemaConverterEPackageTest extends DefaultJsonSchemaConverterTestHelper {

   private static final String EPACKAGE = "ModelEPackage";
   private static final String ABSTRACT_NAMEDELEMENT_CLASS = "NamedElement";
   private static final String NAME_ATTRIBUTE = "name";
   private static final String MODEL_CLASS = "Model";
   private static final String MANDATORY_MODEL_ATTRIBUTE = "version";
   private static final String OPTIONAL_MODEL_ATTRIBUTE = "encoding";
   private static final String OPTIONAL_MODEL_ATTRIBUTE2 = "published";
   private static final String REFERENCE_CLASS = "Reference";
   private static final String PRIORITY_EENUM = "PriorityEEnum";
   private static final String PRIORITY_EENUM_ATTRIBUTE = "priority";

   private EPackage ePackage;
   private EClass namedElementClass;
   private EAttribute nameAttribute;
   private EClass modelClass;
   private EAttribute mandatoryModelAttribute;
   private EAttribute optionalModelAttribute;
   private EAttribute optionalModelAttribute2;
   private EClass refClass;
   private EReference eReference;
   private EEnum priorityEEnum;
   private EAttribute priorityEEnumAttribute;

   private JsonSchemaConverter jsonSchemaCreator;

   @Before
   public void before() {
      jsonSchemaCreator = new DefaultJsonSchemaConverter();

      ePackage = EcoreFactory.eINSTANCE.createEPackage();
      ePackage.setName(EPACKAGE);
      ePackage.setNsURI("http://www.eclipsesource.com/modelserver/example/testmodel");

      namedElementClass = EcoreFactory.eINSTANCE.createEClass();
      namedElementClass.setName(ABSTRACT_NAMEDELEMENT_CLASS);
      namedElementClass.setAbstract(true);

      nameAttribute = EcoreTestUtil.stringEAttribute(NAME_ATTRIBUTE, 1, 1);
      namedElementClass.getEStructuralFeatures().add(nameAttribute);

      modelClass = EcoreFactory.eINSTANCE.createEClass();
      modelClass.setName(MODEL_CLASS);
      modelClass.setAbstract(false);
      modelClass.getESuperTypes().add(namedElementClass);

      refClass = EcoreFactory.eINSTANCE.createEClass();
      refClass.setName(REFERENCE_CLASS);

      priorityEEnum = EcoreTestUtil.createEENum(PRIORITY_EENUM, Arrays.asList("low", "medium", "high", "veryhigh"));
   }

   @Test
   public void createJsonSchemaFromEmptyEPackage() throws JsonProcessingException {
      final JsonNode actual = jsonSchemaCreator.from(ePackage);
      final ObjectNode expected = Json.object(
         prop("$schema", Json.text("http://json-schema.org/draft-07/schema#")),
         prop("$id", Json.text(getIdHelper(ePackage))),
         prop("title", Json.text(getSchemaTitleHelper(ePackage))),
         prop("type", Json.text("object")),
         prop("definitions", Json.object()));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEPackageWithAbstractEClassifier() throws JsonProcessingException {
      ePackage.getEClassifiers().add(namedElementClass);

      final JsonNode actual = jsonSchemaCreator.from(ePackage);
      final ObjectNode expected = Json.object(
         prop("$schema", Json.text("http://json-schema.org/draft-07/schema#")),
         prop("$id", Json.text(getIdHelper(ePackage))),
         prop("title", Json.text(getSchemaTitleHelper(ePackage))),
         prop("type", Json.text("object")),
         prop("definitions", Json.object(
            prop(getNameAsIdHelper(namedElementClass), Json.object(
               prop("$id", Json.text(getIdHelper(namedElementClass))),
               prop("title", Json.text(namedElementClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(namedElementClass)),
                  prop("name", Json.object(prop("type", Json.text("string")))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"))))))));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEPackageWithInheritedAttributes() throws JsonProcessingException {
      ePackage.getEClassifiers().add(namedElementClass);

      mandatoryModelAttribute = EcoreTestUtil.integerEAttribute(MANDATORY_MODEL_ATTRIBUTE, 1, 1);
      modelClass.getEStructuralFeatures().add(mandatoryModelAttribute);
      optionalModelAttribute = EcoreTestUtil.stringEAttribute(OPTIONAL_MODEL_ATTRIBUTE, 0, 1);
      modelClass.getEStructuralFeatures().add(optionalModelAttribute);
      optionalModelAttribute2 = EcoreTestUtil.booleanEAttribute(OPTIONAL_MODEL_ATTRIBUTE2, 0, 1);
      modelClass.getEStructuralFeatures().add(optionalModelAttribute2);

      ePackage.getEClassifiers().add(modelClass);

      final JsonNode actual = jsonSchemaCreator.from(ePackage);
      final ObjectNode expected = Json.object(
         prop("$schema", Json.text("http://json-schema.org/draft-07/schema#")),
         prop("$id", Json.text(getIdHelper(ePackage))),
         prop("title", Json.text(getSchemaTitleHelper(ePackage))),
         prop("type", Json.text("object")),
         prop("definitions", Json.object(
            prop(getNameAsIdHelper(namedElementClass), Json.object(
               prop("$id", Json.text(getIdHelper(namedElementClass))),
               prop("title", Json.text(namedElementClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(namedElementClass)),
                  prop("name", Json.object(prop("type", Json.text("string")))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"))))),
            prop(getNameAsIdHelper(modelClass), Json.object(
               prop("$id", Json.text(getIdHelper(modelClass))),
               prop("title", Json.text(modelClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(modelClass)),
                  prop(NAME_ATTRIBUTE, Json.object(prop("type", Json.text("string")))),
                  prop(MANDATORY_MODEL_ATTRIBUTE, Json.object(prop("type", Json.text("integer")))),
                  prop(OPTIONAL_MODEL_ATTRIBUTE, Json.object(prop("type", Json.text("string")))),
                  prop(OPTIONAL_MODEL_ATTRIBUTE2, Json.object(prop("type", Json.text("boolean")))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"), Json.text("version"))))))));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEPackageWithEReference() throws JsonProcessingException {
      ePackage.getEClassifiers().add(namedElementClass);
      ePackage.getEClassifiers().add(refClass);

      eReference = EcoreTestUtil.eReference(getNameAsIdHelper(refClass), -1, 1, refClass);
      modelClass.getEStructuralFeatures().add(eReference);

      ePackage.getEClassifiers().add(modelClass);

      final JsonNode actual = jsonSchemaCreator.from(ePackage);
      final ObjectNode expected = Json.object(
         prop("$schema", Json.text("http://json-schema.org/draft-07/schema#")),
         prop("$id", Json.text(getIdHelper(ePackage))),
         prop("title", Json.text(getSchemaTitleHelper(ePackage))),
         prop("type", Json.text("object")),
         prop("definitions", Json.object(
            prop(getNameAsIdHelper(namedElementClass), Json.object(
               prop("$id", Json.text(getIdHelper(namedElementClass))),
               prop("title", Json.text(namedElementClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(namedElementClass)),
                  prop("name", Json.object(prop("type", Json.text("string")))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"))))),
            prop(getNameAsIdHelper(refClass), Json.object(
               prop("$id", Json.text(getIdHelper(refClass))),
               prop("title", Json.text(refClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(refClass)))),
               prop("additionalProperties", Json.bool(false)))),
            prop(getNameAsIdHelper(modelClass), Json.object(
               prop("$id", Json.text(getIdHelper(modelClass))),
               prop("title", Json.text(modelClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(modelClass)),
                  prop(NAME_ATTRIBUTE, Json.object(prop("type", Json.text("string")))),
                  prop(getNameAsIdHelper(refClass), Json.object(
                     prop("$ref", Json.text(getRefHelper(refClass))))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"))))))));

      assertEquals(expected, actual);
   }

   @Test
   public void createJsonSchemaFromEPackageWithEENumEAttribute() throws JsonProcessingException {
      ePackage.getEClassifiers().add(namedElementClass);
      ePackage.getEClassifiers().add(priorityEEnum);

      priorityEEnumAttribute = EcoreTestUtil.createEAttribute(PRIORITY_EENUM_ATTRIBUTE, 1, 1, priorityEEnum);
      modelClass.getEStructuralFeatures().add(priorityEEnumAttribute);

      ePackage.getEClassifiers().add(modelClass);

      final JsonNode actual = jsonSchemaCreator.from(ePackage);
      final ObjectNode expected = Json.object(
         prop("$schema", Json.text("http://json-schema.org/draft-07/schema#")),
         prop("$id", Json.text(getIdHelper(ePackage))),
         prop("title", Json.text(getSchemaTitleHelper(ePackage))),
         prop("type", Json.text("object")),
         prop("definitions", Json.object(
            prop(getNameAsIdHelper(namedElementClass), Json.object(
               prop("$id", Json.text(getIdHelper(namedElementClass))),
               prop("title", Json.text(namedElementClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(namedElementClass)),
                  prop("name", Json.object(prop("type", Json.text("string")))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"))))),
            prop(getNameAsIdHelper(priorityEEnum), Json.object(
               prop("type", Json.text("string")),
               prop("enum",
                  Json.array(Json.text("low"), Json.text("medium"), Json.text("high"), Json.text("veryhigh"))))),
            prop(getNameAsIdHelper(modelClass), Json.object(
               prop("$id", Json.text(getIdHelper(modelClass))),
               prop("title", Json.text(modelClass.getName())),
               prop("type", Json.text("object")),
               prop("properties", Json.object(
                  prop("eClass", getEClassPropertyHelper(modelClass)),
                  prop(NAME_ATTRIBUTE, Json.object(prop("type", Json.text("string")))),
                  prop(PRIORITY_EENUM_ATTRIBUTE, Json.object(
                     prop("$ref", Json.text(getRefHelper(priorityEEnum))))))),
               prop("additionalProperties", Json.bool(false)),
               prop("required", Json.array(Json.text("name"), Json.text(PRIORITY_EENUM_ATTRIBUTE))))))));

      assertEquals(expected, actual);
   }

}
