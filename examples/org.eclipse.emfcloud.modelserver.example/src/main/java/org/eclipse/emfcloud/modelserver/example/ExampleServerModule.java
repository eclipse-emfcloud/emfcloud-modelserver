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
package org.eclipse.emfcloud.modelserver.example;

import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.edit.CommandContribution;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.DefaultModelServerModule;

public class ExampleServerModule extends DefaultModelServerModule {

   @Override
   protected void configureEPackages(final MultiBinding<EPackageConfiguration> binding) {
      super.configureEPackages(binding);
      binding.add(CoffeePackageConfiguration.class);
   }

   @Override
   protected void configureRoutings(final MultiBinding<Routing> binding) {
      super.configureRoutings(binding);
      binding.add(CoffeeRouting.class);
   }

   @Override
   protected void configureCommandCodecs(final MapBinding<String, CommandContribution> binding) {
      super.configureCommandCodecs(binding);
      binding.put(UpdateTaskNameCommandContribution.TYPE, UpdateTaskNameCommandContribution.class);
   }
}
