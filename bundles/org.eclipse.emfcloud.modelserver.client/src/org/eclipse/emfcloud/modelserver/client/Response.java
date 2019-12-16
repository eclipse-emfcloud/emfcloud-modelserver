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
import java.util.Objects;
import java.util.function.Function;

public class Response<T> {

   private final okhttp3.Response response;
   private final Function<String, T> demarshaller;
   private T body;

   public Response(final okhttp3.Response response, final Function<String, T> demarshaller) {
      this.response = response;
      this.demarshaller = demarshaller;
   }

   @SuppressWarnings({ "unchecked" })
   public Response(final okhttp3.Response response) {
      this.response = response;
      this.demarshaller = (Function<String, T>) Function.<String> identity();
   }

   public Integer getStatusCode() { return this.response.code(); }

   public T body() {
      if (body == null) {
         try {
            body = this.demarshaller.apply(Objects.requireNonNull(this.response.body()).string());
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      return body;
   }

   public String getMessage() { return this.response.message(); }

   public <U> Response<U> mapBody(final Function<T, U> mapper) {
      return new Response<>(this.response, demarshaller.andThen(mapper));
   }
}
