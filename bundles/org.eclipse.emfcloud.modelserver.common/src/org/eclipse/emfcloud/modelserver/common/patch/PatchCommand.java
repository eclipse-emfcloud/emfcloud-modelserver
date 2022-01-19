/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;

/**
 * Represents a command (or patch) that can be interpreted and executed
 * by the Model Server to modify the model.
 */
public interface PatchCommand<T> {

   /**
    * The type of the command (e.g. {@link ModelServerPathParametersV2#JSON_PATCH},
    * {@link ModelServerPathParametersV2#EMF_COMMAND})
    *
    * @return
    *         the type of the command
    */
   String getType();

   /**
    * The parsed Data of the command. The type of the value depends on {@link #getType()}.
    *
    * @return
    *         The patch command data.
    */
   T getData();
}
