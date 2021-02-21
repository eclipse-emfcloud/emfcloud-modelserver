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
package org.eclipse.emfcloud.modelserver.example;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Machine;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Node;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Task;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.command.BasicCommandCodecContribution;

public class UpdateTaskNameCommandContribution extends BasicCommandCodecContribution<Command> {
   public static final String TYPE = "updateTaskName";

   private static final String PROPERTY_TEXT = "text";

   @Override
   protected CCommand toClient(final Command command, final CCommand origin) throws EncodingException {
      return origin;
   }

   @Override
   protected Command toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      String textToAdd = command.getProperties().get(PROPERTY_TEXT);
      if (textToAdd == null) {
         throw new DecodingException("Missing property '" + PROPERTY_TEXT + "'");
      }
      Resource resource = domain.getResourceSet().getResource(modelUri, false);
      EObject root = resource.getContents().get(0);
      if (!(root instanceof Machine)) {
         throw new DecodingException("Unexpected root '" + root + "'");
      }
      Machine machine = (Machine) root;
      CompoundCommand taskUpdates = new CompoundCommand();
      for (Workflow workflow : machine.getWorkflows()) {
         for (Node node : workflow.getNodes()) {
            if (node instanceof Task) {
               taskUpdates.append(updateTaskName((Task) node, domain, textToAdd));
            }
         }
      }
      return taskUpdates;
   }

   protected Command updateTaskName(final Task task, final EditingDomain domain, final String textToAdd) {
      return new RecordingCommand((TransactionalEditingDomain) domain) {
         @Override
         protected void doExecute() {
            if (task.getName() != null) {
               task.setName(task.getName() + " " + textToAdd);
            } else {
               task.setName(textToAdd);
            }
         }

         @Override
         public Collection<?> getAffectedObjects() { return Collections.singletonList(task); }
      };
   }

}
