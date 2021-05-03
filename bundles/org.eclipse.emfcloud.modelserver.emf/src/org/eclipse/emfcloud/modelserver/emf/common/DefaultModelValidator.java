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
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DefaultModelValidator implements ModelValidator {
   private static final Diagnostician DIAGNOSTICIAN = Diagnostician.INSTANCE;

   protected final ModelRepository modelRepository;
   protected final FacetConfig facetConfig;
   protected final Provider<ObjectMapper> mapperProvider;

   @Inject
   public DefaultModelValidator(final ModelRepository modelRepository, final FacetConfig facetConfig,
      final Provider<ObjectMapper> mapperProvider) {
      this.modelRepository = modelRepository;
      this.facetConfig = facetConfig;
      this.mapperProvider = mapperProvider;
   }

   @Override
   public JsonNode validate(final String modeluri) {
      Optional<EObject> model = this.modelRepository.getModel(modeluri);
      Optional<Resource> res = this.modelRepository.loadResource(modeluri);
      if (model.isEmpty() || res.isEmpty()) {
         return Json.text("Model not found!");
      }
      ObjectMapper mapper = mapperProvider.get();
      mapper.registerModule(new ValidationMapperModule(res.get()));
      mapper.setVisibility(PropertyAccessor.FIELD, Visibility.PROTECTED_AND_PUBLIC);
      BasicDiagnostic diagnostics = DIAGNOSTICIAN.createDefaultDiagnostic(model.get());
      DIAGNOSTICIAN.validate(model.get(), diagnostics, DIAGNOSTICIAN.createDefaultContext());
      return mapper.valueToTree(diagnostics);
   }

   @Override
   public JsonNode getValidationConstraints(final String modeluri) {
      Map<String, Map<String, JsonNode>> jsonResult = new HashMap<>();
      Optional<EObject> eObject = this.modelRepository.getModel(modeluri);
      ObjectMapper mapper = this.mapperProvider.get();
      if (eObject.isEmpty()) {
         return mapper.valueToTree(jsonResult);
      }
      EPackage ePackage = eObject.get().eClass().getEPackage();
      for (EClassifier classifier : ePackage.getEClassifiers()) {
         if (classifier instanceof EClass) {
            // Map Feature -> ExtendedMetaData
            Map<String, JsonNode> featureMap = getFeatures((EClass) classifier, mapper);
            // Map Class -> Features
            if (!featureMap.isEmpty()) {
               jsonResult.put(EcoreUtil.getURI(classifier).toString(), featureMap);
            }
         }
      }
      return mapper.valueToTree(jsonResult);
   }

   protected Map<String, JsonNode> getFeatures(final EClass eClass, final ObjectMapper mapper) {
      Map<String, JsonNode> featureMap = new HashMap<>();
      for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
         if (feature instanceof EAttribute) {
            EDataType dataType = ((EAttribute) feature).getEAttributeType();
            // Map facet -> Value
            Map<String, Object> constraints = getConstraints(dataType);
            EMFFacetConstraints emfFacetConstraints = new EMFFacetConstraints(constraints);
            if (emfFacetConstraints.hasConstraints()) {
               featureMap.put(feature.getName(), mapper.valueToTree(emfFacetConstraints));
            }
         }
      }
      for (EClass parent : eClass.getESuperTypes()) {
         Map<String, JsonNode> parentMap = getFeatures(parent, mapper);
         featureMap.putAll(parentMap);
      }
      return featureMap;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   protected Map<String, Object> getConstraints(final EDataType dataType) {
      Map<String, Object> result = new HashMap<>();
      for (String constraint : facetConfig.getFacetSet()) {
         switch (constraint) {
            case EMFFacetConstraints.WHITESPACE:
               result.put(constraint, ExtendedMetaData.INSTANCE.getWhiteSpaceFacet(dataType));
               break;
            case EMFFacetConstraints.ENUMERATION:
               result.put(constraint, ExtendedMetaData.INSTANCE.getEnumerationFacet(dataType));
               break;
            case EMFFacetConstraints.PATTERN:
               result.put(constraint, ExtendedMetaData.INSTANCE.getPatternFacet(dataType));
               break;
            case EMFFacetConstraints.TOTALDIGITS:
               result.put(constraint, ExtendedMetaData.INSTANCE.getTotalDigitsFacet(dataType));
               break;
            case EMFFacetConstraints.FRACTIONDIGITS:
               result.put(constraint, ExtendedMetaData.INSTANCE.getFractionDigitsFacet(dataType));
               break;
            case EMFFacetConstraints.LENGTH:
               result.put(constraint, ExtendedMetaData.INSTANCE.getLengthFacet(dataType));
               break;
            case EMFFacetConstraints.MINLENGTH:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMinLengthFacet(dataType));
               break;
            case EMFFacetConstraints.MAXLENGTH:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMaxLengthFacet(dataType));
               break;
            case EMFFacetConstraints.MINEXCLUSIVE:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMinExclusiveFacet(dataType));
               break;
            case EMFFacetConstraints.MAXEXCLUSIVE:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMaxExclusiveFacet(dataType));
               break;
            case EMFFacetConstraints.MININCLUSIVE:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMinInclusiveFacet(dataType));
               break;
            case EMFFacetConstraints.MAXINCLUSIVE:
               result.put(constraint, ExtendedMetaData.INSTANCE.getMaxInclusiveFacet(dataType));
               break;
            default:
               break;
         }
      }
      return result;
   }

}
