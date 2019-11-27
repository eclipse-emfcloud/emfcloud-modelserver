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
package org.eclipse.emfcloud.modelserver.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.emf.common.command.Command;

import org.eclipse.emfcloud.modelserver.command.CCommand;

public interface ModelServerClientApiV1<A> {

   CompletableFuture<Response<String>> get(String modelUri);

   CompletableFuture<Response<A>> get(String modelUri, String format);

   CompletableFuture<Response<List<String>>> getAll();

   CompletableFuture<Response<Boolean>> delete(String modelUri);

   CompletableFuture<Response<String>> update(String modelUri, String updatedModel);

   CompletableFuture<Response<A>> update(String modelUri, A updatedModel, String format);

   CompletableFuture<Response<Boolean>> save(String modelUri);

   CompletableFuture<Response<String>> getSchema(String modelUri);

   CompletableFuture<Response<Boolean>> configure(ServerConfiguration configuration);

   CompletableFuture<Response<Boolean>> ping();

   CompletableFuture<Response<Boolean>> edit(String modelUri, Command command);

   CompletableFuture<Response<Boolean>> edit(String modelUri, Command command, String format);

   CompletableFuture<Response<Boolean>> edit(String modelUri, CCommand command, String format);

   void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format);

   boolean unsubscribe(String modelUri);

   EditingContext edit();

   boolean close(EditingContext editingContext);

}
