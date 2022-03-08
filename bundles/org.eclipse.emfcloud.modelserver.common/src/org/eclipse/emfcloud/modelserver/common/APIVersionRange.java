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

/**
 * A range of versions of the Model Server API. A version range may be open (exclusive) or
 * closed (inclusive) on either end. It may be unbounded on the upper end, in which case it
 * is open (exclusive) on that end. The lower end may not be unbounded.
 */
public final class APIVersionRange implements Comparable<APIVersionRange> {

   public static final APIVersionRange ANY = APIVersion.ZERO.range();

   private final APIVersion lower;

   private final boolean lowerOpen;

   private final APIVersion upper;

   private final boolean upperOpen;

   private final int hash;

   public APIVersionRange(final APIVersion lower, final boolean lowerOpen, final APIVersion upper,
      final boolean upperOpen) {

      super();

      if (lower == null) {
         throw new IllegalArgumentException("lower bound is required");
      }
      if (upper == null && !upperOpen) {
         throw new IllegalArgumentException("unlimited upper bound must be open");
      }

      this.lower = lower;
      this.lowerOpen = lowerOpen;
      this.upper = upper;
      this.upperOpen = upperOpen;

      this.hash = Objects.hash(lower, lowerOpen, upper, upperOpen);
   }

   /** Create a range unbounded at the open end. */
   public APIVersionRange(final APIVersion lower, final boolean lowerOpen) {
      this(lower, lowerOpen, null, true);
   }

   /** Create a range closed at the {@code lower} bound and unbounded at the upper end. */
   public APIVersionRange(final APIVersion lower) {
      this(lower, false, null, true);
   }

   public APIVersion lower() {
      return lower;
   }

   public boolean lowerOpen() {
      return lowerOpen;
   }

   public APIVersion upper() {
      return upper;
   }

   public boolean upperOpen() {
      return upperOpen;
   }

   public boolean isUnbounded() { return upper == null; }

   public boolean includes(final APIVersion version) {
      return (lowerOpen ? lower.lessThan(version) : lower.lessThanOrEqual(version))
         && (isUnbounded() || (upperOpen ? upper.greaterThan(version) : upper.greaterThanOrEqual(version)));
   }

   /**
    * Is my lower bound greater than another {@code range}'s, accounting for openness?
    *
    * @param range another range
    * @return {@code true} if my lower bound is greater than {@code range}'s or it is an open bound where
    *         {@code range}'s is closed; {@code false}, otherwise
    */
   public boolean lowerBoundGreaterThan(final APIVersionRange range) {
      if (lower.greaterThan(range.lower)) {
         return true;
      }
      if (lower.equals(range.lower)) {
         // An open lower bound is greater than a closed lower bound
         return lowerOpen && !range.lowerOpen;
      }
      return false;
   }

   @Override
   public int compareTo(final APIVersionRange o) {
      // First compare by lower bound
      int result = lower.compareTo(o.lower);
      if (result == 0) {
         // Sort closed-at-the-lower-bound ranges before open-at-the-lower-bound
         result = Boolean.compare(lowerOpen, o.lowerOpen);
      }

      if (result == 0) {
         // Then compare by upper bound. Take care of unbounded cases
         result = Boolean.compare(isUnbounded(), o.isUnbounded());
         if (result == 0 && !isUnbounded()) {
            result = upper.compareTo(o.upper);
            if (result == 0) {
               // Sort open-at-the-upper-bound ranges before closed-at-the-upper-bound
               result = Boolean.compare(o.upperOpen, upperOpen);
            }
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
      if (!(obj instanceof APIVersionRange)) {
         return false;
      }
      APIVersionRange other = (APIVersionRange) obj;
      return lower.equals(other.lower) && Objects.equals(upper, other.upper)
         && lowerOpen == other.lowerOpen && upperOpen == other.upperOpen;
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder();
      result.append(lowerOpen ? '(' : '[');
      result.append(lower);
      result.append(',');
      if (isUnbounded()) {
         result.append(')');
      } else {
         result.append(' ');
         result.append(upper);
         result.append(upperOpen ? ')' : ']');
      }
      return result.toString();
   }

}
