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

import java.util.Optional;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emfcloud.modelserver.command.CCommand;

/**
 * Wrapper for commands executed on the model server to remember the original client command.
 */
public class ModelServerCommand extends CommandWrapper {
   private final CCommand clientCommand;

   public ModelServerCommand(final Command command, final CCommand clientCommand) {
      super(command);
      this.clientCommand = clientCommand;
   }

   public CCommand getClientCommand() { return clientCommand; }

   public static Command wrap(final Command command, final CCommand userCommand) {
      if (command instanceof ModelServerCommand) {
         return command;
      }
      return new ModelServerCommand(command, userCommand);
   }

   public static Command unwrap(final Command command) {
      return command instanceof ModelServerCommand
         ? ((ModelServerCommand) command).getCommand()
         : command;
   }

   public static Optional<CCommand> getClientCommand(final Command command) {
      return command instanceof ModelServerCommand
         ? Optional.of(((ModelServerCommand) command).getClientCommand())
         : Optional.empty();
   }

}
