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
package org.eclipse.emfcloud.modelserver.jsonschema;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Injectable singleton class that represents a converter from either EPackages or EClasses to valid Json type schemas.
 *
 */
public class DefaultJsonSchemaConverter implements JsonSchemaConverter {

   @Override
   public JsonNode from(final EObject eObject) {
      if (eObject instanceof EPackage) {
         return createJsonSchemaFromEPackage((EPackage) eObject);
      }
      if (eObject instanceof EClass) {
         return createJsonSchemaFromEClass((EClass) eObject);
      }
      return createJsonSchemaFromEClass(eObject.eClass());
   }

   protected JsonNode createJsonSchemaFromEPackage(final EPackage ePackage) {
      final ObjectNode schemaNode = Json.object();
      schemaNode.set("$schema", TextNode.valueOf("http://json-schema.org/draft-07/schema#"));
      schemaNode.set("$id", TextNode.valueOf(ePackage.getNsURI()));
      schemaNode.set("title", TextNode.valueOf("JSON type schema for \'" + ePackage.getName() + "\'"));
      schemaNode.set("type", Json.text("object"));

      final ObjectNode definitionsNode = Json.object();
      ePackage.getEClassifiers().forEach(eClassifier -> {
         definitionsNode.set(eClassifier.getName().trim().toLowerCase(), createDefinitionFromEClassifier(eClassifier));
      });

      schemaNode.set("definitions", definitionsNode);
      return schemaNode;
   }

   protected JsonNode createDefinitionFromEClassifier(final EClassifier eClassifier) {
      final ObjectNode objectNode = Json.object();
      objectNode.set("$id", Json.text("#" + eClassifier.getName().trim().toLowerCase()));
      if (eClassifier instanceof EClass) {
         objectNode.set("title", Json.text(eClassifier.getName()));
         objectNode.set("type", Json.text("object"));
         objectNode.set("properties", createPropertiesFromEClassifier(eClassifier));
         objectNode.set("additionalProperties", Json.bool(false));
         createRequiredProperties(((EClass) eClassifier)).ifPresent(node -> objectNode.set("required", node));
      } else if (eClassifier instanceof EEnum) {
         return deriveType(eClassifier);
      }
      return objectNode;
   }

   protected Optional<JsonNode> createRequiredProperties(final EClass eClassifier) {
      EList<EStructuralFeature> features = eClassifier.getEAllStructuralFeatures();
      final List<String> requiredProps = features.stream()
         .filter(ETypedElement::isRequired)
         .map(ENamedElement::getName)
         .collect(Collectors.toList());
      if (!requiredProps.isEmpty()) {
         final ArrayNode arrayNode = Json.array();
         requiredProps.forEach(arrayNode::add);
         return Optional.of(arrayNode);
      }
      return Optional.empty();
   }

   protected JsonNode createJsonSchemaFromEClass(final EClass eClass) {
      return createJsonSchema(eClass, false);
   }

   protected JsonNode createJsonSchema(final EClass eClass, final boolean eClassAsAttribute) {
      final ObjectNode objectNode = Json.object();
      if (!eClassAsAttribute) {
         objectNode.set("$id", Json.text("#" + eClass.getName().trim().toLowerCase()));
         objectNode.set("title", Json.text(eClass.getName()));
         objectNode.set("type", Json.text("object"));
         objectNode.set("properties", createPropertiesFromEStructuralFeatures(eClass.getEStructuralFeatures()));
         objectNode.set("additionalProperties", Json.bool(false));
         createRequiredProperties(eClass).ifPresent(node -> objectNode.set("required", node));
      } else {
         objectNode.set("$ref", TextNode.valueOf("#/definitions/" + eClass.getName().trim().toLowerCase()));
      }
      return objectNode;
   }

   protected JsonNode createJsonSchemaFromEStructuralFeature(final EStructuralFeature feature,
      final boolean featureAsAttribute) {
      if (feature instanceof EReference) {
         return createJsonSchema((EReference) feature, featureAsAttribute);
      }
      return createJsonSchema((EAttribute) feature);
   }

   protected JsonNode createJsonSchemaFromEStructuralFeature(final EStructuralFeature feature) {
      return createJsonSchemaFromEStructuralFeature(feature, false);
   }

