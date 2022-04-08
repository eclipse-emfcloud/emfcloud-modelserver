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
package org.eclipse.emfcloud.modelserver.tests.util;

import java.util.Optional;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Additional Hamcrest matchers.
 */
public final class MoreMatchers {

   /**
    * Not instantiable by clients.
    */
   private MoreMatchers() {
      super();
   }

   public static Matcher<String> containsRegex(final String pattern) {
      Pattern regex = Pattern.compile(pattern);

      return new TypeSafeMatcher<String>(String.class) {
         @Override
         public void describeTo(final Description description) {
            description.appendText("has a substring matching: " + pattern);
         }

         @Override
         protected boolean matchesSafely(final String item) {
            java.util.regex.Matcher m = regex.matcher(item);
            return m.find();
         }
      };
   }

   public static <T> Matcher<Optional<T>> presentValueThat(final Matcher<? super T> valueMatcher) {
      return new TypeSafeDiagnosingMatcher<>() {
         @Override
         public void describeTo(final Description description) {
            description.appendText("present Optional that ");
            description.appendDescriptionOf(valueMatcher);
         }

         @Override
         protected boolean matchesSafely(final Optional<T> item, final Description mismatchDescription) {
            if (item.isEmpty()) {
               mismatchDescription.appendText("empty Optional");
               return false;
            }
            if (!valueMatcher.matches(item.get())) {
               mismatchDescription.appendText("value that ");
               valueMatcher.describeMismatch(item.get(), mismatchDescription);
               return false;
            }
            return true;
         }
      };
   }

   public static <T> Matcher<Optional<T>> emptyOptional() {
      return new TypeSafeMatcher<>() {
         @Override
         public void describeTo(final Description description) {
            description.appendText("empty optional");
         }

         @Override
         protected boolean matchesSafely(final Optional<T> item) {
            return item.isEmpty();
         }
      };
   }
}
