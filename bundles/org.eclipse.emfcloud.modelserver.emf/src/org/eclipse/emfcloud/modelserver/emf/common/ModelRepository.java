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
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.inject.Inject;

/**
 * Injectable singleton class represents a repository of all loaded models and provides a CRUD API.
 *
 */
public class ModelRepository {
   private static Logger LOG = Logger.getLogger(ModelRepository.class.getSimpleName());

   @Inject
   private final ServerConfiguration serverConfiguration;
   @Inject
   private ModelResourceManager modelResourceManager;

   @Inject
   public ModelRepository(final ServerConfiguration serverConfiguration) {
      this.serverConfiguration = serverConfiguration;
   }

   public void initialize() {
      modelResourceManager.initialize();
   }

   protected boolean hasModel(final String modeluri) {
      return modelResourceManager.isResourceLoaded(modeluri);
   }

   public Optional<Resource> loadResource(final String modeluri) {
      return modelResourceManager.loadResource(modeluri);
   }

   public Optional<EObject> getModel(final String modeluri) {
      return modelResourceManager.loadResource(modeluri)
         .flatMap(res -> {
            List<EObject> contents = res.getContents();
            return contents.isEmpty() ? Optional.empty() : Optional.of(contents.get(0));
         });
   }

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

   public void addModel(final String modeluri, final EObject model) throws IOException {
      modelResourceManager.addResource(modeluri, model);
   }

   /**
    * Replace a model with an update.
    *
    * @param modeluri the URI of the model to replace
    * @param model    the replacement
    * @return the {@code resource} that was replaced, or an empty optional if it
    *         does not exist
    */
   public Optional<Resource> updateModel(final String modeluri, final EObject model) {
      return modelResourceManager.updateResource(modeluri, model);
   }

   public void updateModel(final String modeluri, final CCommand command) throws DecodingException {
      modelResourceManager.updateResource(modeluri, command);
   }

   public void removeModel(final String modeluri) throws IOException {
      modelResourceManager.removeResource(modeluri);
   }

   public boolean saveModel(final String modeluri) {
      return modelResourceManager.save(modeluri);
   }

   public boolean saveAllModels() {
      return modelResourceManager.saveAll();
   }

   public boolean getDirtyState(final String modeluri) {
      return modelResourceManager.getDirtyState(modeluri);
   }

   public CCommand getUndoCommand(final String modeluri) {
      return modelResourceManager.getUndoCommand(modeluri);
   }

   public boolean undo(final String modeluri) {
      return modelResourceManager.undo(modeluri);
   }

   public CCommand getRedoCommand(final String modeluri) {
      return modelResourceManager.getRedoCommand(modeluri);
   }

   public boolean redo(final String modeluri) {
      return modelResourceManager.redo(modeluri);
   }

   public BasicDiagnostic validate(final String modeluri) {
      if (!getModel(modeluri).isEmpty()) {
         BasicDiagnostic diagnostics = Diagnostician.INSTANCE.createDefaultDiagnostic(getModel(modeluri).get());
         Diagnostician.INSTANCE.validate(getModel(modeluri).get(), diagnostics,
            Diagnostician.INSTANCE.createDefaultContext());
         return diagnostics;
      }
      return null;
   }

   public Set<String> getAllModelUris() {
      Set<String> modeluris = new HashSet<>();
      for (URI uri : modelResourceManager.getAllLoadedModelURIs()) {
         modeluris.add(uri.deresolve(serverConfiguration.getWorkspaceRootURI()).toString());
      }
      return modeluris;
   }

   public Set<String> getAbsoluteModelUris() {
      Set<String> modeluris = new HashSet<>();
      for (URI uri : modelResourceManager.getAllLoadedModelURIs()) {
         modeluris.add(uri.toString());
      }
      return modeluris;
   }

   public void addTemporaryCommandResource(final String modeluri, final Resource resource, final CCommand command) {
      modelResourceManager.getResourceSet(modeluri).getResources().add(resource);
      resource.getContents().add(command);
   }

   public void removeTemporaryCommandResource(final String modeluri, final Resource resource) {
      modelResourceManager.getResourceSet(modeluri).getResources().remove(resource);
   }
}
