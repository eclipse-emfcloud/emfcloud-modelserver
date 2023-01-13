/********************************************************************************
 * Copyright (c) 2019-2023 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.resource.JsonResourceFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractResourceTest {
   public static final String TEST_RESOURCES_PATH = "resources/";
   public static final String RESOURCE_PATH = ".temp/";
   @SuppressWarnings({ "checkstyle:VisibilityModifier" })
   protected ResourceSetImpl resourceSet; // needed in ResourceManagerTest.java

   @BeforeClass
   public static void setupTestResources() throws IOException {
      // copy test resources to a temporary resource location to avoid
      // git changes if executing tests due to saving resources
      File sourceDirectory = new File(TEST_RESOURCES_PATH);
      File destinationDirectory = new File(RESOURCE_PATH);
      FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
   }

   @Before
   public void initializeResourceSet() {
      resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json",
         new JsonResourceFactory(EMFJsonConverter.setupDefaultMapper()));
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
   }

   protected Resource loadResource(final String file) throws IOException {
      Resource resource = resourceSet.createResource(URI.createFileURI(toFullPath(file)));
      resource.load(Collections.EMPTY_MAP);
      return resource;
   }

   protected String toFullPath(final String file) {
      return RESOURCE_PATH + file;
   }

   @After
   public void tearDownResourceSet() {
      if (resourceSet != null) {
         resourceSet.getResources().stream().forEach(Resource::unload);
      }
   }

}
