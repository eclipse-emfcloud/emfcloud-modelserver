/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.jsonschema;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
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

   private JsonNode createJsonSchema(final EClass eClass) {
      visitedEClasses.add(eClass);
      final ObjectNode obj = Json.object();
      obj.set("type", Json.text("object"));
      obj.set("properties", properties(eClass.getEStructuralFeatures()));
      obj.set("additionalProperties", Json.bool(false));

      final List<String> requiredProps = eClass.getEStructuralFeatures()
         .stream()
         .filter(ETypedElement::isRequired)
         .map(ENamedElement::getName)
         .collect(Collectors.toList());

      if (!requiredProps.isEmpty()) {
         final ArrayNode required = Json.array();
         requiredProps.forEach(required::add);
         obj.set("required", required);
      }

      return obj;
   }

   private JsonNode createJsonSchema(final EStructuralFeature feature) {
      if (feature instanceof EReference) {
         return createJsonSchema((EReference) feature);
      }
      return createJsonSchema((EAttribute) feature);
   }

   private JsonNode createJsonSchema(final EReference eReference) {
      final ObjectNode obj = Json.object();
      if (eReference.getUpperBound() > 1 || eReference.getUpperBound() == -1) {
         obj.set("type", Json.text("array"));
         obj.set("items", createJsonSchema(eReference.getEReferenceType()));
         return obj;
      }
      return createJsonSchema(eReference.getEReferenceType());
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
      if (Types.isDate(eClassifier)) {
         obj.set("format", TextNode.valueOf("date-time"));
      } else if (Types.isEnum(eClassifier)) {
         EEnum eEnum = (EEnum) eClassifier;
         final ArrayNode literals = Json.array();
         eEnum.getELiterals().forEach(literal -> literals.add(literal.getLiteral()));
         obj.set("enum", literals);
      }
      return obj;
   }

   private static String toJsonSchemaType(final EClassifier eClassifier) {
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
