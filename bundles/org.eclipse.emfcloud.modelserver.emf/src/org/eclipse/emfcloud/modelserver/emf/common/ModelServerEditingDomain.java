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
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;

public class ModelServerEditingDomain extends AdapterFactoryEditingDomain {

   public ModelServerEditingDomain(final AdapterFactory adapterFactory, final ResourceSet resourceSet) {
      super(adapterFactory, new ModelServerCommandStack(), resourceSet);
   }

   @Override
   public ModelServerCommandStack getCommandStack() { return (ModelServerCommandStack)commandStack; }

   public void execute(final Command command) {
      if (commandStack == null) {
         return;
      }
      commandStack.execute(command);
   }

   public boolean canUndo() {
      if (commandStack == null) {
         return false;
      }
      return commandStack.canUndo();
   }

   public boolean canRedo() {
      if (commandStack == null) {
         return false;
      }
      return commandStack.canRedo();
   }

   public void undo() {
      if (commandStack == null) {
         return;
      }
      commandStack.undo();
   }

   public void redo() {
      if (commandStack == null) {
         return;
      }
      commandStack.redo();
   }

   public boolean isDirty() { return ((ModelServerCommandStack)commandStack).isSaveNeeded(); }

   public void saveIsDone() {
      ((ModelServerCommandStack)commandStack).saveIsDone();
   }

}
