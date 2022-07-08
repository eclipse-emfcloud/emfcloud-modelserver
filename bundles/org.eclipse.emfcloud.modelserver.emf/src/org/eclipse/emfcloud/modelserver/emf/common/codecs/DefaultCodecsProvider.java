/********************************************************************************
 * Copyright (c) 2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.util.Optional;
import java.util.Set;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

import com.google.common.collect.Sets;

public class DefaultCodecsProvider implements CodecProvider {

   private final Set<String> supportedFormats = Sets.newHashSet(ModelServerPathParametersV2.FORMAT_XMI,
      ModelServerPathParametersV2.FORMAT_JSON, ModelServerPathParametersV2.FORMAT_JSON_V2);

   @Override
   public Optional<Codec> getCodec(final String modelUri, final String format) {
      if (ModelServerPathParametersV2.FORMAT_XMI.equals(format)) {
         return Optional.of(new XmiCodec());
      }
      if (ModelServerPathParametersV2.FORMAT_JSON.equals(format)) {
         return Optional.of(new JsonCodec());
      }
      if (ModelServerPathParametersV2.FORMAT_JSON_V2.equals(format)) {
         return Optional.of(new JsonCodecV2());
      }
      return Optional.empty();
   }

   @Override
   public Set<String> getAllFormats() { return supportedFormats; }

   @Override
   public int getPriority(final String modelUri, final String format) {
      return getAllFormats().contains(format) ? 1 : NOT_SUPPORTED;
   }

}
