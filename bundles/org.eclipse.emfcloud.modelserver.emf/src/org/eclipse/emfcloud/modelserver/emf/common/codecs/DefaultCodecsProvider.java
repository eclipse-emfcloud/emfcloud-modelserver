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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

public class DefaultCodecsProvider implements CodecProvider {

   private final Map<String, Supplier<Codec>> supportedFormats = new LinkedHashMap<>();

   public DefaultCodecsProvider() {
      this.supportedFormats.put(ModelServerPathParametersV2.FORMAT_XMI, XmiCodec::new);
      this.supportedFormats.put(ModelServerPathParametersV2.FORMAT_JSON, JsonCodec::new);
      this.supportedFormats.put(ModelServerPathParametersV2.FORMAT_JSON_V2, JsonCodecV2::new);

   }

   @Override
   public Optional<Codec> getCodec(final String modelUri, final String format) {
      Supplier<Codec> codecSupplier = supportedFormats.get(format);
      if (codecSupplier == null) {
         return Optional.empty();
      }
      return Optional.of(codecSupplier.get());
   }

   @Override
   public Set<String> getAllFormats() { return supportedFormats.keySet(); }

   @Override
   public int getPriority(final String modelUri, final String format) {
      return getAllFormats().contains(format) ? 1 : NOT_SUPPORTED;
   }

}
