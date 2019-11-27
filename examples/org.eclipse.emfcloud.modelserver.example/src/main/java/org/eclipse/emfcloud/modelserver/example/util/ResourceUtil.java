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
package org.eclipse.emfcloud.modelserver.example.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public final class ResourceUtil {
   private static final ResourceUtil INSTANCE = new ResourceUtil();

   public static boolean copyFromResource(final String resourcePath, final File destFile) {
      final ClassLoader classLoader = INSTANCE.getClass().getClassLoader();
      try {

         final InputStream stream = classLoader.getResourceAsStream(resourcePath);
         if (stream == null) {
            return false;
         }
         FileUtils.copyInputStreamToFile(stream, destFile);
      } catch (final IOException e) {
         return false;
      }
      return true;
   }

   private ResourceUtil() {}

}
