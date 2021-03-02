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

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.command.UpdateModelCommandContribution.UpdateModelCommand;

public class UpdateModelCommandContribution extends BasicCommandContribution<UpdateModelCommand> {
   public static final String TYPE = "updateModel";

   @Override
   protected UpdateModelCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      EObject newRoot = command.getObjectValues().get(0);
      return new UpdateModelCommand(modelUri, domain, newRoot);
   }

   public static CCommand createClientCommand(final EObject newRoot) {
      CCommand command = CCommandFactory.eINSTANCE.createCommand();
      command.getObjectValues().add(newRoot);
      command.setType(TYPE);
      return command;
   }

   public static class UpdateModelCommand extends RecordingCommand {
      private final URI modelUri;
      private final EditingDomain domain;
      private final EObject newRoot;

      public UpdateModelCommand(final URI modelUri, final EditingDomain domain, final EObject newRoot) {
         super((TransactionalEditingDomain) domain);
         this.modelUri = modelUri;
         this.domain = domain;
         this.newRoot = newRoot;
      }

      @Override
      protected void doExecute() {
         Resource resource = domain.getResourceSet().getResource(modelUri, true);
         ECollections.setEList(resource.getContents(), ECollections.singletonEList(newRoot));
      }
   }
}
