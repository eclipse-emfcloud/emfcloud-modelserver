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

interface ModelServerPathsV1 {

   String MODEL_BASE_PATH = "models"; // accepts query parameter "modeluri"
   String MODEL_ELEMENT = "modelelement"; // accepts query parameters "modeluri" and "elementId"

   String MODEL_URIS = "modeluris";

   String SUBSCRIPTION = "subscribe"; // accepts query parameter "modeluri"
   String EDIT = "edit"; // accepts query parameter "modeluri"
   String SAVE = "save"; // accepts query parameter "modeluri"

   String SCHEMA = "schema"; // accepts query parameter "modeluri"
   String TYPE_SCHEMA = "typeschema"; // accepts query parameter "modeluri"

   String UI_SCHEMA = "uischema";

   String SERVER_CONFIGURE = "server/configure";
   String SERVER_PING = "server/ping";

}
