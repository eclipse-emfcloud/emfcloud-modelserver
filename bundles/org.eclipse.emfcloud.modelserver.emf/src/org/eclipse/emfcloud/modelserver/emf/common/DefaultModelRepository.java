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
package org.eclipse.emfcloud.modelserver.emf.common;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.inject.Inject;

/**
 * Injectable singleton class represents a repository of all loaded models and provides a CRUD API.
 *
 */
public class DefaultModelRepository implements ModelRepository {
   protected static Logger LOG = Logger.getLogger(DefaultModelRepository.class.getSimpleName());

   @Inject
   private final ServerConfiguration serverConfiguration;

   @Inject
   private ModelResourceManager modelResourceManager;

   @Inject
   public DefaultModelRepository(final ServerConfiguration serverConfiguration) {
      this.serverConfiguration = serverConfiguration;
   }

   @Override
   public void initialize() {
      modelResourceManager.initialize();
   }

   @Override
   public boolean hasModel(final String modeluri) {
      return modelResourceManager.isResourceLoaded(modeluri);
   }

   @Override
   public Optional<Resource> loadResource(final String modeluri) {
      return modelResourceManager.loadResource(modeluri);
   }

   @Override
   public Optional<EObject> getModel(final String modeluri) {
      return modelResourceManager.loadResource(modeluri)
         .flatMap(res -> {
            List<EObject> contents = res.getContents();
            return contents.isEmpty() ? Optional.empty() : Optional.of(contents.get(0));
         });
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<EObject> getModelElementById(final String modeluri, final String elementid) {
      return modelResourceManager.loadResource(modeluri)
         .flatMap(res -> {
            try {
               EObject modelElement = res.getEObject(elementid);
               return Optional.ofNullable(modelElement);
            } catch (Exception e) {
               LOG.error("Could not load element with URI fragment: " + elementid);
               return Optional.empty();
            }
         });
   }

   @Override
   public Optional<EObject> getModelElementByName(final String modeluri, final String elementname) {
      return modelResourceManager.loadResource(modeluri)
         .flatMap(res -> {
            TreeIterator<EObject> contentIterator = res.getAllContents();
            while (contentIterator.hasNext()) {
               EObject type = contentIterator.next();
               EStructuralFeature name = type.eClass().getEStructuralFeature("name");
               if (name != null) {
                  if (type.eGet(name).equals(elementname)) {
                     return Optional.of(type);
                  }
               }
            }
            return Optional.empty();
         });
   }

   @Override
   public Map<URI, EObject> getAllModels() throws IOException {
      LinkedHashMap<URI, EObject> models = new LinkedHashMap<>();
      modelResourceManager.getAllLoadedResourceSets().forEach(resourceSet -> {
         resourceSet.getResources().forEach(resource -> {
            if (!resource.getContents().isEmpty()) {
               models.put(resource.getURI(), resource.getContents().get(0));
            } else {
               LOG.warn("Could not retrieve empty resource with URI: " + resource.getURI());
            }
         });
      });
      return models;
   }

   @Override
   public void addModel(final String modeluri, final EObject model) throws IOException {
      modelResourceManager.addResource(modeluri, model);
   }

   @Override
   public Optional<Resource> updateModel(final String modeluri, final EObject model) {
      return modelResourceManager.updateResource(modeluri, model);
   }

   @Override
   public CCommandExecutionResult executeCommand(final String modeluri, final CCommand command)
      throws DecodingException {
      return modelResourceManager.execute(modeluri, command);
   }

   @Override
   public void deleteModel(final String modeluri) throws IOException {
      modelResourceManager.deleteResource(modeluri);
   }

   @Override
   public boolean saveModel(final String modeluri) {
      return modelResourceManager.save(modeluri);
   }

   @Override
   public boolean saveAllModels() {
      return modelResourceManager.saveAll();
   }

   @Override
   public boolean getDirtyState(final String modeluri) {
      return modelResourceManager.getDirtyState(modeluri);
   }

   @Override
   public Optional<CCommandExecutionResult> undo(final String modeluri) {
      return modelResourceManager.undo(modeluri);
   }

   @Override
   public Optional<CCommandExecutionResult> redo(final String modeluri) {
      return modelResourceManager.redo(modeluri);
   }

   @Override
   public Set<String> getRelativeModelUris() {
      Set<String> modeluris = new HashSet<>();
      for (URI uri : modelResourceManager.getAllLoadedModelURIs()) {
         modeluris.add(uri.deresolve(serverConfiguration.getWorkspaceRootURI()).toString());
      }
      return modeluris;
   }

   @Override
   public Set<String> getAbsoluteModelUris() {
      Set<String> modeluris = new HashSet<>();
      for (URI uri : modelResourceManager.getAllLoadedModelURIs()) {
         modeluris.add(uri.toString());
      }
      return modeluris;
   }
}
