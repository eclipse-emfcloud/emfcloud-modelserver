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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfcloud.modelserver.command.CCommand;
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

   void updateResource(String modeluri, CCommand command) throws DecodingException;

   void removeResource(String modeluri) throws IOException;

   Command undo(String modeluri);

   Command redo(String modeluri);

   boolean save(String modeluri);

   boolean saveAll();

   boolean getDirtyState(String modeluri);

}
