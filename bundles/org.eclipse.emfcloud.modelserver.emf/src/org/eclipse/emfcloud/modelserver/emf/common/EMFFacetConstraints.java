/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.List;

public class EMFFacetConstraints {

   private int whiteSpace;
   private List<String> enumeration;
   private List<String> pattern;
   private int totalDigets;
   private int fractionDigets;
   private int length;
   private int minLength;
   private int maxLength;
   private String minExclusive;
   private String maxExclusive;
   private String minInclusive;
   private String maxInclusive;

   public int getWhiteSpace() { return whiteSpace; }

   public void setWhiteSpace(final int whiteSpace) { this.whiteSpace = whiteSpace; }

   public List<String> getEnumeration() { return enumeration; }

   public void setEnumeration(final List<String> enumeration) { this.enumeration = enumeration; }

   public List<String> getPattern() { return pattern; }

   public void setPattern(final List<String> pattern) { this.pattern = pattern; }

   public int getTotalDigets() { return totalDigets; }

   public void setTotalDigets(final int totalDigets) { this.totalDigets = totalDigets; }

   public int getFractionDigets() { return fractionDigets; }

   public void setFractionDigets(final int fractionDigets) { this.fractionDigets = fractionDigets; }

   public int getLength() { return length; }

   public void setLength(final int length) { this.length = length; }

   public int getMinLength() { return minLength; }

   public void setMinLength(final int minLength) { this.minLength = minLength; }

   public int getMaxLength() { return maxLength; }

   public void setMaxLength(final int maxLength) { this.maxLength = maxLength; }

   public String getMinExclusive() { return minExclusive; }

   public void setMinExclusive(final String minExclusive) { this.minExclusive = minExclusive; }

   public String getMaxExclusive() { return maxExclusive; }

   public void setMaxExclusive(final String maxExclusive) { this.maxExclusive = maxExclusive; }

   public String getMinInclusive() { return minInclusive; }

   public void setMinInclusive(final String minInclusive) { this.minInclusive = minInclusive; }

   public String getMaxInclusive() { return maxInclusive; }

   public void setMaxInclusive(final String maxInclusive) { this.maxInclusive = maxInclusive; }

   public boolean isWhiteSpaceDefault() { return whiteSpace == 0; }

   public boolean isEnumerationDefault() { return enumeration.isEmpty(); }

   public boolean isPatternDefault() { return pattern.isEmpty(); }

   public boolean isTotalDigetsDefault() { return totalDigets == -1; }

   public boolean isFractionDigetsDefault() { return fractionDigets == -1; }

   public boolean isLengthDefault() { return length == -1; }

   public boolean isMinLengthDefault() { return minLength == -1; }

   public boolean isMaxLengthDefault() { return maxLength == -1; }

   public boolean isMinExclusiveDefault() { return minExclusive == null; }

   public boolean isMaxExclusiveDefault() { return maxExclusive == null; }

   public boolean isMinInclusiveDefault() { return minInclusive == null; }

   public boolean isMaxInclusiveDefault() { return maxInclusive == null; }

   public EMFFacetConstraints(final int whiteSpace, final List<String> enumeration, final List<String> pattern,
      final int totalDigets,
      final int fractionDigets, final int length, final int minLength, final int maxLength, final String minExclusive,
      final String maxExclusive,
      final String minInclusive, final String maxInclusive) {
      this.whiteSpace = whiteSpace;
      this.enumeration = enumeration;
      this.pattern = pattern;
      this.totalDigets = totalDigets;
      this.fractionDigets = fractionDigets;
      this.length = length;
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.minExclusive = minExclusive;
      this.maxExclusive = maxExclusive;
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

}
