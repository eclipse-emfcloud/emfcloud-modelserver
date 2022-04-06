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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;

/**
 * A typed subscription listener that consumes message payloads as type {@code T}.
 *
 * @param <T> the message payload type accepted by the listener
 */
public class TypedSubscriptionListener<T> implements NotificationSubscriptionListener<T> {
   private static Logger LOG = LogManager.getLogger(TypedSubscriptionListener.class);

   private final BiFunction<? super String, ? super String, Optional<? extends T>> updateFunction;

   /**
    * Initializes me with a function that transforms the message body to the listener's input
    * type. This function cannot discriminate on the message type but is expected to transform
    * all message payloads in the same way.
    *
    * @param updateFunction the message payload transform
    */
   public TypedSubscriptionListener(final Function<String, Optional<? extends T>> updateFunction) {
      this((data, type) -> updateFunction.apply(data));
   }

   /**
    * Initializes me with a function that transforms the message body to the listener's input
    * type. This function is also given the the message type so that it may transform
    * different message payloads in the diffferent ways.
    *
    * @param updateFunction the message payload transform: the first argument is the message payload text
    *                          and the second is the message type as enumerated in the
    *                          {@link JsonResponseType} class
    */
   public TypedSubscriptionListener(final BiFunction<String, String, Optional<? extends T>> updateFunction) {
      super();

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
            T fullUpdateData = notification.getData()
               .flatMap(data -> updateFunction.apply(data, notification.getType()))
               .orElseThrow(() -> new RuntimeException("Could not parse 'data' field"));
            onFullUpdate(fullUpdateData);
            break;
         case JsonResponseType.INCREMENTALUPDATE:
            T incrementalUpdateData = notification.getData()
               .flatMap(data -> updateFunction.apply(data, notification.getType()))
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
   public void onError(final Optional<String> message) {
      LOG.error("Error: " + message.orElse("Unknown"));
   }

   @Override
   public void onDirtyChange(final boolean isDirty) {}

   @Override
   public void onFullUpdate(final T fullUpdate) {}

   @Override
   public void onIncrementalUpdate(final T incrementalUpdate) {}

   @Override
   public void onUnknown(final ModelServerNotification notification) {
      LOG.warn("Unknown notification type: " + notification.getType());
   }

   @Override
   public void onOpen(final Response<String> response) {}

   @Override
   public void onClosing(final int code, final String reason) {}

   @Override
   public void onClosed(final int code, final String reason) {}

   @Override
   public void onFailure(final Throwable throwable, final Response<String> response) {
      LOG.error("Failure: " + response.getMessage(), throwable);
   }

   @Override
   public void onFailure(final Throwable throwable) {
      LOG.error("Failure: ", throwable);
   }
}
