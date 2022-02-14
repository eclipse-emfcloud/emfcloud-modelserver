/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common.tests.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.utils.APIVersionMap;
import org.junit.Test;

@SuppressWarnings("checkstyle:MemberName")
public class APIVersionMapTest {

   private final APIVersion v1 = APIVersion.of(1);
   private final APIVersion v1_0_1 = APIVersion.of(1, 0, 1);
   private final APIVersion v1_1 = APIVersion.of(1, 1);
   private final APIVersion v2 = APIVersion.of(2);
   private final APIVersion v2_0_2 = APIVersion.of(2, 0, 2);
   private final APIVersion v2_1 = APIVersion.of(2, 1);

   public APIVersionMapTest() {
      super();
   }

   @Test
   public void get_nonOverlapping() {
      APIVersionMap<String> map = new APIVersionMap<>();

      map.put(v1.range(v2), "b");
      map.put(v2.range(), "c");
      map.put(APIVersion.ZERO.range(v1), "a");

      assertThat(map.get(v1_0_1), is("b"));
      assertThat(map.get(v2_1), is("c"));
      assertThat(map.get(APIVersion.of(0, 5)), is("a"));
   }

   @Test
   public void get_overlapping() {
      APIVersionMap<String> map = new APIVersionMap<>();

      map.put(v1_1.rangeInclusive(v2_1), "b");
      map.put(v1.range(v2), "c");
      map.put(APIVersion.ZERO.range(), "a");

      assertThat(map.get(v1), is("c"));
      assertThat(map.get(v1_0_1), is("c"));
      assertThat(map.get(v1_1), is("b"));
      assertThat(map.get(v2_1), is("b"));
      assertThat(map.get(v2_0_2), is("b"));
      assertThat(map.get(APIVersion.of(0, 5)), is("a"));
      assertThat(map.get(APIVersion.of(2, 1, 1)), is("a"));
   }

}
