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

public interface NotificationSubscriptionListener<T> extends SubscriptionListener {
   void onSuccess(Optional<String> message);

   void onError(Optional<String> message);

   void onDirtyChange(boolean isDirty);

   void onFullUpdate(T root);

   void onIncrementalUpdate(T command);

   void onUnknown(ModelServerNotification notification);
}
