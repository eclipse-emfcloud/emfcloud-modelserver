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
import java.util.Optional;

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

}
