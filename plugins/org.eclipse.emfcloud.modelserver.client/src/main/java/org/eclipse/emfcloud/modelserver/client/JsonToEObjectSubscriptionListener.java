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

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;

public class JsonToEObjectSubscriptionListener extends TypedSubscriptionListener<EObject> {
   private static Logger LOG = Logger.getLogger(JsonToEObjectSubscriptionListener.class.getSimpleName());

   public JsonToEObjectSubscriptionListener() {
      super(JsonToEObjectSubscriptionListener::decode);
   }

   private static Optional<EObject> decode(final String payload) {
      try {
         return new DefaultJsonCodec().decode(payload);
      } catch (DecodingException e) {
         LOG.error("Failed to decode notification", e);
         return Optional.empty();
      }
   }
}
