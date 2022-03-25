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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;

public class JsonValueUtilTest {

   public JsonValueUtilTest() {
      super();
   }

   @Test
   public void isInteger_intSized() {
      assertThat(JsonValueUtil.isInteger(42.0), is(true));
      assertThat(JsonValueUtil.isInteger(0.0), is(true));
      assertThat(JsonValueUtil.isInteger(Math.PI), is(false));
   }

   @Test
   public void isInteger_longSized() {
      assertThat(JsonValueUtil.isInteger(42_000_000_000.0), is(true));
      assertThat(JsonValueUtil.isInteger(Math.PI + 10_000_000_000.0), is(false));
   }

   @Test
   public void isInteger_bigSized() {
      assertThat(JsonValueUtil.isInteger(42_000_000_000_000_000_000.0), is(true));
      // These are too big to have a fractional part
   }

   @Test
   public void valueOf_intSized() {
      assertThat(JsonValueUtil.valueOf(42.0), is(IntNode.valueOf(42)));
      assertThat(JsonValueUtil.valueOf(0.0), is(IntNode.valueOf(0)));
      assertThat(JsonValueUtil.valueOf(Math.PI), is(DoubleNode.valueOf(Math.PI)));
   }

   @Test
   public void valueOf_longSized() {
      assertThat(JsonValueUtil.valueOf(42_000_000_000.0), is(LongNode.valueOf(42_000_000_000L)));
      assertThat(JsonValueUtil.valueOf(Math.PI + 10_000_000_000.0), is(DoubleNode.valueOf(Math.PI + 10_000_000_000.0)));
   }

   @Test
   public void valueOf_bigSized() {
      assertThat(JsonValueUtil.valueOf(42_000_000_000_000_000_000.0),
         is(BigIntegerNode.valueOf(new BigInteger("42000000000000000000"))));
   }

}
