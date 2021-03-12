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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;

public class DefaultUriHelper implements UriHelper {
   protected static Logger LOG = Logger.getLogger(DefaultUriHelper.class.getSimpleName());

   @Override
   public String toUriString(final java.net.URI uri) {
      return toUriString(URI.createURI(uri.normalize().toString()));
   }

   @Override
   public String toUriString(final URI uri) {
      return withEmptyAuthority(uri).toString();
   }

   @Override
   public java.net.URI toJavaUri(final URI uri) {
      return toJavaUri(toUriString(uri));
   }

   @Override
   public java.net.URI toJavaUri(final String uri) {
      try {
         return new java.net.URI(uri);
      } catch (URISyntaxException exception) {
         throw new RuntimeException(exception);
      }
   }

   @Override
   public URI toEmfUri(final String stringUri) {
      return toEmfUri(toJavaUri(stringUri));
   }

   @Override
   public URI toEmfUri(final java.net.URI netUri) {
      String decoded = toDecodedString(netUri);
      URI uri = URI.createURI(decoded, false);
      URI result = withEmptyAuthority(uri);
      return result;
   }

   @Override
   public URI withTrailingSeparator(final URI uri) {
      return uri.hasTrailingPathSeparator() ? uri : uri.appendSegment("");
   }

   @Override
   public Optional<URI> toFileUri(final String fileUrl) {
      try {
         File file = getAbsoluteFile(fileUrl);
         URI uri = withTrailingSeparator(URI.createFileURI(file.toURI().normalize().getPath()));
         return Optional.ofNullable(uri).filter(URI::isFile);
      } catch (NullPointerException | IllegalArgumentException | UnsupportedEncodingException e) {
         LOG.warn(String.format("Could not convert to filePath! ’%s’ is not a valid URL", fileUrl));
         return Optional.empty();
      }
   }

   protected File getAbsoluteFile(final String fileUrl) throws UnsupportedEncodingException {
      String decodedUrl = URLDecoder.decode(withoutFileScheme(fileUrl), "UTF-8");
      File file = new File(decodedUrl);
      if (!file.isAbsolute()) {
         file = new File(System.getProperty("user.dir"), decodedUrl);
      }
      return file;
   }

   @Override
   public Optional<URI> toDirectoryUri(final String uri) {
      return toFileUri(uri).map(this::withTrailingSeparator);
   }

   @Override
   public boolean exists(final String uri) {
      return toFileUri(uri).map(fileUri -> new File(fileUri.toFileString()).exists()).orElse(false);
   }

   public static String toDecodedString(final java.net.URI uri) {
      String scheme = uri.getScheme();
      String part = uri.getSchemeSpecificPart();
      return scheme == null ? part : scheme + ":" + part;
   }

   public static URI withEmptyAuthority(final URI uri) {
      return uri.isFile() && uri.authority() == null
         ? URI.createHierarchicalURI(uri.scheme(), "", uri.device(), uri.segments(), uri.query(), uri.fragment())
         : uri;
   }

   public static URI withoutFileScheme(final URI uri) {
      return uri.scheme() == "file"
         ? URI.createHierarchicalURI(null, "", uri.device(), uri.segments(), uri.query(), uri.fragment())
         : uri;
   }

   public static String withoutFileScheme(final String uriString) {
      URI uri = uriString.startsWith("file:") ? URI.createURI(uriString) : URI.createFileURI(uriString);
      return withoutFileScheme(uri).toString();
   }

}
