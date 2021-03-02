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
package org.eclipse.emfcloud.modelserver.common.utils;

import java.lang.reflect.ParameterizedType;

public final class GenericsUtil {
   private GenericsUtil() {}

   public static ParameterizedType getParametrizedType(final Class<?> clazz, final Class<?> genericBaseclass) {
      if (clazz.getSuperclass().equals(genericBaseclass)) { // check that we are at the top of the hierarchy
         return (ParameterizedType) clazz.getGenericSuperclass();
      }
      return getParametrizedType(clazz.getSuperclass(), genericBaseclass);
   }

   public static Class<?> getGenericTypeParameterClass(final Class<?> clazz, final Class<?> genericBaseclass) {
      return (Class<?>) (GenericsUtil.getParametrizedType(clazz, genericBaseclass))
         .getActualTypeArguments()[0];
   }
}
