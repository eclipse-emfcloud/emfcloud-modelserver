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
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.DefaultCommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;

public class DefaultModelServerModule extends ModelServerModule {

   @Override
   protected AdapterFactory bindAdapterFactory() {
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

}
