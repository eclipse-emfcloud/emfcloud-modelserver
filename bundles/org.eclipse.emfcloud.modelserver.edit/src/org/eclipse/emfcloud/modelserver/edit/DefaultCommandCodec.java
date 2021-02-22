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

package org.eclipse.emfcloud.modelserver.edit;

import static com.google.common.collect.Iterables.getFirst;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.eclipse.emf.common.notify.Notification.NO_INDEX;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

/**
 * Default implementation of {@link Command} codec.
 */
public class DefaultCommandCodec implements CommandCodec {

   /**
    * Initializes me.
    */
   public DefaultCommandCodec() {
      super();
   }

   @Override
   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS" })
   public CCommand encode(final Command command) throws EncodingException {
      if (command instanceof CompoundCommand) {
         return encodeCompoundCommand(command);
      }
      if (command instanceof AddCommand) {
         return encodeAddCommand((AddCommand) command);
      }
      if (command instanceof RemoveCommand) {
         return encodeRemoveCommand((RemoveCommand) command);
      }
      if (command instanceof SetCommand) {
         return encodeSetCommand((SetCommand) command);
      }
      throw new EncodingException("unsupported command type: " + command.getClass().getName());
   }

   protected CCommand encodeSetCommand(final SetCommand set) {
      // FIXME: Handle the UNSET value (needs model change)
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.SET);
      result.setOwner(set.getOwner());
      result.setFeature(set.getFeature().getName());
      result.getIndices().add(set.getIndex());

