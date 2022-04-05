/********************************************************************************
 * Copyright (c) 2020-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.example.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.EObjectSubscriptionListener;
import org.eclipse.emfcloud.modelserver.client.ModelServerNotification;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.utils.APIVersionMap;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.example.util.PrintUtil;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;

/**
 * A subscription listener that demonstrates the {@link EObjectSubscriptionListener} that
 * provides full and incremental updates as {@link EObject} models.
 */
public class ExampleEObjectSubscriptionListener extends EObjectSubscriptionListener {

   private static final APIVersionMap<Codec> JSON_CODECS = new APIVersionMap<>(Map.of(
      APIVersion.ZERO.range(APIVersion.API_V2), new JsonCodec(),
      APIVersion.API_V2.range(), new JsonCodecV2()));

   private final String modelUri;
   private final DateFormat df;

   public ExampleEObjectSubscriptionListener(final String modelUri, final APIVersion apiVersion) {
      super(JSON_CODECS.get(apiVersion));

      this.modelUri = modelUri;

      this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   protected void printResponse(final String message) {
      String nowAsISOString = df.format(new Date());
      System.out.println(nowAsISOString + " | [" + this.modelUri + "]: " + message);
   }

   @Override
   public void onOpen(final Response<String> response) {
      printResponse("Connected: " + response.getMessage());
   }

   @Override
   public void onSuccess(final Optional<String> message) {
      printResponse("Success: " + message.get());
   }

   @Override
   public void onIncrementalUpdate(final JsonPatch patch) {
      printResponse(
         "Incremental <JsonPatch> update from model server received:\n" + PrintUtil.toPrettyString(patch));
   }

   @Override
   public void onIncrementalUpdate(final CCommandExecutionResult commandExecutionResult) {
      printResponse(
         "Incremental <CCommandExecutionResult> update from model server received:\n"
            + PrintUtil.toPrettyString(commandExecutionResult));
   }

   @Override
   public void onDirtyChange(final boolean isDirty) {
      printResponse("Dirty State: " + isDirty);
   }

   @Override
   public void onUnknown(final ModelServerNotification notification) {
      printResponse("Unknown notification of type " + notification.getType() + ": " + notification.getData());
   }

   @Override
   public void onFullUpdate(final EObject fullUpdate) {
      printResponse("Full <EObject> update from model server received: " + PrintUtil.toPrettyString(fullUpdate));
   }

   @Override
   public void onError(final Optional<String> message) {
      printResponse("Error from model server received: " + message.get());
   }

   @Override
   public void onFailure(final Throwable t, final Response<String> response) {
      printResponse("Failure: " + response.getMessage());
      t.printStackTrace();
   }

   @Override
   public void onFailure(final Throwable t) {
      printResponse("Failure: ");
      t.printStackTrace();
   }

   @Override
   public void onClosing(final int code, final String reason) {
      printResponse("Closing connection to model server, reason: " + reason);
   }

   @Override
   public void onClosed(final int code, final String reason) {
      printResponse("Closed connection to model server, reason: " + reason);
   }

}
