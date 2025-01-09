/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.example.util;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.jsonpatch.Value;
import org.eclipse.emfcloud.modelserver.jsonpatch.util.JsonPatchSwitch;

public final class PrintUtil {

   private PrintUtil() {
      super();
   }

   public static String toString(final EObject object, final boolean pretty) {
      Prettier prettier = pretty ? new Pretty() : new Compact();

      PrinterSwitch printer = new PrinterSwitch(prettier);

      return printer.print(object);
   }

   public static String toString(final EObject object) {
      return toString(object, false);
   }

   public static String toPrettyString(final EObject object) {
      return toString(object, true);
   }

   //
   // Nested types
   //

   private interface Prettier {
      default void beginObject(final StringBuilder builder, final String header) {
         print(builder, header);
      }

      default void endObject(final StringBuilder builder, final String footer) {
         print(builder, footer);
      }

      void print(StringBuilder builder, String line);
   }

   static final class Compact implements Prettier {
      @Override
      public void print(final StringBuilder builder, final String line) {
         builder.append(line);
      }
   }

   static final class Pretty implements Prettier {
      private int depth;

      @Override
      public void beginObject(final StringBuilder builder, final String header) {
         Prettier.super.beginObject(builder, header);
         depth = depth + 1;
      }

      @Override
      public void endObject(final StringBuilder builder, final String footer) {
         depth = depth - 1;
         Prettier.super.endObject(builder, footer);
      }

      @Override
      public void print(final StringBuilder builder, final String line) {
         for (int i = 0; i < depth; i++) {
            builder.append("  ");
         }
         builder.append(line);
         builder.append(System.lineSeparator());
      }
   }

   static final class PrinterSwitch extends JsonPatchSwitch<StringBuilder> {
      private final StringBuilder result = new StringBuilder();
      private final Prettier prettier;

      PrinterSwitch(final Prettier prettier) {
         super();

         this.prettier = prettier;
      }

      String print(final EObject object) {
         doSwitch(object);
         return result.toString();
      }

      private void print(final String line) {
         prettier.print(result, line);
      }

      private void beginObject(final String header) {
         prettier.beginObject(result, header);
      }

      private void endObject(final String footer) {
         prettier.endObject(result, footer);
      }

      private void recurse(final EObject object) {
         object.eContents().forEach(this::doSwitch);
      }

      @Override
      public StringBuilder defaultCase(final EObject object) {
         beginObject(object.eClass().getName() + " {");

         for (EAttribute attr : object.eClass().getEAllAttributes()) {
            Object value = object.eGet(attr);
            if (attr.isMany()) {
               Stream<String> printableValues = ((Collection<?>) value).stream()
                  .map(v -> EcoreUtil.convertToString(attr.getEAttributeType(), v));
               print(String.format("%s: %s", attr.getName(),
                  printableValues.collect(Collectors.toList()).toString()));
            } else {
               print(String.format("%s: %s", attr.getName(),
                  EcoreUtil.convertToString(attr.getEAttributeType(), value)));
            }
         }

         recurse(object);

         endObject("}");

         return result;
      }

      @Override
      public StringBuilder caseValue(final Value object) {
         print(String.format("value: %s", object.stringValue()));
         return result;
      }
   }
}
