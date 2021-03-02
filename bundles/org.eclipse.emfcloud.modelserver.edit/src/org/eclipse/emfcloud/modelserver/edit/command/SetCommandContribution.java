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
import static java.util.Collections.singleton;
import static org.eclipse.emf.common.notify.Notification.NO_INDEX;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;

public class SetCommandContribution extends BasicCommandContribution<SetCommand> {

   @Override
   protected CCommand toClient(final SetCommand command, final CCommand origin) throws EncodingException {
      return clientCommand(command);
   }

   @Override
   protected SetCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      return serverCommand(domain, command);
   }

   public static SetCommand serverCommand(final EditingDomain domain, final CCommand command) {
      EObject owner = command.getOwner();
      EStructuralFeature feature = owner.eClass().getEStructuralFeature(command.getFeature());
      Object value;
      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         value = command.getDataValues().isEmpty() ? null
            : EcoreUtil.createFromString(dataType, command.getDataValues().get(0));
      } else {
         value = getFirst(command.getObjectValues(), null);
      }
      int index = command.getIndices().isEmpty() ? NO_INDEX : command.getIndices().get(0);
      return (SetCommand) SetCommand.create(domain, owner, feature, value, index);
   }

   public static CCommand clientCommand(final SetCommand command) {
      return clientCommand(command.getOwner(), command.getFeature(), command.getIndex(),
         command.getValue());
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature,
      final Object value) {
      return clientCommand(owner, feature, CommandParameter.NO_INDEX, value);
   }

   public static CCommand clientCommand(final EObject owner, final EStructuralFeature feature, final int index,
      final Object value) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.SET);
      result.setOwner(owner);
      result.setFeature(feature.getName());
      result.getIndices().add(index);

      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         CommandUtil.collectCommandValues(singleton(value), dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(singleton(value), result, true);
      }
      return result;
   }
}
