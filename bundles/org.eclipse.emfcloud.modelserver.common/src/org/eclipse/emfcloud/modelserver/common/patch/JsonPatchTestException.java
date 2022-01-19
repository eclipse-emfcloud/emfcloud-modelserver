/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An exception thrown when a Json Patch "test" operation fails.
 */
public class JsonPatchTestException extends Exception {

   private static final long serialVersionUID = 8333555068740826391L;

   public JsonPatchTestException(final JsonNode testOperation) {
      // Empty
   }

}
