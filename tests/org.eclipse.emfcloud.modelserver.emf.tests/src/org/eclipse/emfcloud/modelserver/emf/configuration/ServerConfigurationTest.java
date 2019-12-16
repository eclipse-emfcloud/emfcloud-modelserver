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
      assertThat(serverConfiguration.getWorkspaceRoot(), endsWith("foo/"));
   }

   @Test
   public void normalizeWorkspaceRootEncoded() throws UnsupportedEncodingException {
      serverConfiguration.setWorkspaceRoot("file:/c%3A/foo%20bar/");
      assertThat(serverConfiguration.getWorkspaceRoot(), endsWith("c:/foo bar/"));
   }

   @Test
   public void normalizeWorkspaceRootSlashAlreadyPresent() {
      serverConfiguration.setWorkspaceRoot("foo/");
      assertThat(serverConfiguration.getWorkspaceRoot(), endsWith("foo/"));
   }

   @Test
   public void setWorkspaceRootURI() {
      File cwd = getCWD();

      serverConfiguration.setWorkspaceRootURI(URI.createFileURI(cwd.getAbsolutePath()));
      assertThat(serverConfiguration.getWorkspaceRoot(), is(cwd.getAbsolutePath()));
   }

   @Test
   public void setWorkspaceRoot() {
      File cwd = getCWD();

      serverConfiguration.setWorkspaceRoot(".");
      URI expected = URI.createFileURI(cwd.getAbsolutePath()).appendSegment(""); // trailing slash
      assertThat(serverConfiguration.getWorkspaceRootURI(), is(expected));
      assertThat(serverConfiguration.getWorkspaceRoot(), is(cwd.getAbsolutePath() + "/"));
   }

   @Test
   public void isValidWorkspaceRoot() {
      assertThat(ServerConfiguration.isValidWorkspaceRoot(getCWD().getAbsolutePath()), is(true));
      assertThat("file URI deemed invalid",
         ServerConfiguration.isValidWorkspaceRoot(URI.createFileURI(getCWD().getAbsolutePath()).toString()),
         is(true));
      assertThat("relative path deemed invalid", ServerConfiguration.isValidWorkspaceRoot("."), is(true));
      String bogusPath = new File(getCWD(), "$this$/cannot/likely/exist").getAbsolutePath();
      assertThat("non-existent path deemed valid", ServerConfiguration.isValidWorkspaceRoot(bogusPath), is(false));
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
