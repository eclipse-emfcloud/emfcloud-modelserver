/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;

import com.fasterxml.jackson.databind.JsonNode;

public interface ModelListener {
   void modelCreated(String modeluri);

   void modelUpdated(String modeluri);

   /**
    * @deprecated Use the {@link #commandExecuted(String, Supplier, Supplier)} API, instead, which
    *             supports broadcast of model change notifications to v1 and v2 API subscribers
    */
   @Deprecated
   void commandExecuted(String modeluri, CCommandExecutionResult execution);

   /**
    * Notify of execution of a command or application of a JSON Patch.
    * On execution of a command, the execution result will be provided but also a patch diff if
    * the receiver wants it.
    *
    * @param modeluri  the model that was changed
    * @param execution a supplier of the execution result describing the changes performed.
    *                     Not itself {@code null} but it can supply a {@code null} if a JSON Patch was applied to the
    *                     model
    * @param patch     a supplier of the patch diff describing the changes performed.
    *                     Not itself {@code null} but it can supply a {@code null} if for some reason it is unavailable
    */
   default void commandExecuted(final String modeluri, final Supplier<? extends CCommandExecutionResult> execution,
      final Supplier<Map<URI, JsonNode>> patch) {

      CCommandExecutionResult result = execution.get();
      if (result != null) {
         commandExecuted(modeluri, result);
      }
   }

   void modelDeleted(String modeluri);

   /**
    * Notifies when a model resource is closed.
    *
    * @param modeluri the concerned model's URI
    */
   void modelClosed(String modeluri);

   void modelSaved(String modeluri);

   void allModelsSaved();
}
