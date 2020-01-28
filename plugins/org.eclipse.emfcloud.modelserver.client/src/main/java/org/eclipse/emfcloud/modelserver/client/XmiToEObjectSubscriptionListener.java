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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

public class XmiToEObjectSubscriptionListener extends TypedSubscriptionListener<EObject> {
   public XmiToEObjectSubscriptionListener() {
      super(XmiToEObjectSubscriptionListener::decode);
   }

   @Override
   public void onIncrementalUpdate(final EObject command) {
      if (!(command instanceof CCommand)) {
         throw new IllegalArgumentException("Expected CCommand but received: " + command);
      }
      onIncrementalUpdate((CCommand) command);
   }

   public void onIncrementalUpdate(final CCommand command) {}

   public static Optional<EObject> decode(final String xmiString) {
      try {
         return new XmiCodec().decode(xmiString);
      } catch (DecodingException e) {
         throw new RuntimeException(e);
      }
   }
}
