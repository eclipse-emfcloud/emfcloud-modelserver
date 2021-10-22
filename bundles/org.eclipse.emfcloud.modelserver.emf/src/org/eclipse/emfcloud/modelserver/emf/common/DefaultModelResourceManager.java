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
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandExecutionType;
import org.eclipse.emfcloud.modelserver.edit.ModelServerCommand;
import org.eclipse.emfcloud.modelserver.edit.command.UpdateModelCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DefaultModelResourceManager implements ModelResourceManager {
   protected static final Logger LOG = Logger.getLogger(DefaultModelResourceManager.class.getSimpleName());

   @Inject
   protected CommandCodec commandCodec;

   @Inject
   protected final ServerConfiguration serverConfiguration;

   protected final Set<EPackageConfiguration> configurations;
   protected final AdapterFactory adapterFactory;
   protected final Map<URI, ResourceSet> resourceSets = Maps.newLinkedHashMap();
   protected final Map<ResourceSet, ModelServerEditingDomain> editingDomains = Maps.newLinkedHashMap();

   protected boolean isInitializing;

   @Inject
   public DefaultModelResourceManager(final Set<EPackageConfiguration> configurations,
      final AdapterFactory adapterFactory, final ServerConfiguration serverConfiguration) {

      this.configurations = Sets.newLinkedHashSet(configurations);
      this.adapterFactory = adapterFactory;
      this.serverConfiguration = serverConfiguration;
      initialize();
   }

   @Override
   public void initialize() {
      this.isInitializing = true;
      try {
         EPackageConfiguration.setup(configurations.toArray(EPackageConfiguration[]::new));

         String workspacePath = this.serverConfiguration.getWorkspaceRootURI().toFileString();
         if (workspacePath != null) {
            resourceSets.clear();
            editingDomains.clear();
            loadSourceResources(workspacePath);
            removeErroneousResources();
            initializeEditingDomains();
         }
      } finally {
         this.isInitializing = false;
      }
   }

   @Override
   public ResourceSet getResourceSet(final String modeluri) {
      return resourceSets.get(createURI(modeluri));
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

   protected URI createURI(final String modeluri) {
      return modeluri.startsWith("file:")
         ? URI.createURI(modeluri, true)
         : URI.createFileURI(modeluri);
   }

   /**
    * Loads a resource from its modeluri.
    *
    * @param modeluri
    *                    The URI of the resource to load
    * @return The loaded resource, or {@link Optional#empty()} if an error occurred
    *         during loading
    */
   @Override
   @SuppressWarnings("checkstyle:IllegalCatch")
   public Optional<Resource> loadResource(final String modeluri) {
      try {
         Resource resource = getResourceSet(modeluri).getResource(createURI(modeluri), true);
         resource.load(Collections.EMPTY_MAP);
         return Optional.of(resource);
      } catch (final Throwable e) {
         handleLoadError(modeluri, this.isInitializing, e);
         return Optional.empty();
      }
   }

   /**
    * Handle an exception while trying to load a resource.
    *
    * @param modeluri
    *                          The URI of the resource that was being loaded
    * @param isInitializing
    *                          A flag indicating if we're in the initialization phase (while the model server attempts
    *                          to load all possible resources). If false, this probably means that a client explicitly
    *                          requested the resource.
    * @param exception
    *                          The exception that was thrown during resource loading
    */
   protected void handleLoadError(final String modeluri, final boolean isInitializing, final Throwable exception) {
      if (isInitializing) {
         // Ignore exceptions during initialization; but still log a short debug message
         LOG.debug("Could not load resource with URI: " + modeluri);
      } else {
         LOG.error("Could not load resource with URI: " + modeluri, exception);
      }
      // properly remove model again so the resource set does not hold a broken resource
      removeResource(modeluri);
   }

   /**
    * Unloads a resource and remove it from the resource set.
    *
    * @param modeluri
    *                    The URI of the Resource to remove
    */
   protected void removeResource(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      if (resourceSet == null) {
         // No ResourceSet is associated to this URI; so there is nothing
         // to unload or remove. Skip.
         return;
      }
      Resource resource = resourceSet.getResource(createURI(modeluri), false);
      if (resource != null) {
         resourceSet.getResources().remove(resource);
         if (resource.isLoaded()) {
            resource.unload();
         }
      }
   }

   @Override
   public void deleteResource(final String modeluri) throws IOException {
      ResourceSet resourceSet = getResourceSet(modeluri);
      Resource resource = resourceSet.getResource(createURI(modeluri), false);
      if (resource != null) {
         resource.delete(null);
      }
   }

   /**
    * Closes a resource, forgetting about its content and any current modification.
    * Since we always keep track of resourceSets and their content, resource will immediately be reloaded from file's
    * content.
    *
    * @param modeluri the URI of the model resource
    */
   @Override
   public void closeResource(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      if (resourceSet != null) {
         URI uri = createURI(modeluri);
         boolean resourceStillExists = resourceSet.getURIConverter().exists(uri, resourceSet.getLoadOptions());
         Resource resource = resourceSet.getResource(uri, false);
         if (resource != null) {
            resource.unload();
            // remove resource and clear resource set and editing domain when necessary
            /*
             * wasMainResource is generally true with this default implementation,
             * but we don't eliminate the case of a loaded library for extensibility.
             */
            boolean wasMainResource = resourceSet.getResources().indexOf(resource) == 0;
            resourceSet.getResources().remove(resource);
            if (wasMainResource) {
               ModelServerEditingDomain domain = getEditingDomain(resourceSet);
               domain.dispose();
               editingDomains.remove(resourceSet);
               resourceSets.remove(uri);
            }
         }
         /*
          * DefaultModelResourceManager has a greedy resources loading mechanics,
          * so then, we should reload it immediately.
          * (I don't want to assume no other resource has been loaded in resource set)
          */
         if (resourceStillExists) {
            // recreate resource set when necessary
            resourceSets.computeIfAbsent(uri, u -> {
               ResourceSetImpl created = new ResourceSetImpl();
               createEditingDomain(created);
               return created;
            });
            // reload
            loadResource(modeluri);
         }
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
      ResourceSet resourceSet = getResourceSet(modeluri);
      return resourceSet != null && resourceSet.getResource(createURI(modeluri), false) != null;
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
      Optional<Resource> resource = loadResource(modeluri);
      resource.ifPresent(res -> execute(modeluri, UpdateModelCommandContribution.createClientCommand(updatedModel)));
      return resource;
   }

   @Override
   public Optional<CCommandExecutionResult> undo(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      ModelServerEditingDomain domain = getEditingDomain(resourceSet);
      Optional<Command> undoCommand = domain.getUndoableCommand();
      if (undoCommand.isEmpty()) {
         return Optional.empty();
      }

      Optional<CCommand> clientCommand = ModelServerCommand.getClientCommand(undoCommand.get());
      if (clientCommand.isEmpty()) {
         return Optional.empty();
      }

      Optional<CommandExecutionContext> context = undoCommand(domain, undoCommand.get(), clientCommand.get());
      if (context.isEmpty()) {
         return Optional.empty();
      }

      CCommandExecutionResult result = createExecutionResult(context.get());
      ReadResourceSet readResourceSet = new ReadResourceSet(domain);
      readResourceSet.resolve(result, "$command.undo.res");
      return Optional.of(result);
   }

   protected Optional<CommandExecutionContext> undoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand, final CCommand clientCommand) {
      if (!domain.undo()) {
         return Optional.empty();
      }
      return Optional.of(new CommandExecutionContext(CommandExecutionType.UNDO, clientCommand, serverCommand));
   }

   @Override
   public Optional<CCommandExecutionResult> redo(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      ModelServerEditingDomain domain = getEditingDomain(resourceSet);
      Optional<Command> redoCommand = domain.getRedoableCommand();
      if (redoCommand.isEmpty()) {
         return Optional.empty();
      }

      Optional<CCommand> clientCommand = ModelServerCommand.getClientCommand(redoCommand.get());
      if (clientCommand.isEmpty()) {
         return Optional.empty();
      }

      Optional<CommandExecutionContext> context = redoCommand(domain, redoCommand.get(), clientCommand.get());
      if (context.isEmpty()) {
         return Optional.empty();
      }

      CCommandExecutionResult result = createExecutionResult(context.get());
      ReadResourceSet readResourceSet = new ReadResourceSet(domain);
      readResourceSet.resolve(result, "$command.redo.res");
      return Optional.of(result);
   }

   protected Optional<CommandExecutionContext> redoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand, final CCommand clientCommand) {
      if (!domain.redo()) {
         return Optional.empty();
      }
      return Optional.of(new CommandExecutionContext(CommandExecutionType.REDO, clientCommand, serverCommand));
   }

   protected CCommand encodeCommand(final Command command) {
      try {
         return commandCodec.serverToClient(command);
      } catch (EncodingException e) {
         LOG.error("Encoding of " + command + " failed: " + e.getMessage());
         throw new IllegalArgumentException(e);
      }
   }

   @Override
   public CCommandExecutionResult execute(final String modeluri, final CCommand clientCommand) {
      try {

         ResourceSet resourceSet = getResourceSet(modeluri);
         ModelServerEditingDomain domain = getEditingDomain(resourceSet);
         URI uri = createURI(modeluri);

         // resolve client command
         ReadResourceSet readResourceSet = new ReadResourceSet(domain);
         readResourceSet.resolve(clientCommand, "$command.res");

         // translate to server EMF command
         Command command = commandCodec.clientToServer(uri, domain, clientCommand);
         Command modelServerCommand = ModelServerCommand.wrap(command, clientCommand);

         // execute command
         CommandExecutionContext context = executeCommand(domain, modelServerCommand, clientCommand);

         // create result
         CCommandExecutionResult result = createExecutionResult(context);
         readResourceSet.resolve(result, "$command.exec.res");
         return result;
      } catch (DecodingException exception) {
         LOG.error("Encoding of " + clientCommand + " failed: " + exception.getMessage());
         throw new IllegalArgumentException(exception);
      }
   }

   protected CommandExecutionContext executeCommand(final ModelServerEditingDomain domain, final Command serverCommand,
      final CCommand clientCommand) {
      domain.execute(serverCommand);
      return new CommandExecutionContext(CommandExecutionType.EXECUTE, clientCommand, serverCommand);
   }

   protected CCommandExecutionResult createExecutionResult(final CommandExecutionContext context) {
      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();
      result.setType(context.getType());
      result.setSource(EcoreUtil.copy(context.getClientCommand()));
      Collection<?> affectedObjects = context.getServerCommand().getAffectedObjects();
      if (affectedObjects != null) {
         affectedObjects.stream()
            .filter(EObject.class::isInstance)
            .map(EObject.class::cast)
            .forEach(result.getAffectedObjects()::add);
      }
      return result;
   }

   @Override
   public boolean save(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      if (resourceSet == null) {
         return false;
      }
      Resource resource = resourceSet.getResource(createURI(modeluri), true);
      boolean result = saveResource(resource);
      if (result) {
         getEditingDomain(resourceSet).saveIsDone();
      }
      return result;
   }

   @Override
   public boolean saveAll() {
      boolean result = false;
      for (ResourceSet rs : resourceSets.values()) {
         boolean tempResult = rs.getResources().stream().allMatch(this::saveResource);
         if (tempResult) {
            getEditingDomain(rs).saveIsDone();
         }
         result = tempResult;
      }
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

   /**
    * Adapt the model URI specified by the client to an absolute <tt>file</tt>
    * scheme URI.
    *
    * @param modelUri the client-supplied model URI
    * @return the absolute file URI
    */
   @Override
   public String adaptModelUri(final String modelUri) {
      URI uri = URI.createURI(modelUri, true);
      if (uri.isRelative()) {
         if (serverConfiguration.getWorkspaceRootURI().isFile()) {
            return uri.resolve(serverConfiguration.getWorkspaceRootURI()).toString();
         }
         return URI.createFileURI(modelUri).toString();
      }
      // Create file URI from path if modelUri is already absolute path (file:/ or full path file:///)
      // to ensure consistent usage of org.eclipse.emf.common.util.URI
      if (uri.hasDevice() && !Strings.isNullOrEmpty(uri.device())) {
         return URI.createFileURI(uri.device() + uri.path()).toString();
      }
      return URI.createFileURI(uri.path()).toString();
   }

   public static class CommandExecutionContext {
      private final String type;
      private final CCommand clientCommand;
      private final Command serverCommand;

      public CommandExecutionContext(final String type, final CCommand clientCommand, final Command serverCommand) {
         super();
         this.type = type;
         this.clientCommand = clientCommand;
         this.serverCommand = serverCommand;
      }

      public String getType() { return type; }

      public CCommand getClientCommand() { return clientCommand; }

      public Command getServerCommand() { return serverCommand; }

   }
}
