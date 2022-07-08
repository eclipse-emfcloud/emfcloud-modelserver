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

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultUriHelper;
import org.eclipse.emfcloud.modelserver.emf.common.UriHelper;

public final class CLIParser {

   protected static final Logger LOG = LogManager.getLogger(CLIParser.class);

   public static final String OPTION_PORT = "p";
   public static final String OPTION_WORKSPACE_ROOT = "r";
   public static final String OPTION_UI_SCHEMA_ROOT = "u";
   public static final String OPTION_HELP = "h";
   public static final String OPTION_LOG_CONFIGURATION = "l";
   public static final String OPTION_ENABLE_DEV_LOGGING = "e";

   private static final UriHelper URI_HELPER = new DefaultUriHelper();

   private final Options options;
   private final String processName;
   private final int defaultPort;

   private String[] args;
   private CommandLine cmd;

   public CLIParser(final String[] args, final Options options, final String processName, final int defaultPort) {
      this.args = args;
      this.options = options;
      this.processName = processName;
      this.defaultPort = defaultPort;
      this.cmd = parseOptions(options, args);
   }

   protected CommandLine parseOptions(final Options options, final String[] args) {
      CommandLine cmd = null;
      try {
         cmd = new DefaultParser().parse(options, args);
      } catch (UnrecognizedOptionException e) {
         LOG.error("Unrecognized command line argument(s) used!\n");
         printHelp();
         System.exit(1);
      } catch (ParseException e) {
         LOG.error(e.getMessage(), e);
         printHelp();
         System.exit(1);
      }
      return cmd;
   }

   public CommandLine getCommandLine() { return cmd; }

   public boolean optionExists(final String option) {
      return cmd.hasOption(option);
   }

   public void setOption(final String option, final Object value) {
      args = Arrays.copyOf(args, args.length + 1);
      args[args.length - 1] = "-" + option + "=" + value;
      this.cmd = parseOptions(options, args);
   }

   /**
    * Parses and validates the port argument.
    *
    * @return the parsed port argument if present, default port otherwise
    * @throws ParseException is thrown if the parsed argument is not a valid port
    */
   public Integer parsePort() throws ParseException {
      String portArg = cmd.getOptionValue(OPTION_PORT);
      int port = defaultPort;
      if (portArg != null) {
         try {
            port = Integer.parseInt(portArg);
            if (!isValidPort(port)) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException e) {
            throw new ParseException(String.format("'%s' is not a valid port! The default port '%s' is used",
               portArg, defaultPort));
         }
      }
      return port;
   }

   protected boolean isValidPort(final Integer port) {
      return port >= 0 && port <= 65535;
   }

   public Optional<String> parseWorkspaceRoot() throws ParseException {
      return parsePath(OPTION_WORKSPACE_ROOT, "Could not set workspace!");
   }

   public Optional<String> parseUiSchemaFolder() throws ParseException {
      return parsePath(OPTION_UI_SCHEMA_ROOT, "Could not set UI schema folder!");
   }

   public Optional<String> parseLogConfigurationPath() throws ParseException {
      return parsePath(OPTION_LOG_CONFIGURATION, "Could not use path to log configuration file!");
   }

   protected Optional<String> parsePath(final String cliOption, final String errorMsg) throws ParseException {
      String pathArg = cmd.getOptionValue(cliOption);
      if (pathArg != null) {
         if (!URI_HELPER.exists(pathArg)) {
            throw new ParseException(
               String.format("%s The path '%s' is invalid.", errorMsg, pathArg));
         }
         return Optional.of(pathArg);
      }
      return Optional.empty();
   }

   public void printHelp() {
      printHelp(this.processName, this.options);
   }

   public static void printHelp(final String executableName, final Options options) {
      HelpFormatter formatter = new HelpFormatter();
      String cmdLineSyntax = "java -jar " + executableName;
      formatter.printHelp(90, cmdLineSyntax, "\noptions:", options, "", true);
   }

   public static Options getDefaultCLIOptions() {
      Options options = new Options();
      options.addOption(OPTION_HELP, "help", false, "Display usage information about ModelServer");
      options.addOption(OPTION_PORT, "port", true, "Set server port, otherwise a default port is used");
      options.addOption(OPTION_WORKSPACE_ROOT, "root", true, "Set workspace root");
      options.addOption(OPTION_UI_SCHEMA_ROOT, "uiSchemaUri", true, "Set UI schema folder uri");
      options.addOption(OPTION_LOG_CONFIGURATION, "logConfig", true, "Set path to Log4j configuration file (*.xml)");
      options.addOption(OPTION_ENABLE_DEV_LOGGING, "enableDevLogging", false,
         "Enable Javalin dev logging (extensive request/response logging meant for development)");
      return options;
   }
}
