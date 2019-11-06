/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;

import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Injectable singleton class that provides helper methods for managing
 * (load,save...) EMF resources.
 *
 */
public class ResourceManager {
   private static Logger LOG = Logger.getLogger(ResourceManager.class.getSimpleName());
   private final Set<EPackageConfiguration> configurations;

   @Inject
   public ResourceManager(final Set<EPackageConfiguration> configurations) {
      this.configurations = configurations;
      registerExtensions(configurations);
      initialize();
   }

   public void initialize() {
      configurations.forEach(EPackageConfiguration::registerEPackage);
   }

   private void registerExtensions(final Set<EPackageConfiguration> configurations) {
      Map<String, Object> map = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      // register default ResourceFactories (XMI and JSON)
      map.put("*", new XMIResourceFactoryImpl());
      map.put("json", new JsonResourceFactory(EMFJsonConverter.setupDefaultMapper()));
      // register additional ResourceFactories
      configurations.forEach(conf -> map.putAll(registerExtensions(conf)));

   }

   private Map<String, Object> registerExtensions(final EPackageConfiguration configuration) {
      Map<String, Object> map = Maps.newHashMap();
      configuration.getFileExtensions().forEach(ext -> {
         configuration.getResourceFactory(ext).ifPresent(fac -> map.put(ext, fac));
      });

      return map;
   }

   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<Resource> loadResource(final URI resourceURI, final ResourceSet rs) {
      try {
         Resource resource = rs.getResource(resourceURI, true);
         resource.load(Collections.EMPTY_MAP);
         return Optional.of(resource);
      } catch (final Throwable e) {
         LOG.error("Could not load resource with URI: " + resourceURI);
         return Optional.empty();
      }
   }

   public Optional<Resource> loadResource(final String resourceURI, final ResourceSet rs) {
      return loadResource(URI.createURI(resourceURI), rs);
   }

   public <T extends EObject> Optional<T> loadModel(final URI resourceURI, final ResourceSet rs, final Class<T> clazz) {
      Optional<Resource> res = loadResource(resourceURI, rs);
      if (res.isPresent() && !res.get().getContents().isEmpty()) {
         EObject root = res.get().getContents().get(0);
         if (clazz.isInstance(root)) {
            return Optional.of(clazz.cast(root));
         }
         LOG.info(String.format("Root element of resource \"%s\" is not an instance of %s", resourceURI, clazz));
      }
      LOG.info("Resource appears to be empty: " + resourceURI);
      return Optional.empty();
   }

   public <T extends EObject> Optional<T> loadModel(final String resourceURI, final ResourceSet rs,
      final Class<T> clazz) {
      return loadModel(URI.createURI(resourceURI), rs, clazz);
   }

   public boolean save(final ResourceSet rs) {
      return rs.getResources().stream().allMatch(this::save);
   }

   private boolean save(final Resource resource) {
      if (resource.getURI() != null) {

         try {
            resource.save(Collections.EMPTY_MAP);
            return true;
         } catch (IOException e) {
            LOG.error("Could not save resource: " + resource.getURI(), e);
         }
      }
      return false;
   }
}
