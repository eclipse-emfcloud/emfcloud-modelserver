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
package org.eclipse.emfcloud.modelserver.edit.command;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandCodec;

public class SetCommandCodecContribution extends BasicCommandCodecContribution<SetCommand> {

   @Override
   protected CCommand toClient(final SetCommand command, final CCommand origin) throws EncodingException {
      return EMFCommandCodec.clientCommand(command);
   }

   @Override
   protected SetCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      return EMFCommandCodec.setCommand(domain, command);
   }

}
