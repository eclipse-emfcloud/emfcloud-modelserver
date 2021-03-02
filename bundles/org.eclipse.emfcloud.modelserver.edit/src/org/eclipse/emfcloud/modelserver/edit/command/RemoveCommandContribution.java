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

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;

import com.google.common.primitives.Ints;

public class RemoveCommandContribution extends BasicCommandContribution<RemoveCommand> {

   @Override
   protected CCommand toClient(final RemoveCommand command, final CCommand origin) throws EncodingException {
      return clientCommand(command);
   }

   @Override
   protected RemoveCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      return serverCommand(domain, command);
   }

   public static RemoveCommand serverCommand(final EditingDomain domain, final CCommand command)
      throws DecodingException {
      EObject owner = command.getOwner();
      EStructuralFeature feature = owner.eClass().getEStructuralFeature(command.getFeature());
      if (!command.getIndices().isEmpty()) {
         return (RemoveCommand) RemoveCommand.create(domain, owner, feature, Ints.toArray(command.getIndices()));
      }
      if (!command.getObjectValues().isEmpty()) {
         return (RemoveCommand) RemoveCommand.create(domain, owner, feature, command.getObjectValues());
      }
      if (!command.getDataValues().isEmpty()) {
         return (RemoveCommand) RemoveCommand.create(domain, owner, feature, command.getDataValues());
      }
      throw new DecodingException("incomplete remove command specification");
   }

   public static CCommand clientCommand(final RemoveCommand command) {
      return clientCommand(command.getOwner(), command.getFeature(), CommandUtil.getIndices(command),
         command.getCollection());
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature,
      final Collection<?> toRemove) {
      return clientCommand(owner, feature, null, toRemove);
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature, final int[] indices,
      final Collection<?> toRemove) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.REMOVE);
      result.setOwner(owner);
      result.setFeature(feature.getName());
      if (indices != null) {
         result.getIndices().addAll(Ints.asList(indices));
      }

      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         CommandUtil.collectCommandValues(toRemove, dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(toRemove, result, false);
      }
      return result;
   }
}
