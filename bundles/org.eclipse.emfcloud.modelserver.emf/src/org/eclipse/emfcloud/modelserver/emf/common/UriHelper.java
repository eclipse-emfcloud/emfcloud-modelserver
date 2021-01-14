/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;

public interface UriHelper {
   URI toEmfUri(String stringUri);

   URI toEmfUri(java.net.URI uri);

   String toUriString(java.net.URI uri);

   String toUriString(URI uri);

   java.net.URI toJavaUri(URI uri);

   java.net.URI toJavaUri(String uri);

   URI withTrailingSeparator(URI uri);

   Optional<URI> toFileUri(String uri);

   Optional<URI> toDirectoryUri(String uri);

   boolean exists(String uri);
}
