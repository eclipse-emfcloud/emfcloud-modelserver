/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.DefaultCommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultFacetConfig;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelValidator;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelValidator;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.FacetConfig;

public class DefaultModelServerModule extends ModelServerModule {

   @Override
   protected AdapterFactory provideAdapterFactory() {
      return new ComposedAdapterFactory();
   }

   @Override
   protected Class<? extends CommandCodec> bindCommandCodec() {
      return DefaultCommandCodec.class;
   }

   @Override
   protected Class<? extends ModelResourceManager> bindModelResourceManager() {
      return DefaultModelResourceManager.class;
   }

   @Override
   protected Class<? extends ModelValidator> bindModelValidator() {
      return DefaultModelValidator.class;
   }

   @Override
   protected Class<? extends FacetConfig> bindFacetConfig() {
      return DefaultFacetConfig.class;
   }

   @Override
   protected void configureEPackages(final MultiBinding<EPackageConfiguration> binding) {
      binding.addAll(MultiBindingDefaults.DEFAULT_EPACKAGE_CONFIGURATIONS);
   }

   @Override
   protected void configureRoutings(final MultiBinding<Routing> binding) {
      binding.addAll(MultiBindingDefaults.DEFAULT_ROUTINGS);
   }

   @Override
   protected void configureAppEntryPoints(final MapBinding<EntryPointType, AppEntryPoint> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_APP_ENTRY_POINTS);
   }

   @Override
   protected void configureCodecs(final MapBinding<String, Codec> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_CODECS);
   }
}
