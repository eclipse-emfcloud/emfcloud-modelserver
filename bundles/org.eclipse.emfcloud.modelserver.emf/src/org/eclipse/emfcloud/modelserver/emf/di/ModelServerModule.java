/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.common.di.AbstractModuleWithInitializers;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.launch.ModelServerStartup;

import com.google.inject.Singleton;

public abstract class ModelServerModule extends AbstractModuleWithInitializers {

   protected static final Logger LOG = LogManager.getLogger(ModelServerModule.class);

   @Override
   protected void configure() {
      super.configure();

      // minimal setup
      bind(ModelServerStartup.class).to(bindModelServerStartup()).in(Singleton.class);
      bind(ServerConfiguration.class).to(bindServerConfiguration()).in(Singleton.class);
   }

   protected <K, V> void configure(final MapBinding<K, V> binding, final Consumer<MapBinding<K, V>> configurator) {
      configurator.accept(binding);
      binding.applyBinding(binder());
   }

   protected <T> void configure(final MultiBinding<T> binding, final Consumer<MultiBinding<T>> configurator) {
      configurator.accept(binding);
      binding.applyBinding(binder());
   }

   protected abstract Class<? extends ModelServerStartup> bindModelServerStartup();

   protected abstract Class<? extends ServerConfiguration> bindServerConfiguration();
}
