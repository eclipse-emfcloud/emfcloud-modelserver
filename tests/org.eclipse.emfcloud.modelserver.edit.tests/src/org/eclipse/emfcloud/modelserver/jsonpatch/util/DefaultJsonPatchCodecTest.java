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

import static org.eclipse.emfcloud.modelserver.tests.util.EMFMatchers.eEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.BooleanValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Copy;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.Move;
import org.eclipse.emfcloud.modelserver.jsonpatch.NumberValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.OpKind;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;
import org.eclipse.emfcloud.modelserver.jsonpatch.Remove;
import org.eclipse.emfcloud.modelserver.jsonpatch.Replace;
import org.eclipse.emfcloud.modelserver.jsonpatch.StringValue;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class DefaultJsonPatchCodecTest {

   private JsonPatchCodec fixture;

   private Codec codec;

   private EClass foo;

   private JsonNode patches;

   public DefaultJsonPatchCodecTest() {
      super();
   }

   @Test
   public void decode_addOp_boolean() {
      JsonNode addPatchNode = patches.get("add-boolean");
      JsonPatch patch = requireDecode(addPatchNode);
      Add addOp = requireOp(patch, Add.class);
      assertThat(addOp.getOp(), is(OpKind.ADD));
      assertThat(addOp.getPath(), is("/path/to/booleans/-"));
      BooleanValue value = requireValue(addOp, BooleanValue.class);
      assertThat(value.isValue(), is(true));
   }

   @Test
   public void decode_addOp_string() {
      JsonNode addPatchNode = patches.get("add-string");
      JsonPatch patch = requireDecode(addPatchNode);
      Add addOp = requireOp(patch, Add.class);
      assertThat(addOp.getOp(), is(OpKind.ADD));
      assertThat(addOp.getPath(), is("/path/to/strings/3"));
      StringValue value = requireValue(addOp, StringValue.class);
      assertThat(value.getValue(), is("Hello, world!"));
   }

   @Test
   public void decode_addOp_number() {
      JsonNode addPatchNode = patches.get("add-number");
      JsonPatch patch = requireDecode(addPatchNode);
      Add addOp = requireOp(patch, Add.class);
      assertThat(addOp.getOp(), is(OpKind.ADD));
      assertThat(addOp.getPath(), is("/path/to/numbers/-"));
      NumberValue value = requireValue(addOp, NumberValue.class);
      assertThat(value.getValue(), is(42.0));
   }

   @Test
   public void decode_addOp_object() {
      JsonNode addPatchNode = patches.get("add-object");
      JsonPatch patch = requireDecode(addPatchNode);
      Add addOp = requireOp(patch, Add.class);
      assertThat(addOp.getOp(), is(OpKind.ADD));
      assertThat(addOp.getPath(), is("/path/to/objects/-"));
      ObjectValue value = requireValue(addOp, ObjectValue.class);
      assertThat(value.getValue(), eEqualTo(foo));
   }

   @Test
   public void decode_removeOp() {
      JsonNode removePatchNode = patches.get("remove");
      JsonPatch patch = requireDecode(removePatchNode);
      Remove removeOp = requireOp(patch, Remove.class);
      assertThat(removeOp.getOp(), is(OpKind.REMOVE));
      assertThat(removeOp.getPath(), is("/path/to/strings/3"));
   }

   @Test
   public void decode_replaceOp_boolean() {
      JsonNode replacePatchNode = patches.get("replace-boolean");
      JsonPatch patch = requireDecode(replacePatchNode);
      Replace replaceOp = requireOp(patch, Replace.class);
      assertThat(replaceOp.getOp(), is(OpKind.REPLACE));
      assertThat(replaceOp.getPath(), is("/path/to/booleans/0"));
      BooleanValue value = requireValue(replaceOp, BooleanValue.class);
      assertThat(value.isValue(), is(true));
   }

   @Test
   public void decode_replaceOp_string() {
      JsonNode replacePatchNode = patches.get("replace-string");
      JsonPatch patch = requireDecode(replacePatchNode);
      Replace replaceOp = requireOp(patch, Replace.class);
      assertThat(replaceOp.getOp(), is(OpKind.REPLACE));
      assertThat(replaceOp.getPath(), is("/path/to/strings/3"));
      StringValue value = requireValue(replaceOp, StringValue.class);
      assertThat(value.getValue(), is("Hello, world!"));
   }

   @Test
   public void decode_replaceOp_number() {
      JsonNode replacePatchNode = patches.get("replace-number");
      JsonPatch patch = requireDecode(replacePatchNode);
      Replace replaceOp = requireOp(patch, Replace.class);
      assertThat(replaceOp.getOp(), is(OpKind.REPLACE));
      assertThat(replaceOp.getPath(), is("/path/to/numbers/1"));
      NumberValue value = requireValue(replaceOp, NumberValue.class);
      assertThat(value.getValue(), is(42.0));
   }

   @Test
   public void decode_replaceOp_object() {
      JsonNode replacePatchNode = patches.get("replace-object");
      JsonPatch patch = requireDecode(replacePatchNode);
      Replace replaceOp = requireOp(patch, Replace.class);
      assertThat(replaceOp.getOp(), is(OpKind.REPLACE));
      assertThat(replaceOp.getPath(), is("/path/to/objects/2"));
      ObjectValue value = requireValue(replaceOp, ObjectValue.class);
      assertThat(value.getValue(), eEqualTo(foo));
   }

   @Test
   public void decode_moveOp() {
      JsonNode movePatchNode = patches.get("move");
      JsonPatch patch = requireDecode(movePatchNode);
      Move moveOp = requireOp(patch, Move.class);
      assertThat(moveOp.getOp(), is(OpKind.MOVE));
      assertThat(moveOp.getPath(), is("/path/to/there/-"));
      assertThat(moveOp.getFrom(), is("/path/to/here/3"));
   }

   @Test
   public void decode_copyOp() {
      JsonNode copyPatchNode = patches.get("copy");
      JsonPatch patch = requireDecode(copyPatchNode);
      Copy copyOp = requireOp(patch, Copy.class);
      assertThat(copyOp.getOp(), is(OpKind.COPY));
      assertThat(copyOp.getPath(), is("/path/to/there/-"));
      assertThat(copyOp.getFrom(), is("/path/to/here/3"));
   }

   @Test
   public void decode_testOp_boolean() {
      JsonNode testPatchNode = patches.get("test-boolean");
      JsonPatch patch = requireDecode(testPatchNode);
      org.eclipse.emfcloud.modelserver.jsonpatch.Test testOp = requireOp(patch,
         org.eclipse.emfcloud.modelserver.jsonpatch.Test.class);
      assertThat(testOp.getOp(), is(OpKind.TEST));
      assertThat(testOp.getPath(), is("/path/to/booleans/0"));
      BooleanValue value = requireValue(testOp, BooleanValue.class);
      assertThat(value.isValue(), is(true));
   }

   @Test
   public void decode_testOp_string() {
      JsonNode testPatchNode = patches.get("test-string");
      JsonPatch patch = requireDecode(testPatchNode);
      org.eclipse.emfcloud.modelserver.jsonpatch.Test testOp = requireOp(patch,
         org.eclipse.emfcloud.modelserver.jsonpatch.Test.class);
      assertThat(testOp.getOp(), is(OpKind.TEST));
      assertThat(testOp.getPath(), is("/path/to/strings/1"));
      StringValue value = requireValue(testOp, StringValue.class);
      assertThat(value.getValue(), is("Hello, world!"));
   }

   @Test
   public void decode_testOp_number() {
      JsonNode testPatchNode = patches.get("test-number");
      JsonPatch patch = requireDecode(testPatchNode);
      org.eclipse.emfcloud.modelserver.jsonpatch.Test testOp = requireOp(patch,
         org.eclipse.emfcloud.modelserver.jsonpatch.Test.class);
      assertThat(testOp.getOp(), is(OpKind.TEST));
      assertThat(testOp.getPath(), is("/path/to/numbers/3"));
      NumberValue value = requireValue(testOp, NumberValue.class);
      assertThat(value.getValue(), is(42.0));
   }

   @Test
   public void decode_testOp_object() {
      JsonNode testPatchNode = patches.get("test-object");
      JsonPatch patch = requireDecode(testPatchNode);
      org.eclipse.emfcloud.modelserver.jsonpatch.Test testOp = requireOp(patch,
         org.eclipse.emfcloud.modelserver.jsonpatch.Test.class);
      assertThat(testOp.getOp(), is(OpKind.TEST));
      assertThat(testOp.getPath(), is("/path/to/objects/2"));
      ObjectValue value = requireValue(testOp, ObjectValue.class);
      assertThat(value.getValue(), eEqualTo(foo));
   }

   @Test
   public void encode_patch() {
      for (JsonNode patchNode : patches) {
         JsonPatch patch = requireDecode(patchNode);
         ArrayNode encoded = encode(patch);
         assertThat(encoded.toString(), equalTo(patchNode.toString()));
      }
   }

   //
   // Test framework
   //

   @Before
   public void createFixture() {
      codec = new DefaultJsonCodec();
      fixture = JsonPatchCodec.Factory.DEFAULT.createCodec(codec, EcorePackage.Literals.EPACKAGE);
   }

   @Before
   public void createTestModel() {
      foo = EcoreFactory.eINSTANCE.createEClass();
      foo.setName("Foo");
      foo.setAbstract(true);
   }

   @Before
   public void loadTestPatches() throws IOException {
      ObjectMapper mapper = new ObjectMapper();
      URL patchesJson = new File("resources/patches.json").getAbsoluteFile().toURI().toURL();
      patches = mapper.readTree(patchesJson);
   }

   JsonPatch requireDecode(final JsonNode patchNode) {
      try {
         Optional<JsonPatch> result = fixture.decode(patchNode);
         assertThat("No JsonPatch decoded", result.isPresent(), is(true));
         return result.get();
      } catch (DecodingException e) {
         e.printStackTrace();
         fail("Failed to decode patch");
         return null; // Unreachable
      }
   }

   ArrayNode encode(final JsonPatch patch) {
      try {
         return fixture.encode(patch);
      } catch (EncodingException e) {
         e.printStackTrace();
         fail("Failed to encode patch");
         return null; // Unreachable
      }
   }

   <O extends Operation> O requireOp(final JsonPatch patch, final Class<O> type) {
      assertThat(patch.getPatch(), hasItem(instanceOf(type)));
      return patch.getPatch().stream().filter(type::isInstance).findAny().map(type::cast).get();
   }

   <V extends Value> V requireValue(final Add add, final Class<V> type) {
      assertThat(add.getValue(), instanceOf(type));
      return type.cast(add.getValue());
   }

   <V extends Value> V requireValue(final Replace replace, final Class<V> type) {
      assertThat(replace.getValue(), instanceOf(type));
      return type.cast(replace.getValue());
   }

   <V extends Value> V requireValue(final org.eclipse.emfcloud.modelserver.jsonpatch.Test test, final Class<V> type) {
      assertThat(test.getValue(), instanceOf(type));
      return type.cast(test.getValue());
   }
}
