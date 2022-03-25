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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

public class Response<T> {

   private static final Map<okhttp3.Response, String> RESPONSE_BODIES = new WeakHashMap<>();

   private final okhttp3.Response response;
   private final Function<String, T> demarshaller;
   private T body;

   public Response(final okhttp3.Response response, final Function<String, T> demarshaller) {
      this.response = response;
      this.demarshaller = demarshaller;
   }

   @SuppressWarnings({ "unchecked" })
   public Response(final okhttp3.Response response) {
      this(response, (Function<String, T>) Function.<String> identity());
   }

   public Integer getStatusCode() { return this.response.code(); }

   public T body() {
      if (body == null) {
         body = this.demarshaller.apply(body(this.response));
      }
      return body;
   }

   public String getMessage() { return this.response.message(); }

   public <U> Response<U> mapBody(final Function<T, U> mapper) {
      return new Response<>(this.response, demarshaller.andThen(mapper));
   }

   private static String body(final okhttp3.Response response) {
      // The response body can only be read once (it's a stream)
      return RESPONSE_BODIES.computeIfAbsent(response, key -> {
         try {
            return Objects.requireNonNull(key.body()).string();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      });
   }

}
