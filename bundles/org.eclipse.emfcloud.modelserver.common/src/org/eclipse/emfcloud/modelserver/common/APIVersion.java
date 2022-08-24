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
package org.eclipse.emfcloud.modelserver.common;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A version of the Model Server API.
 */
public final class APIVersion implements Comparable<APIVersion> {

   private static final Pattern API_PATTERN = Pattern.compile("^/?api/v(\\d+)\\b");

   /** The indeterminate API version (<tt>0.0.0</tt>). */
   public static final APIVersion ZERO = new APIVersion(0, 0, 0);

   /** The <em>API v1</em> version (<tt>1.0.0</tt>). */
   public static final APIVersion API_V1 = APIVersion.of(1);

   /** The <em>API v2</em> version (<tt>2.0.0</tt>). */
   public static final APIVersion API_V2 = APIVersion.of(2);

   private final int major;

   private final int minor;

   private final int patch;

   private final int hash;

   public APIVersion(final int major, final int minor, final int patch) {
      super();

      if (major < 0) {
         throw new IllegalArgumentException("negative major version");
      }
      if (minor < 0) {
         throw new IllegalArgumentException("negative minor version");
      }
      if (patch < 0) {
         throw new IllegalArgumentException("negative patch version");
      }

      this.major = major;
      this.minor = minor;
      this.patch = patch;

      this.hash = Objects.hash(major, minor, patch);
   }

   public APIVersion(final int major, final int minor) {
      this(major, minor, 0);
   }

   public APIVersion(final int major) {
      this(major, 0, 0);
   }

   public int major() {
      return major;
   }

   public int minor() {
      return minor;
   }

   public int patch() {
      return patch;
   }

   /**
    * Create an unbounded range with myself as the closed lower bound.
    */
   public APIVersionRange range() {
      return new APIVersionRange(this);
   }

   /**
    * Create range with myself as the closed lower bound up to but not including
    * the given upper bound.
    */
   public APIVersionRange range(final APIVersion to) {
      return new APIVersionRange(this, false, to, true);
   }

   /**
    * Create range with myself as the closed lower bound up to and including
    * the given upper bound.
    */
   public APIVersionRange rangeInclusive(final APIVersion to) {
      return new APIVersionRange(this, false, to, false);
   }

   public boolean lessThan(final APIVersion other) {
      return this.compareTo(other) < 0;
   }

   public boolean lessThanOrEqual(final APIVersion other) {
      return this.compareTo(other) <= 0;
   }

   public boolean greaterThan(final APIVersion other) {
      return this.compareTo(other) > 0;
   }

   public boolean greaterThanOrEqual(final APIVersion other) {
      return this.compareTo(other) >= 0;
   }

   @Override
   public int compareTo(final APIVersion o) {
      int result = major - o.major;
      if (result == 0) {
         result = minor - o.minor;
         if (result == 0) {
            result = patch - o.patch;
         }
      }
      return result;
   }

   @Override
   public int hashCode() {
      return hash;
   }

   @Override
   public boolean equals(final Object obj) {
      if (!(obj instanceof APIVersion)) {
         return false;
      }
      APIVersion other = (APIVersion) obj;
      return other.major == major && other.minor == minor && other.patch == patch;
   }

   @Override
   public String toString() {
      return String.format("%s.%s.%s", major, minor, patch);
   }

   public static APIVersion of(final int major) {
      return new APIVersion(major);
   }

   public static APIVersion of(final int major, final int minor) {
      return new APIVersion(major, minor);
   }

   public static APIVersion of(final int major, final int minor, final int patch) {
      return new APIVersion(major, minor, patch);
   }

   /**
    * Extract the API version from a request URI.
    *
    * @param requestURI a request URI, which usually has a form like {@code "/api/v2/..."}
    * @return the API version, or {@link #ZERO} if the URI is not a versioned API request URI
    */
   public static APIVersion forRequestURI(final String requestURI) {
      if (requestURI == null) {
         return ZERO;
      }

      Matcher m = API_PATTERN.matcher(requestURI);
      return m.find() ? APIVersion.of(Integer.parseInt(m.group(1))) : ZERO;
   }

}
