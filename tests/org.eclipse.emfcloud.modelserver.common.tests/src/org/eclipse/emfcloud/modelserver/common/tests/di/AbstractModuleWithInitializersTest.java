/********************************************************************************
 * Copyright (c) 2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common.tests.di;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.eclipse.emfcloud.modelserver.common.di.AbstractModuleWithInitializers;
import org.eclipse.emfcloud.modelserver.common.di.Initialize;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.ProvisionException;

public class AbstractModuleWithInitializersTest {

   public AbstractModuleWithInitializersTest() {
      super();
   }

   @Test
   public void basicInitializer() {
      inject(InitializeMe.class).verify();
   }

   @Test
   public void inheritedInitializer() {
      inject(SubInitializeMe.class).verify();
   }

   @Test
   public void overriddenInitializer() {
      inject(OverrideMe.class).verify();
   }

   @Test
   public void inheritedInitializerNotReannotated() {
      inject(OverrideMe2.class).verify();
   }

   @Test(expected = ProvisionException.class)
   public void tooManyInitializers() {
      inject(Bomb.class);
   }

   //
   // Test framework
   //

   InjectMe inject(final Class<? extends InjectMe> binding) {
      Module module = new AbstractModuleWithInitializers() {
         @Override
         protected void configure() {
            super.configure();

            bind(InjectMe.class).to(binding);
         }
      };

      return Guice.createInjector(module).getInstance(InjectMe.class);
   }

   //
   // Test classes
   //

   public static final class Dependency {
      // Empty
   }

   public interface InjectMe {
      void verify();
   }

   public static class InitializeMe implements InjectMe {
      @Inject
      private Dependency dependency;

      private boolean initialized;

      @Initialize
      void init() {
         assertThat("Dependency not injected yet", dependency, notNullValue());
         initialized = true;
      }

      @Override
      public void verify() {
         assertThat("Not initialized by Guice", initialized, is(true));
      }
   }

   public static class SubInitializeMe extends InitializeMe {
      // Empty
   }

   public static class OverrideMe extends InitializeMe {
      @Override
      @Initialize
      void init() {
         super.init();
      }
   }

   public static class OverrideMe2 extends OverrideMe {
      @Override
      void init() {
         super.init();
      }
   }

   public static class Bomb extends InitializeMe {
      @Initialize
      void tooManyInitializers() {
         fail("Should not be called");
      }
   }

}
