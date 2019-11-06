/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common;

interface ModelServerPathsV1 {

   String MODEL_BASE_PATH = "models"; // accepts query parameter "modeluri"

   String MODEL_URIS = "modeluris";

   String SUBSCRIPTION = "subscribe"; // accepts query parameter "modeluri"
   String EDIT = "edit"; // accepts query parameter "modeluri"
   String SAVE = "save"; // accepts query parameter "modeluri"

   String SCHEMA = "schema"; // accepts query parameter "modeluri"

   String SERVER_CONFIGURE = "server/configure";
   String SERVER_PING = "server/ping";

}
