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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emfcloud.modelserver.emf.configuration.FacetConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class DefaultModelValidator implements ModelValidator {

   protected final ModelRepository modelRepository;
   protected final FacetConfig facetConfig;

   @Inject
   private ObjectMapper mapper;

   @Inject
   public DefaultModelValidator(final ModelRepository modelRepository, final FacetConfig facetConfig) {
      this.modelRepository = modelRepository;
      this.facetConfig = facetConfig;
   }

   @Override
   public JsonNode validate(final String modeluri, final ObjectMapper mapper) {
      Optional<EObject> model = this.modelRepository.getModel(modeluri);
      if (model.isPresent()) {
         Optional<Resource> res = this.modelRepository.loadResource(modeluri);
         if (res.isPresent()) {
            mapper.registerModule(new ValidationMapperModule(res.get()));
            BasicDiagnostic diagnostics = Diagnostician.INSTANCE.createDefaultDiagnostic(model.get());
            Diagnostician.INSTANCE.validate(model.get(), diagnostics,
               Diagnostician.INSTANCE.createDefaultContext());
            return mapper.valueToTree(diagnostics);
         }
      }
      return null;
   }

   @Override
   @SuppressWarnings({ "checkstyle:MethodLength", "JavaNCSS", "checkstyle:CyclomaticComplexity" })
   public JsonNode getValidationConstraints(final String modeluri) {
      Map<String, Map<String, JsonNode>> jsonResult = new HashMap<>();
      Optional<EObject> eObject = this.modelRepository.getModel(modeluri);
      if (eObject.isPresent()) {
         EPackage ePackage = eObject.get().eClass().getEPackage();
         for (EClassifier e : ePackage.getEClassifiers()) {
            if (e instanceof EClass) {
               // Map Feature -> ExtendedMetaData
               Map<String, JsonNode> featureMap = new HashMap<>();
               for (EStructuralFeature esf : ((EClass) e).getEStructuralFeatures()) {
                  if (esf instanceof EAttribute) {
                     EDataType dataType = ((EAttribute) esf).getEAttributeType();
                     // Map facet -> Value
                     Map<String, Object> result = new HashMap<>();

                     for (String s : facetConfig.getFacetSet()) {
                        switch (s) {
                           case EMFFacetConstraints.WHITESPACE:
                              result.put(s, ExtendedMetaData.INSTANCE.getWhiteSpaceFacet(dataType));
                              break;
                           case EMFFacetConstraints.ENUMERATION:
                              result.put(s, ExtendedMetaData.INSTANCE.getEnumerationFacet(dataType));
                              break;
                           case EMFFacetConstraints.PATTERN:
                              result.put(s, ExtendedMetaData.INSTANCE.getPatternFacet(dataType));
                              break;
                           case EMFFacetConstraints.TOTALDIGITS:
                              result.put(s, ExtendedMetaData.INSTANCE.getTotalDigitsFacet(dataType));
                              break;
                           case EMFFacetConstraints.FRACTIONDIGITS:
                              result.put(s, ExtendedMetaData.INSTANCE.getFractionDigitsFacet(dataType));
                              break;
                           case EMFFacetConstraints.LENGTH:
                              result.put(s, ExtendedMetaData.INSTANCE.getLengthFacet(dataType));
                              break;
                           case EMFFacetConstraints.MINLENGTH:
                              result.put(s, ExtendedMetaData.INSTANCE.getMinLengthFacet(dataType));
                              break;
                           case EMFFacetConstraints.MAXLENGTH:
                              result.put(s, ExtendedMetaData.INSTANCE.getMaxLengthFacet(dataType));
                              break;
                           case EMFFacetConstraints.MINEXCLUSIVE:
                              result.put(s, ExtendedMetaData.INSTANCE.getMinExclusiveFacet(dataType));
                              break;
                           case EMFFacetConstraints.MAXEXCLUSIVE:
                              result.put(s, ExtendedMetaData.INSTANCE.getMaxExclusiveFacet(dataType));
                              break;
                           case EMFFacetConstraints.MININCLUSIVE:
                              result.put(s, ExtendedMetaData.INSTANCE.getMinInclusiveFacet(dataType));
                              break;
                           case EMFFacetConstraints.MAXINCLUSIVE:
                              result.put(s, ExtendedMetaData.INSTANCE.getMaxInclusiveFacet(dataType));
                              break;
                           default:
                              break;
                        }
                     }
                     EMFFacetConstraints emfFacetConstraints = new EMFFacetConstraints(result);
                     featureMap.put(esf.getName(), mapper.valueToTree(emfFacetConstraints));
                  }
               }
               // Map Class -> Features
               if (!featureMap.isEmpty()) {
                  jsonResult.put(EcoreUtil.getURI(e).toString(), featureMap);
               }
            }
         }
      }
      return mapper.valueToTree(jsonResult);
   }

}
