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

import java.util.Set;

import org.eclipse.emf.common.util.URI;

/**
 * Singleton class that holds configurations parameters for a model server
 * instance.
 *
 */
public interface ServerConfiguration {
   URI getWorkspaceRootURI();

   void setWorkspaceRootURI(URI uri);

   boolean setWorkspaceRoot(String workspaceRoot);

   URI getUiSchemaFolderURI();

   void setUiSchemaFolderURI(URI uri);

   void setUiSchemaFolder(String uiSchemaFolder);

   boolean isUiSchemaFolder(String folder);

   Set<String> getWorkspaceEntries();

   int getServerPort();

   void setServerPort(int serverPort);
}
