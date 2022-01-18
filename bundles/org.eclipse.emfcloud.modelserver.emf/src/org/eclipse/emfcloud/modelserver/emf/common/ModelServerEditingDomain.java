/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.edit.ModelServerCommand;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;

public class ModelServerEditingDomain extends TransactionalEditingDomainImpl {

   private CompoundCommand openCompound;

   public ModelServerEditingDomain(final AdapterFactory adapterFactory, final ResourceSet resourceSet) {
      super(adapterFactory, new ModelServerCommandStack(), resourceSet);
      // turn off validation since we have our own validation mechanism
      setDefaultTransactionOptions(Map.of(Transaction.OPTION_NO_VALIDATION, true));
   }

   @Override
   public ModelServerCommandStack getCommandStack() { return (ModelServerCommandStack) commandStack; }

   public void execute(final Command command) {
      if (commandStack == null) {
         return;
      }

      if (openCompound != null) {
         inWriteTransaction(() -> openCompound.appendAndExecute(command));
      } else {
         getCommandStack().execute(command);
      }
   }

   protected boolean canUndo() {
      return commandStack != null && commandStack.canUndo();
   }

   public Optional<Command> getUndoableCommand() {
      return canUndo() ? Optional.of(commandStack.getUndoCommand()) : Optional.empty();
   }

   public boolean undo() {
      if (canUndo()) {
         commandStack.undo();
         return true;
      }
      return false;
   }

   protected boolean canRedo() {
      return commandStack != null && commandStack.canRedo();
   }

   public Optional<Command> getRedoableCommand() {
      return canRedo() ? Optional.of(commandStack.getRedoCommand()) : Optional.empty();
   }

   public boolean redo() {
      if (canRedo()) {
         commandStack.redo();
         return true;
      }
      return false;
   }

   public boolean isDirty() { return ((ModelServerCommandStack) commandStack).isSaveNeeded(); }

   public void saveIsDone() {
      ((ModelServerCommandStack) commandStack).saveIsDone();
   }

   /**
    * Open a compound command. While the compound is open, all {@linkplain #execute(Command) execution of commands}
    * will be performed immediately and collected in the compound, not added to the stack.
    *
    * @see #closeCompoundCommand()
    */
   public void openCompoundCommand() {
      if (openCompound != null) {
         throw new IllegalStateException("Compound command already open");
      }
      if (commandStack == null) {
         throw new IllegalStateException("No command stack in which to open a compound command");
      }

      openCompound = new OpenCompoundCommand();
   }

   /**
    * Close the currently open compound command, pushing it onto the stack.
    *
    * @see #openCompoundCommand()
    */
   public void closeCompoundCommand() {
      if (openCompound == null) {
         throw new IllegalStateException("No compound command is open");
      }

      CompoundCommand command = openCompound;
      openCompound = null;

      if (!command.canExecute()) {
         throw new IllegalStateException("Compound command is not executable");
      }

      // A compound for broadcast of undo/redo events to subscribers
      Optional<CCommand> cCompound = command.getCommandList().stream()
         .map(ModelServerCommand::getClientCommand)
         .filter(Optional::isPresent).map(Optional::get)
         .reduce(CommandUtil::compose);

      // Don't unwrap the compound because execute must be a no-op, just placing it on the stack
      cCompound.map(compound -> new ModelServerCommand(command, compound))
         .ifPresent(this::execute);
   }

   /**
    * Undo all commands so far collected in the open compound command and discard it.
    *
    * @see #openCompoundCommand()
    */
   public void rollbackCompoundCommand() {
      if (openCompound == null) {
         throw new IllegalStateException("No compound command is open");
      }

      Command command = openCompound;
      openCompound = null;

      try {
         if (command.canUndo()) {
            // This is a transactional editing domain. We need a transaction in which to write
            inWriteTransaction(() -> command.undo());
         }
      } finally {
         command.dispose();
      }
   }

   /**
    * Run an {@code operation} in a write transaction.
    *
    * @param operation an operation to modify the model
    * @throws WrappedException if any exception occurs either in starting or committing the transaction or in running
    *                             the {@code operation}
    */
   @SuppressWarnings("checkstyle:IllegalCatch")
   private void inWriteTransaction(final Runnable operation) {
      try {
         Transaction transaction = startTransaction(false, null);
         try {
            operation.run();
            transaction.commit();
         } catch (Exception e) {
            transaction.rollback();
            throw e;
         }
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new WrappedException(e);
      }
   }

   //
   // Nested types
   //

   /**
    * A specialized compound command for transactions that only supports the
    * {@linkplain #appendAndExecute(Command) append-and-execute} API for composing it.
    */
   final class OpenCompoundCommand extends CompoundCommand {
      OpenCompoundCommand() {
         super();
      }

      @Override
      public void execute() {
         // All commands are already executed
      }

      @Override
      public boolean canExecute() {
         // As all commands are appended only if executable, this is sufficient test
         return !commandList.isEmpty();
      }

      @Override
      public boolean appendAndExecute(final Command command) {
         return super.appendAndExecute(command);
      }

      @Override
      public void append(final Command command) {
         throw new UnsupportedOperationException("Cannot append an unexecuted command");
      }

      @Override
      public boolean appendIfCanExecute(final Command command) {
         throw new UnsupportedOperationException("Cannot append an unexecuted command");
      }
   }

}
