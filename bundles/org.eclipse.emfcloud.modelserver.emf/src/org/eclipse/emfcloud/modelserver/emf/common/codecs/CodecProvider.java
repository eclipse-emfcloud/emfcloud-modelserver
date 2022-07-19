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

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emfcloud.modelserver.common.codecs.Codec;

public interface CodecProvider {

   int NOT_SUPPORTED = -1;

   /**
    * This returns all known formats that this codec provider supports.
    *
    * @return all known formats as a set
    */
   Set<String> getAllFormats();

   /**
    * The priority of this codec provider for the provided modelUri and format.
    *
    * @param modelUri The modelUri to get the priority for
    * @param format   The format to get the priority for
    * @return a number indicating the priority, the higher the prio the more likely it is to get used,
    *         return NOT_SUPPORTED if not supported
    */
   int getPriority(String modelUri, String format);

   /**
    * The codec this provider can return for the provided modelUri and format.
    *
    * @param modelUri The modelUri to get the codec for
    * @param format   The format to get the codec for
    * @return the codec this provider offers for the modelUri, format combination, an empty optional if not supported
    */
   Optional<Codec> getCodec(String modelUri, String format);

   static Optional<? extends CodecProvider> getCodecProvider(
      final Collection<? extends CodecProvider> codecProviders,
      final String modelUri, final String format) {
      return codecProviders.stream()
         .max((cp1, cp2) -> cp1.getPriority(modelUri, format) - cp2.getPriority(modelUri, format));
   }

   static Optional<Codec> getCodec(final Collection<? extends CodecProvider> codecProviders,
      final String modelUri, final String format) {
      Optional<? extends CodecProvider> bestCodecProvider = getCodecProvider(codecProviders, modelUri, format);
      if (bestCodecProvider.isEmpty()) {
         return Optional.empty();
      }
      return bestCodecProvider.get().getCodec(modelUri, format);
   }
}
