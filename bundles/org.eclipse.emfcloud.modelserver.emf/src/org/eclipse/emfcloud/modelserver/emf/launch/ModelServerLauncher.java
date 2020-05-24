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

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.ModelServerModule;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class ModelServerLauncher {
   private static final Logger LOG = Logger.getLogger(ModelServerLauncher.class.getSimpleName());
   public static final int DEFAULT_JAVALIN_PORT = 8081;
   private static String PROCESS_NAME = "java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar";

   private final Collection<Module> modules;
   private Injector injector;
   private String[] args;
   private ServerConfiguration configuration;

   public static void configureLogger() {
      Logger root = Logger.getRootLogger();
      if (!root.getAllAppenders().hasMoreElements()) {
         root.addAppender(new ConsoleAppender(
            new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
      }
      root.setLevel(Level.DEBUG);

   }

   public ModelServerLauncher() {
      modules = Sets.newHashSet(ModelServerModule.create());

   }

   public ModelServerLauncher(final String[] args) {
      this();
      this.args = args;
   }

   protected Injector doSetup() {
      Injector injector = Guice.createInjector(modules);
      this.configuration = injector.getInstance(ServerConfiguration.class);
      return injector;
   }

   public void start() {
      if (injector == null) {
         injector = doSetup();
      }
      run();
   }

   protected void run() {
      if (parseCLIArguments(args)) {
         injector.getInstance(ModelServerStartup.class).boot(EntryPointType.REST, configuration.getServerPort());
      }
   }

   protected boolean parseCLIArguments(final String[] args) {
      try {
         if (!CLIParser.initialized()) {
            configureCLIParser();
         }
         CLIParser parser = CLIParser.getInstance();

         if (parser.optionExists("h")) {
            CLIParser.getInstance().printHelp();
            return false;

         }

         if (parser.optionExists("e")) {
            Logger.getRootLogger().setLevel(Level.ERROR);
         }
         configuration.setServerPort(parser.parsePort());
         parser.parseWorkspaceRoot().ifPresent(configuration::setWorkspaceRoot);
         parser.parseUISchemaFolder().ifPresent(configuration::setUISchemaFolder);
         return true;
      } catch (UnrecognizedOptionException e) {
         LOG.error("Unrecognized command line argument(s) used!\n");
         CLIParser.printHelp("", CLIParser.getDefaultCLIOptions());
      } catch (ParseException e) {
         LOG.error(e.getMessage(), e);
      }

      return false;
   }

   protected void configureCLIParser() throws ParseException {
      CLIParser.create(args, CLIParser.getDefaultCLIOptions(), PROCESS_NAME);
   }

   public void shutdown() {

   }

   public void addEPackageConfigurations(final Collection<Class<? extends EPackageConfiguration>> configs) {
      this.modules.forEach(m -> {
         ((ModelServerModule) m).addEPackageConfigurations(configs);
      });
   }

   public Collection<? extends Module> getModules() { return modules; }

   public Injector getInjector() { return injector; }
}
