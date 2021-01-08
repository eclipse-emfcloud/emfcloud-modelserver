/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
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
import java.util.Optional;
import java.util.TimeZone;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.ModelServerNotification;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.XmiToEObjectSubscriptionListener;

public class ExampleXMISubscriptionListener extends XmiToEObjectSubscriptionListener {

   private final String modelUri;

   public ExampleXMISubscriptionListener(final String modelUri) {
      this.modelUri = modelUri;
   }

   protected void printResponse(final String message) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      df.setTimeZone(TimeZone.getTimeZone("UTC"));
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
   public void onIncrementalUpdate(final EObject incrementalUpdate) {
      printResponse("Incremental <XmiEObject> update from model server received: " + incrementalUpdate.toString());
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
      printResponse("Full <XmiEObject> update from model server received: " + fullUpdate.toString());
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
