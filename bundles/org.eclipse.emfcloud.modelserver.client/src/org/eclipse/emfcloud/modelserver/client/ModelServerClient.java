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
package org.eclipse.emfcloud.modelserver.client;

import java.net.MalformedURLException;
import java.util.Map;

import org.eclipse.emfcloud.modelserver.client.v2.ModelServerClientV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;

import okhttp3.OkHttpClient;

public class ModelServerClient extends ModelServerClientV2 {

   public static final Map<String, Codec> DEFAULT_SUPPORTED_FORMATS = ModelServerClientV2.SUPPORTED_FORMATS;
   public static final String PATCH = ModelServerClientV2.PATCH;
   public static final String POST = ModelServerClientV2.POST;

   public ModelServerClient(final String baseUrl, final EPackageConfiguration... configurations)
      throws MalformedURLException {
      super(baseUrl, configurations);
   }

   public ModelServerClient(final String baseUrl, final Map<String, Codec> supportedFormats,
      final EPackageConfiguration... configurations)
      throws MalformedURLException {

      super(baseUrl, supportedFormats, configurations);
   }

   public ModelServerClient(final OkHttpClient client, final String baseUrl,
      final EPackageConfiguration... configurations) throws MalformedURLException {
      super(client, baseUrl, configurations);
   }

}
