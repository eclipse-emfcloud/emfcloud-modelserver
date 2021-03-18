/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.tests.util;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;

import org.hamcrest.Matcher;

public final class OSUtil {
   private static String OS_NAME = System.getProperty("os.name", "unknown").toLowerCase();

   private OSUtil() {}

   public static boolean isWindows() { return OS_NAME.contains("win"); }

   public static boolean isMac() { return OS_NAME.contains("mac"); }

   public static boolean isUnix() {
      return OS_NAME.contains("nux") || OS_NAME.contains("nix") || OS_NAME.contains("aix");
   }

   public static String osLineSeparator(final String text) {
      // assume linux by default
      return isWindows()
         ? text.replaceAll("\\n", System.lineSeparator())
         : text;
   }

   public static String osFileSeparator(final String text) {
      return isWindows()
         ? text.replace("/", File.separator)
         : text;
   }

   public static Matcher<String> osEndsWith(final String suffix) {
      return endsWith(osFileSeparator(suffix));
   }

   public static Matcher<String> osIs(final String text) {
      return is(osFileSeparator(text));
   }
}
