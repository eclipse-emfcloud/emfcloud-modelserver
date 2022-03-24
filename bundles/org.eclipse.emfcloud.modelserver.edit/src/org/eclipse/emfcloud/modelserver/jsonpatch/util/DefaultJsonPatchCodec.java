/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Copy;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatchFactory;
import org.eclipse.emfcloud.modelserver.jsonpatch.Move;
import org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;
import org.eclipse.emfcloud.modelserver.jsonpatch.Replace;
import org.eclipse.emfcloud.modelserver.jsonpatch.StringValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Test;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The default JSON Patch codec.
 */
public class DefaultJsonPatchCodec implements JsonPatchCodec {

   private final Codec codec;

   private final OperationParser operationParser;
   private final ValueParser valueParser;

   private final OperationUnparser operationUnparser;
   private final ValueUnparser valueUnparser;

   private final TypeInference typeInference;

   public DefaultJsonPatchCodec(final Codec codec, final EClass modelType) {
      super();

      this.codec = codec;
      this.operationParser = new OperationParser();
      this.valueParser = new ValueParser();
      this.operationUnparser = new OperationUnparser();
      this.valueUnparser = new ValueUnparser();
      this.typeInference = new TypeInference(modelType, codec);
   }

   @Override
   public Optional<JsonPatch> decode(final JsonNode patch) throws DecodingException {
      JsonPatch result = JsonPatchFactory.eINSTANCE.createJsonPatch();

      if (patch.isArray()) {
         ArrayNode array = (ArrayNode) patch;
         for (JsonNode item : array) {
            parseOperation(item, result);
         }
      } else {
         parseOperation(patch, result);
      }

      return Optional.of(result).filter(p -> !p.getPatch().isEmpty());
   }

   @Override
   public ArrayNode encode(final JsonPatch patch) throws EncodingException {
      ArrayNode result = Json.array();

      for (Operation operation : patch.getPatch()) {
         unparseOperation(operation, result);
      }

      return result;
   }

   protected void parseOperation(final JsonNode operationNode, final JsonPatch patch) throws DecodingException {
      Operation operation = parseOperation(operationNode);
      if (operation != null) {
         patch.getPatch().add(operation);
      }
   }

   protected Operation parseOperation(final JsonNode operationNode) throws DecodingException {
      try {
         return operationParser.doSwitch(operationNode);
      } catch (WrappedException e) {
         if (e.exception() instanceof DecodingException) {
            throw (DecodingException) e.exception();
         }
         throw e;
      }
   }

   protected Value parseValue(final JsonNode valueNode, final String path) {
      Value result;

      // If the value is an object and it has no type information, we would not be able to
      // instantiate the correct EClass. So then infer the EClass to instantiate
      String inferredTypeProperty = null;
      if (valueNode.isObject() && !typeInference.hasTypeProperty(valueNode)) {
         inferredTypeProperty = typeInference.inferType(valueNode, path);
      }

      result = valueParser.doSwitch(valueNode);

      if (inferredTypeProperty != null) {
         typeInference.uninferType(valueNode, inferredTypeProperty);
      }

      return result;
   }

   protected void unparseOperation(final Operation operation, final ArrayNode patch) throws EncodingException {
      JsonNode operationNode = unparseOperation(operation);
      if (operationNode != null) {
         patch.add(operationNode);
      }
   }

   protected JsonNode unparseOperation(final Operation operation) throws EncodingException {
      try {
         return operationUnparser.doSwitch(operation);
      } catch (WrappedException e) {
         if (e.exception() instanceof EncodingException) {
            throw (EncodingException) e.exception();
         }
         throw e;
      }
   }

   protected JsonNode unparseValue(final Value value) {
      return valueUnparser.doSwitch(value);
   }

   //
   // Nested types
   //

   protected class OperationParser extends OperationSwitch<Operation> {

      @Override
      public Operation caseAdd(final JsonNode addOp) {
         Add result = JsonPatchFactory.eINSTANCE.createAdd();
         setPath(result, addOp);
         result.setValue(parseValue(addOp.get("value"), result.getPath()));
         return result;
      }

      @Override
      public Operation caseRemove(final JsonNode removeOp) {
         Remove result = JsonPatchFactory.eINSTANCE.createRemove();
         setPath(result, removeOp);
         return result;
      }

      @Override
      public Operation caseReplace(final JsonNode replaceOp) {
         Replace result = JsonPatchFactory.eINSTANCE.createReplace();
         setPath(result, replaceOp);
         result.setValue(parseValue(replaceOp.get("value"), result.getPath()));
         return result;
      }

      @Override
      public Operation caseMove(final JsonNode moveOp) {
         Move result = JsonPatchFactory.eINSTANCE.createMove();
         setPath(result, moveOp);
         getString(moveOp, "from").ifPresent(result::setFrom);
         return result;
      }

      @Override
      public Operation caseCopy(final JsonNode copyOp) {
         Copy result = JsonPatchFactory.eINSTANCE.createCopy();
         setPath(result, copyOp);
         getString(copyOp, "from").ifPresent(result::setFrom);
         return result;
      }

      @Override
      public Operation caseTest(final JsonNode testOp) {
         Test result = JsonPatchFactory.eINSTANCE.createTest();
         setPath(result, testOp);
         result.setValue(parseValue(testOp.get("value"), result.getPath()));
         return result;
      }

