/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics..
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emfcloud.modelserver.common.patch.LazyCompoundCommand;

/**
 * <p>
 * A Compound Command that uses lazily-instantiated commands. Instead of creating
 * all Commands when instantiating the {@link LazyTransactionalCompoundCommand}, actual
 * commands will be supplied during execution. The first command of the list
 * may be created earlier, when testing for {@link #canExecute()}.
 * </p>
 *
 * <p>
 * Supports undo/redo via {@link RecordingCommand}.
 * </p>
 */
public class LazyTransactionalCompoundCommand extends RecordingCommand implements LazyCompoundCommand {

   private final List<Callable<Command>> commands = new ArrayList<>();

   public LazyTransactionalCompoundCommand(final TransactionalEditingDomain domain) {
      super(domain);
   }

   public LazyTransactionalCompoundCommand(final TransactionalEditingDomain domain, final String label) {
      super(domain, label);
   }

   public LazyTransactionalCompoundCommand(final TransactionalEditingDomain domain, final String label,
      final List<Callable<Command>> commands) {
      super(domain, label);
      this.commands.addAll(commands);
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalExceptionCatch")
   protected void doExecute() {
      try {
         for (Callable<Command> commandSupplier : commands) {
            Command command = commandSupplier.call();
            if (command == null) {
               throw new RuntimeException("Failed to create sub-command");
            }
            if (command.canExecute()) {
               command.execute();
            } else {
               throw new RuntimeException(
                  "Can't execute command: " + getLabel() + "; Sub-command: " + command.getLabel());
            }
         }
      } catch (RuntimeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new RuntimeException("Failed to create sub-command", ex);
      }
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalExceptionCatch")
   protected boolean prepare() {
      if (commands.isEmpty()) {
         return true;
      }
      try {
         Command firstCommand = commands.get(0).call();
         return firstCommand.canExecute();
      } catch (Exception ex) {
         return false;
      }
   }

   @Override
   public List<Callable<Command>> getCommands() { return this.commands; }

   @Override
   public void append(final Callable<Command> command) {
      this.commands.add(command);
   }

   @Override
   public void append(final Command command) {
      this.append(() -> command);
   }

}
