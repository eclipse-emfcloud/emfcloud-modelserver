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

import org.jetbrains.annotations.NotNull;

public interface SubscriptionListener {
   void onOpen(Response<String> response);

   void onNotification(ModelServerNotification notification);

   void onClosing(int code, @NotNull String reason);

   void onClosed(int code, @NotNull String reason);

   void onFailure(Throwable t, Response<String> response);

   void onFailure(Throwable t);
}
