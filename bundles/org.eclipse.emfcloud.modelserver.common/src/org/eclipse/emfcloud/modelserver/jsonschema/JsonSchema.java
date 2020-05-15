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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

public class JsonSchema {

   private final Set<EClassifier> visitedEClasses = new LinkedHashSet<>();

   public static JsonNode from(final EClass eClass) {
      final JsonSchema conversion = new JsonSchema();
      return conversion.createJsonSchema(eClass);
   }

   public static JsonNode from(final EObject eObject) {
      final JsonSchema conversion = new JsonSchema();
      if (eObject instanceof EPackage) {
         return conversion.createJsonSchema((EPackage) eObject);
      }
      return conversion.createJsonSchema(eObject.eClass());
   }

   private JsonNode createJsonSchema(final EPackage ePackage) {
      final ObjectNode schema = Json.object();
      schema.set("$schema", TextNode.valueOf("http://json-schema.org/draft-07/schema#"));
      schema.set("$id", TextNode.valueOf(ePackage.getNsURI()));
      schema.set("title", TextNode.valueOf("JSON Schema for " + ePackage.getName()));

      final ObjectNode definitions = Json.object();
      ePackage.getEClassifiers().forEach(eClassifier -> {
         definitions.set(eClassifier.getName().trim().toLowerCase(), createJsonSchema(eClassifier));
      });

      schema.set("definitions", definitions);
      schema.set("type", Json.text("object"));
      return schema;
   }

   private JsonNode createJsonSchema(final EClassifier eClassifier) {
      final ObjectNode objectNode = Json.object();
      objectNode.set("$id", Json.text("#" + eClassifier.getName().trim().toLowerCase()));
      objectNode.set("type", Json.text("object"));
      if (eClassifier instanceof EClass) {
         final ArrayNode arrayNode = Json.array();
         ((EClass) eClassifier).getESuperTypes().forEach(eSuperType -> {
            ObjectNode obj = Json.object();
            obj.set("$ref", TextNode.valueOf("#/definitions/" + eSuperType.getName().trim().toLowerCase()));
            arrayNode.add(obj);
         });
      }
      objectNode.set("title", Json.text(eClassifier.getName()));
      if (eClassifier instanceof EClass) {
         objectNode.set("properties", properties(eClassifier));
         objectNode.set("additionalProperties", Json.bool(false));
         createJsonNode(((EClass) eClassifier).getEStructuralFeatures())
            .ifPresent(node -> objectNode.set("required", node));
      }
      return objectNode;
   }

   private JsonNode createJsonSchema(final EClass eClass) {
      return createJsonSchema(eClass, false);
   }

   private JsonNode createJsonSchema(final EClass eClass, final boolean eClassAsAttribute) {
      visitedEClasses.add(eClass);
      final ObjectNode obj = Json.object();
      if (!eClassAsAttribute) {
         obj.set("type", Json.text("object"));
         obj.set("$id", Json.text("#" + eClass.getName().trim().toLowerCase()));
         obj.set("title", Json.text(eClass.getName()));
         obj.set("properties", properties(eClass.getEStructuralFeatures()));
         obj.set("additionalProperties", Json.bool(false));
         createJsonNode(eClass.getEAllStructuralFeatures()).ifPresent(node -> obj.set("required", node));
      } else {
         obj.set("$ref", TextNode.valueOf("#/definitions/" + eClass.getName().trim().toLowerCase()));
      }
      return obj;
   }

   private Optional<JsonNode> createJsonNode(final EList<EStructuralFeature> features) {
      final List<String> requiredProps = features
         .stream()
         .filter(ETypedElement::isRequired)
         .map(ENamedElement::getName)
         .collect(Collectors.toList());

      if (!requiredProps.isEmpty()) {
         final ArrayNode required = Json.array();
         requiredProps.forEach(required::add);
         return Optional.of(required);
      }

      return Optional.empty();
   }

   private JsonNode createJsonSchema(final EStructuralFeature feature, final boolean featureAsAttribute) {
      if (feature instanceof EReference) {
         return createJsonSchema((EReference) feature, featureAsAttribute);
      }
      return createJsonSchema((EAttribute) feature);
   }

