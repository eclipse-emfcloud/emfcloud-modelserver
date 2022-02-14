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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

@SuppressWarnings("checkstyle:MemberName")
public class APIVersionRangeTest {

   private final APIVersion v1 = APIVersion.of(1);
   private final APIVersion v1_0_1 = APIVersion.of(1, 0, 1);
   private final APIVersion v1_1 = APIVersion.of(1, 1);
   private final APIVersion v2_0_2 = APIVersion.of(2, 0, 2);
   private final APIVersion v2_1 = APIVersion.of(2, 1);

   public APIVersionRangeTest() {
      super();
   }

   @Test
   public void equals() {
      assertThat(v1.range(), is(v1.range()));
      assertThat(v1.range(v2_1), is(v1.range(v2_1)));
      assertThat(v1.range(v2_1), not(v1.rangeInclusive(v2_1)));
      assertThat(v1.range(v2_1), not(new APIVersionRange(v1, true, v2_1, true)));
   }

   @Test
   public void includes() {
      assertThat(v1.range(), includes(v2_1));
      assertThat(v1.range(v2_1), includes(v2_0_2));
      assertThat(v1.range(v2_1), not(includes(v2_1)));
      assertThat(v1.rangeInclusive(v2_1), includes(v2_1));

      assertThat(v1_0_1.range(v2_1), includes(v1_0_1));
      assertThat(v1_0_1.range(v2_1), not(includes(v1)));
      assertThat(new APIVersionRange(v1_0_1, true, v2_1, true), not(includes(v1_0_1)));
   }

   @Test
   public void ordering() {
      assertThat(v1.range(), orderedBefore(v1_0_1.range()));
      // Closed lower bound before open lower bound
      assertThat(v1.range(), orderedBefore(new APIVersionRange(v1, true)));
      // Open upper bound before closed upper bound
      assertThat(v1.range(v2_1), orderedBefore(v1.rangeInclusive(v2_1)));
      // Lower bound is more significant than upper bound
      assertThat(v1.range(v2_1), orderedBefore(v1_0_1.range(v1_1)));
   }

   //
   // Test framework
   //

   Matcher<APIVersionRange> includes(final APIVersion version) {
      return new TypeSafeDiagnosingMatcher<APIVersionRange>(APIVersionRange.class) {
         @Override
         protected boolean matchesSafely(final APIVersionRange item, final Description mismatchDescription) {
            boolean result = item.includes(version);
            if (!result) {
               mismatchDescription.appendText("does not include version " + version);
            }
            return result;
         }

         @Override
         public void describeTo(final Description description) {
            description.appendText("includes version " + version);
         }
      };
   }

   Matcher<APIVersionRange> orderedBefore(final APIVersionRange range) {
      return new TypeSafeDiagnosingMatcher<APIVersionRange>(APIVersionRange.class) {
         @Override
         protected boolean matchesSafely(final APIVersionRange item, final Description mismatchDescription) {
            boolean result = item.compareTo(range) < 0;
            if (!result) {
               mismatchDescription.appendText("does not order before range " + range);
            }
            return result;
         }

         @Override
         public void describeTo(final Description description) {
            description.appendText("is ordered before range " + range);
         }
      };
   }

}
