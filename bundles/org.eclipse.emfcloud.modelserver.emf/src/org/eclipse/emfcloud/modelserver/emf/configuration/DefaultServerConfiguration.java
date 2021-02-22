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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.common.UriHelper;

import com.google.inject.Inject;

/**
 * Singleton class that holds configurations parameters for a model server
 * instance.
 *
 */
public class DefaultServerConfiguration implements ServerConfiguration {
   private static Logger LOG = Logger.getLogger(DefaultServerConfiguration.class.getSimpleName());

   private URI workspaceRootURI = URI.createURI("");
   private URI uiSchemaFolderURI = URI.createURI("");
   private int serverPort = 8081;

   private final UriHelper uriHelper;

   @Inject
   public DefaultServerConfiguration(final UriHelper uriHelper) {
      this.uriHelper = uriHelper;
   }

   @Override
   public int getServerPort() { return serverPort; }

   @Override
   public void setServerPort(final int serverPort) { this.serverPort = serverPort; }

   @Override
   public URI getWorkspaceRootURI() { return workspaceRootURI; }

   @Override
   public void setWorkspaceRootURI(final URI uri) { this.workspaceRootURI = uri; }

   @Override
   public boolean setWorkspaceRoot(final String workspaceRoot) {
      Optional<URI> newUri = uriHelper.toDirectoryUri(workspaceRoot);
      newUri.ifPresent(this::setWorkspaceRootURI);
      return newUri.isPresent();
   }

   @Override
   public URI getUiSchemaFolderURI() { return this.uiSchemaFolderURI; }

   @Override
   public void setUiSchemaFolderURI(final URI uri) { this.uiSchemaFolderURI = uri; }

   @Override
   public void setUiSchemaFolder(final String uiSchemaFolder) {
      uriHelper.toDirectoryUri(uiSchemaFolder).ifPresent(this::setUiSchemaFolderURI);
   }

   @Override
   public boolean isUiSchemaFolder(final String folder) {
      return uriHelper.toDirectoryUri(folder).map(getUiSchemaFolderURI()::equals).orElse(false);
   }

   @Override
   public Set<String> getWorkspaceEntries() {
      Set<String> filePaths = new HashSet<>();

      if (workspaceRootURI.isFile()) {
         try (Stream<Path> paths = Files.walk(Paths.get(workspaceRootURI.toFileString()))) {
            paths
               .filter(Files::isRegularFile)
               .forEach(file -> filePaths.add(file.toString()));
         } catch (InvalidPathException i) {
            LOG.error(String.format("Could not get workspace path! ’%s’ cannot be converted to a valid Path",
               this.workspaceRootURI));
         } catch (IOException io) {
            LOG.error(String.format("IOException occured while reading files in workspace ’%s’: " + io.getMessage(),
               this.workspaceRootURI));
         } catch (SecurityException s) {
            LOG.error("Security manager denies access to files under workspace " + this.workspaceRootURI);
         }
      }
      return filePaths;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("DefaultServerConfiguration [workspaceRootURI=").append(workspaceRootURI)
         .append(", uiSchemaFolderURI=").append(uiSchemaFolderURI).append(", serverPort=").append(serverPort)
         .append("]");
      return builder.toString();
   }
}
