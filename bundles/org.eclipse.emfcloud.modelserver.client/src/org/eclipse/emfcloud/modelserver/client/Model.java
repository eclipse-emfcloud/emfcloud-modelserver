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
package org.eclipse.emfcloud.modelserver.client;

import java.util.Objects;

public class Model<A> {
   private String modelUri;
   private A content;

   public Model() {}

   public Model(final String modelUri, final A content) {
      this.modelUri = modelUri;
      this.content = content;
   }

   public String getModelUri() { return modelUri; }

   public void setModelUri(final String modelUri) { this.modelUri = modelUri; }

   public A getContent() { return content; }

   public void setContent(final A content) { this.content = content; }

   @Override
   public int hashCode() {
      return Objects.hash(content, modelUri);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof Model)) {
         return false;
      }
      Model<?> other = (Model<?>) obj;
      return Objects.equals(content, other.content) && Objects.equals(modelUri, other.modelUri);
   }

}
