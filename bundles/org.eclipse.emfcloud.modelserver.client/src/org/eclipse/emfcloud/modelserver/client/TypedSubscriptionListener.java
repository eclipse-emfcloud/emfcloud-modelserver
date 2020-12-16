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

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;

public class TypedSubscriptionListener<T> implements NotificationSubscriptionListener<T> {
   private final Function<? super String, Optional<T>> updateFunction;

   public TypedSubscriptionListener(final Function<String, Optional<T>> updateFunction) {
      this.updateFunction = updateFunction;
   }

   @Override
   public void onNotification(final ModelServerNotification notification) {
      switch (notification.getType()) {
         case JsonResponseType.SUCCESS:
            onSuccess(notification.getData());
            break;
         case JsonResponseType.ERROR:
            onError(notification.getData());
            break;
         case JsonResponseType.DIRTYSTATE:
            Boolean isDirty = notification.getData().map(Boolean::parseBoolean)
               .orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
            onDirtyChange(isDirty);
            break;
         case JsonResponseType.FULLUPDATE:
            T fullUpdateData = notification.getData().flatMap(updateFunction)
               .orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
            onFullUpdate(fullUpdateData);
            break;
         case JsonResponseType.INCREMENTALUPDATE:
            T incrementalUpdateData = notification.getData().flatMap(updateFunction)
               .orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
            onIncrementalUpdate(incrementalUpdateData);
            break;
         default:
            onUnknown(notification);
      }
   }

   @Override
   public void onSuccess(final Optional<String> message) {}

   @Override
   public void onError(final Optional<String> message) {}

   @Override
   public void onDirtyChange(final boolean isDirty) {}

   @Override
   public void onFullUpdate(final T root) {}

   @Override
   public void onIncrementalUpdate(final T command) {}

   @Override
   public void onUnknown(final ModelServerNotification notification) {}

   @Override
   public void onOpen(final Response<String> response) {}

   @Override
   public void onClosing(final int code, final String reason) {}

   @Override
   public void onClosed(final int code, final String reason) {}

   @Override
   public void onFailure(final Throwable t, final Response<String> response) {}

   @Override
   public void onFailure(final Throwable t) {}
}
