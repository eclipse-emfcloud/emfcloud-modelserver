/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest.getAPIVersion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.eclipse.emfcloud.modelserver.common.utils.APIVersionMap;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

/**
 * A URI converter that resolves relative URIs against the Model Server workspace.
 */
public class DefaultModelURIConverter extends ExtensibleURIConverterImpl implements ModelURIConverter {

   public static final String MODEL_URI_VALIDATOR = "modelURIValidator";

   public static final String MODEL_URI_RESOLVERS = "modelURIResolvers";

   public static final String MODEL_URI_DERESOLVERS = "modelURIDeresolvers";

   protected static final Pattern SCHEME_PATTERN = Pattern.compile("^([a-zA-Z_0-9@-]+):");

   protected final APIVersionMap<Function<? super URI, Optional<URI>>> modelURIResolvers = new APIVersionMap<>();

   protected final APIVersionMap<Function<? super URI, URI>> modelURIDeresolvers = new APIVersionMap<>();

   protected final ServerConfiguration serverConfiguration;

   @Inject
   public DefaultModelURIConverter(final ServerConfiguration serverConfiguration) {
      super();

      this.serverConfiguration = serverConfiguration;
   }

   @Override
   public URI normalize(URI uri) {
      if (uri.isRelative()) {
         // Resolve relative URIs against the workspace root, not the current working directory
         uri = uri.resolve(getWorkspaceRootDirectoryURI(serverConfiguration));
      }

      return sanitizeFileURI(super.normalize(uri));
   }

   /**
    * Ensure that an URI, if it has file scheme, does not have an empty authority.
    * This normalizes URIs of the form <tt>file:///path/to/resource.json</tt> into the form
    * <tt>file:/path/to/resource.json</tt> so that both will identify the same resource.
    *
    * @param uri a URI that may have file scheme or may not
    * @return the sanitized URI
    */
   static URI sanitizeFileURI(URI uri) {
      if (!uri.isRelative() && uri.isFile() && uri.hasAuthority()) {
         // File URI should only have an authority if they are Windows UNC paths.
         // An empty authority should be deleted so that the URI looks like file:/path/to/resource.json
         if (uri.authority().isBlank()) {
            uri = URI.createHierarchicalURI(uri.scheme(), null /* authority */, uri.device(), uri.segments(),
               uri.query(), uri.fragment());
         }
      }
      return uri;
   }

   @Override
   public Optional<URI> resolveModelURI(final Context ctx, final String key) {
      APIVersion apiVersion = getAPIVersion(ctx);
      return getModelURI(ctx.queryParamMap(), key)
         .flatMap(uri -> makeAbsolute(apiVersion, uri));
   }

   @Override
   public Optional<URI> resolveModelURI(final WsContext ctx, final String key) {
      APIVersion apiVersion = getAPIVersion(ctx);
      return getModelURI(ctx.queryParamMap(), key)
         .flatMap(uri -> makeAbsolute(apiVersion, uri));
   }

   @Override
   public URI deresolveModelURI(final Context ctx, final URI modelURI) {
      return makeRelative(getAPIVersion(ctx), modelURI);
   }

   @Override
   public URI deresolveModelURI(final WsContext ctx, final URI modelURI) {
      return makeRelative(getAPIVersion(ctx), modelURI);
   }

   protected Optional<URI> getModelURI(final Map<String, List<String>> queryParameters, final String key) {
      return Optional.of(queryParameters.getOrDefault(key, List.of()))
         .filter(Predicate.not(List::isEmpty)).map(list -> list.get(0)).map(DefaultModelURIConverter::parseURI);
   }

   /**
    * Parse an incoming URI string as an EMF URI without trying to resolve it.
    * This handles the case of a Windows path string with a leading device.
    *
    * @param uri the incoming URI string
    * @return the parsed URI
    */
   public static URI parseURI(final String uri) {
      Matcher scheme = SCHEME_PATTERN.matcher(uri);
      if (scheme.find() && scheme.group(1).length() == 1) {
         // Looks like a Windows file path. Convert separators and remove any leading separators
         // because the hierarchical URI will already include a slash after the device
         List<String> filePath = Arrays.asList(uri.substring(scheme.end()).replace('\\', '/').split("/+"));

         // Remove empty segments, especially leading segments because a device URI already includes the leading /
         filePath = filePath.stream().filter(not(String::isEmpty)).collect(Collectors.toList());

         // File URIs for the model URI must not include a query or a fragment, and certainly not an authority
         return URI.createHierarchicalURI("file", null, scheme.group(0), filePath.toArray(String[]::new), null, null);
      }

      // It's either a relative or an absolute URI, but either way, it's not an absolute Windows file path.
      // If there were legitimate backslashes that should have been within path segments, they should have
      // been escaped, so this heuristic handles naïve Windows file URIs
      return sanitizeFileURI(URI.createURI(uri.replace('\\', '/')));
   }

