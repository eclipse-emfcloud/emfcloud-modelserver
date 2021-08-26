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
package org.eclipse.emfcloud.modelserver.emf.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.resource.JsonResourceFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;

public interface EPackageConfiguration {
   /**
    * Unique identifier for the EPackage (typically the NS-URI).
    *
    * @return Id as String
    */
   String getId();

   /**
    * Getter for a collection of file extensions that are related to this language.
    *
    * @return Collection of file extensions.
    */
   Collection<String> getFileExtensions();

   /**
    * Initializes and registers the ePackage. Typically this is is achieved by
    * calling eINSTANCE.eClass() of the corresponding EPackage implementation.
    *
    */
   void registerEPackage();

   /**
    * Optional ResourceFactory that is needed to load this EPackage from a resource
    * with a certain extension.
    *
    * @param extension the file extension
    * @return ResourceFactory that is needed for this EPackage or empty optional if
    *         the default XMI Factory can be used
    */
   default Optional<Resource.Factory> getResourceFactory(final String extension) {
      return Optional.empty();
   }

   default void registerFileExtensions() {
      registerFileExtensions(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap());
   }

   default void registerFileExtensions(final Map<String, Object> extensionToFactoryMap) {
      getFileExtensions().forEach(fileExtension -> {
         getResourceFactory(fileExtension).ifPresent(factory -> extensionToFactoryMap.put(fileExtension, factory));
      });
   }

   default void register() {
      register(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap());
   }

   default void register(final Map<String, Object> extensionToFactoryMap) {
      registerEPackage();
      registerFileExtensions(extensionToFactoryMap);
   }

   static void setup(final EPackageConfiguration... configurations) {
      setup(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap(), configurations);
   }

   static void setup(final Map<String, Object> extensionToFactoryMap, final EPackageConfiguration... configurations) {
      // default initialization as XMI resources
      extensionToFactoryMap.put("*", new XMIResourceFactoryImpl());
      // default json mapping
      extensionToFactoryMap.put("json", new JsonResourceFactory(EMFJsonConverter.setupDefaultMapper()));
      // custom configurations: may add or override default configurations (order matters)
      Arrays.stream(configurations).forEach(configuration -> configuration.register(extensionToFactoryMap));
   }
}
