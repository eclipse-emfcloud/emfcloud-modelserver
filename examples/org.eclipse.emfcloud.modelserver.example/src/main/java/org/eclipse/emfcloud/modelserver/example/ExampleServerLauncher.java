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
package org.eclipse.emfcloud.modelserver.example;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.di.DefaultModelServerModule;
import org.eclipse.emfcloud.modelserver.emf.launch.CLIParser;
import org.eclipse.emfcloud.modelserver.emf.launch.ModelServerLauncher;
import org.eclipse.emfcloud.modelserver.example.util.ResourceUtil;

import com.google.common.collect.Lists;

public final class ExampleServerLauncher {
   private static String TEMP_DIR = ".temp";
   private static String WORKSPACE_ROOT = "workspace";
   private static String ECORE_TEST_FILE = "Coffee.ecore";
   private static String COFFEE_TEST_FILE = "SuperBrewer3000.coffee";
   private static String JSON_TEST_FILE = "SuperBrewer3000.json";
   private static String UISCHEMA_FOLDER = ".ui-schemas";
   private static String WORKSPACE_UISCHEMA_FOLDER = "workspace" + "/" + UISCHEMA_FOLDER;
   private static String UISCHEMA_AUTOMATICTASK_FILE = "automatictask.json";
   private static String UISCHEMA_BREWINGUNIT_FILE = "brewingunit.json";
   private static String UISCHEMA_CONTROLUNIT_FILE = "controlunit.json";
   private static String UISCHEMA_DECISION_FILE = "decision.json";
   private static String UISCHEMA_DIPTRAY_FILE = "diptray.json";
   private static String UISCHEMA_FLOW_FILE = "flow.json";
   private static String UISCHEMA_MACHINE_FILE = "machine.json";
   private static String UISCHEMA_MANUALTASK_FILE = "manualtask.json";
   private static String UISCHEMA_MERGE_FILE = "merge.json";
   private static String UISCHEMA_WATERTANK_FILE = "watertank.json";
   private static String UISCHEMA_WEIGHTEDFLOW_FILE = "weightedflow.json";
   private static String PROCESS_NAME = "java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar";

   private static Logger LOG = Logger.getLogger(ExampleServerLauncher.class.getSimpleName());

   private ExampleServerLauncher() {}

   public static void main(String[] args) throws ParseException {
      ModelServerLauncher.configureLogger();
      CLIParser.create(args, CLIParser.getDefaultCLIOptions(), PROCESS_NAME);

      if (!CLIParser.getInstance().optionExists("r")) {
         // No workspace root was specified, use test workspace
         final File workspaceRoot = new File(TEMP_DIR + "/" + WORKSPACE_ROOT);
         if (!setupTempTestWorkspace(workspaceRoot)) {
            LOG.error("Could not setup test workspace");
            System.exit(0);
         }
         Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanupTempTestWorkspace(workspaceRoot)));
         args = Arrays.copyOf(args, args.length + 1);
         args[args.length - 1] = "--root=" + workspaceRoot.toURI();

         // No ui schema folder was specified, use folder in just loaded temp test workspace
         if (!CLIParser.getInstance().optionExists("u")) {
            final File uiSchemaFolder = new File(TEMP_DIR + "/" + WORKSPACE_UISCHEMA_FOLDER);
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "-u=" + uiSchemaFolder.toURI();
         }

         CLIParser.create(args, CLIParser.getDefaultCLIOptions(), PROCESS_NAME);
      }

      final ModelServerLauncher launcher = new ModelServerLauncher(args, new DefaultModelServerModule());
      launcher.addEPackageConfigurations(Lists.newArrayList(CoffeePackageConfiguration.class));
      launcher.start();
   }

   private static boolean setupTempTestWorkspace(final File workspaceRoot) {
      cleanupTempTestWorkspace(workspaceRoot);
      boolean result = workspaceRoot.mkdirs();
      result &= ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/" + ECORE_TEST_FILE,
         new File(workspaceRoot, ECORE_TEST_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/" + COFFEE_TEST_FILE,
         new File(workspaceRoot, COFFEE_TEST_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/" + JSON_TEST_FILE,
         new File(workspaceRoot, JSON_TEST_FILE));
      result &= setupTempUiSchemaTestWorkspace(new File(workspaceRoot + "/" + UISCHEMA_FOLDER + "/"), result);
      return result;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   private static boolean setupTempUiSchemaTestWorkspace(final File workspaceUiSchemaRoot, boolean result) {
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_AUTOMATICTASK_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_AUTOMATICTASK_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_BREWINGUNIT_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_BREWINGUNIT_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_CONTROLUNIT_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_CONTROLUNIT_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_DECISION_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_DECISION_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_DIPTRAY_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_DIPTRAY_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_FLOW_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_FLOW_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_MACHINE_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_MACHINE_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_MANUALTASK_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_MANUALTASK_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_MERGE_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_MERGE_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_WATERTANK_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_WATERTANK_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_UISCHEMA_FOLDER + "/" + UISCHEMA_WEIGHTEDFLOW_FILE,
         new File(workspaceUiSchemaRoot, UISCHEMA_WEIGHTEDFLOW_FILE));
      return result;
   }

   private static void cleanupTempTestWorkspace(final File workspaceRoot) {
      if (workspaceRoot.exists()) {
         try {
            FileUtils.deleteDirectory(workspaceRoot);
         } catch (IOException e) {
            LOG.warn(e);
         }
      }
   }

}
