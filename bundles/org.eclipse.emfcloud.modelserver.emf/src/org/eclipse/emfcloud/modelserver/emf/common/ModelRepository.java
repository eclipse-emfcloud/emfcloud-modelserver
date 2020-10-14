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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.ResourceManager;
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
   private final ResourceManager resourceManager;
   @Inject
   private CommandCodec commandCodec;

   private final ResourceSet resourceSet = new ResourceSetImpl();
   private final EditingDomain domain;

   @Inject
   public ModelRepository(final AdapterFactory adapterFactory, final ServerConfiguration serverConfiguration,
      final ResourceManager resourceManager) {
      this.domain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack(), resourceSet);
      this.serverConfiguration = serverConfiguration;
      this.resourceManager = resourceManager;
      initialize();
   }

   public void initialize() {
      String workspacePath = this.serverConfiguration.getWorkspaceRootURI().toFileString();
      if (workspacePath != null) {
         resourceSet.getResources().forEach(Resource::unload);
         resourceSet.getResources().clear();
         loadSourceResources(workspacePath);
         removeErroneousResources();
      }
   }

   protected void loadSourceResources(final String directoryPath) {
      if (directoryPath == null || directoryPath.isEmpty()) {
         return;
      }
      File directory = new File(directoryPath);
      for (File file : directory.listFiles()) {
         if (isSourceDirectory(file)) {
            loadSourceResources(file.getAbsolutePath());
         } else if (file.isFile()) {
            resourceManager.loadResource(createURI(file.getAbsolutePath()), resourceSet);
         }
      }
   }

   protected void removeErroneousResources() {
      // any resources loaded with errors are probably not resources in the first place
      final List<Resource> resourcesWithErrors = resourceSet.getResources().stream()
         .filter(resource -> !resource.getErrors().isEmpty())
         .collect(Collectors.toList());
      for (Resource resource : resourcesWithErrors) {
         resourceSet.getResources().remove(resource);
      }
   }

   protected boolean isSourceDirectory(final File file) {
      System.out.println("isSource[" + file.getAbsolutePath() + "] " + file.isDirectory() + " - "
         + (!this.serverConfiguration.isUISchemaFolder(file.getAbsolutePath())));
      return file.isDirectory() && !this.serverConfiguration.isUISchemaFolder(file.getAbsolutePath());
   }

   protected boolean hasModel(final String modeluri) {
      final URI uri = createURI(modeluri);
      return resourceSet.getResource(uri, false) != null;
   }

   public Optional<EObject> getModel(final String modeluri) {
      return loadResource(modeluri)
         .flatMap(res -> {
            List<EObject> contents = res.getContents();
            if (contents.isEmpty()) {
               return Optional.empty();
            }
            return Optional.of(contents.get(0));
         });
   }

   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<EObject> getModelElementById(final String modeluri, final String elementid) {
      return loadResource(modeluri)
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
      return loadResource(modeluri)
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

   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<Resource> loadResource(final String modeluri) {
      Resource resource = null;
      try {
         URI uri = createURI(modeluri);
         resource = resourceSet.getResource(uri, true);
         if (resource != null && !resource.getContents().isEmpty()) {
            return Optional.of(resource);
         }
      } catch (Exception exception) {
         // simply fall through
      }
      // properly remove model again so the resource set does not hold a broken resource
      LOG.error("Could not load resource with URI: " + modeluri);
      removeModelSafe(modeluri);
      return Optional.empty();
   }

   public Map<URI, EObject> getAllModels() throws IOException {
      EList<Resource> resources = resourceSet.getResources();
      for (Resource resource : resources) {
         resource.load(null);
      }
      LinkedHashMap<URI, EObject> models = new LinkedHashMap<>();
      resources.forEach(resource -> {
         if (!resource.getContents().isEmpty()) {
            models.put(resource.getURI(), resource.getContents().get(0));
         } else {
            LOG.warn("Could not retrieve empty resource with URI: " + resource.getURI());
         }
      });
      return models;
   }

   public void addModel(final String modeluri, final EObject model) throws IOException {
      final Resource resource = resourceSet.createResource(createURI(modeluri));
      resourceSet.getResources().add(resource);
      resource.getContents().add(model);
      resource.save(null);
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
      return loadResource(modeluri).map(res -> {
         ECollections.setEList(res.getContents(), ECollections.singletonEList(model));
         return res;
      });
   }

   public void updateModel(final String modelURI, final CCommand command) throws DecodingException {
      Command decoded = commandCodec.decode(domain, command);
      domain.getCommandStack().execute(decoded);
   }

   public void removeModel(final String modeluri) throws IOException {
      Resource resource = resourceSet.getResource(createURI(modeluri), false);
      if (resource != null) {
         resource.delete(null);
      }
   }

   private void removeModelSafe(final String modeluri) {
      try {
         removeModel(modeluri);
      } catch (IOException exception) {
         LOG.error("Could not remove resource with URI: " + modeluri, exception);
      }
   }

   public boolean saveModel(final String modeluri) {
      return this.resourceManager.save(resourceSet);
   }

   public Set<String> getAllModelUris() {
      Set<String> modeluris = new HashSet<>();
      for (Resource resource : resourceSet.getResources()) {
         modeluris.add(resource.getURI().deresolve(serverConfiguration.getWorkspaceRootURI()).toString());
      }
      return modeluris;
   }

   ResourceSet getResourceSet() { return resourceSet; }

   private URI createURI(final String modeluri) {
      return modeluri.startsWith("file:")
         ? URI.createURI(modeluri, true)
         : URI.createFileURI(modeluri);
   }
}
