/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.configuration;

public class ServerConfigurationDTO {
   private String workspaceRoot;
   private String uiSchemaFolder;

   public ServerConfigurationDTO() {}

   public String getWorkspaceRoot() { return workspaceRoot; }

   public void setWorkspaceRoot(final String workspaceRoot) { this.workspaceRoot = workspaceRoot; }

   public String getUiSchemaFolder() { return uiSchemaFolder; }

   public void setUiSchemaFolder(final String uiSchemaFolder) { this.uiSchemaFolder = uiSchemaFolder; }

}
