/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.launch;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.di.ModelServerModule;

import com.google.inject.Injector;

public class CLIBasedModelServerLauncher extends ModelServerLauncher {

   protected static final Logger LOG = LogManager.getLogger(CLIBasedModelServerLauncher.class);

   protected final CLIParser parser;

   public CLIBasedModelServerLauncher(final CLIParser parser, final ModelServerModule modelServerModule) {
      super(modelServerModule);
      this.parser = parser;
   }

   @Override
   public void run() {
      try {
         if (parser.optionExists(CLIParser.OPTION_HELP)) {
            parser.printHelp();
            return;
         }
         super.run(parser.parseLogConfigurationPath(), parser.optionExists(CLIParser.OPTION_ENABLE_DEV_LOGGING));
      } catch (UnrecognizedOptionException e) {
         LOG.error("Unrecognized command line argument(s) used!\n");
         parser.printHelp();
         return;
      } catch (ParseException e) {
         LOG.error(e.getMessage(), e);
         parser.printHelp();
         return;
      }
   }

   @Override
   protected ServerConfiguration getServerConfiguration(final Injector injector) {
      ServerConfiguration configuration = super.getServerConfiguration(injector);
      return !configure(configuration) ? null : configuration;
   }

   protected boolean configure(final ServerConfiguration configuration) {
      try {
         if (configuration != null) {
            configuration.setServerPort(parser.parsePort());
            parser.parseWorkspaceRoot().ifPresent(configuration::setWorkspaceRoot);
            parser.parseUiSchemaFolder().ifPresent(configuration::setUiSchemaFolder);
            return true;
         }
      } catch (UnrecognizedOptionException e) {
         LOG.error("Unrecognized command line argument(s) used!\n");
         parser.printHelp();
      } catch (ParseException e) {
         LOG.error(e.getMessage(), e);
      }
      return false;
   }
}
