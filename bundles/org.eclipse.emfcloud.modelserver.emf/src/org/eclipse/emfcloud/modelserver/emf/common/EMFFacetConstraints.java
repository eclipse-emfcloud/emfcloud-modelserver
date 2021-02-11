/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EMFFacetConstraints {

   public static final String WHITESPACE = "whiteSpace";
   public static final String ENUMERATION = "enumeration";
   public static final String PATTERN = "pattern";
   public static final String TOTALDIGITS = "totalDigits";
   public static final String FRACTIONDIGITS = "fractionDigits";
   public static final String LENGTH = "length";
   public static final String MINLENGTH = "minLength";
   public static final String MAXLENGTH = "maxLength";
   public static final String MINEXCLUSIVE = "minExclusive";
   public static final String MAXEXCLUSIVE = "maxExclusive";
   public static final String MININCLUSIVE = "minInclusive";
   public static final String MAXINCLUSIVE = "maxInclusive";

   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = WhiteSpaceFilter.class)
   private Integer whiteSpace;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = StringListFilter.class)
   private List<String> enumeration;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = StringListFilter.class)
   private List<String> pattern;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IntegerFilter.class)
   private Integer totalDigits;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IntegerFilter.class)
   private Integer fractionDigits;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IntegerFilter.class)
   private Integer length;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IntegerFilter.class)
   private Integer minLength;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IntegerFilter.class)
   private Integer maxLength;
   private String minExclusive;
   private String maxExclusive;
   private String minInclusive;
   private String maxInclusive;

   public Integer getWhiteSpace() { return whiteSpace; }

   public void setWhiteSpace(final Integer whiteSpace) { this.whiteSpace = whiteSpace; }

   public List<String> getEnumeration() { return enumeration; }

   public void setEnumeration(final List<String> enumeration) { this.enumeration = enumeration; }

   public List<String> getPattern() { return pattern; }

   public void setPattern(final List<String> pattern) { this.pattern = pattern; }

   public Integer getTotalDigits() { return totalDigits; }

   public void setTotalDigits(final Integer totalDigits) { this.totalDigits = totalDigits; }

   public Integer getFractionDigits() { return fractionDigits; }

   public void setFractionDigits(final Integer fractionDigits) { this.fractionDigits = fractionDigits; }

   public Integer getLength() { return length; }

   public void setLength(final Integer length) { this.length = length; }

   public Integer getMinLength() { return minLength; }

   public void setMinLength(final Integer minLength) { this.minLength = minLength; }

   public Integer getMaxLength() { return maxLength; }

   public void setMaxLength(final Integer maxLength) { this.maxLength = maxLength; }

   public String getMinExclusive() { return minExclusive; }

   public void setMinExclusive(final String minExclusive) { this.minExclusive = minExclusive; }

   public String getMaxExclusive() { return maxExclusive; }

   public void setMaxExclusive(final String maxExclusive) { this.maxExclusive = maxExclusive; }

   public String getMinInclusive() { return minInclusive; }

   public void setMinInclusive(final String minInclusive) { this.minInclusive = minInclusive; }

   public String getMaxInclusive() { return maxInclusive; }

   public void setMaxInclusive(final String maxInclusive) { this.maxInclusive = maxInclusive; }

   public EMFFacetConstraints(final Map<String, Object> facetMap) {
      this.whiteSpace = (Integer) facetMap.getOrDefault(WHITESPACE, null);
      this.enumeration = (List<String>) facetMap.getOrDefault(ENUMERATION, null);
      this.pattern = (List<String>) facetMap.getOrDefault(PATTERN, null);
      this.totalDigits = (Integer) facetMap.getOrDefault(TOTALDIGITS, null);
      this.fractionDigits = (Integer) facetMap.getOrDefault(FRACTIONDIGITS, null);
      this.length = (Integer) facetMap.getOrDefault(LENGTH, null);
      this.minLength = (Integer) facetMap.getOrDefault(MINLENGTH, null);
      this.maxLength = (Integer) facetMap.getOrDefault(MAXLENGTH, null);
      this.minExclusive = (String) facetMap.getOrDefault(MINEXCLUSIVE, null);
      this.maxExclusive = (String) facetMap.getOrDefault(MAXEXCLUSIVE, null);
      this.minInclusive = (String) facetMap.getOrDefault(MININCLUSIVE, null);
      this.maxInclusive = (String) facetMap.getOrDefault(MAXINCLUSIVE, null);
   }

   public EMFFacetConstraints() {

   }

}

class WhiteSpaceFilter {
   @Override
   public boolean equals(final Object obj) {
      if (obj instanceof Integer) {
         return obj == Integer.valueOf(0);
      }
      return true;
   }

   @Override
   public int hashCode() {
      // TODO Auto-generated method stub
      return super.hashCode();
   }
}

class IntegerFilter {
   @Override
   public boolean equals(final Object obj) {
      if (obj instanceof Integer) {
         return obj == Integer.valueOf(-1);
      }
      return true;
   }

   @Override
   public int hashCode() {
      // TODO Auto-generated method stub
      return super.hashCode();
   }
}

class StringListFilter {
   @Override
   public boolean equals(final Object obj) {
      if (obj instanceof List) {
         return ((List) obj).isEmpty();
      }
      return true;
   }

   @Override
   public int hashCode() {
      // TODO Auto-generated method stub
      return super.hashCode();
   }
}
