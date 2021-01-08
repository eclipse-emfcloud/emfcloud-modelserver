/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;

public class ModelServerEditingDomain extends AdapterFactoryEditingDomain {

   public ModelServerEditingDomain(final AdapterFactory adapterFactory, final ResourceSet resourceSet) {
      super(adapterFactory, new ModelServerCommandStack(), resourceSet);
   }

   @Override
   public ModelServerCommandStack getCommandStack() { return (ModelServerCommandStack) commandStack; }

   public void execute(final Command command) {
      if (commandStack == null) {
         return;
      }
      commandStack.execute(command);
   }

   protected boolean canUndo() {
      if (commandStack == null) {
         return false;
      }
      return commandStack.canUndo();
   }

   protected boolean canRedo() {
      if (commandStack == null) {
         return false;
      }
      return commandStack.canRedo();
   }

   public Command getUndoCommand() {
      /*
       * The undoCommand represents the executed command on the command stack that will be undone.
       * The BasicCommandStack has its own internal logic on how to undo a certain command.
       * As our clients will most likely not make use of any concept similar to a command stack, it is necessary
       * to provide an update which holds the command to undo the previous changes as incrementalUpdate.
       * Therefore we need to invert the command, which is currently based on the already implemented commands
       * in the DefaultCommandCodec.
       */
      if (canUndo()) {
         return createInverseCommand(commandStack.getUndoCommand());
      }
      return null;
   }

   public boolean undo() {
      if (canUndo()) {
         commandStack.undo();
         return true;
      }
      return false;
   }

   public Command getRedoCommand() {
      if (canRedo()) {
         return commandStack.getRedoCommand();
      }
      return null;
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

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   protected Command createInverseCommand(final Command undoCommand) {
      if (undoCommand instanceof CompoundCommand) {
         CompoundCommand undoCompoundCommand = (CompoundCommand) undoCommand;

         CompoundCommand inverseCompoundCommand = new CompoundCommand();
         for (Command next : undoCompoundCommand.getCommandList()) {
            inverseCompoundCommand.append(createInverseCommand(next));
         }
         return inverseCompoundCommand;

      } else if (undoCommand instanceof AddCommand) {
         AddCommand undoAddCommand = (AddCommand) undoCommand;
         return RemoveCommand.create(undoAddCommand.getDomain(), undoAddCommand.getOwner(),
            undoAddCommand.getFeature(), undoAddCommand.getResult());

      } else if (undoCommand instanceof RemoveCommand) {
         RemoveCommand undoRemoveCommand = (RemoveCommand) undoCommand;
         return AddCommand.create(undoRemoveCommand.getDomain(), undoRemoveCommand.getOwner(),
            undoRemoveCommand.getFeature(), undoRemoveCommand.getResult(), undoRemoveCommand.getIndices()[0]);
      } else if (undoCommand instanceof SetCommand) {
         // FIXME: Handle the UNSET value, see also DefaultCommandCodec where it is also not yet implemented
         SetCommand undoSetCommand = (SetCommand) undoCommand;
         return SetCommand.create(undoSetCommand.getDomain(), undoSetCommand.getOwner(),
            undoSetCommand.getFeature(), undoSetCommand.getOldValue(), undoSetCommand.getIndex());
      } else if (undoCommand instanceof ReplaceCommand) {
         // TODO see also DefaultCommandCodec
      } else if (undoCommand instanceof MoveCommand) {
         // TODO see also DefaultCommandCodec
      }
      return undoCommand;
   }

}
