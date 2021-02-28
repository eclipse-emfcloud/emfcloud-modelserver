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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;

import com.google.inject.Inject;

public class CompoundCommandContribution extends BasicCommandContribution<CompoundCommand> {

   private CommandCodec commandCodec;

   @Inject
   public CompoundCommandContribution(final CommandCodec commandCodec) {
      this.commandCodec = commandCodec;
   }

   protected void setCommandCodec(final CommandCodec commandCodec) { this.commandCodec = commandCodec; }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      CompoundCommand compound = new CompoundCommand();
      for (CCommand next : ((CCompoundCommand) command).getCommands()) {
         compound.append(commandCodec.clientToServer(modelUri, domain, next));
      }
      return compound;
   }

   @Override
   protected CCommand toClient(final CompoundCommand command, final CCommand origin) throws EncodingException {
      CCompoundCommand compound = CCommandFactory.eINSTANCE.createCompoundCommand();
      compound.setType(EMFCommandType.COMPOUND);
      EList<CCommand> commands = compound.getCommands();
      for (Command next : command.getCommandList()) {
         commands.add(commandCodec.serverToClient(next));
      }
      return compound;
   }

}
