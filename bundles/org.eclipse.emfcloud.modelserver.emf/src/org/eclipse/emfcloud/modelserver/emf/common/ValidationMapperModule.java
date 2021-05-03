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

   public static final String ID = "id";
   public static final String SEVERITY = "severity";
   public static final String SOURCE = "source";
   public static final String CODE = "code";
   public static final String MESSAGE = "message";
   public static final String EXCEPTION = "exception";
   public static final String DATA = "data";
   public static final String CHILDREN = "children";
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public ValidationMapperModule(final Resource res) {
      addSerializer(BasicDiagnostic.class, new BasicDiagnosticSerializer(res));
      addDeserializer(BasicDiagnostic.class, new BasicDiagnosticDeserializer());
   }

   public ValidationMapperModule() {
      addDeserializer(BasicDiagnostic.class, new BasicDiagnosticDeserializer());
   }

   private static class BasicDiagnosticSerializer extends JsonSerializer<BasicDiagnostic> {

      private final Resource res;

      BasicDiagnosticSerializer(final Resource res) {
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
         gen.writeObjectField(ValidationMapperModule.ID, res.getURIFragment((eObject.get())));
         gen.writeObjectField(ValidationMapperModule.SEVERITY, value.getSeverity());
         gen.writeObjectField(ValidationMapperModule.SOURCE, value.getSource());
         gen.writeObjectField(ValidationMapperModule.CODE, value.getCode());
         gen.writeObjectField(ValidationMapperModule.MESSAGE, value.getMessage());
         gen.writeObjectField(ValidationMapperModule.EXCEPTION, value.getException());
         gen.writeObjectField(ValidationMapperModule.DATA, value.getData());
         gen.writeObjectField(ValidationMapperModule.CHILDREN, value.getChildren());
         gen.writeEndObject();
      }

   }

   private static class BasicDiagnosticDeserializer extends JsonDeserializer<BasicDiagnostic> {

      @Override
      public BasicDiagnostic deserialize(final JsonParser p, final DeserializationContext ctxt)
         throws IOException, JsonProcessingException {
         JsonNode node = p.getCodec().readTree(p);

         String source = node.get(ValidationMapperModule.SOURCE).asText();
         int code = node.get(ValidationMapperModule.CODE).asInt();
         String message = node.get(ValidationMapperModule.MESSAGE).asText();
         Object[] data = p.getCodec().treeToValue(node.get(ValidationMapperModule.DATA), Object[].class);

         if (node.get(ValidationMapperModule.CHILDREN).isEmpty()) {
            int severity = node.get(ValidationMapperModule.SEVERITY).asInt();
            return new BasicDiagnostic(severity, source, code, message, data);
         }

         List<BasicDiagnostic> children = new ArrayList<>();
         for (JsonNode child : node.get(ValidationMapperModule.CHILDREN)) {
            children.add(p.getCodec().treeToValue(child, BasicDiagnostic.class));
         }
         return new BasicDiagnostic(source, code, children, message, data);
      }
   }
}
