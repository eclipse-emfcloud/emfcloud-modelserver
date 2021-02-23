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
package org.eclipse.emfcloud.modelserver.edit;

import static com.google.common.collect.Iterables.getFirst;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.eclipse.emf.common.notify.Notification.NO_INDEX;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.command.AddCommandCodecContribution;
import org.eclipse.emfcloud.modelserver.edit.command.CompoundCommandCodecContribution;
import org.eclipse.emfcloud.modelserver.edit.command.RemoveCommandCodecContribution;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandCodecContribution;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;

import com.google.common.primitives.Ints;

public class EMFCommandCodec extends DICommandCodec {
   public EMFCommandCodec() {
      super();
      setCommandCodecs(Map.of(
         EMFCommandType.ADD, new AddCommandCodecContribution(),
         EMFCommandType.SET, new SetCommandCodecContribution(),
         EMFCommandType.REMOVE, new RemoveCommandCodecContribution(),
         EMFCommandType.COMPOUND, new CompoundCommandCodecContribution(this)));
   }

   public static AddCommand addCommand(final EditingDomain domain, final CCommand clientCommand) {
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

   public static CCommand clientCommand(final AddCommand command) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.ADD);
      result.setOwner(command.getOwner());
      result.setFeature(command.getFeature().getName());
      result.getIndices().add(command.getIndex());

      if (command.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) command.getFeature()).getEAttributeType();
         CommandUtil.collectCommandValues(command.getCollection(), dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(command.getCollection(), result, true);
      }
      return result;
   }

   public static SetCommand setCommand(final EditingDomain domain, final CCommand command) {
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
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.SET);
      result.setOwner(command.getOwner());
      result.setFeature(command.getFeature().getName());
      result.getIndices().add(command.getIndex());

      if (command.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) command.getFeature()).getEAttributeType();
         CommandUtil.collectCommandValues(singleton(command.getValue()), dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(singleton(command.getValue()), result, true);
      }
      return result;
   }

   public static RemoveCommand removeCommand(final EditingDomain domain, final CCommand command)
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
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(EMFCommandType.REMOVE);
      result.setOwner(command.getOwner());
      result.setFeature(command.getFeature().getName());
      int[] indices = CommandUtil.getIndices(command);
      if (indices != null) {
         result.getIndices().addAll(Ints.asList(indices));
      }

      if (command.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) command.getFeature()).getEAttributeType();
         CommandUtil.collectCommandValues(command.getCollection(), dataType, result);
      } else {
         CommandUtil.collectCommandEObjects(command.getCollection(), result, false);
      }
      return result;
   }
}
