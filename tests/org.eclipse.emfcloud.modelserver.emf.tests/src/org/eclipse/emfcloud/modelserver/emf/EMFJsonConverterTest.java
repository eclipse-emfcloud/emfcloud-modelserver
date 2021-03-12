/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf;

import static org.eclipse.emfcloud.modelserver.tests.util.OSUtil.osLineSeparator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EMFJsonConverterTest extends AbstractResourceTest {

   private EMFJsonConverter emfJsonConverter;

   private String simpleTestJson;
   private EClass simpleTestEClass;
   private String coffeeJson;
   private Resource coffeeResource;

   @BeforeClass
   public static void beforeClass() {
      BasicConfigurator.configure();
   }

   @Before
   public void before() throws IOException {
      this.emfJsonConverter = new EMFJsonConverter();
      simpleTestJson = osLineSeparator("{\n"
         + "  \"eClass\" : \"http://www.eclipse.org/emf/2002/Ecore#//EClass\",\n"
         + "  \"name\" : \"SimpleTest\"\n"
         + "}");

      simpleTestEClass = EcoreFactory.eINSTANCE.createEClass();
      simpleTestEClass.setName("SimpleTest");
      coffeeJson = FileUtils.readFileToString(new File(RESOURCE_PATH + "/" + "Coffee.json"), "UTF8");

      coffeeResource = loadResource("Coffee.ecore");
   }

   @Test
   public void testToJsonSimple() {
      String expected = simpleTestJson;
      Optional<String> result = emfJsonConverter.toJson(simpleTestEClass);
      assertTrue(result.isPresent());
      assertEquals(result.get(), expected);
   }

   @Test
   public void testFromJsonSimple() {
      EObject expected = simpleTestEClass;
      Optional<EObject> result = emfJsonConverter.fromJson(simpleTestJson);
      assertTrue(result.isPresent());
      assertTrue(EcoreUtil.equals(expected, result.get()));
   }

   @Test
   public void testFromJsonExplicitCast() {
      Optional<EPackage> result = emfJsonConverter.fromJson(coffeeJson, EPackage.class);
      assertTrue(result.isPresent());
      assertTrue(EPackage.class.isInstance(result.get()));

   }

   @Test
   public void testFromJsonFailedCast() {
      Optional<EEnumLiteral> result = emfJsonConverter.fromJson(coffeeJson, EEnumLiteral.class);
      assertFalse(result.isPresent());

   }

   @Test
   public void testToJsonCoffeeJson() {
      String expected = coffeeJson;
      Optional<String> result = emfJsonConverter.toJson(coffeeResource.getContents().get(0));
      assertTrue(result.isPresent());
      assertEquals(result.get(), expected);

   }

   @Test
   public void testFromJsonCoffeeJson() {
      EObject expected = coffeeResource.getContents().get(0);
      Optional<EObject> result = emfJsonConverter.fromJson(coffeeJson);
      assertTrue(result.isPresent());
      assertTrue(quickIsEqual(expected, result.get()));

   }

   private boolean quickIsEqual(final EObject pack1, final EObject pack2) {
      if (!pack1.getClass().equals(pack2.getClass())) {
         return false;
      }

      if (pack1 instanceof ENamedElement) {
         if (!((ENamedElement) pack1).getName().equals(((ENamedElement) pack2).getName())) {
            return false;
         }

      }

      if (pack1.eContents().size() != pack2.eContents().size()) {
         return false;
      }

      return true;
   }
}
