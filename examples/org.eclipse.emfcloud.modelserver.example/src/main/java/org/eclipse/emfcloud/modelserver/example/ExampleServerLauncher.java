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
package org.eclipse.emfcloud.modelserver.example;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.launch.CLIBasedModelServerLauncher;
import org.eclipse.emfcloud.modelserver.emf.launch.CLIParser;
import org.eclipse.emfcloud.modelserver.example.util.ResourceUtil;

public final class ExampleServerLauncher {
   private static final String TEMP_DIR = ".temp";
   private static final String WORKSPACE_ROOT = "workspace";
   private static final String ECORE_TEST_FILE = "Coffee.ecore";
   private static final String COFFEE_TEST_FILE = "SuperBrewer3000.coffee";
   private static final String JSON_TEST_FILE = "SuperBrewer3000.json";
   private static final String UISCHEMA_FOLDER = ".ui-schemas";
   private static final String WORKSPACE_UISCHEMA_FOLDER = "workspace" + "/" + UISCHEMA_FOLDER;
   private static final String UISCHEMA_AUTOMATICTASK_FILE = "automatictask.json";
   private static final String UISCHEMA_BREWINGUNIT_FILE = "brewingunit.json";
   private static final String UISCHEMA_CONTROLUNIT_FILE = "controlunit.json";
   private static final String UISCHEMA_DECISION_FILE = "decision.json";
   private static final String UISCHEMA_DIPTRAY_FILE = "diptray.json";
   private static final String UISCHEMA_FLOW_FILE = "flow.json";
   private static final String UISCHEMA_MACHINE_FILE = "machine.json";
   private static final String UISCHEMA_MANUALTASK_FILE = "manualtask.json";
   private static final String UISCHEMA_MERGE_FILE = "merge.json";
   private static final String UISCHEMA_WATERTANK_FILE = "watertank.json";
   private static final String UISCHEMA_WEIGHTEDFLOW_FILE = "weightedflow.json";
   private static final String EXECUTABLE_NAME = "org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar";

   private static Logger LOG = LogManager.getLogger(ExampleServerLauncher.class);

   private ExampleServerLauncher() {}

   public static void main(final String[] args) {
      final CLIBasedModelServerLauncher launcher = new CLIBasedModelServerLauncher(
         createCLIParser(args),
         new ExampleServerModule());
      launcher.run();
   }

   protected static CLIParser createCLIParser(final String[] args) {
      CLIParser parser = new CLIParser(args, CLIParser.getDefaultCLIOptions(), EXECUTABLE_NAME, 8081);
      ensureWorkspaceRoot(parser);
      return parser;
   }

   protected static void ensureWorkspaceRoot(final CLIParser parser) {
      if (!parser.optionExists(CLIParser.OPTION_WORKSPACE_ROOT)) {
         // No workspace root was specified, use test workspace
         final File workspaceRoot = new File(TEMP_DIR + "/" + WORKSPACE_ROOT);
         if (!setupTempTestWorkspace(workspaceRoot)) {
            LOG.error("Could not setup test workspace");
            System.exit(1);
         }
         Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanupTempTestWorkspace(workspaceRoot)));
         parser.setOption(CLIParser.OPTION_WORKSPACE_ROOT, workspaceRoot.toURI());

         ensureUISchemaFolder(parser);
      }
   }

   protected static void ensureUISchemaFolder(final CLIParser parser) {
      if (!parser.optionExists(CLIParser.OPTION_UI_SCHEMA_ROOT)) {
         URI uiSchemaRoot = new File(TEMP_DIR + "/" + WORKSPACE_UISCHEMA_FOLDER).toURI();
         parser.setOption(CLIParser.OPTION_UI_SCHEMA_ROOT, uiSchemaRoot);
      }
   }

   private static boolean setupTempTestWorkspace(final File workspaceRoot) {
      cleanupTempTestWorkspace(workspaceRoot);
      boolean result = workspaceRoot.mkdirs();
      result &= ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/" + ECORE_TEST_FILE,
         new File(workspaceRoot, ECORE_TEST_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/" + COFFEE_TEST_FILE,
         new File(workspaceRoot, COFFEE_TEST_FILE));
      result &= result && ResourceUtil.copyFromResource(WORKSPACE_ROOT + "/nested/" + COFFEE_TEST_FILE,
         new File(workspaceRoot.getAbsolutePath() + "/nested/" + COFFEE_TEST_FILE));
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