      void setPath(final Operation operation, final JsonNode operationNode) {
         getPath(operationNode).ifPresent(operation::setPath);
      }

   }

   protected class ValueParser extends ValueSwitch<Value> {
      @Override
      public Value caseBoolean(final boolean value) {
         return JsonPatchFactory.eINSTANCE.createValue(value);
      }

      @Override
      public Value caseString(final String value) {
         return JsonPatchFactory.eINSTANCE.createValue(value);
      }

      @Override
      public Value caseNumber(final double value) {
         return JsonPatchFactory.eINSTANCE.createValue(value);
      }

      @Override
      public Value caseObject(final JsonNode value) {
         Optional<EObject> object;

         try {
            object = codec.decode(value.toString());
         } catch (DecodingException e) {
            throw new WrappedException(e);
         }

         return object.map(JsonPatchFactory.eINSTANCE::createValue).orElse(null);
      }
   }

   protected class OperationUnparser extends JsonPatchSwitch<JsonNode> {

      @Override
      public ObjectNode caseOperation(final Operation object) {
         ObjectNode result = Json.object();
         result.set("op", Json.text(object.getOp().getLiteral()));
         if (object.getPath() != null) {
            result.set("path", Json.text(object.getPath()));
         }
         return result;
      }

      @Override
      public ObjectNode caseAdd(final Add object) {
         ObjectNode result = caseOperation(object);
         result.set("value", unparseValue(object.getValue()));
         return result;
      }

      @Override
      public ObjectNode caseReplace(final Replace object) {
         ObjectNode result = caseOperation(object);
         result.set("value", unparseValue(object.getValue()));
         return result;
      }

      @Override
      public ObjectNode caseMove(final Move object) {
         ObjectNode result = caseOperation(object);
         if (object.getFrom() != null) {
            result.set("from", Json.text(object.getFrom()));
         }
         return result;
      }

      @Override
      public ObjectNode caseCopy(final Copy object) {
         ObjectNode result = caseOperation(object);
         if (object.getFrom() != null) {
            result.set("from", Json.text(object.getFrom()));
         }
         return result;
      }

      @Override
      public ObjectNode caseTest(final Test object) {
         ObjectNode result = caseOperation(object);
         result.set("value", unparseValue(object.getValue()));
         return result;
      }

   }

   protected class ValueUnparser extends JsonPatchSwitch<JsonNode> {

      @Override
      public JsonNode caseBooleanValue(final BooleanValue object) {
         return Json.bool(object.isValue());
      }

      @Override
      public JsonNode caseStringValue(final StringValue object) {
         return Json.text(object.getValue());
      }

      @Override
      public JsonNode caseNumberValue(final NumberValue object) {
         return JsonValueUtil.valueOf(object.getValue());
      }

      @Override
      public JsonNode caseObjectValue(final ObjectValue object) {
         try {
            return codec.encode(object.getValue());
         } catch (EncodingException e) {
            throw new WrappedException(e);
         }
      }
   }

   protected static class TypeInference {
      private static final Pattern SLASH = Pattern.compile("/");
      private static final Pattern INDEX = Pattern.compile("^-|\\d+$");

      private final EClass modelType;

      private final String typeProperty;

      TypeInference(final EClass modelType, final Codec codec) {
         super();

         this.modelType = modelType;
         typeProperty = isJsonV2(codec.getClass()) ? "$type" : "eClass";
      }

      // TODO: Support for custom JSON codecs
      private static boolean isJsonV2(final Class<?> codecClass) {
         String classname = codecClass.getSimpleName().toLowerCase();
         if (classname.contains("json") && classname.contains("v2")) {
            return true;
         }
         Class<?> superclass = codecClass.getSuperclass();
         if (superclass != null && superclass != Object.class) {
            return isJsonV2(superclass);
         }
         return false;
      }

      boolean hasTypeProperty(final JsonNode node) {
         return node.has(typeProperty);
      }

      String inferType(final JsonNode node, final String path) {
         EClass referenceType = inferReferenceType(path);
         if (referenceType == null) {
            return null;
         }

         ((ObjectNode) node).set(typeProperty, Json.text(EcoreUtil.getURI(referenceType).toString()));
         return typeProperty;
      }

      void uninferType(final JsonNode node, final String typeProperty) {
         ((ObjectNode) node).remove(typeProperty);
      }

      private EClass inferReferenceType(final String path) {
         String relativePath = path.startsWith("/") ? path.substring(1) : path;
         return inferReferenceType(modelType, List.of(SLASH.split(relativePath)));
      }

      private EClass inferReferenceType(final EClass eClass, final List<String> path) {
         if (path.isEmpty()) {
            return eClass;
         }

         String first = path.get(0);
         EStructuralFeature feature = eClass.getEStructuralFeature(first);
         if (feature instanceof EReference) {
            EClass referenceType = ((EReference) feature).getEReferenceType();
            return inferReferenceType(referenceType, path.subList(1, path.size()));
         }

         // Is the segment an index? Elide it
         if (INDEX.asMatchPredicate().test(first)) {
            return inferReferenceType(eClass, path.subList(1, path.size()));
         }

         // Failed
         return null;
      }

   }

}
