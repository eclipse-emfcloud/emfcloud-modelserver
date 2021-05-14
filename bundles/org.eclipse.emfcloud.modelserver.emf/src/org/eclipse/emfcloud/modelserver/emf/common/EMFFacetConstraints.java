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
import java.util.Objects;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

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

   public static final int WHITESPACE_DEFAULT = ExtendedMetaData.INSTANCE
      .getWhiteSpaceFacet(EcorePackage.Literals.EBOOLEAN);
   public static final List<String> ENUMERATION_DEFAULT = ExtendedMetaData.INSTANCE
      .getEnumerationFacet(EcorePackage.Literals.EBOOLEAN);
   public static final List<String> PATTERN_DEFAULT = ExtendedMetaData.INSTANCE
      .getPatternFacet(EcorePackage.Literals.EBOOLEAN);
   public static final int TOTALDIGITS_DEFAULT = ExtendedMetaData.INSTANCE
      .getTotalDigitsFacet(EcorePackage.Literals.EBOOLEAN);
   public static final int FRACTIONDIGITS_DEFAULT = ExtendedMetaData.INSTANCE
      .getFractionDigitsFacet(EcorePackage.Literals.EBOOLEAN);
   public static final int LENGTH_DEFAULT = ExtendedMetaData.INSTANCE
      .getLengthFacet(EcorePackage.Literals.EBOOLEAN);
   public static final int MINLENGTH_DEFAULT = ExtendedMetaData.INSTANCE
      .getMinLengthFacet(EcorePackage.Literals.EBOOLEAN);
   public static final int MAXLENGTH_DEFAULT = ExtendedMetaData.INSTANCE
      .getMaxLengthFacet(EcorePackage.Literals.EBOOLEAN);
   public static final String MINEXCLUSIVE_DEFAULT = ExtendedMetaData.INSTANCE
      .getMinExclusiveFacet(EcorePackage.Literals.EBOOLEAN);
   public static final String MAXEXCLUSIVE_DEFAULT = ExtendedMetaData.INSTANCE
      .getMaxExclusiveFacet(EcorePackage.Literals.EBOOLEAN);
   public static final String MININCLUSIVE_DEFAULT = ExtendedMetaData.INSTANCE
      .getMinInclusiveFacet(EcorePackage.Literals.EBOOLEAN);
   public static final String MAXINCLUSIVE_DEFAULT = ExtendedMetaData.INSTANCE
      .getMaxInclusiveFacet(EcorePackage.Literals.EBOOLEAN);

   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = WhiteSpaceFilter.class)
   private Integer whiteSpace;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = EnumerationFilter.class)
   private List<String> enumeration;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = PatternFilter.class)
   private List<String> pattern;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = TotalDigitsFilter.class)
   private Integer totalDigits;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = FractionDigitsFilter.class)
   private Integer fractionDigits;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LengthFilter.class)
   private Integer length;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MinLengthFilter.class)
   private Integer minLength;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MaxLengthFilter.class)
   private Integer maxLength;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MinExclusiveFilter.class)
   private String minExclusive;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MaxExclusiveFilter.class)
   private String maxExclusive;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MinInclusiveFilter.class)
   private String minInclusive;
   @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = MaxInclusiveFilter.class)
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

   @SuppressWarnings("unchecked")
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

   public EMFFacetConstraints() {}

   @SuppressWarnings({ "CyclomaticComplexity", "BooleanExpressionComplexity" })
   public boolean hasConstraints() {
      return !(Objects.equals(this.whiteSpace, WHITESPACE_DEFAULT)
         && Objects.equals(this.enumeration, ENUMERATION_DEFAULT)
         && Objects.equals(this.pattern, PATTERN_DEFAULT)
         && Objects.equals(this.totalDigits, TOTALDIGITS_DEFAULT)
         && Objects.equals(this.fractionDigits, FRACTIONDIGITS_DEFAULT)
         && Objects.equals(this.length, LENGTH_DEFAULT)
         && Objects.equals(this.minLength, MINLENGTH_DEFAULT)
         && Objects.equals(this.maxLength, MAXLENGTH_DEFAULT)
         && Objects.equals(this.minExclusive, MINEXCLUSIVE_DEFAULT)
         && Objects.equals(this.maxExclusive, MAXEXCLUSIVE_DEFAULT)
         && Objects.equals(this.minInclusive, MININCLUSIVE_DEFAULT)
         && Objects.equals(this.maxInclusive, MAXINCLUSIVE_DEFAULT));
   }
}

class DefaultValueFilter {
   protected Object defaultValue;

   DefaultValueFilter(final Object defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public boolean equals(final Object obj) {
      return Objects.equals(defaultValue, obj);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }
}

class WhiteSpaceFilter extends DefaultValueFilter {
   WhiteSpaceFilter() {
      super(EMFFacetConstraints.WHITESPACE_DEFAULT);
   }
}

class EnumerationFilter extends DefaultValueFilter {
   EnumerationFilter() {
      super(EMFFacetConstraints.ENUMERATION_DEFAULT);
   }
}

class PatternFilter extends DefaultValueFilter {
   PatternFilter() {
      super(EMFFacetConstraints.PATTERN_DEFAULT);
   }
}

class TotalDigitsFilter extends DefaultValueFilter {
   TotalDigitsFilter() {
      super(EMFFacetConstraints.TOTALDIGITS_DEFAULT);
   }
}

class FractionDigitsFilter extends DefaultValueFilter {
   FractionDigitsFilter() {
      super(EMFFacetConstraints.FRACTIONDIGITS_DEFAULT);
   }
}

class LengthFilter extends DefaultValueFilter {
   LengthFilter() {
      super(EMFFacetConstraints.LENGTH_DEFAULT);
   }
}

class MinLengthFilter extends DefaultValueFilter {
   MinLengthFilter() {
      super(EMFFacetConstraints.MINLENGTH_DEFAULT);
   }
}

class MaxLengthFilter extends DefaultValueFilter {
   MaxLengthFilter() {
      super(EMFFacetConstraints.MAXLENGTH_DEFAULT);
   }
}

class MinExclusiveFilter extends DefaultValueFilter {
   MinExclusiveFilter() {
      super(EMFFacetConstraints.MINEXCLUSIVE_DEFAULT);
   }
}

class MaxExclusiveFilter extends DefaultValueFilter {
   MaxExclusiveFilter() {
      super(EMFFacetConstraints.MAXEXCLUSIVE_DEFAULT);
   }
}

class MinInclusiveFilter extends DefaultValueFilter {
   MinInclusiveFilter() {
      super(EMFFacetConstraints.MININCLUSIVE_DEFAULT);
   }
}

class MaxInclusiveFilter extends DefaultValueFilter {
   MaxInclusiveFilter() {
      super(EMFFacetConstraints.MAXINCLUSIVE_DEFAULT);
   }
}
