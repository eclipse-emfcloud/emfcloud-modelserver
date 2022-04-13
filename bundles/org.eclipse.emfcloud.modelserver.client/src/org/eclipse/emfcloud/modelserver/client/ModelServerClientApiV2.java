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
package org.eclipse.emfcloud.modelserver.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.emfcloud.modelserver.command.CCommand;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ModelServerClientApiV2<A> {

   CompletableFuture<Response<String>> get(String modelUri);

   CompletableFuture<Response<A>> get(String modelUri, String format);

   CompletableFuture<Response<List<Model<String>>>> getAll();

   CompletableFuture<Response<List<Model<A>>>> getAll(String format);

   CompletableFuture<Response<List<String>>> getModelUris();

   CompletableFuture<Response<String>> getModelElementById(String modelUri, String elementid);

   CompletableFuture<Response<A>> getModelElementById(String modelUri, String elementid, String format);

   CompletableFuture<Response<String>> getModelElementByName(String modelUri, String elementname);

   CompletableFuture<Response<A>> getModelElementByName(String modelUri, String elementname, String format);

   CompletableFuture<Response<Boolean>> delete(String modelUri);

   CompletableFuture<Response<Boolean>> close(String modelUri);

   CompletableFuture<Response<String>> create(String modelUri, String createdModelAsJsonText);

   CompletableFuture<Response<A>> create(String modelUri, A createdModel, String format);

   CompletableFuture<Response<String>> update(String modelUri, String updatedModelAsJsonText);

   CompletableFuture<Response<A>> update(String modelUri, A updatedModel, String format);

   CompletableFuture<Response<Boolean>> save(String modelUri);

   CompletableFuture<Response<Boolean>> saveAll();

   CompletableFuture<Response<String>> validate(String modelUri);

   CompletableFuture<Response<String>> getValidationConstraints(String modelUri);

   CompletableFuture<Response<String>> getTypeSchema(String modelUri);

   CompletableFuture<Response<String>> getUiSchema(String schemaName);

   CompletableFuture<Response<Boolean>> configure(ServerConfiguration configuration);

   CompletableFuture<Response<Boolean>> ping();

   CompletableFuture<Response<String>> edit(String modelUri, CCommand command, String format);

   CompletableFuture<Response<String>> edit(String modelUri, ArrayNode jsonPatch, String format);

   /**
    * Subscribe to notifications from the server about the given model with default options and
    * not including live validation results.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribe(String modelUri, SubscriptionListener subscriptionListener);

   /**
    * Subscribe to notifications from the server about the given model without live validation results,
    * in the given message {@code format}.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param format               the format in which to encode the subscription messages
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format);

   /**
    * Subscribe to notifications from the server about the given model without live validation results.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param timeout              a timeout, in milleseconds, after which idle time to close the subscription
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribe(String modelUri, SubscriptionListener subscriptionListener, long timeout);

   /**
    * Subscribe to notifications from the server about the given model without live validation results,
    * in the given message {@code format}.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param format               the format in which to encode the subscription messages
    * @param timeout              a timeout, in milleseconds, after which idle time to close the subscription
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format, long timeout);

   /**
    * Subscribe to notifications from the server about the given model, including live validation
    * results, but otherwise with default options.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener);

   /**
    * Subscribe to notifications from the server about the given model, including live validation
    * results, in the given message {@code format}.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param format               the format in which to encode the subscription messages
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format);

   /**
    * Subscribe to notifications from the server about the given model, including live validation
    * results.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param timeout              a timeout, in milleseconds, after which idle time to close the subscription
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, long timeout);

   /**
    * Subscribe to notifications from the server about the given model, including live validation
    * results, in the given message {@code format}.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param format               the format in which to encode the subscription messages
    * @param timeout              a timeout, in milleseconds, after which idle time to close the subscription
    *
    * @see {@link #subscribe(String, SubscriptionListener, SubscriptionOptions)} for a more generic and flexible
    *      way to specific subscription options
    */
   void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format,
      long timeout);

   /**
    * Subscribe to notifications from the server about the given model.
    *
    * @param modelUri             the URI identifying the model to which to subscribe
    * @param subscriptionListener the listener to call on subscription events
    * @param options              the subscription options. May be {@code null} if no options are required
    */
   void subscribe(String modelUri, SubscriptionListener subscriptionListener, SubscriptionOptions options);

   boolean send(String modelUri, String message);

   boolean unsubscribe(String modelUri);

   CompletableFuture<Response<String>> undo(String modelUri);

   CompletableFuture<Response<String>> redo(String modelUri);

}
