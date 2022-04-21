/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.edit.util;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.transaction.util.CompositeChangeDescription;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;

import com.google.common.collect.Iterables;

public final class CommandUtil {
   private CommandUtil() {}

   /**
    * Collect primitive (attribute) {@code values} of a {@link Command} into the
    * {@linkplain CCommand#getDataValues() string values} of the {@code command} model.
    *
    * @param values   attribute values to encode as strings in the model
    * @param dataType the data type of the attribute (and thus its {@code values})
    * @param command  the command model to fill
    */
   public static void collectCommandValues(final Collection<?> values, final EDataType dataType,
      final CCommand command) {
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
   public static void collectCommandEObjects(final Collection<?> objects, final CCommand command,
      final boolean adding) {
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

   /**
    * Get the indices specified for {@code RemoveCommand} or {@code MoveCommand}
    * specified by index and not by value.
    *
    * @param command a remove command or a move command
    * @return the indices, or {@code null} if none
    */
   public static int[] getIndices(final Command command) {
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

   public static EObject createProxy(final EClass eClass, final String uri) {
      EObject eObject = EcoreUtil.create(eClass);
      ((InternalEObject) eObject).eSetProxyURI(URI.createURI(uri));
      return eObject;
   }

   public static CCommandExecutionResult compose(final CCommandExecutionResult result1,
      final CCommandExecutionResult result2) {
      if (result1 == null) {
         return result2;
      }
      if (result2 == null) {
         return result1;
      }

      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();

      result.setType(result1.getType());
      result.setSource(compose(result1.getSource(), result2.getSource()));

      result.getAffectedObjects().addAll(result1.getAffectedObjects());
      result.getAffectedObjects().addAll(result2.getAffectedObjects());

      result.getDetails().putAll(result1.getDetails());
      result.getDetails().putAll(result2.getDetails());

      result.setChangeDescription(compose(asChangeDescription(result1.getChangeDescription()),
         asChangeDescription(result2.getChangeDescription())));

      return result;
   }

   private static ChangeDescription compose(final ChangeDescription change1, final ChangeDescription change2) {
      if (change1 == null) {
         return change2;
      }
      if (change2 == null) {
         return change1;
      }

      CompositeChangeDescription result = new CompositeChangeDescription();
      result.add(change1);
      result.add(change2);

      return result;
   }

   private static ChangeDescription asChangeDescription(final EObject probablyChange) {
      return probablyChange instanceof ChangeDescription ? (ChangeDescription) probablyChange : null;
   }

   public static CCommand compose(final CCommand command1, final CCommand command2) {
      if (command1 == null) {
         return command2;
      }
      if (command2 == null) {
         return command1;
      }

      CCompoundCommand result = CCommandFactory.eINSTANCE.createCompoundCommand();
      result.setType(EMFCommandType.COMPOUND);

      if (command1 instanceof CCompoundCommand) {
         result.getCommands().addAll(((CCompoundCommand) command1).getCommands());
      } else {
         result.getCommands().add(command1);
      }
      if (command2 instanceof CCompoundCommand) {
         result.getCommands().addAll(((CCompoundCommand) command2).getCommands());
      } else {
         result.getCommands().add(command2);
      }

      return result;
   }

}
