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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emfcloud.modelserver.emf.configuration.FacetConfig;

public class DefaultFacetConfig implements FacetConfig {

   @Override
   public Set<String> getFacetSet() {
      Set<String> facetSet = new HashSet<>();
      facetSet.add(EMFFacetConstraints.WHITESPACE);
      facetSet.add(EMFFacetConstraints.ENUMERATION);
      facetSet.add(EMFFacetConstraints.PATTERN);
      facetSet.add(EMFFacetConstraints.TOTALDIGITS);
      facetSet.add(EMFFacetConstraints.FRACTIONDIGITS);
      facetSet.add(EMFFacetConstraints.LENGTH);
      facetSet.add(EMFFacetConstraints.MINLENGTH);
      facetSet.add(EMFFacetConstraints.MAXLENGTH);
      facetSet.add(EMFFacetConstraints.MAXEXCLUSIVE);
      facetSet.add(EMFFacetConstraints.MINEXCLUSIVE);
      facetSet.add(EMFFacetConstraints.MININCLUSIVE);
      facetSet.add(EMFFacetConstraints.MAXINCLUSIVE);
      return facetSet;
   }

}
