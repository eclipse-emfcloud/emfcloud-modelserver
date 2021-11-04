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
 * Manages the {@link ModelWatcher} instances for loaded resources.
 *
 * @author vhemery
 */
public interface ModelWatchersManager {

   /**
    * Watch for modifications on the model resource
    *
    * @param resource the model resource to watch for
    */
   void watch(Resource resource);
}
