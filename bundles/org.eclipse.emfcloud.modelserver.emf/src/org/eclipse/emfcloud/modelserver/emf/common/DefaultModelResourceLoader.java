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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.emfjson.jackson.resource.JsonResourceFactory;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class DefaultModelResourceLoader implements ModelResourceLoader {

   private static Logger LOG = Logger.getLogger(DefaultModelResourceLoader.class.getSimpleName());

   @Inject
   private CommandCodec commandCodec;
   @Inject
   private final ServerConfiguration serverConfiguration;

   private final Set<EPackageConfiguration> configurations;
   private final AdapterFactory adapterFactory;
   private final Map<URI, ResourceSet> resourceSets = Maps.newLinkedHashMap();
   private final Map<ResourceSet, ModelServerEditingDomain> editingDomains = Maps.newLinkedHashMap();

   @Inject
   public DefaultModelResourceLoader(final Set<EPackageConfiguration> configurations,
      final AdapterFactory adapterFactory, final ServerConfiguration serverConfiguration) {

      this.configurations = configurations;
      this.adapterFactory = adapterFactory;
      this.serverConfiguration = serverConfiguration;
      initialize();
   }

   @Override
   public void initialize() {
      registerExtensions(configurations);
      configurations.forEach(EPackageConfiguration::registerEPackage);

      String workspacePath = this.serverConfiguration.getWorkspaceRootURI().toFileString();
      if (workspacePath != null) {
         resourceSets.clear();
         loadSourceResources(workspacePath);
         removeErroneousResources();
         initializeEditingDomains();
      }
   }

   private void registerExtensions(final Set<EPackageConfiguration> configurations) {
      Map<String, Object> map = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      // register default ResourceFactories (XMI and JSON)
      map.put("*", new XMIResourceFactoryImpl());
      map.put("json", new JsonResourceFactory(EMFJsonConverter.setupDefaultMapper()));
      // register additional ResourceFactories
      configurations.forEach(conf -> map.putAll(registerExtensions(conf)));
   }

   private Map<String, Object> registerExtensions(final EPackageConfiguration configuration) {
      Map<String, Object> map = Maps.newHashMap();
      configuration.getFileExtensions().forEach(ext -> {
         configuration.getResourceFactory(ext).ifPresent(fac -> map.put(ext, fac));
      });

      return map;
   }

   @Override
   public ResourceSet getResourceSet(final String modeluri) {
      URI u = createURI(modeluri);
      return resourceSets.get(u);
   }

   @Override
   public ModelServerEditingDomain getEditingDomain(final ResourceSet resourceSet) {
      return editingDomains.get(resourceSet);
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
            resourceSets.put(createURI(file.getAbsolutePath()), new ResourceSetImpl());
            loadResource(file.getAbsolutePath());
         }
      }
   }

   protected boolean isSourceDirectory(final File file) {
      return file.isDirectory() && !this.serverConfiguration.isUiSchemaFolder(file.getAbsolutePath());
   }

   protected void removeErroneousResources() {
      resourceSets.values().forEach(resourceSet -> {
         // any resources loaded with errors are probably not resources in the first place
         final List<Resource> resourcesWithErrors = resourceSet.getResources().stream()
            .filter(resource -> !resource.getErrors().isEmpty())
            .collect(Collectors.toList());
         for (Resource resource : resourcesWithErrors) {
            resourceSet.getResources().remove(resource);
         }
      });
   }

   protected void initializeEditingDomains() {
      resourceSets.values().forEach(resourceSet -> createEditingDomain(resourceSet));
   }

   protected void createEditingDomain(final ResourceSet resourceSet) {
      editingDomains.put(resourceSet, new ModelServerEditingDomain(adapterFactory, resourceSet));
   }

   protected boolean hasModel(final String modeluri) {
      final URI uri = createURI(modeluri);
      return getResourceSet(modeluri).getResource(uri, false) != null;
   }

   private URI createURI(final String modeluri) {
      return modeluri.startsWith("file:")
         ? URI.createURI(modeluri, true)
         : URI.createFileURI(modeluri);
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<Resource> loadResource(final String modeluri) {
      try {
         Resource resource = getResourceSet(modeluri).getResource(createURI(modeluri), true);
         resource.load(Collections.EMPTY_MAP);
         return Optional.of(resource);
      } catch (final Throwable e) {
         // properly remove model again so the resource set does not hold a broken resource
         LOG.error("Could not load resource with URI: " + modeluri);
         removeResourceSafe(modeluri);
         return Optional.empty();
      }
   }

   private void removeResourceSafe(final String modeluri) {
      try {
         removeResource(modeluri);
      } catch (IOException exception) {
         LOG.error("Could not remove resource with URI: " + modeluri, exception);
      }
   }

   @Override
   public void removeResource(final String modeluri) throws IOException {
      Resource resource = getResourceSet(modeluri).getResource(createURI(modeluri), false);
      if (resource != null) {
         resource.delete(null);
      }
   }

   @Override
   public <T extends EObject> Optional<T> loadModel(final String modeluri, final Class<T> clazz) {
      Optional<Resource> res = loadResource(modeluri);
      if (res.isPresent() && !res.get().getContents().isEmpty()) {
         EObject root = res.get().getContents().get(0);
         if (clazz.isInstance(root)) {
            return Optional.of(clazz.cast(root));
         }
         LOG.info(String.format("Root element of resource \"%s\" is not an instance of %s", modeluri, clazz));
      }
      LOG.info("Resource appears to be empty: " + modeluri);
      return Optional.empty();
   }

   @Override
   public boolean isResourceLoaded(final String modeluri) {
      return getResourceSet(modeluri).getResource(createURI(modeluri), false) != null;
   }

   @Override
   public Collection<ResourceSet> getAllLoadedResourceSets() { return resourceSets.values(); }

   @Override
   public Set<URI> getAllLoadedModelURIs() { return resourceSets.keySet(); }

   @Override
   public void addResource(final String modeluri, final EObject model) throws IOException {
      resourceSets.put(createURI(modeluri), new ResourceSetImpl());
      ResourceSet newResourceSet = getResourceSet(modeluri);
      final Resource resource = newResourceSet.createResource(createURI(modeluri));
      newResourceSet.getResources().add(resource);
      resource.getContents().add(model);
      resource.save(null);
      createEditingDomain(newResourceSet);
   }

   /**
    * Replace a model with an update.
    *
    * @param modeluri     the URI of the model to replace
    * @param updatedModel the replacement
    * @return the {@code resource} that was replaced, or an empty optional if it
    *         does not exist
    */
   @Override
   public Optional<Resource> updateResource(final String modeluri, final EObject updatedModel) {
      return loadResource(modeluri).map(res -> {
         ECollections.setEList(res.getContents(), ECollections.singletonEList(updatedModel));
         return res;
      });
   }

   @Override
   public void updateResource(final String modeluri, final CCommand command) throws DecodingException {
      Command decoded = commandCodec.decode(getEditingDomain(getResourceSet(modeluri)), command);
      getEditingDomain(getResourceSet(modeluri)).execute(decoded);
   }

   @Override
   public Command undo(final String modeluri) {
      ModelServerEditingDomain editingDomain = getEditingDomain(getResourceSet(modeluri));
      Command undoCommand = editingDomain.getCommandStack().getUndoCommand();
      if (editingDomain.canUndo()) {
         editingDomain.undo();
         return createInverseCommand(undoCommand);
      }
      return null;
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   private Command createInverseCommand(final Command undoCommand) {
      if (undoCommand instanceof CompoundCommand) {
         CompoundCommand undoCompoundCommand = (CompoundCommand) undoCommand;

         CompoundCommand inverseCompoundCommand = new CompoundCommand();
         for (Command next : undoCompoundCommand.getCommandList()) {
            inverseCompoundCommand.append(createInverseCommand(next));
         }
         return inverseCompoundCommand;

      } else if (undoCommand instanceof AddCommand) {
         AddCommand undoAddCommand = (AddCommand) undoCommand;
         return RemoveCommand.create(undoAddCommand.getDomain(), undoAddCommand.getOwner(),
            undoAddCommand.getFeature(), undoAddCommand.getResult());

      } else if (undoCommand instanceof RemoveCommand) {
         RemoveCommand undoRemoveCommand = (RemoveCommand) undoCommand;
         return AddCommand.create(undoRemoveCommand.getDomain(), undoRemoveCommand.getOwner(),
            undoRemoveCommand.getFeature(), undoRemoveCommand.getResult());
      } else if (undoCommand instanceof SetCommand) {
         // FIXME: Handle the UNSET value, see also DefaultCommandCodec where it is also not yet implemented
         SetCommand undoSetCommand = (SetCommand) undoCommand;
         return SetCommand.create(undoSetCommand.getDomain(), undoSetCommand.getOwner(),
            undoSetCommand.getFeature(), undoSetCommand.getOldValue(), undoSetCommand.getIndex());
      } else if (undoCommand instanceof ReplaceCommand) {
         // TODO see also DefaultCommandCodec
      } else if (undoCommand instanceof MoveCommand) {
         // TODO see also DefaultCommandCodec
      }
      return undoCommand;
   }

   @Override
   public Command redo(final String modeluri) {
      ModelServerEditingDomain editingDomain = getEditingDomain(getResourceSet(modeluri));
      Command redoCommand = editingDomain.getCommandStack().getRedoCommand();
      if (editingDomain.canRedo()) {
         editingDomain.redo();
         return redoCommand;
      }
      return null;
   }

   @Override
   public boolean save(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      boolean result = resourceSet.getResources().stream().allMatch(this::saveResource);
      getEditingDomain(resourceSet).saveIsDone();
      return result;
   }

   protected boolean saveResource(final Resource resource) {
      if (resource.getURI() != null) {
         try {
            resource.save(Collections.EMPTY_MAP);
            return true;
         } catch (IOException e) {
            LOG.error("Could not save resource: " + resource.getURI(), e);
         }
      }
      return false;
   }

   @Override
   public boolean getDirtyState(final String modeluri) {
      return getEditingDomain(getResourceSet(modeluri)).isDirty();
   }

}
