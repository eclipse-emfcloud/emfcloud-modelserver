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

public interface ModelServerPathParametersV2 {

   String MODEL_URI = "modeluri";
   String FORMAT = "format";
   String FORMAT_JSON = "json";
   String FORMAT_XMI = "xmi";
   String ELEMENT_ID = "elementid";
   String ELEMENT_NAME = "elementname";
   String SCHEMA_NAME = "schemaname";
   String TIMEOUT = "timeout";
   String LIVE_VALIDATION = "livevalidation";

   // V2 Parameters

   /**
    * The Json Format for V2 APIs.
    */
   String FORMAT_JSON_V2 = "json-v2";

   /**
    * Patch Commands in the Json Patch format.
    */
   String JSON_PATCH = "modelserver.patch";

   /**
    * Patch Commands in the EMF CCommand format.
    */
   String EMF_COMMAND = "modelserver.emfcommand";

}
