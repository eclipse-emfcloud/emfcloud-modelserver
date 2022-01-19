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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;

/**
 * Simple implementation of {@link LazyCompoundCommand}, that doesn't support Undo/Redo.
 */
public class SimpleLazyCompoundCommand extends AbstractCommand implements LazyCompoundCommand {

   private final List<Callable<Command>> commands = new ArrayList<>();

   public SimpleLazyCompoundCommand(final String label) {
      super(label);
      this.commands.addAll(commands);
   }

   @Override
   public boolean canExecute() {
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

   @Override
   @SuppressWarnings("checkstyle:IllegalExceptionCatch")
   public void execute() {
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
   public boolean canUndo() {
      return false;
   }

   @Override
   public void redo() {
      throw new UnsupportedOperationException();
   }

}
