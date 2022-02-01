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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.ChangeFactory;
import org.eclipse.emf.ecore.change.ChangePackage;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
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

   public static Optional<CCommandExecutionResult> aggregateExecutionResults(
      final List<? extends CCommandExecutionResult> executionResults) {
      if (executionResults.isEmpty()) {
         return Optional.empty();
      }

      CCommandExecutionResult proto = executionResults.get(0);
      if (executionResults.size() == 1) {
         return Optional.of(proto);
      }

      // Compose the source command
      Optional<CCommand> composed = executionResults.stream().map(CCommandExecutionResult::getSource)
         .reduce(CommandUtil::compose);

      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();

      result.setType(proto.getType());
      composed.ifPresent(result::setSource);
      executionResults.stream().map(CCommandExecutionResult::getAffectedObjects)
         .forEach(result.getAffectedObjects()::addAll);
      executionResults.stream().map(CCommandExecutionResult::getDetails).forEach(result.getDetails()::putAll);

      ChangeDescription changes = ChangeFactory.eINSTANCE.createChangeDescription();
      result.setChangeDescription(changes);
      executionResults.stream().map(CCommandExecutionResult::getChangeDescription)
         .filter(ChangeDescription.class::isInstance).map(ChangeDescription.class::cast).forEach(change -> {
            // It's OK to steal resource changes, objects to attach/detach, and feature changes
            // from the source change descriptions because those source change descriptions
            // will be discarded when we've finished here
            changes.getResourceChanges().addAll(change.getResourceChanges());
            changes.getObjectsToAttach().addAll(change.getObjectsToAttach());
            changes.getObjectsToDetach().addAll(change.getObjectsToDetach());

            change.getObjectChanges().forEach(entry -> {
               if (!changes.getObjectChanges().containsKey(entry.getKey())) {
                  changes.getObjectChanges()
                     .add(ChangeFactory.eINSTANCE.createEObjectToChangesMapEntry(entry.getKey()));
               }
               changes.getObjectChanges().get(entry.getKey()).addAll(entry.getValue());
            });
         });

      return Optional.of(result);
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

      // It's OK to steal resource changes, objects to attach, feature changes,
      // or even entire change descriptions from the source execution results
      // from the source change descriptions because those source execution
      // results will be discarded when we've finished here
      ChangeDescription result = ChangeFactory.eINSTANCE.createChangeDescription();

      result.getResourceChanges().addAll(change1.getResourceChanges());
      result.getResourceChanges().addAll(change2.getResourceChanges());

      result.getObjectsToAttach().addAll(change1.getObjectsToAttach());
      result.getObjectsToAttach().addAll(change2.getObjectsToAttach());

      result.getObjectsToDetach().addAll(change1.getObjectsToDetach());
      result.getObjectsToDetach().addAll(change2.getObjectsToDetach());

      mergeObjectChanges(result, change1);
      mergeObjectChanges(result, change2);

      return result;
   }

   private static ChangeDescription asChangeDescription(final EObject probablyChange) {
      return probablyChange instanceof ChangeDescription ? (ChangeDescription) probablyChange : null;
   }

   private static void mergeObjectChanges(final ChangeDescription dst, final ChangeDescription src) {
      src.getObjectChanges().forEach(entry -> {
         if (!dst.getObjectChanges().containsKey(entry.getKey())) {
            dst.getObjectChanges().add(ChangeFactory.eINSTANCE.createEObjectToChangesMapEntry(entry.getKey()));
         }

         Map<String, FeatureChange> srcFeatureChanges = mapFeatureChanges(entry.getValue());
         Map<String, FeatureChange> dstFeatureChanges = mapFeatureChanges(dst.getObjectChanges().get(entry.getKey()));

         Iterator<Map.Entry<String, FeatureChange>> iter = srcFeatureChanges.entrySet().iterator();
         while (iter.hasNext()) {
            Map.Entry<String, FeatureChange> srcFC = iter.next();
            FeatureChange dstFC = dstFeatureChanges.computeIfAbsent(srcFC.getKey(),
               __ -> {
                  FeatureChange newFC = ChangeFactory.eINSTANCE.createFeatureChange();
                  dst.getObjectChanges().get(entry.getKey()).add(newFC);
                  return newFC;
               });

            mergeFeatureChanges(dstFC, srcFC.getValue());

            iter.remove();
         }
      });
   }

   private static Map<String, FeatureChange> mapFeatureChanges(
      final Collection<? extends FeatureChange> featureChanges) {
      return featureChanges.stream().collect(Collectors.toMap(FeatureChange::getFeatureName, Function.identity(),
         CommandUtil::pickA, LinkedHashMap::new));
   }

   private static <T> T pickA(final T a, final T b) {
      return a;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   private static void mergeFeatureChanges(final FeatureChange dst, final FeatureChange src) {
      if (src.isSet()) {
         dst.setSet(true);
      }

      if (src.isSetFeature()) {
         dst.setFeature(src.getFeature());
      }
      if (src.isSetFeatureName()) {
         dst.setFeatureName(src.getFeatureName());
      }

      // Setting any reference value clears any data value previously set.
      // Always retain the first data/reference value encountered because
      // subsequent values don't matter to ChangeDescription::apply
      if (src.eIsSet(ChangePackage.Literals.FEATURE_CHANGE__DATA_VALUE)
         && !dst.eIsSet(ChangePackage.Literals.FEATURE_CHANGE__DATA_VALUE)) {
         dst.setDataValue(src.getDataValue());
      } else if (src.eIsSet(ChangePackage.Literals.FEATURE_CHANGE__REFERENCE_VALUE)
         && !dst.eIsSet(ChangePackage.Literals.FEATURE_CHANGE__REFERENCE_VALUE)) {
         dst.setReferenceValue(src.getReferenceValue());
      }

      if (src.eIsSet(ChangePackage.Literals.FEATURE_CHANGE__LIST_CHANGES)) {
         dst.getListChanges().addAll(src.getListChanges());
      }
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
