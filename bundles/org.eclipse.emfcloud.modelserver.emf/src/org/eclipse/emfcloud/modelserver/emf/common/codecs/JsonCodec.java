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
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.di.ProviderDefaults;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonCodec extends DefaultJsonCodec {

   public static JsonNode encode(final Object obj) throws EncodingException {
      try {
         return ProviderDefaults.provideObjectMapper().valueToTree(obj);
      } catch (IllegalArgumentException ex) {
         throw new EncodingException(ex);
      }
   }

   @Override
   protected ObjectMapper getObjectMapper() { return ProviderDefaults.provideObjectMapper(); }

}
