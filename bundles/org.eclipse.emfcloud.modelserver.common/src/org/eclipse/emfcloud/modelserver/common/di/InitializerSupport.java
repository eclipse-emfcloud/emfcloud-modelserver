/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common.di;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.inject.ConfigurationException;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Support for installation of the Guice hooks for post-injection object initialization
 * via {@link Initialize @Initialize} methods.
 */
public final class InitializerSupport implements TypeListener {

   private final InjectionListener<Object> listener = this::afterInjection;

   private final Map<Class<?>, Initializer> initializers = new HashMap<>();

   public InitializerSupport() {
      super();
   }

   @Override
   public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
      encounter.register(listener);
   }

   private void afterInjection(final Object injectee) {
      Initializer initializer = getInitializer(injectee);
      if (initializer != null) {
         initializer.initialize(injectee);
      }
   }

   private Initializer getInitializer(final Object object) {
      return getInitializerForClass(object.getClass());
   }

   private Initializer getInitializerForClass(final Class<?> clazz) {
      if (clazz == null) {
         // Object has no superclass
         return null;
      }

      Initializer result = initializers.get(clazz);
      if (result == null) {
         result = createInitializer(clazz);
         if (result == null) {
            result = Initializer.NULL;
         }
         initializers.put(clazz, result);
      }

      return result == Initializer.NULL ? null : result;
   }

   private Initializer createInitializer(final Class<?> clazz) {
      Initializer baseInitializer = getInitializerForClass(clazz.getSuperclass());

      Method initializeMethod = findInitializeMethod(clazz);
      if (initializeMethod == null || (baseInitializer != null && baseInitializer.overriddenBy(initializeMethod))) {
         // Overriding the method and maintaining the annotation? Superclass initializer will suffice
         return baseInitializer;
      }

      return new Initializer(initializeMethod).after(baseInitializer);
   }

   private Method findInitializeMethod(final Class<?> clazz) {
      Method result = null;
      List<Message> messages = new ArrayList<>(2);
      boolean multipleMessage = false;

      for (Method method : clazz.getDeclaredMethods()) {
         if (method.isAnnotationPresent(Initialize.class)) {
            if (result != null) {
               if (!multipleMessage) {
                  messages.add(new Message(clazz,
                     "Multiple methods annotated with @Initialize in class " + clazz.getCanonicalName()));
                  multipleMessage = true;
               }
            } else {
               result = method;
            }

            if (method.getParameterCount() > 0) {
               messages.add(
                  new Message(method, "@Initialize method " + method.getName() + "must not have parameters in class "
                     + clazz.getCanonicalName()));
            }
         }
      }

      if (!messages.isEmpty()) {
         throw new ConfigurationException(messages);
      }

      return result;
   }

   //
   // Nested types
   //

   private static final class Initializer {
      static final Initializer NULL = new Initializer(null);

      private final Method initializeMethod;
      private Initializer before;

      Initializer(final Method initializeMethod) {
         super();

         this.initializeMethod = initializeMethod;
      }

      @SuppressWarnings("checkstyle:IllegalCatch")
      void initialize(final Object target) {
         try {
            if (before != null) {
               before.initialize(target);
            }

            doInitialize(target);
         } catch (ProvisionException e) {
            throw e;
         } catch (IllegalAccessException e) {
            throw new ProvisionException("Inaccessible @Initialize method", e);
         } catch (IllegalArgumentException e) {
            throw new ProvisionException("Invalid @Initialize method", e);
         } catch (InvocationTargetException | RuntimeException e) {
            throw new ProvisionException("@Initialize method failed with an exception", e);
         }
      }

      private void doInitialize(final Object target)
         throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
         if (initializeMethod != null && !initializeMethod.canAccess(target)) {
            initializeMethod.setAccessible(true);
         }

         initializeMethod.invoke(target);
      }

      Initializer after(final Initializer before) {
         if (before != NULL) {
            this.before = before;
         }
         return this;
      }

      boolean overriddenBy(final Method subclassMethod) {
         return initializeMethod != null && subclassMethod.getName().equals(initializeMethod.getName())
            && canOverride(subclassMethod, initializeMethod);
      }

      private static boolean canOverride(final Method subclassMethod, final Method superclassMethod) {
         int subclassModifiers = subclassMethod.getModifiers();
         int superclassModifiers = superclassMethod.getModifiers();

         if (Modifier.isPrivate(superclassModifiers) || Modifier.isPrivate(subclassModifiers)) {
            // Private methods cannot be involved in overrides
            return false;
         }

         if (Modifier.isPublic(subclassModifiers)) {
            // Public methods always override if they can
            return true;
         }

         if (Modifier.isProtected(superclassModifiers) && Modifier.isPrivate(subclassModifiers)) {
            return true;
         }

         return Objects.equals(subclassMethod.getDeclaringClass().getPackage(),
            superclassMethod.getDeclaringClass().getPackage());
      }
   }
}
