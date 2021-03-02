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

import static com.google.common.collect.Iterables.getFirst;
import static java.util.stream.Collectors.toList;
import static org.eclipse.emf.common.notify.Notification.NO_INDEX;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;

public class AddCommandContribution extends BasicCommandContribution<AddCommand> {

   @Override
   protected AddCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand clientCommand)
      throws DecodingException {
      return serverCommand(domain, clientCommand);
   }

   @Override
   protected CCommand toClient(final AddCommand serverCommand, final CCommand origin) throws EncodingException {
      return clientCommand(serverCommand);
   }

   public static CCommand clientCommand(final AddCommand command) {
      return clientCommand(command.getOwner(), command.getFeature(), command.getIndex(),
         command.getCollection());
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature,
      final Collection<?> toAdd) {
      return clientCommand(owner, feature, CommandParameter.NO_INDEX, toAdd);
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature, final int index,
      final Collection<?> toAdd) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.ADD);
      result.setOwner(owner);
      result.setFeature(feature.getName());
      result.getIndices().add(index);

      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         CommandUtil.collectCommandValues(toAdd, dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(toAdd, result, true);
      }
      return result;
   }

   public static AddCommand serverCommand(final EditingDomain domain, final CCommand clientCommand) {
      EObject owner = clientCommand.getOwner();
      EStructuralFeature feature = owner.eClass().getEStructuralFeature(clientCommand.getFeature());
      Collection<?> values;
      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         values = clientCommand.getDataValues().stream()
            .map(value -> EcoreUtil.createFromString(dataType, value))
            .collect(toList());
      } else {
         values = clientCommand.getObjectValues();
      }
      EList<Integer> indices = clientCommand.getIndices();
      int index = indices.isEmpty() ? NO_INDEX : indices.get(0);
      return (AddCommand) AddCommand.create(domain, owner, feature, values, getFirst(indices, index));
   }
}
