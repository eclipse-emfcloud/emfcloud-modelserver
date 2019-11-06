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
package org.eclipse.emfcloud.modelserver.edit;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

/**
 * Codec for serialization of EMF {@link Command}s.
 */
public interface CommandCodec {

   /**
    * Encode a {@code command} for serialization.
    * 
    * @param command the command to encode
    * @return the serialization representation of the {@code command}
    * 
    * @throws EncodingException if the {@code command} includes a command or detail
    *                              that is not supported
    */
   CCommand encode(Command command) throws EncodingException;

   /**
    * Decode a {@code command} from serialization.
    * 
    * @param domain  the editing domain in which to instantiate the EMF command
    * @param command the command to decode
    * @return the canonical EMF implementation of the {@code command}
    * 
    * @throws DecodingException if the {@code command} includes a command or detail
    *                              that is not supported
    */
   Command decode(EditingDomain domain, CCommand command) throws DecodingException;

}
