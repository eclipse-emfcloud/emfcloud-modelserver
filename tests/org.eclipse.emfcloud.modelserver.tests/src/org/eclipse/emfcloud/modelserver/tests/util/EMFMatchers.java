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
package org.eclipse.emfcloud.modelserver.tests.util;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * Hamcrest matchers for EMF objects.
 */
public final class EMFMatchers {

   /**
    * Not instantiable by clients.
    */
   private EMFMatchers() {
      super();
   }

   public static <T extends EObject> Matcher<T> eEqualTo(final T expected) {
      return new CustomTypeSafeMatcher<>("structurally equal to " + expected.eClass().getName()) {
         @Override
         protected boolean matchesSafely(final EObject item) {
            return EcoreUtil.equals(item, expected);
         }
      };
   }

   /**
    * Test structural equality of an object with the given {@code expected} shape, {@code except} for
    * one or more features that are ignored by the comparison.
    */
   public static <T extends EObject> Matcher<T> eEqualTo(final T expected, final EStructuralFeature except,
      final EStructuralFeature... more) {

      Set<EStructuralFeature> exceptedFeatures = new HashSet<>();
      exceptedFeatures.add(except);
      exceptedFeatures.addAll(List.of(more));

      @SuppressWarnings("serial")
      EcoreUtil.EqualityHelper helper = new EcoreUtil.EqualityHelper() {
         @Override
         protected boolean haveEqualFeature(final EObject eObject1, final EObject eObject2,
            final EStructuralFeature feature) {
            return exceptedFeatures.contains(feature) || super.haveEqualFeature(eObject1, eObject2, feature);
         }
      };

      return new CustomTypeSafeMatcher<>("structurally equal to " + expected.eClass().getName()) {
         @Override
         protected boolean matchesSafely(final EObject item) {
            return helper.equals(item, expected);
         }
      };
   }

   @SuppressWarnings({ "checkstyle:CyclomaticComplexity" })
   private static boolean eEquals(final Object actual, final Object expected) {
      if (expected instanceof Collection<?> && actual instanceof Collection<?>) {
         Collection<?> expectedCollection = (Collection<?>) expected;
         Collection<?> actualCollection = (Collection<?>) actual;
         if (expectedCollection.size() != actualCollection.size()) {
            return false;
         }
         Iterator<?> expecteds = expectedCollection.iterator();
         Iterator<?> actuals = actualCollection.iterator();
         while (expecteds.hasNext()) {
            if (!eEquals(actuals.next(), expecteds.next())) {
               return false;
            }
            return true;
         }
      } else if (expected instanceof EObject && actual instanceof EObject) {
         return EcoreUtil.equals((EObject) actual, (EObject) expected);
      }
      return Objects.deepEquals(actual, expected);
   }

   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:AnonInnerLength" })
   public static Matcher<Command> commandEqualTo(final Command expected) {
      return new CustomTypeSafeMatcher<>("equivalent to " + expected.getClass().getSimpleName()) {
         @Override
         protected boolean matchesSafely(final Command item) {
            if (expected instanceof CompoundCommand) {
               if (!(item instanceof CompoundCommand)) {
                  return false;
               }
            } else if (item.getClass() != expected.getClass()) {
               return false;
            }

            if (item instanceof SetCommand) {
               SetCommand set = (SetCommand) item;
               SetCommand expectedSet = (SetCommand) expected;

               return set.getDomain() == expectedSet.getDomain() //
                  && set.getFeature() == expectedSet.getFeature() //
                  && set.getOwner() == expectedSet.getOwner() //
                  && set.getIndex() == expectedSet.getIndex() //
                  && eEquals(set.getValue(), expectedSet.getValue());
            } else if (item instanceof AddCommand) {
               AddCommand add = (AddCommand) item;
               AddCommand expectedAdd = (AddCommand) expected;

               return add.getDomain() == expectedAdd.getDomain() //
                  && add.getFeature() == expectedAdd.getFeature() //
                  && add.getOwner() == expectedAdd.getOwner() //
                  && add.getIndex() == expectedAdd.getIndex() //
                  && eEquals(add.getCollection(), expectedAdd.getCollection());
            } else if (item instanceof RemoveCommand) {
               RemoveCommand remove = (RemoveCommand) item;
               RemoveCommand expectedRemove = (RemoveCommand) expected;

               return remove.getDomain() == expectedRemove.getDomain() //
                  && remove.getFeature() == expectedRemove.getFeature() //
                  && remove.getOwner() == expectedRemove.getOwner() //
                  && Objects.deepEquals(remove.getIndices(), expectedRemove.getIndices()) //
                  && eEquals(remove.getCollection(), expectedRemove.getCollection());
            } else if (item instanceof CompoundCommand) {
               CompoundCommand compound = (CompoundCommand) item;
               CompoundCommand expectedCompound = (CompoundCommand) expected;

               if (compound.getCommandList().size() != expectedCompound.getCommandList().size()) {
                  return false;
               }

               Iterator<Command> commands = compound.getCommandList().iterator();
               Iterator<Command> expecteds = expectedCompound.getCommandList().iterator();
               while (commands.hasNext()) {
                  if (!commandEqualTo(expecteds.next()).matches(commands.next())) {
                     return false;
                  }
               }

               return true;
            }

            return false;
         }
      };
   }

   public static Matcher<Object> eNamedElement(final Class<? extends ENamedElement> expectedType, final String name) {
      return eNamedElement(expectedType, is(name));
   }

   public static Matcher<Object> eNamedElement(final Class<? extends ENamedElement> expectedType,
      final Matcher<? super String> nameMatcher) {
      Matcher<Object> typeFeature = instanceOf(expectedType);
      @SuppressWarnings({ "unchecked", "rawtypes" })
      Matcher<Object> nameFeature = (Matcher) new FeatureMatcher<ENamedElement, String>(nameMatcher, "name", "name") {
         @Override
         protected String featureValueOf(final ENamedElement actual) {
            return actual.getName();
         }
      };

      return both(typeFeature).and(nameFeature);
   }

   public static Matcher<Object> eObjectOfClass(final String eClassName) {
      return new CustomTypeSafeMatcher<>("EObject of class " + eClassName) {
         @Override
         protected boolean matchesSafely(final Object item) {
            EClass eClass = (item instanceof EObject) ? ((EObject) item).eClass() : null;
            return eClass != null && Objects.equals(eClass.getName(), eClassName);
         }
      };

   }

}
