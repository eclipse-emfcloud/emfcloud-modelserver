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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.emfcloud.modelserver.emf.di.ModelServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ResourceManagerTest extends AbstractResourceTest {

   private static ResourceManager resourceManager;

   @BeforeClass
   public static void beforeClass() {
      Injector injector = Guice.createInjector(ModelServerModule.create());
      resourceManager = injector.getInstance(ResourceManager.class);
   }

   @Test
   public void testLoadModelCastToExactType() {
      String resourceURI = toFullPath("Test1.ecore");
      Optional<EPackage> result = resourceManager.loadModel(resourceURI, resourceSet, EPackage.class);
      assertNotNull(result);
      assertTrue(result.isPresent());
      assertEquals("test1", result.get().getName());
   }

   @Test
   public void testLoadModelCastToSupertype() {
      String resourceURI = toFullPath("Test1.ecore");
      Optional<EObject> result = resourceManager.loadModel(resourceURI, resourceSet, EObject.class);
      assertNotNull(result);
      assertTrue(result.isPresent());
   }

   @Test
   public void testLoadModelInvalidCast() {
      String resourceURI = toFullPath("Test1.ecore");
      Optional<EClass> result = resourceManager.loadModel(resourceURI, resourceSet, EClass.class);

      assertNotNull(result);
      assertFalse(result.isPresent());
   }

   @Test
   public void testLoadModelFromJson() throws IOException {
      Resource expectedResource = loadResource("Test1.ecore");
      Optional<Resource> result = resourceManager.loadResource(toFullPath("Test1.json"), resourceSet);
      assertTrue(result.isPresent());
      assertTrue(EcoreUtil.equals(expectedResource.getContents(), result.get().getContents()));

   }
}
