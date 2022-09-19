/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A service for synchronization of model edits and/or accesses in a serial queue
 * on one or more threads for safe concurrency.
 */
public interface ModelSynchronizer {

   /**
    * Execute a model read or write action without result and wait for it to finish.
    *
    * @param action an action on the model(s)
    */
   void syncExec(Runnable action);

   /**
    * Execute a model read or write action without result.
    *
    * @param action an action on the model(s)
    * @return a future that will notify when the {@code code} has completed
    */
   Future<Void> asyncExec(Runnable action);

   /**
    * Execute a model read or write action with a result.
    *
    * @param action an action on the model(s)
    * @return the result of the {@code action}, or {@code null} (with a log) if it failed
    */
   <T> T syncCall(Callable<T> action);

   /**
    * Execute a model read or write action with a result.
    *
    * @param action an action on the model(s)
    * @return the future result of the {@code action}
    */
   <T> Future<T> asyncCall(Callable<T> action);

}
