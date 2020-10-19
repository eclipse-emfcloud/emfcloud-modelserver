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
package org.eclipse.emfcloud.modelserver.client;

public interface ServerConfiguration {

   String getWorkspaceRoot();

   String getUiSchemaFolder();

   static ServerConfiguration create(final String workspaceRoot, final String uiSchemaFolder) {
      return new ServerConfiguration() {
         @Override
         public String getWorkspaceRoot() { return workspaceRoot; }

         @Override
         public String getUiSchemaFolder() { return uiSchemaFolder; }
      };
   }
}
