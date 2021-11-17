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
package org.eclipse.emfcloud.modelserver.emf.common.watchers;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Watches for changes on model resources to adopt a strategy to update models.
 *
 * @author vhemery
 */
public interface ModelWatcher {

   /**
    * Factory to create a {@link ModelWatcher} from a supported {@link Resource}.
    */
   public interface Factory {

      /**
       * Test whether factory can handle this model resource (test often based on URI).
       *
       * @param resource the resource to create {@link ModelWatcher} for
       * @return true when resource is supported
       */
      boolean handles(Resource resource);

      /**
       * Create or return a {@link ModelWatcher} instance for this model resource.<br/>
       * (implementation may choose to create a new instance each time, or reuse an existing instance when relevant)
       *
       * @param resource the resource to create {@link ModelWatcher} for
       * @return created instance
       */
      ModelWatcher createWatcher(Resource resource);

   }

   /**
    * Watch for modifications on the model resource.
    *
    * @param resource the model resource to watch for
    */
   void watch(Resource resource);
}
