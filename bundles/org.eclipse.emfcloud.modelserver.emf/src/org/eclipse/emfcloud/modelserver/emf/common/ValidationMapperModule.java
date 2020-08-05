/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
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
package org.eclipse.emfcloud.modelserver.emf.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ValidationMapperModule extends SimpleModule {

   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public ValidationMapperModule(final Resource res) {
      addSerializer(BasicDiagnostic.class, new BasicDiagnosticSerializer(res));
      addDeserializer(BasicDiagnostic.class, new BasicDiagnosticDeserializer());
      addSerializer(EMFFacetConstraints.class, new EMFFacetConstraintsSerializer());
      addDeserializer(EMFFacetConstraints.class, new EMFFacetConstraintsDeserializer());
   }

   public ValidationMapperModule() {
      addDeserializer(BasicDiagnostic.class, new BasicDiagnosticDeserializer());
      addSerializer(EMFFacetConstraints.class, new EMFFacetConstraintsSerializer());
      addDeserializer(EMFFacetConstraints.class, new EMFFacetConstraintsDeserializer());
   }

   private static class BasicDiagnosticSerializer extends JsonSerializer<BasicDiagnostic> {

      private final Resource res;

      public BasicDiagnosticSerializer(final Resource res) {
         this.res = res;
      }

      @Override
      public void serialize(final BasicDiagnostic value, final JsonGenerator gen, final SerializerProvider serializers)
         throws IOException {
         gen.writeStartObject();
         Optional<EObject> eObject = value.getData().stream()
            .filter(EObject.class::isInstance)
            .map(EObject.class::cast)
            .findFirst();
         gen.writeObjectField("id", res.getURIFragment((eObject.get())));
         gen.writeObjectField("severity", value.getSeverity());
         gen.writeObjectField("source", value.getSource());
         gen.writeObjectField("code", value.getCode());
         gen.writeObjectField("message", value.getMessage());
         gen.writeObjectField("exception", value.getException());
         gen.writeObjectField("data", value.getData());
         gen.writeObjectField("children", value.getChildren());
         gen.writeEndObject();
      }

   }

   private static class BasicDiagnosticDeserializer extends JsonDeserializer<BasicDiagnostic> {

      @Override
      public BasicDiagnostic deserialize(final JsonParser p, final DeserializationContext ctxt)
         throws IOException, JsonProcessingException {
         JsonNode node = p.getCodec().readTree(p);

         String source = node.get("source").asText();
         int code = node.get("code").asInt();
         String message = node.get("message").asText();
         Object[] data = p.getCodec().treeToValue(node.get("data"), Object[].class);
         List<BasicDiagnostic> children = new ArrayList<>();
         for (JsonNode child : node.get("children")) {
            children.add(p.getCodec().treeToValue(child, BasicDiagnostic.class));
         }
         return new BasicDiagnostic(source, code, children, message, data);
      }
   }

   private static class EMFFacetConstraintsSerializer extends JsonSerializer<EMFFacetConstraints> {

      @Override
      public void serialize(final EMFFacetConstraints value, final JsonGenerator gen,
         final SerializerProvider serializers)
         throws IOException {
         gen.writeStartObject();
         gen.writeObjectField("whiteSpace", value.getWhiteSpace());
         gen.writeObjectField("enumeration", value.getEnumeration());
         gen.writeObjectField("pattern", value.getPattern());
         gen.writeObjectField("totalDigets", value.getTotalDigets());
         gen.writeObjectField("fractionDigets", value.getFractionDigets());
         gen.writeObjectField("length", value.getLength());
         gen.writeObjectField("minLength", value.getMinLength());
         gen.writeObjectField("maxLength", value.getMaxLength());
         gen.writeObjectField("minExclusive", value.getMinExclusive());
         gen.writeObjectField("maxExclusive", value.getMaxExclusive());
         gen.writeObjectField("minInclusive", value.getMinInclusive());
         gen.writeObjectField("maxInclusive", value.getMaxInclusive());
         gen.writeEndObject();
      }

   }

   private static class EMFFacetConstraintsDeserializer extends JsonDeserializer<EMFFacetConstraints> {

      @Override
      public EMFFacetConstraints deserialize(final JsonParser p, final DeserializationContext ctxt)
         throws IOException, JsonProcessingException {
         JsonNode node = p.getCodec().readTree(p);

         int whiteSpace = node.get("whiteSpace").asInt();
         List<String> enumeration = new ArrayList<>();
         for (JsonNode n : node.get("enumeration")) {
            enumeration.add(n.asText());
         }
         List<String> pattern = new ArrayList<>();
         for (JsonNode n : node.get("pattern")) {
            pattern.add(n.asText());
         }
         int totalDigets = node.get("totalDigets").asInt();
         int fractionDigets = node.get("fractionDigets").asInt();
         int length = node.get("length").asInt();
         int minLength = node.get("minLength").asInt();
         int maxLength = node.get("maxLength").asInt();
         String minExclusive = node.get("minExclusive").asText();
         String maxExclusive = node.get("maxExclusive").asText();
         String minInclusive = node.get("minInclusive").asText();
         String maxInclusive = node.get("maxInclusive").asText();

         return new EMFFacetConstraints(whiteSpace, enumeration, pattern, totalDigets, fractionDigets, length,
            minLength, maxLength, minExclusive, maxExclusive, minInclusive, maxInclusive);
      }
   }
}