      if (set.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) set.getFeature()).getEAttributeType();
         collectCommandValues(singleton(set.getValue()), dataType, result);
      } else {
         collectCommandEObjects(singleton(set.getValue()), result, true);
      }
      return result;
   }

   protected CCommand encodeRemoveCommand(final RemoveCommand remove) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.REMOVE);
      result.setOwner(remove.getOwner());
      result.setFeature(remove.getFeature().getName());
      int[] indices = getIndices(remove);
      if (indices != null) {
         result.getIndices().addAll(Ints.asList(indices));
      }

      if (remove.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) remove.getFeature()).getEAttributeType();
         collectCommandValues(remove.getCollection(), dataType, result);
      } else {
         collectCommandEObjects(remove.getCollection(), result, false);
      }
      return result;
   }

   protected CCommand encodeAddCommand(final AddCommand add) {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.ADD);
      result.setOwner(add.getOwner());
      result.setFeature(add.getFeature().getName());
      result.getIndices().add(add.getIndex());

      if (add.getFeature() instanceof EAttribute) {
         EDataType dataType = ((EAttribute) add.getFeature()).getEAttributeType();
         collectCommandValues(add.getCollection(), dataType, result);
      } else {
         collectCommandEObjects(add.getCollection(), result, true);
      }
      return result;
   }

   protected CCommand encodeCompoundCommand(final Command command) throws EncodingException {
      // TODO: Various special commands are compounds (e.g., Delete).
      CCompoundCommand compound = CCommandFactory.eINSTANCE.createCompoundCommand();
      compound.setType(CommandKind.COMPOUND);

      EList<CCommand> commands = compound.getCommands();
      for (Command next : ((CompoundCommand) command).getCommandList()) {
         commands.add(encode(next));
      }
      return compound;
   }

   /**
    * Get the indices specified for {@code RemoveCommand} or {@code MoveCommand}
    * specified by index and not by value.
    *
    * @param command a remove command or a move command
    * @return the indices, or {@code null} if none
    */
   protected int[] getIndices(final Command command) {
      int[] result = null;

      Collection<?> collection = (command instanceof RemoveCommand) //
         ? ((RemoveCommand) command).getCollection() //
         : (command instanceof MoveCommand) //
            ? Collections.singleton(((MoveCommand) command).getValue())
            : Collections.emptySet();
      if (collection.size() == 1) {
         Object element = Iterables.getOnlyElement(collection);
         if (element instanceof CommandParameter.Indices) {
            result = ((CommandParameter.Indices) element).getIndices();
         }
      }

      return result;
   }

   /**
    * Collect primitive (attribute) {@code values} of a {@link Command} into the
    * {@linkplain CCommand#getDataValues() string values} of the {@code command} model.
    *
    * @param values   attribute values to encode as strings in the model
    * @param dataType the data type of the attribute (and thus its {@code values})
    * @param command  the command model to fill
    */
   protected void collectCommandValues(final Collection<?> values, final EDataType dataType, final CCommand command) {
      values.stream() //
         .map(value -> EcoreUtil.convertToString(dataType, value)) //
         .forEach(command.getDataValues()::add);
   }

   /**
    * Collect (reference) {@code objects} of a {@link Command} into the
    * {@linkplain CCommand#getObjectValues() object values} of the {@code command} model.
    *
    * @param objects referenced objects to include in the model
    * @param command the command model to fill
    * @param adding  whether the {@code command} being constructed has add semantics, in which
    *                   case if the objects being referenced are not yet attached to the user model, they need
    *                   to be attached to the {@code command} model via the
    *                   {@linkplain CCommand#getObjectsToAdd() objects to add} list
    */
   protected void collectCommandEObjects(final Collection<?> objects, final CCommand command, final boolean adding) {
      Stream<EObject> collected = objects.stream() //
         .filter(EObject.class::isInstance).map(EObject.class::cast);
      if (adding) {
         collected = collected.peek(obj -> {
            if (obj.eResource() == null) {
               // It's not contained in the model so we have to include it in this payload
               command.getObjectsToAdd().add(obj);
            }
         });
      }

      collected.forEach(command.getObjectValues()::add);
   }

   @Override
   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS" })
   public Command decode(final EditingDomain domain, final CCommand command) throws DecodingException {
      switch (command.getType()) {
         case COMPOUND:
            return decodeCompoundCommand(domain, (CCompoundCommand) command);
         case ADD: {
            return decodeAddCommand(domain, command);
         }
         case REMOVE: {
            return decodeRemoveCommand(domain, command);
         }
         case SET: {
            return decodeSetCommand(domain, command);
         }
         case MOVE: {
            throw new DecodingException("todo");
         }
         case REPLACE: {
            throw new DecodingException("todo");
         }
         default:
            throw new DecodingException("unsupported command type: " + command.getType().getLiteral());
      }
   }

   protected Command decodeSetCommand(final EditingDomain domain, final CCommand command) {
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
      return SetCommand.create(domain, owner, feature, value, index);
   }

   protected Command decodeRemoveCommand(final EditingDomain domain, final CCommand command) throws DecodingException {
      EObject owner = command.getOwner();
      EStructuralFeature feature = owner.eClass().getEStructuralFeature(command.getFeature());
      if (!command.getIndices().isEmpty()) {
         return RemoveCommand.create(domain, owner, feature, Ints.toArray(command.getIndices()));
      }
      if (!command.getObjectValues().isEmpty()) {
         return RemoveCommand.create(domain, owner, feature, command.getObjectValues());
      }
      if (!command.getDataValues().isEmpty()) {
         return RemoveCommand.create(domain, owner, feature, command.getDataValues());
      }
      throw new DecodingException("incomplete remove command specification");
   }

   protected Command decodeAddCommand(final EditingDomain domain, final CCommand command) {
      EObject owner = command.getOwner();
      EStructuralFeature feature = owner.eClass().getEStructuralFeature(command.getFeature());
      Collection<?> values;
      if (feature instanceof EAttribute) {
         EDataType dataType = ((EAttribute) feature).getEAttributeType();
         values = command.getDataValues().stream() //
            .map(value -> EcoreUtil.createFromString(dataType, value)).collect(toList());
      } else {
         values = command.getObjectValues();
      }
      int index = command.getIndices().isEmpty() ? NO_INDEX : command.getIndices().get(0);
      return AddCommand.create(domain, owner, feature, values, getFirst(command.getIndices(), index));
   }

   protected Command decodeCompoundCommand(final EditingDomain domain, final CCompoundCommand ccompound)
      throws DecodingException {
      CompoundCommand compound = new CompoundCommand();
      for (CCommand next : ccompound.getCommands()) {
         compound.append(decode(domain, next));
      }
      return compound;
   }

}
