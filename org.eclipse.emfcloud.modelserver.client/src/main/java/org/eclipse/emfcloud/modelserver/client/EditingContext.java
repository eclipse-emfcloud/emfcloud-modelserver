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
package org.eclipse.emfcloud.modelserver.client;

import org.eclipse.emf.common.command.Command;

import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

/**
 * Client interface for editing operations on managed models.
 */
public interface EditingContext {

   /**
    * Execute a {@code command} to change the model.
    *
    * @param command a JSON representation of the command to execute
    *
    * @return whether the command was successfully applied on the model. If
    *         {@code false}, the model is unchanged
    *
    * @throws EncodingException if the {@code command} is not supported for serialization
    */
   boolean execute(Command command) throws EncodingException;

   /**
    * Execute a {@code command} to change the model.
    *
    * @param command a JSON representation of the command to execute
    *
    * @return whether the command was successfully applied on the model. If
    *         {@code false}, the model is unchanged
    */
   boolean execute(String command);

}
