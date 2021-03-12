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
package org.eclipse.emfcloud.modelserver.emf.configuration;

import static org.eclipse.emfcloud.modelserver.tests.util.OSUtil.osEndsWith;
import static org.eclipse.emfcloud.modelserver.tests.util.OSUtil.osIs;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultUriHelper;
import org.junit.Test;

public class DefaultServerConfigurationTest {

   private static final DefaultUriHelper URI_HELPER = new DefaultUriHelper();
   private final ServerConfiguration serverConfiguration = new DefaultServerConfiguration(URI_HELPER);

   @Test
   public void normalizeWorkspaceRoot() {
      serverConfiguration.setWorkspaceRoot("foo");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), osEndsWith("foo" + File.separator));
   }

   @Test
   public void normalizeWorkspaceRootEncoded() throws UnsupportedEncodingException {
      serverConfiguration.setWorkspaceRoot("file:/c%3A/foo%20bar/");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), osEndsWith("c:/foo bar/"));
   }

   @Test
   public void normalizeWorkspaceRootSlashAlreadyPresent() {
      serverConfiguration.setWorkspaceRoot("foo/");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), osEndsWith("foo/"));
   }

   @Test
   public void setWorkspaceRootURI() {
      File cwd = getCWD();

      serverConfiguration.setWorkspaceRootURI(URI.createFileURI(cwd.getAbsolutePath()));
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), is(cwd.getAbsolutePath()));
   }

   @Test
   public void setWorkspaceRoot() {
      File cwd = getCWD();

      serverConfiguration.setWorkspaceRoot(".");
      URI expected = URI.createFileURI(cwd.getAbsolutePath()).appendSegment(""); // trailing slash
      assertThat(serverConfiguration.getWorkspaceRootURI(), is(expected));
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(),
         is(cwd.getAbsolutePath() + File.separator));
   }

   @Test
   public void getUiSchemaFolder() {
      File cwd = getCWD();

      serverConfiguration.setUiSchemaFolder("./ui-schema-folder");
      URI expected = URI.createFileURI(cwd.getAbsolutePath() + "/ui-schema-folder").appendSegment(""); // trailing slash
      assertThat(serverConfiguration.getUiSchemaFolderURI(), is(expected));
      assertThat(serverConfiguration.getUiSchemaFolderURI().toFileString(),
         osIs(cwd.getAbsolutePath() + "/ui-schema-folder/"));
   }

   @Test
   public void isUiSchemaFolder() {
      File cwd = getCWD();

      serverConfiguration.setUiSchemaFolder("./ui-schema-folder");
      String expected = cwd.getAbsolutePath() + File.separator + "ui-schema-folder";

      // true with or without trailing slash
      assertThat(serverConfiguration.isUiSchemaFolder(expected), is(true));
      assertThat(serverConfiguration.isUiSchemaFolder(expected + File.separator), is(true));

      // false for: parent, child
      assertThat(serverConfiguration.isUiSchemaFolder(cwd.getAbsolutePath()), is(false));
      assertThat(serverConfiguration.isUiSchemaFolder(expected + "test" + File.separator), is(false));
   }

   @Test
   public void isValidFileURI() {
      assertThat(URI_HELPER.exists(getCWD().getAbsolutePath()), is(true));
      assertThat("file URI deemed invalid",
         URI_HELPER.exists(URI.createFileURI(getCWD().getAbsolutePath()).toString()),
         is(true));
      assertThat("relative path deemed invalid", URI_HELPER.exists("."), is(true));
      String bogusPath = new File(getCWD(), "$this$/cannot/likely/exist").getAbsolutePath();
      assertThat("non-existent path deemed valid", URI_HELPER.exists(bogusPath), is(false));
   }

   @Test
   public void getWorkspaceEntries() {
      assumeThat(getCWD().getName(), is("org.eclipse.emfcloud.modelserver.emf.tests"));
      serverConfiguration.setWorkspaceRoot(".");
      assertThat(serverConfiguration.getWorkspaceEntries(),
         hasItem(endsWith(File.separator + "DefaultServerConfigurationTest.class")));
   }

   //
   // Test framework
   //

   static File getCWD() { return new File(System.getProperty("user.dir")); }

}
