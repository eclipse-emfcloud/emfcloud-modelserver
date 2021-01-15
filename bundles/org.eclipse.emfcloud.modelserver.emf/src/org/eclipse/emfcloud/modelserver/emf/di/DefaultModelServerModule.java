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

import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.DefaultCommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;

import com.google.common.collect.Maps;

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

   @Override
   protected Map<String, Class<? extends Codec>> bindFormatCodecs() {
      Map<String, Class<? extends Codec>> codecs = Maps.newHashMapWithExpectedSize(2);
      codecs.put(ModelServerPathParameters.FORMAT_XMI, XmiCodec.class);
      codecs.put(ModelServerPathParameters.FORMAT_JSON, JsonCodec.class);
      return codecs;
   }

}
