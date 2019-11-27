/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common.codecs;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.fasterxml.jackson.databind.JsonNode;

public interface Codec {

   JsonNode encode(EObject eObject) throws EncodingException;

   Optional<EObject> decode(String payload) throws DecodingException;

   Optional<EObject> decode(String payload, URI workspaceURI) throws DecodingException;
}