   /**
    * Make an absolute URI from the given model URI query parameter.
    *
    * @param apiVersion the API version of the request or message context
    * @param modelURI   a model URI from the client request query parameter
    *
    * @return the absolute URI, if validly computable from the query parameter
    */
   protected Optional<URI> makeAbsolute(final APIVersion apiVersion, final URI modelURI) {
      return modelURIResolvers.getOrDefault(apiVersion, uri -> Optional.ofNullable(uri).filter(not(URI::isRelative)))
         .apply(modelURI);
   }

   /**
    * Make a relative URI from the given absolute model URI.
    *
    * @param apiVersion the API version of the request or message context
    * @param modelURI   an absolute model URI resolved through this strategy
    *
    * @return the relative URI for return to the client
    */
   protected URI makeRelative(final APIVersion apiVersion, final URI modelURI) {
      return modelURIDeresolvers.getOrDefault(apiVersion, identity()).apply(modelURI);
   }

   @Inject(optional = true)
   protected void setModelURIResolvers(
      @Named(MODEL_URI_RESOLVERS) final Map<APIVersionRange, Function<? super URI, Optional<URI>>> modelURIResolvers) {

      this.modelURIResolvers.putAll(modelURIResolvers);
   }

   @Inject(optional = true)
   protected void setModelURIDeresolvers(
      @Named(MODEL_URI_DERESOLVERS) final Map<APIVersionRange, Function<? super URI, URI>> modelURIDeresolvers) {

      this.modelURIDeresolvers.putAll(modelURIDeresolvers);
   }

   /**
    * Get the workspace root URI as a directory URI, with a trailing slash.
    *
    * @return the workspace root directory URI
    */
   static URI getWorkspaceRootDirectoryURI(final ServerConfiguration serverConfig) {
      URI result = serverConfig.getWorkspaceRootURI();
      if (result != null && !result.hasTrailingPathSeparator()) {
         result = result.appendSegment("");
      }
      return result;
   }

   //
   // Nested types
   //

   /** Model URI parameter resolver for API V1. */
   public static class APIV1Resolver implements Function<URI, Optional<URI>> {
      protected final ModelResourceManager modelResourceManager;

      @Inject
      public APIV1Resolver(final ModelResourceManager modelResourceManager) {
         super();

         this.modelResourceManager = modelResourceManager;
      }

      @Override
      public Optional<URI> apply(final URI modelURI) {
         // Compatibility
         @SuppressWarnings("deprecation")
         String adaptedURI = modelResourceManager.adaptModelUri(modelURI.toString());
         return Optional.ofNullable(adaptedURI).map(URI::createURI).map(DefaultModelURIConverter::sanitizeFileURI);
      }
   }

   /** Model URI result deresolver for API V1. */
   public static class APIV1Deresolver implements Function<URI, URI> {
      @Override
      public URI apply(final URI modelURI) {
         // Compatibility
         return modelURI;
      }
   }

   /** Model URI parameter resolver for API V2. */
   public static class APIV2Resolver implements Function<URI, Optional<URI>> {
      protected final ServerConfiguration serverConfiguration;

      @Named(MODEL_URI_VALIDATOR)
      @Inject(optional = true)
      protected Predicate<? super URI> modelURIValidator = URI::isRelative;

      @Inject
      public APIV2Resolver(final ServerConfiguration serverConfiguration) {
         super();

         this.serverConfiguration = serverConfiguration;
      }

      @Override
      public Optional<URI> apply(final URI modelURI) {
         return Optional.ofNullable(modelURI).filter(modelURIValidator)
            .map(uri -> uri.resolve(getWorkspaceRootDirectoryURI(serverConfiguration)));
      }
   }

   /** Model URI result deresolver for API V2. */
   public static class APIV2Deresolver implements Function<URI, URI> {
      protected final ServerConfiguration serverConfiguration;

      @Inject
      public APIV2Deresolver(final ServerConfiguration serverConfiguration) {
         super();

         this.serverConfiguration = serverConfiguration;
      }

      @Override
      public URI apply(final URI modelURI) {
         if (modelURI.isRelative()) {
            return modelURI;
         }

         return modelURI.deresolve(getWorkspaceRootDirectoryURI(serverConfiguration));
      }
   }

}
