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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

public abstract class DefaultJsonSchemaConverterTestHelper {

   protected void prettyPrintResultsHelper(final JsonNode expected, final JsonNode actual)
      throws JsonProcessingException {
      ObjectMapper mapper = new ObjectMapper();
      System.out.println("expected:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected));
      System.out.println("actual:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual));
   }

   protected String getSchemaTitleHelper(final ENamedElement eNamedElement) {
      return "JSON type schema for \'" + eNamedElement.getName() + "\'";
   }

   protected String getIdHelper(final ENamedElement eNamedElement) {
      return "#" + getNameAsIdHelper(eNamedElement);
   }

   protected String getIdHelper(final EPackage ePackage) {
      return ePackage.getNsURI();
   }

   protected String getRefHelper(final ENamedElement eNamedElement) {
      return "#/definitions/" + getNameAsIdHelper(eNamedElement);
   }

   protected String getNameAsIdHelper(final ENamedElement eNamedElement) {
      return eNamedElement.getName().trim().toLowerCase();
   }

   protected JsonNode getEClassPropertyHelper(final EClassifier eClassifier) {
      return Json.object().set("const",
         TextNode.valueOf(eClassifier.getEPackage().getNsURI() + "#//" + eClassifier.getName()));
   }
}
