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
package org.eclipse.emfcloud.modelserver.common;

public interface ModelServerPathsV1 {
   String BASE_PATH = "api/v1";

   String MODEL_BASE_PATH = "models"; // accepts query parameter "modeluri"
   String MODEL_ELEMENT = "modelelement"; // accepts query parameters "modeluri" and either "elementId" or "elementname"

   String MODEL_URIS = "modeluris";

   String SUBSCRIPTION = "subscribe"; // accepts query parameter "modeluri"
   String EDIT = "edit"; // accepts query parameter "modeluri"
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

}
