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
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.module.EMFModule;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.tests.constrainttest.ConstraintTestPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit tests for the {@link ModelController} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultModelValidatorTest {

   @Mock
   private ModelRepository modelRepository;

   private DefaultModelValidator modelValidator;

   private JsonNode jsonNode;

   @Before
   public void before() throws NoSuchFieldException, SecurityException {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
      resourceSet.getPackageRegistry().put(ConstraintTestPackage.eNS_URI, ConstraintTestPackage.eINSTANCE);

      Optional<Resource> concreteClass1 = Optional
         .of(resourceSet.getResource(URI.createFileURI("resources/ConstraintTest_SubClass.xmi"), true));
      when(modelRepository.getModel("ConstraintTest_SubClass.xmi"))
         .thenReturn(Optional.of(concreteClass1.get().getContents().get(0)));

      modelValidator = new DefaultModelValidator(modelRepository, new DefaultFacetConfig(),
         EMFModule::setupDefaultMapper);

      jsonNode = modelValidator.getValidationConstraints("ConstraintTest_SubClass.xmi");
   }

   @Test
   public void getValidationConstraints_NoConstraints() throws DecodingException, IOException {
      assertTrue(jsonNode.get(ConstraintTestPackage.eNS_URI + "#//NoConstraintsClass") == null);
   }

   @Test
   public void getValidationConstraints_SuperClassConstraint() throws DecodingException, IOException {
      JsonNode parentClass = jsonNode.get(ConstraintTestPackage.eNS_URI + "#//SuperClassWithConstraint");
      int parentClassMinLength = parentClass.get("name").get(EMFFacetConstraints.MINLENGTH).asInt();
      assertTrue(parentClassMinLength == 5);
   }

   @Test
   public void getValidationConstraints_HideConstraints() throws DecodingException, IOException {
      JsonNode parentClass = jsonNode.get(ConstraintTestPackage.eNS_URI + "#//SuperClassWithConstraint");
      JsonNode name = parentClass.get("name");
      assertTrue(name.get(EMFFacetConstraints.MAXLENGTH) == null);
   }

   @Test
   public void getValidationConstraints_ParentConstraint() throws DecodingException, IOException {
      JsonNode subClass = jsonNode.get(ConstraintTestPackage.eNS_URI + "#//SubClass");
      int subClassMinLength = subClass.get("name").get(EMFFacetConstraints.MINLENGTH).asInt();
      JsonNode subSubClass = jsonNode.get(ConstraintTestPackage.eNS_URI + "#//SubSubClass");
      int subSubClassMinLength = subSubClass.get("name").get(EMFFacetConstraints.MINLENGTH).asInt();
      assertTrue(subClassMinLength == 5);
      assertTrue(subSubClassMinLength == 5);
   }

   @Test
   public void getValidationConstraints_SubClassWithConstraint() throws DecodingException, IOException {
      JsonNode subClass = jsonNode.get(ConstraintTestPackage.eNS_URI + "#//SubClassWithConstraint");
      int minLength = subClass.get("name").get(EMFFacetConstraints.MINLENGTH).asInt();
      int maxLength = subClass.get("id").get(EMFFacetConstraints.MAXLENGTH).asInt();
      assertTrue(minLength == 5);
      assertTrue(maxLength == 5);
   }
}
