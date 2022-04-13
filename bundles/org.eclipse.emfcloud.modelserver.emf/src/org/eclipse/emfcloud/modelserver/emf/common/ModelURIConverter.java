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

import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.MODEL_URI;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.missingParameter;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.modelNotFound;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

/**
 * A service that resolves the or URI indicated in incoming requests and messages.
 * Request handlers should use this service also to deresolve model URIs if outgoing responses
 * and messages, where such may make reference to model URIs that can be turned around by clients
 * as parameters for subsequent resquests and responses. The most basic case of this being the
 * handler for the {@link ModelServerPathsV2#MODEL_URIS /api/v2/modeluris} GET request.
 */
public interface ModelURIConverter extends URIConverter {

   /**
    * Resolve the model URI to which a request pertains to an absolute URI suitable for access to an EMF
    * {@link ResourceSet}.
    * The result is optional to allow for implementations to refuse to resolve <tt>modeluri</tt> parameters matching
    * whatever
    * validation criteria they may impose, e.g. disallowing simple file paths or absolute URIs.
    *
    * @param ctx the incoming request context
    * @param key the request parameter key to look up to get the model URI
    * @return the resolved absolute URI, if the request parameter is resolvable
    */
   Optional<URI> resolveModelURI(Context ctx, String key);

   /**
    * Resolve the model URI to which a message pertains to an absolute URI suitable for access to an EMF
    * {@link ResourceSet}.
    * The result is optional to allow for implementations to refuse to resolve <tt>modeluri</tt> parameters matching
    * whatever
    * validation criteria they may impose, e.g. disallowing simple file paths or absolute URIs.
    *
    * @param ctx the incoming message context
    * @param key the message context parameter key to look up to get the model URI
    * @return the resolved absolute URI, if the message context parameter is resolvable
    */
   Optional<URI> resolveModelURI(WsContext ctx, String key);

   /**
    * Apply a {@code processor} function to the resolved model URI in the request.
    * If the model URI does not resolve, responds automatically with a 404.
    *
    * @param <T>       the processor result
    * @param ctx       the request context
    * @param key       the request parameter key to look up to get the model URI
    * @param processor a function to process the resolved URI
    *
    * @return the result of the {@code processor}, or empty if 404
    */
   default <T> Optional<T> applyResolvedModelURI(final Context ctx, final String key,
      final Function<? super String, T> processor) {

      return this.resolveModelURI(ctx, key).map(URI::toString).map(processor).or(() -> {
         respondMissingOrUnspecifiedModel(ctx, key);
         return Optional.empty();
      });
   }

   default void respondMissingOrUnspecifiedModel(final Context ctx, final String key) {
      Optional.ofNullable(ctx.queryParam(key)).ifPresentOrElse(
         modeluri -> modelNotFound(ctx, deresolveModelURI(ctx, modeluri)),
         () -> missingParameter(ctx, key));
   }

   /**
    * Apply a {@code processor} operation to the resolved model URI in the request.
    * If the model URI does not resolve, responds automatically with a 404.
    *
    * @param ctx       the request context
    * @param key       the request parameter key to look up to get the model URI
    * @param processor an operation to process the resolved URI
    */
   default void withResolvedModelURI(final Context ctx, final String key, final Consumer<? super String> processor) {
      this.resolveModelURI(ctx, key).map(URI::toString).ifPresentOrElse(processor,
         () -> respondMissingOrUnspecifiedModel(ctx, key));
   }

   /**
    * Resolve the model URI to which a request pertains to an absolute URI suitable for access to an EMF
    * {@link ResourceSet}.
    * The result is optional to allow for implementations to refuse to resolve <tt>modeluri</tt> parameters matching
    * whatever
    * validation criteria they may impose, e.g. disallowing simple file paths or absolute URIs.
    *
    * @param ctx the incoming request context
    * @return the resolved absolute URI, if the request parameter is resolvable
    */
   default Optional<URI> resolveModelURI(final Context ctx) {
      return resolveModelURI(ctx, MODEL_URI);
   }

