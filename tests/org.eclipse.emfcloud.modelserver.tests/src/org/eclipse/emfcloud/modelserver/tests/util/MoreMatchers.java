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

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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

}
