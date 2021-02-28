/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.edit;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

public interface CommandContribution {
   /**
    * Encode a {@code command} for serialization.
    *
    * @param serverCommand       the command to encode
    * @param originClientCommand the client command from which the server command was created
    * @return the serialization representation of the {@code command}
    *
    * @throws EncodingException if the {@code command} includes a command or detail
    *                              that is not supported
    */
   CCommand serverToClient(Command serverCommand, CCommand originClientCommand) throws EncodingException;

   /**
    * Decode a {@code command} from serialization.
    *
    * @param modelUri      model uri
    * @param domain        the editing domain in which to instantiate the EMF command
    * @param clientCommand the command to decode
    * @return the canonical EMF implementation of the {@code command}
    *
    * @throws DecodingException if the {@code command} includes a command or detail
    *                              that is not supported
    */
   Command clientToServer(URI modelUri, EditingDomain domain, CCommand clientCommand) throws DecodingException;
}
