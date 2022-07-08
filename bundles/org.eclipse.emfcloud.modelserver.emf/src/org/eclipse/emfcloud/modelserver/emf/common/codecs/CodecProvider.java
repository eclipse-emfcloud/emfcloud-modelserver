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

import org.eclipse.emfcloud.modelserver.common.codecs.Codec;

public interface CodecProvider {

   int NOT_SUPPORTED = -1;

   Set<String> getAllFormats();

   int getPriority(String modelUri, String format);

   Optional<Codec> getCodec(String modelUri, String format);
}
