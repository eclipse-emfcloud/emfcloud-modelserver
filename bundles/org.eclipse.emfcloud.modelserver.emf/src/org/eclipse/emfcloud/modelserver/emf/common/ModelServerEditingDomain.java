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

import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;

public class ModelServerEditingDomain extends TransactionalEditingDomainImpl {

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
      getCommandStack().execute(command);
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
       * As we manage the command stack locally, and our clients will most likely not make use of any concept similar to
       * a command stack, it is necessary to provide an update which holds the command to undo the previous changes as
       * an incrementalUpdate.
       */
      if (canUndo()) {
         return commandStack.getUndoCommand();
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
      /*
       * As we manage the command stack locally, and our clients will most likely not make use of any concept similar to
       * a command stack, it is necessary to provide an update which holds the command to redo the previous changes as
       * an incrementalUpdate.
       */
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

}
