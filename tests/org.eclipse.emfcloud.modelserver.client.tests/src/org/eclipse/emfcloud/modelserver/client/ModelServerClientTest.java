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

import org.eclipse.emfcloud.modelserver.client.v2.ModelServerClientV2;
import org.eclipse.emfcloud.modelserver.client.v2.ModelServerClientV2Test;

import okhttp3.OkHttpClient;

public class ModelServerClientTest extends ModelServerClientV2Test {

   @Override
   protected ModelServerClientV2 createClient(final OkHttpClient httpClient, final String baseURL)
      throws MalformedURLException {
      return new ModelServerClient(httpClient, baseURL);
   }

}