   private JsonNode createJsonSchema(final EStructuralFeature feature) {
      return createJsonSchema(feature, false);
   }

   private JsonNode createJsonSchema(final EReference eReference, final boolean featureAsAttribute) {
      final ObjectNode obj = Json.object();
      if (eReference.getUpperBound() > 1 || eReference.getUpperBound() == -1) {
         obj.set("type", Json.text("array"));
         JsonNode feature = Json.object();
         if (!featureAsAttribute) {
            feature = createJsonSchema(eReference.getEReferenceType());
         } else {
            feature = Json.object().set("$ref",
               TextNode.valueOf("#/definitions/" + eReference.getEType().getName().trim().toLowerCase()));
         }
         obj.set("items", feature);
         return obj;
      }
      return createJsonSchema(eReference.getEReferenceType(), true);
   }

   private ObjectNode createJsonSchema(final EAttribute eAttribute) {
      return deriveType(eAttribute.getEType(), eAttribute.getUpperBound());
   }

   private ObjectNode properties(final Collection<? extends EStructuralFeature> features) {
      ObjectNode properties = Json.object();
      for (EStructuralFeature feature : features) {
         if (!isCircular(feature)) {
            JsonNode jsonElement = createJsonSchema(feature);
            properties.set(feature.getName(), jsonElement);
         }
      }
      return properties;
   }

   private ObjectNode properties(final EClassifier eClassifier) {
      final Collection<? extends EStructuralFeature> features = ((EClass) eClassifier).getEStructuralFeatures();
      ObjectNode properties = Json.object();
      properties.set("eClass",
         Json.object().set("const",
            TextNode.valueOf(eClassifier.getEPackage().getNsURI() + "#//" + eClassifier.getName())));

      ((EClass) eClassifier).getESuperTypes().forEach(eSuperType -> {
         for (EStructuralFeature feature : eSuperType.getEStructuralFeatures()) {
            if (!(feature.getName().equals("eClass"))) {
               JsonNode jsonElement = createJsonSchema(feature, true);
               properties.set(feature.getName(), jsonElement);
            }
         }
      });

      for (EStructuralFeature feature : features) {
         if (!isCircular(feature)) {
            JsonNode jsonElement = createJsonSchema(feature, true);
            properties.set(feature.getName(), jsonElement);
         }
      }
      return properties;
   }

   private boolean isCircular(final EStructuralFeature feature) {
      if (feature instanceof EReference) {
         return visitedEClasses.contains(feature.getEType());
      }
      return false;
   }

   private static ObjectNode deriveType(final EClassifier eClassifier, final int upper) {
      if (upper > 1 || upper == -1) {
         final ObjectNode obj = Json.object();
         obj.set("type", TextNode.valueOf("array"));
         final ObjectNode items = Json.object();
         items.set("type", deriveType(eClassifier));
         obj.set("items", items);
         return obj;
      }
      return deriveType(eClassifier);
   }

   private static ObjectNode deriveType(final EClassifier eClassifier) {
      final ObjectNode obj = Json.object();
      obj.set("type", TextNode.valueOf(toJsonSchemaType(eClassifier)));
      if (Types.isEnum(eClassifier)) {
         EEnum eEnum = (EEnum) eClassifier;
         final ArrayNode literals = Json.array();
         eEnum.getELiterals().forEach(literal -> literals.add(literal.getLiteral()));
         obj.set("enum", literals);
      } else if (Types.isDate(eClassifier)) {
         obj.set("format", TextNode.valueOf("date-time"));
      }
      return obj;
   }

   private static String toJsonSchemaType(final EClassifier eClassifier) {
      if (Types.isEnum(eClassifier)) {
         return "string";
      } else if (Types.isBoolean(eClassifier)) {
         return "boolean";
      } else if (Types.isInteger(eClassifier)) {
         return "integer";
      } else if (Types.isNumber(eClassifier)) {
         return "number";
      }
      return "string";
   }
}
