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

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.resource.JsonResourceFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractResourceTest {
   public static final String RESOURCE_PATH = "resources/";
   @SuppressWarnings({ "checkstyle:VisibilityModifier" })
   protected ResourceSetImpl resourceSet; // needed in ResourceManagerTest.java

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
