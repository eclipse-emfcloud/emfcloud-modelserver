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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatcher.Factory;

import com.google.inject.Inject;

/**
 * Manages the {@link ModelWatcher} instances for loaded resources, relying on injected factories.
 *
 * @author vhemery
 */
public class DIModelWatchersManager implements ModelWatchersManager {

   /** Logger. */
   protected static final Logger LOG = Logger.getLogger(DIModelWatchersManager.class.getSimpleName());

   /** The registered {@link ModelWatcher} factories. */
   @Inject(optional = true)
   private final Set<ModelWatcher.Factory> watcherFactories = Collections.emptySet();

   @Override
   public void watch(final Resource resource) {
      Optional<Factory> supportingFactory = watcherFactories.stream().filter(f -> f.handles(resource)).findFirst();
      supportingFactory.ifPresentOrElse(f -> {
         ModelWatcher watcher = f.createWatcher(resource);
         watcher.watch(resource);
      }, () -> {
         // just trace it
         String modelUri = resource.getURI().toString();
         LOG.trace(MessageFormat.format("No model watcher could be constructed for resource {0}", modelUri));
      });
   }

}
