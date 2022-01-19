/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.util;

import java.util.Optional;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Representation of a message on a websocket.
 *
 * @param <T> the type of data payload in the message
 */
public final class Message<T> {

   protected static Logger LOG = Logger.getLogger(Message.class.getSimpleName());

   private final String type;
   private final T data;

   public Message(final String type, final T data) {
      super();

      this.type = type;
      this.data = data;
   }

   public Message(final String type) {
      this(type, null);
   }

   public String getType() { return type; }

   public T getData() { return data; }

   /**
    * Obtain a message of my {@link #getType() type} with my {@link #getData() data} cast to the given
    * {@code subtype} of my data class.
    *
    * @param <U>     the transformed value type
    * @param subtype the subtype of my data type
    * @return a coerced variant of myself, or empty if my {@link #getData() data} is not of the given {@code subtype}
    */
   public <U extends T> Optional<Message<U>> as(final Class<U> subtype) {
      return subtype.isInstance(data) ? Optional.of(new Message<>(type, subtype.cast(data)))
         : Optional.empty();
   }

   /**
    * Obtain a message of my {@link #getType() type} with my {@link #getData() data} transformed under the given
    * {@code mapper} function.
    *
    * @param <U>    the transformed value type
    * @param mapper the transformation function
    * @return a transformed variant of myself
    */
   public <U> Optional<Message<U>> as(final Function<? super T, ? extends U> mapper) {
      return Optional.ofNullable(data == null ? null : mapper.apply(data))
         .map(u -> new Message<>(type, u));
   }

   /**
    * Obtain a message of my {@link #getType() type} without any data.
    *
    * @param <U> the target {@code null} value type
    * @return a null variant of myself
    */
   public <U> Optional<Message<U>> asNull() {
      return Optional.of(new Message<U>(type, null));
   }

   public <E extends EObject> Optional<Message<E>> fromJSON(final Codec codec, final Class<E> modelType) {
      return fromJSON(codec, modelType, null);
   }

   public <E extends EObject> Optional<Message<E>> fromJSON(final Codec codec, final Class<E> modelType,
      final URI workspaceURI) {

      if (Json.isEmpty(data)) {
         // Cast to null
         return Optional.of(new Message<>(type, null));
      }

      String jsonString = (data instanceof String) ? (String) data
         : (data instanceof JsonNode) ? ((JsonNode) data).toString() : null;

      return Optional.ofNullable(jsonString).flatMap(json -> {
         try {
            if (workspaceURI != null) {
               return codec.decode(json, workspaceURI);
            }
            return codec.decode(json);
         } catch (DecodingException e) {
            LOG.error("Failed to decode JSON message data.", e);
            return null;
         }
      }).filter(modelType::isInstance).map(modelType::cast).map(model -> new Message<>(type, model));
   }

}
