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

import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

public final class CLIParser {
   private static CLIParser INSTANCE;
   private final CommandLine cmd;
   private final Options options;
   private final String processName;

   private CLIParser(final String[] args, final Options options, final String processName) throws ParseException {
      this.cmd = new DefaultParser().parse(options, args);
      this.options = options;
      this.processName = processName;
   }

   public static void create(final String[] args, final Options options, final String processName)
      throws ParseException {
      INSTANCE = new CLIParser(args, options, processName);
   }

   public static CLIParser getInstance() { return INSTANCE; }

   public static boolean initialized() {
      return INSTANCE != null;
   }

   public boolean optionExists(final String identifier) {
      return cmd.hasOption(identifier);
   }

   /**
    * Parses and validates the port argument.
    *
    * @return the parsed port argument if present, default port otherwise
    * @throws ParseException is thrown if the parsed argument is not a valid port
    */
   public Integer parsePort() throws ParseException {
      String portArg = cmd.getOptionValue("p");
      int port = ModelServerLauncher.DEFAULT_JAVALIN_PORT;
      if (portArg != null) {
         try {
            port = Integer.parseInt(portArg);
            if (!ServerConfiguration.isValidPort(port)) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException e) {
            throw new ParseException(String.format("'%s' is not a valid port! The default port '%s' is used",
               portArg, ModelServerLauncher.DEFAULT_JAVALIN_PORT));
         }
      }

      return port;
   }

   public Optional<String> parseWorkspaceRoot() throws ParseException {
      String rootArg = cmd.getOptionValue("r");
      if (rootArg != null) {
         if (!ServerConfiguration.isValidFileURI(rootArg)) {
            throw new ParseException(String.format("Could not set workspace! The path '%s' is invalid.", rootArg));
         }
         return Optional.of(rootArg);
      }
      return Optional.empty();
   }

   public Optional<String> parseUISchemaFolder() throws ParseException {
      String uiSchemaFolderArg = cmd.getOptionValue("u");
      if (uiSchemaFolderArg != null) {
         if (!ServerConfiguration.isValidFileURI(uiSchemaFolderArg)) {
            throw new ParseException(
               String.format("Could not set UI schema folder! The path '%s' is invalid.", uiSchemaFolderArg));
         }
         return Optional.of(uiSchemaFolderArg);
      }
      return Optional.empty();
   }

   public void printHelp() {
      CLIParser.printHelp(this.processName, this.options);
   }

   public static void printHelp(final String processName, final Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(90, processName, "\noptions:", options, "", true);
   }

   public static Options getDefaultCLIOptions() {
      Options options = new Options();
      options.addOption("h", "help", false, "Display usage information about ModelServer");
      options.addOption("p", "port", true, "Set server port, otherwise default port 8081 is used");
      options.addOption("r", "root", true, "Set workspace root");
      options.addOption("u", "uiSchemaUri", true, "Set UI schema folder uri");
      options.addOption("e", "errorsOnly", false, "Only log errors");
      return options;
   }
}
