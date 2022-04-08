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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

/**
 * <p>
 * An {@link EObject} subscription listener using the {@link ModelServerPathParametersV1#FORMAT_XMI xmi} format.
 * </p>
 * <p>
 * For API v2 or later, use the {@link EObjectSubscriptionListener} class with the {@link XmiCodec}.
 * </p>
 */
public class XmiToEObjectSubscriptionListener extends EObjectSubscriptionListener {
   public XmiToEObjectSubscriptionListener() {
      super(new XmiCodec());
   }

   @Override
   public void onIncrementalUpdate(final EObject command) {
      if (!(command instanceof CCommandExecutionResult)) {
         throw new IllegalArgumentException("Expected CCommandExecutionResult but received: " + command);
      }
      onIncrementalUpdate((CCommandExecutionResult) command);
   }

   @Override
   public void onIncrementalUpdate(final CCommandExecutionResult command) {}

   /**
    * @deprecated Since the 0.8 release, this method is no longer used.
    */
   @Deprecated
   public static Optional<EObject> decode(final String xmiString) {
      try {
         return new XmiCodec().decode(xmiString);
      } catch (DecodingException e) {
         throw new RuntimeException(e);
      }
   }
}