   protected JsonNode createJsonSchema(final EReference eReference, final boolean featureAsAttribute) {
      final ObjectNode objectNode = Json.object();
      if (eReference.getUpperBound() > 1 || eReference.getUpperBound() == -1) {
         objectNode.set("type", Json.text("array"));
         JsonNode feature = Json.object();
         if (!featureAsAttribute) {
            feature = createJsonSchemaFromEClass(eReference.getEReferenceType());
         } else {
            feature = Json.object().set("$ref",
               TextNode.valueOf("#/definitions/" + eReference.getEType().getName().trim().toLowerCase()));
         }
         objectNode.set("items", feature);
         return objectNode;
      }
      return createJsonSchema(eReference.getEReferenceType(), true);
   }

   protected ObjectNode createJsonSchema(final EAttribute eAttribute) {
      return deriveType(eAttribute, eAttribute.getUpperBound());
   }

   protected ObjectNode createPropertiesFromEStructuralFeatures(
      final Collection<? extends EStructuralFeature> features) {
      ObjectNode properties = Json.object();
      for (EStructuralFeature feature : features) {
         JsonNode jsonNode = createJsonSchemaFromEStructuralFeature(feature);
         properties.set(feature.getName(), jsonNode);
      }
      return properties;
   }

   protected ObjectNode createPropertiesFromEClassifier(final EClassifier eClassifier) {
      final Collection<? extends EStructuralFeature> features = ((EClass) eClassifier).getEStructuralFeatures();
      ObjectNode properties = Json.object();
      properties.set("eClass",
         Json.object().set("const",
            TextNode.valueOf(eClassifier.getEPackage().getNsURI() + "#//" + eClassifier.getName())));

      fetchPropertiesFromEAllSuperTypes(((EClass) eClassifier).getEAllSuperTypes(), properties);

      for (EStructuralFeature feature : features) {
         JsonNode jsonNode = createJsonSchemaFromEStructuralFeature(feature, true);
         properties.set(feature.getName(), jsonNode);
      }
      return properties;
   }

   protected void fetchPropertiesFromEAllSuperTypes(final EList<EClass> eSuperTypes, final ObjectNode properties) {
      eSuperTypes.forEach(eSuperType -> {
         for (EStructuralFeature feature : eSuperType.getEStructuralFeatures()) {
            if (!(feature.getName().equals("eClass"))) {
               JsonNode jsonNode = createJsonSchemaFromEStructuralFeature(feature, true);
               properties.set(feature.getName(), jsonNode);
            }
         }
      });
   }

   protected ObjectNode deriveType(final EAttribute eAttribute, final int upperBound) {
      if (upperBound > 1 || upperBound == -1) {
         final ObjectNode obj = Json.object();
         obj.set("type", TextNode.valueOf("array"));
         final ObjectNode items = Json.object();
         items.set("type", deriveType(eAttribute));
         obj.set("items", items);
         return obj;
      }
      return deriveType(eAttribute);
   }

   protected ObjectNode deriveType(final EAttribute eAttribute) {
      final ObjectNode objectNode = Json.object();
      if (Types.isEnum(eAttribute.getEType())) {
         objectNode.set("$ref", Json.text("#/definitions/" + eAttribute.getEType().getName().trim().toLowerCase()));
      } else {
         objectNode.set("type", TextNode.valueOf(toJsonSchemaType(eAttribute.getEType())));
         if (Types.isDate(eAttribute.getEType())) {
            objectNode.set("format", TextNode.valueOf("date-time"));
         }
      }
      return objectNode;
   }

   protected ObjectNode deriveType(final EClassifier eClassifier) {
      final ObjectNode objectNode = Json.object();
      if (Types.isEnum(eClassifier)) {
         objectNode.set("type", TextNode.valueOf("string"));
         EEnum eEnum = (EEnum) eClassifier;
         final ArrayNode literals = Json.array();
         eEnum.getELiterals().forEach(literal -> literals.add(literal.getLiteral()));
         objectNode.set("enum", literals);
      } else {
         objectNode.set("type", TextNode.valueOf(toJsonSchemaType(eClassifier)));
         if (Types.isDate(eClassifier)) {
            objectNode.set("format", TextNode.valueOf("date-time"));
         }
      }
      return objectNode;
   }

   protected String toJsonSchemaType(final EClassifier eClassifier) {
      if (Types.isBoolean(eClassifier)) {
         return "boolean";
      } else if (Types.isInteger(eClassifier)) {
         return "integer";
      } else if (Types.isNumber(eClassifier)) {
         return "number";
      }
      return "string";
   }
}
