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
package org.eclipse.emfcloud.modelserver.emf.launch;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.ModelServerModule;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class ModelServerLauncher implements Runnable {
   protected static final Logger LOG = Logger.getLogger(ModelServerLauncher.class.getSimpleName());

   static {
      configureLogger();
   }

   protected final Set<Module> modules;
   protected Injector injector;

   public ModelServerLauncher(final ModelServerModule modelServerModule) {
      this.modules = Sets.newHashSet(modelServerModule);
   }

   public Set<Module> getModules() { return modules; }

   public void addModule(final Module module) {
      if (injector != null) {
         throw new IllegalStateException("Launcher was already started, module is not part of the launched server.");
      }
      this.modules.add(module);
   }

   public Injector getInjector() {
      if (injector == null) {
         injector = createInjector();
      }
      return injector;
   }

   @Override
   public void run() {
      Injector runInjector = getInjector();

      ServerConfiguration configuration = getServerConfiguration(runInjector);
      if (configuration == null) {
         return;
      }

      ModelServerStartup startup = getModelServerStartup(runInjector);
      if (startup == null) {
         return;
      }

      doRun(startup, configuration);
   }

   protected void doRun(final ModelServerStartup startup, final ServerConfiguration configuration) {
      startup.start(EntryPointType.REST, configuration.getServerPort());
   }

   protected Injector createInjector() {
      return Guice.createInjector(modules);
   }

   protected ServerConfiguration getServerConfiguration(final Injector injector) {
      return injector.getInstance(ServerConfiguration.class);
   }

   protected ModelServerStartup getModelServerStartup(final Injector injector) {
      return injector.getInstance(ModelServerStartup.class);
   }

   /**
    * This method should no longer be used and will be removed in future versions. You can customize the
    * configurations in the module server module with <code>configureEPackages</code>.
    *
    * @param configs configurations
    */
   @Deprecated
   public void addEPackageConfigurations(final Collection<Class<? extends EPackageConfiguration>> configs) {
      addModule(new AbstractModule() {
         @Override
         protected void configure() {
            Multibinder<EPackageConfiguration> binder = Multibinder.newSetBinder(binder(), EPackageConfiguration.class);
            configs.forEach(binder.addBinding()::to);
         }
      });
   }

   public static void configureLogger() {
      Logger root = Logger.getRootLogger();
      if (!root.getAllAppenders().hasMoreElements()) {
         root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
      }
      root.setLevel(Level.DEBUG);
   }
}
