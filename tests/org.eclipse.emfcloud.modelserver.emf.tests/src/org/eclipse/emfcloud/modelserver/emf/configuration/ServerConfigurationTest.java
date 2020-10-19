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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

public class ServerConfigurationTest {

   private final ServerConfiguration serverConfiguration = new ServerConfiguration();

   @Test
   public void normalizeWorkspaceRoot() {
      serverConfiguration.setWorkspaceRoot("foo");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), endsWith("foo/"));
   }

   @Test
   public void normalizeWorkspaceRootEncoded() throws UnsupportedEncodingException {
      serverConfiguration.setWorkspaceRoot("file:/c%3A/foo%20bar/");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), endsWith("c:/foo bar/"));
   }

   @Test
   public void normalizeWorkspaceRootSlashAlreadyPresent() {
      serverConfiguration.setWorkspaceRoot("foo/");
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), endsWith("foo/"));
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
      assertThat(serverConfiguration.getWorkspaceRootURI().toFileString(), is(cwd.getAbsolutePath() + "/"));
   }

   @Test
   public void getUiSchemaFolder() {
      File cwd = getCWD();

      serverConfiguration.setUiSchemaFolder("./ui-schema-folder");
      URI expected = URI.createFileURI(cwd.getAbsolutePath() + "/ui-schema-folder").appendSegment(""); // trailing slash
      assertThat(serverConfiguration.getUiSchemaFolderURI(), is(expected));
      assertThat(serverConfiguration.getUiSchemaFolderURI().toFileString(),
         is(cwd.getAbsolutePath() + "/ui-schema-folder/"));
   }

   @Test
   public void isUiSchemaFolder() {
      File cwd = getCWD();

      serverConfiguration.setUiSchemaFolder("./ui-schema-folder");
      String expected = cwd.getAbsolutePath() + "/ui-schema-folder";

      // true with or without trailing slash
      assertThat(serverConfiguration.isUiSchemaFolder(expected), is(true));
      assertThat(serverConfiguration.isUiSchemaFolder(expected + "/"), is(true));

      // false for: parent, child
      assertThat(serverConfiguration.isUiSchemaFolder(cwd.getAbsolutePath()), is(false));
      assertThat(serverConfiguration.isUiSchemaFolder(expected + "test/"), is(false));
   }

   @Test
   public void isValidFileURI() {
      assertThat(ServerConfiguration.isValidFileURI(getCWD().getAbsolutePath()), is(true));
      assertThat("file URI deemed invalid",
         ServerConfiguration.isValidFileURI(URI.createFileURI(getCWD().getAbsolutePath()).toString()),
         is(true));
      assertThat("relative path deemed invalid", ServerConfiguration.isValidFileURI("."), is(true));
      String bogusPath = new File(getCWD(), "$this$/cannot/likely/exist").getAbsolutePath();
      assertThat("non-existent path deemed valid", ServerConfiguration.isValidFileURI(bogusPath), is(false));
   }

   @Test
   public void getWorkspaceEntries() {
      assumeThat(getCWD().getName(), is("org.eclipse.emfcloud.modelserver.emf.tests"));
      serverConfiguration.setWorkspaceRoot(".");
      assertThat(serverConfiguration.getWorkspaceEntries(), hasItem(endsWith("/ServerConfigurationTest.class")));
   }

   //
   // Test framework
   //

   static File getCWD() { return new File(System.getProperty("user.dir")); }

}
