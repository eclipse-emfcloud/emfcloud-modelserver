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
package org.eclipse.emfcloud.modelserver.common;

import org.eclipse.emf.common.util.Diagnostic;

public interface ModelServerPathsV2 {
   String BASE_PATH = "api/v2";

   String MODEL_BASE_PATH = "models"; // accepts query parameter "modeluri"
   String MODEL_ELEMENT = "modelelement"; // accepts query parameters "modeluri" and either "elementId" or "elementname"

   String MODEL_URIS = "modeluris";

   String SUBSCRIPTION = "subscribe"; // accepts query parameter "modeluri"
   String CLOSE = "close"; // accepts query parameter "modeluri"

   String SAVE = "save"; // accepts query parameter "modeluri"
   String SAVE_ALL = "saveall";
   String UNDO = "undo"; // accepts query parameter "modeluri"
   String REDO = "redo"; // accepts query parameter "modeluri"

   String VALIDATION = "validation"; // accepts query parameter "modeluri"
   String VALIDATION_CONSTRAINTS = "validation/constraints"; // accepts the query parameter "modeluri"

   String TYPE_SCHEMA = "typeschema"; // accepts query parameter "modeluri"
   String UI_SCHEMA = "uischema"; // accepts query parameter "schemaname"

   String SERVER_CONFIGURE = "server/configure";
   String SERVER_PING = "server/ping";

   /**
    * <p>
    * Endpoint on which to POST to open a transaction, returning the transaction's URI.
    * Make PATCH requests on the returned URI to execute commands in the context of the transaction
    * and PUT an error {@link Diagnostic} to roll back. Otherwise, DELETE the transaction resource URI
    * to close it.
    * </p>
    * <p>
    * Parameters:
    * </p>
    * <dl>
    * <dt>modeluri</dt>
    * <dd>(required) the model resource on which to open a transaction</dd>
    * <dt>timeout</dt>
    * <dd>(required) milliseconds after the last request on the resource at which to automatically roll back and delete
    * the transaction</dd>
    * </dl>
    */
   String TRANSACTION = "transaction"; // accepts query parameters "modeluri" and "timeout"

}
