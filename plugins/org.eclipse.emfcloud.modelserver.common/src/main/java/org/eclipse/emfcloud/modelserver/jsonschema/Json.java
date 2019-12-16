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

import static java.util.Map.entry;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public final class Json {

   private Json() {}

   private static ObjectMapper mapper = new ObjectMapper();

   public static <T extends JsonNode> T parse(final String jsonAsString) throws IOException {
      final JsonNode jsonNode = mapper.readTree(jsonAsString);
      return jsonNode.deepCopy();
   }

   public static <T> T parse(final String objectAsString, final Class<T> as) throws IOException {
      return mapper.readValue(objectAsString, as);
   }

   public static ObjectNode object() {
      return mapper.createObjectNode();
   }

   public static ObjectNode object(final Map<String, JsonNode> props) {
      final ObjectNode obj = mapper.createObjectNode();
      props.forEach(obj::set);
      return obj;
   }

   public static Map.Entry<String, JsonNode> prop(final String name, final JsonNode value) {
      return entry(name, value);
   }

   @SafeVarargs
   public static ObjectNode object(final Map.Entry<String, JsonNode>... props) {
      final ObjectNode obj = mapper.createObjectNode();
      for (Map.Entry<String, JsonNode> prop : props) {
         obj.set(prop.getKey(), prop.getValue());
      }
      return obj;
   }

   public static ArrayNode array() {
      return mapper.createArrayNode();
   }

   public static ArrayNode array(final JsonNode... json) {
      final ArrayNode arrayNode = mapper.createArrayNode();
      for (JsonNode node : json) {
         arrayNode.add(node);
      }
      return arrayNode;
   }

   public static ArrayNode array(final List<String> list) {
      final ArrayNode array = mapper.createArrayNode();
      list.forEach(array::add);
      return array;
   }

   public static TextNode text(final String s) {
      return TextNode.valueOf(s);
   }

   public static BooleanNode bool(final Boolean b) {
      return BooleanNode.valueOf(b);
   }

   // based on https://stackoverflow.com/a/11459962
   public static JsonNode merge(final JsonNode mainNode, final JsonNode updateNode) {

      Iterator<String> fieldNames = updateNode.fieldNames();
      while (fieldNames.hasNext()) {

         String fieldName = fieldNames.next();
         JsonNode jsonNode = mainNode.get(fieldName);

         // if field exists and is an embedded object
         if (jsonNode != null && jsonNode.isObject()) {
            merge(jsonNode, updateNode.get(fieldName));
         } else {
            if (mainNode instanceof ObjectNode) {
               // Overwrite field
               JsonNode value = updateNode.get(fieldName);
               ((ObjectNode) mainNode).set(fieldName, value);
            }
         }

      }

      return mainNode;
   }
}