   /**
    * Resolve the model URI to which a message pertains to an absolute URI suitable for access to an EMF
    * {@link ResourceSet}.
    * The result is optional to allow for implementations to refuse to resolve <tt>modeluri</tt> parameters matching
    * whatever
    * validation criteria they may impose, e.g. disallowing simple file paths or absolute URIs.
    *
    * @param ctx the incoming message context
    * @return the resolved absolute URI, if the message context parameter is resolvable
    */
   default Optional<URI> resolveModelURI(final WsContext ctx) {
      return resolveModelURI(ctx, MODEL_URI);
   }

   /**
    * Apply a {@code processor} function to the resolved model URI in the request.
    * If the model URI does not resolve, responds automatically with a 404.
    *
    * @param <T>       the processor result
    * @param ctx       the request context
    * @param processor a function to process the resolved URI
    *
    * @return the result of the {@code processor}, or empty if 404
    */
   default <T> Optional<T> applyResolvedModelURI(final Context ctx, final Function<? super String, T> processor) {
      return applyResolvedModelURI(ctx, MODEL_URI, processor);
   }

   /**
    * Apply a {@code processor} operation to the resolved model URI in the request.
    * If the model URI does not resolve, responds automatically with a 404.
    *
    * @param ctx       the request context
    * @param processor an operation to process the resolved URI
    */
   default void withResolvedModelURI(final Context ctx, final Consumer<? super String> processor) {
      withResolvedModelURI(ctx, MODEL_URI, processor);
   }

   /**
    * Deresolve an absolute model URI for response to the given context request.
    *
    * @param ctx      the request context
    * @param modelURI the absolute model URI to deresolve
    *
    * @return the deresolved URI
    *
    * @throws IllegalArgumentException if the given model URI is {@link URI#isRelative() not an absolute URI}
    */
   URI deresolveModelURI(Context ctx, URI modelURI);

   /**
    * Deresolve an absolute model URI for reply to the given incoming message context.
    *
    * @param ctx      the message context
    * @param modelURI the absolute model URI to deresolve
    *
    * @return the deresolved URI
    *
    * @throws IllegalArgumentException if the given model URI is {@link URI#isRelative() not an absolute URI}
    */
   URI deresolveModelURI(WsContext ctx, URI modelURI);

   /**
    * Deresolve an absolute model URI for response to the given context request.
    *
    * @param ctx      the request context
    * @param modelURI the absolute model URI to deresolve
    *
    * @return the deresolved URI
    *
    * @throws IllegalArgumentException if the given model URI is {@link URI#isRelative() not an absolute URI}
    */
   default String deresolveModelURI(final Context ctx, final String modelURI) {
      return deresolveModelURI(ctx, URI.createURI(modelURI)).toString();
   }

   /**
    * Deresolve an absolute model URI for reply to the given incoming message.
    *
    * @param ctx      the message context
    * @param modelURI the absolute model URI to deresolve
    *
    * @return the deresolved URI
    *
    * @throws IllegalArgumentException if the given model URI is {@link URI#isRelative() not an absolute URI}
    */
   default String deresolveModelURI(final WsContext ctx, final String modelURI) {
      return deresolveModelURI(ctx, URI.createURI(modelURI)).toString();
   }

   /**
    * Encapsulate the given request context in a function that deresolves model URIs.
    *
    * @param ctx the current request context
    * @return a model URI deresolving function
    */
   default UnaryOperator<String> deresolver(final Context ctx) {
      return modeluri -> deresolveModelURI(ctx, modeluri);
   }

   /**
    * Encapsulate the given websocket session context in a function that deresolves model URIs.
    *
    * @param ctx the current websocket session context
    * @return a model URI deresolving function
    */
   default UnaryOperator<String> deresolver(final WsContext ctx) {
      return modeluri -> deresolveModelURI(ctx, modeluri);
   }

}
