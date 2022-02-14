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
package org.eclipse.emfcloud.modelserver.common.utils;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;

/**
 * A specialized map that keys values by API version ranges. It provides accessors
 * to look up values by version, returning the first value from a range that includes
 * the requested version. Entries in the map are sorted such that a version look-up
 * will match the range with the least lower bound and least upper bound that span it.
 *
 * @param <T> the mapped value type
 */
public final class APIVersionMap<T> extends TreeMap<APIVersionRange, T> {

   private static final long serialVersionUID = 1L;

   public APIVersionMap() {
      super();
   }

   public APIVersionMap(final Map<APIVersionRange, ? extends T> map) {
      this();

      putAll(map);
   }

   /**
    * Get the value for the first range that matches the given {@code version}.
    *
    * @param version an API version to look up
    * @return the matching value, or {@code null} if no value is mapped by a range that includes the {@code version}
    */
   public T get(final APIVersion version) {
      return getOrDefault(version, null);
   }

   /**
    * Get the value for the first range that matches the given {@code version} or else some default.
    *
    * @param version an API version to look up
    * @return the matching value, or {@code defaultValue} if no value is mapped by a range that includes the
    *         {@code version}
    */
   public T getOrDefault(final APIVersion version, final T defaultValue) {
      // Find the narrowest range that includes the version. Narrowest is not
      // literally the narrowest because we don't have a measure of the magnitude
      // of a version number, but it's the first one that has the greatest lower bound
      // that includes the version (ranges with the same lower bound ordering themselves
      // by upper bound)
      APIVersionRange narrowest = null;

      for (Map.Entry<APIVersionRange, T> next : entrySet()) {
         APIVersionRange range = next.getKey();
         if (range.includes(version)) {
            if (narrowest == null || range.lowerBoundGreaterThan(narrowest)) {
               narrowest = range;
            }
         } else if (range.lower().greaterThan(version)) {
            // No subsequent ranges can include this version
            break;
         }
      }

      return narrowest == null ? defaultValue : get(narrowest);
   }

}
