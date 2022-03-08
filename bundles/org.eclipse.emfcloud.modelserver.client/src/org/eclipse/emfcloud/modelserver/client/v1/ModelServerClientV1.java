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
package org.eclipse.emfcloud.modelserver.client.v1;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.EditingContext;
import org.eclipse.emfcloud.modelserver.client.Model;
import org.eclipse.emfcloud.modelserver.client.ModelServerClientApiV1;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.client.SubscriptionListener;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseMember;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.internal.client.ModelServerClientDelegate;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ModelServerClientV1 implements ModelServerClientApiV1<EObject>, ModelServerPathsV1, AutoCloseable {

   public static final Set<String> SUPPORTED_FORMATS = Set.of(ModelServerPathParametersV1.FORMAT_JSON,
      ModelServerPathParametersV1.FORMAT_XMI);

   protected static final Logger LOG = LogManager.getLogger(ModelServerClientV1.class);

   protected final ModelServerClientDelegate delegate;

   public ModelServerClientV1(final String baseUrl, final EPackageConfiguration... configurations)
      throws MalformedURLException {

      this(new ModelServerClientDelegate(baseUrl, ModelServerPathParametersV1.FORMAT_JSON, SUPPORTED_FORMATS,
         configurations));
   }

   public ModelServerClientV1(final String baseUrl, final Set<String> supportedFormats,
      final EPackageConfiguration... configurations)
      throws MalformedURLException {

      this(new ModelServerClientDelegate(baseUrl, ModelServerPathParametersV1.FORMAT_JSON, supportedFormats,
         configurations));
   }

   public ModelServerClientV1(final OkHttpClient client, final String baseUrl,
      final EPackageConfiguration... configurations) throws MalformedURLException {

      this(new ModelServerClientDelegate(client, baseUrl, ModelServerPathParametersV1.FORMAT_JSON, SUPPORTED_FORMATS,
         configurations));
   }

   protected ModelServerClientV1(final ModelServerClientDelegate delegate) {
      super();

      this.delegate = delegate;
   }

   @Override
   public void close() {
      delegate.close();
   }

   @Override
   public CompletableFuture<Response<Boolean>> edit(final String modelUri, final CCommand command,
      final String format) {
      String checkedFormat = delegate.checkedFormat(format);
      final Request request = new Request.Builder()
         .url(
            delegate.createHttpUrlBuilder(delegate.makeUrl(EDIT))
               .addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
               .addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat)
               .build())
         .patch(
            RequestBody.create(
               Json.object(
                  Json.prop(JsonResponseMember.DATA, Json.text(delegate.encode(command, checkedFormat)))).toString(),
               MediaType.parse("application/json")))
         .build();

      return delegate.makeCallAndExpectSuccess(request);
   }

   @Override
   public EditingContext edit() {
      return delegate.edit(delegate, EDIT, ModelServerClientDelegate::encode);
   }

   //
   // Delegation methods
   //

   @Override
   public CompletableFuture<Response<String>> get(final String modelUri) {
      return delegate.get(modelUri);
   }

   @Override
   public CompletableFuture<Response<EObject>> get(final String modelUri, final String format) {
      return delegate.get(modelUri, format);
   }

   @Override
   public CompletableFuture<Response<List<Model<String>>>> getAll() { return delegate.getAll(); }

   @Override
   public CompletableFuture<Response<List<Model<EObject>>>> getAll(final String format) {
      return delegate.getAll(format);
   }

   @Override
   public CompletableFuture<Response<List<String>>> getModelUris() { return delegate.getModelUris(); }

   @Override
   public CompletableFuture<Response<String>> getModelElementById(final String modelUri, final String elementid) {
      return delegate.getModelElementById(modelUri, elementid);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementById(final String modelUri, final String elementid,
      final String format) {

      return delegate.getModelElementById(modelUri, elementid, format);
   }

   @Override
   public CompletableFuture<Response<String>> getModelElementByName(final String modelUri, final String elementname) {
      return delegate.getModelElementByName(modelUri, elementname);
   }

   @Override
   public CompletableFuture<Response<EObject>> getModelElementByName(final String modelUri, final String elementname,
      final String format) {

      return delegate.getModelElementByName(modelUri, elementname, format);
   }

   @Override
   public CompletableFuture<Response<Boolean>> delete(final String modelUri) {
      return delegate.delete(modelUri);
   }

   @Override
   public CompletableFuture<Response<Boolean>> close(final String modelUri) {
      return delegate.close(modelUri);
   }

   @Override
   public CompletableFuture<Response<String>> create(final String modelUri, final String createdModelAsJsonText) {
      return delegate.create(modelUri, createdModelAsJsonText);
   }

   @Override
   public CompletableFuture<Response<EObject>> create(final String modelUri, final EObject createdModel,
      final String format) {

      return delegate.create(modelUri, createdModel, format);
   }

   @Override
   public CompletableFuture<Response<String>> update(final String modelUri, final String updatedModelAsJsonText) {
      return delegate.update(modelUri, updatedModelAsJsonText);
   }

   @Override
   public CompletableFuture<Response<EObject>> update(final String modelUri, final EObject updatedModel,
      final String format) {

      return delegate.update(modelUri, updatedModel, format);
   }

   @Override
   public CompletableFuture<Response<Boolean>> save(final String modelUri) {
      return delegate.save(modelUri);
   }

   @Override
   public CompletableFuture<Response<Boolean>> saveAll() {
      return delegate.saveAll();
   }

   @Override
   public CompletableFuture<Response<String>> validate(final String modelUri) {
      return delegate.validate(modelUri);
   }

   @Override
   public CompletableFuture<Response<String>> getValidationConstraints(final String modelUri) {
      return delegate.getValidationConstraints(modelUri);
   }

   @Override
   public CompletableFuture<Response<String>> getTypeSchema(final String modelUri) {
      return delegate.getTypeSchema(modelUri);
   }

   @Override
   public CompletableFuture<Response<String>> getUiSchema(final String schemaName) {
      return delegate.getUiSchema(schemaName);
   }

   @Override
   public CompletableFuture<Response<Boolean>> configure(final ServerConfiguration configuration) {
      return delegate.configure(configuration);
   }

   @Override
   public CompletableFuture<Response<Boolean>> ping() {
      return delegate.ping();
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener) {
      delegate.subscribe(modelUri, subscriptionListener);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format) {
      delegate.subscribe(modelUri, subscriptionListener, format);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final long timeout) {
      delegate.subscribe(modelUri, subscriptionListener, timeout);
   }

   @Override
   public void subscribe(final String modelUri, final SubscriptionListener subscriptionListener, final String format,
      final long timeout) {

      delegate.subscribe(modelUri, subscriptionListener, format, timeout);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener) {
      delegate.subscribeWithValidation(modelUri, subscriptionListener);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format) {

      delegate.subscribeWithValidation(modelUri, subscriptionListener, format);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final long timeout) {

      delegate.subscribeWithValidation(modelUri, subscriptionListener, timeout);
   }

   @Override
   public void subscribeWithValidation(final String modelUri, final SubscriptionListener subscriptionListener,
      final String format, final long timeout) {

      delegate.subscribeWithValidation(modelUri, subscriptionListener, format, timeout);
   }

   @Override
   public boolean send(final String modelUri, final String message) {
      return delegate.send(modelUri, message);
   }

   @Override
   public boolean unsubscribe(final String modelUri) {
      return delegate.unsubscribe(modelUri);
   }

   @Override
   public boolean close(final EditingContext editingContext) {
      return delegate.close(editingContext);
   }

   @Override
   public CompletableFuture<Response<Boolean>> undo(final String modelUri) {
      return delegate.undo(modelUri);
   }

   @Override
   public CompletableFuture<Response<Boolean>> redo(final String modelUri) {
      return delegate.redo(modelUri);
   }

}
