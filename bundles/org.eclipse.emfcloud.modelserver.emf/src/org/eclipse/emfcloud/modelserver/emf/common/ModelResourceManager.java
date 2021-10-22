/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;

public interface ModelResourceManager {

   void initialize();

   Optional<Resource> loadResource(String modeluri);

   <T extends EObject> Optional<T> loadModel(String modeluri, Class<T> clazz);

   boolean isResourceLoaded(String modeluri);

   ResourceSet getResourceSet(String modeluri);

   ModelServerEditingDomain getEditingDomain(ResourceSet resourceSet);

   Collection<ResourceSet> getAllLoadedResourceSets();

   Set<URI> getAllLoadedModelURIs();

   void addResource(String modeluri, EObject model) throws IOException;

   Optional<Resource> updateResource(String modeluri, EObject updatedModel);

   /**
    * Delete a resource from the ModelServer.
    *
    * @param modeluri
    * @throws IOException
    */
   void deleteResource(String modeluri) throws IOException;

   /**
    * Closes a resource, forgetting about its content and any current modification.
    * Resource can be reopened later from its persisted state with a simple {@link #loadResource(String)} call.
    *
    * @param modeluri the URI of the model resource
    */
   void closeResource(String modeluri);

   CCommandExecutionResult execute(String modeluri, CCommand command) throws DecodingException;

   Optional<CCommandExecutionResult> undo(String modeluri);

   Optional<CCommandExecutionResult> redo(String modeluri);

   boolean save(String modeluri);

   boolean saveAll();

   boolean getDirtyState(String modeluri);

   /**
    * Adapt the model URI specified by the client.
    *
    * Subclasses may override for taking in account the server configuration and specific URI schemes.
    *
    * @param modelUri the client-supplied model URI
    * @return the adapted URI ready to be consumed
    */
   default String adaptModelUri(final String modelUri) {
      URI uri = URI.createURI(modelUri, true);
      // we do not know the server configuration for relative URIs nor the possible schemes here...
      // concrete implementations would probably override
      return uri.toString();
   }
}
