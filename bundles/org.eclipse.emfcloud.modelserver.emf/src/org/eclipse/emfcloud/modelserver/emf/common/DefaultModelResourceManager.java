/********************************************************************************
 * Copyright (c) 2020-2022 EclipseSource and others.
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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.di.Initialize;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandExecutionType;
import org.eclipse.emfcloud.modelserver.edit.ModelServerCommand;
import org.eclipse.emfcloud.modelserver.edit.command.UpdateModelCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatcher;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatchersManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.util.JsonPatchHelper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DefaultModelResourceManager implements ModelResourceManager {
   protected static final Logger LOG = LogManager.getLogger(DefaultModelResourceManager.class);

   // A scheme must comprise at least two characters, otherwise it's assumed to be a Windows drive letter
   protected static final Pattern SCHEME_PATTERN = Pattern.compile("^([a-zA-Z_0-9@-]{2,}):");

   @Inject
   protected CommandCodec commandCodec;

   @Inject
   protected final ServerConfiguration serverConfiguration;

   @Inject
   protected ModelRepository modelRepository;

   protected final Set<EPackageConfiguration> configurations;
   protected final AdapterFactory adapterFactory;
   protected ModelWatchersManager watchersManager;
   protected final Map<URI, ResourceSet> resourceSets = Maps.newLinkedHashMap();
   protected final Map<ResourceSet, ModelServerEditingDomain> editingDomains = Maps.newLinkedHashMap();
   /** Lock for synchronization of all access to the {@link #resourceSets} and {@link #editingDomains} maps. */
   protected final Lock resourceSetsLock = new ReentrantLock();
   protected ResourceSetFactory resourceSetFactory;

   // Inject a provider to break the dependency cycle (the helper needs the resource manager)
   protected final Provider<JsonPatchHelper> jsonPatchHelper;

   protected boolean isInitializing;

   @Inject
   public DefaultModelResourceManager(final Set<EPackageConfiguration> configurations,
      final AdapterFactory adapterFactory, final ServerConfiguration serverConfiguration,
      final ModelWatchersManager watchersManager, final Provider<JsonPatchHelper> jsonPatchHelper) {

      this.configurations = Sets.newLinkedHashSet(configurations);
      this.adapterFactory = adapterFactory;
      this.serverConfiguration = serverConfiguration;
      this.watchersManager = watchersManager;
      this.jsonPatchHelper = jsonPatchHelper;
   }

   // Only initialize after injection of the object. This also avoids the antipattern of invoking
   // a method from a superclass constructor that can be overridden in subclasses and so "see"
   // a partially-uninitialized object
   @Initialize
   @Override
   public void initialize() {
      resourceSetsLock.lock();
      this.isInitializing = true;

      try {
         EPackageConfiguration.setup(configurations.toArray(EPackageConfiguration[]::new));

         String workspacePath = this.serverConfiguration.getWorkspaceRootURI().toFileString();
         if (workspacePath != null) {
            resourceSets.values().forEach(this::safeUnload);
            resourceSets.clear();
            editingDomains.clear();
            loadSourceResources(workspacePath);
            removeErroneousResources();
            initializeEditingDomains();
         }
      } finally {
         this.isInitializing = false;
         resourceSetsLock.unlock();
      }
   }

   /**
    * Safely unload a resource set to ensure that adapters, if any, are removed.
    * The unload is performed in a read-only transaction on the editing domain for exclusive access
    * to prevent interleaving of operations from other components such as the {@link ModelWatcher}.
    *
    * @param resource the resource to unload
    */
   @SuppressWarnings("checkstyle:IllegalCatch") // The point is to trap uncaught exceptions and continue unloading
   protected final void safeUnload(final ResourceSet resourceSet) {
      Runnable safeUnloader = () -> {
         try {
            resourceSet.getResources().forEach(this::basicSafeUnload);
            resourceSet.getResources().clear();
         } catch (RuntimeException e) {
            LOG.warn("Uncaught exception (probably in an object adapter) while clearing resource set.", e);
         }
      };

      Optional.ofNullable(TransactionUtil.getEditingDomain(resourceSet)).ifPresentOrElse(
         domain -> runExclusive(domain, safeUnloader,
            () -> "Cannot access editing domain to unload resource set."),
         safeUnloader);
   }

   /**
    * Safely unload a resource to ensure that adapters, if any, are removed.
    * The unload is performed in a read-only transaction on the editing domain for exclusive access
    * to prevent interleaving of operations from other components such as the {@link ModelWatcher}.
    *
    * @param resource the resource to unload
    */
   @SuppressWarnings("checkstyle:IllegalCatch") // The point is to trap uncaught exceptions and continue unloading
   protected final void safeUnload(final Resource resource) {
      safeUnloadWithResult(resource, Function.identity());
   }

   private <T> T safeUnloadWithResult(final Resource resource, final Function<? super Resource, T> result) {
      RunnableWithResult<T> safeUnloader = new RunnableWithResult.Impl<>() {
         @Override
         public void run() {
            basicSafeUnload(resource);
            setResult(result.apply(resource));
         }
      };

      Optional.ofNullable(TransactionUtil.getEditingDomain(resource)).ifPresentOrElse(
         domain -> runExclusive(domain, safeUnloader,
            () -> String.format("Cannot access editing domain to unload resource %s.",
               Optional.ofNullable(resource.getURI()).map(URI::toString).orElse("<unknown>"))),
         safeUnloader);

      return safeUnloader.getResult();
   }

   /**
    * Safely unload a resource to ensure that adapters, if any, are removed.
    * This is not synchronized on any transactional editing domain, so it should only be
    * invoked in such a synchronized context if appropriate.
    *
    * @param resource the resource to unload
    */
   @SuppressWarnings("checkstyle:IllegalCatch") // The point is to trap uncaught exceptions and continue unloading
   protected final void basicSafeUnload(final Resource resource) {
      try {
         resource.unload();
      } catch (RuntimeException e) {
         String resourceURI = Optional.ofNullable(resource.getURI()).map(URI::toString).orElse("<unknown>");
         LOG.warn(String.format("Uncaught exception (probably in an object adapter) while unloading resource %s.",
            resourceURI), e);
      }
   }

   /**
    * Run an {@code operation} in an read-only transaction on the given editing {@code domain}.
    *
    * @param domain             the editing domain context
    * @param operation          the operation to run
    * @param interruptedMessage an optional supplier of a message to log on interrupt while waiting for the transaction.
    *                              If {@code null}, a generic message will be logged
    */
   protected final void runExclusive(final TransactionalEditingDomain domain, final Runnable operation,
      final Supplier<String> interruptedMessage) {
      try {
         domain.runExclusive(operation);
      } catch (InterruptedException e) {
         String message = interruptedMessage != null ? interruptedMessage.get()
            : "Operation was interrupted waiting for access to the resource.";
         LOG.error(message, e);
      }
   }

   @Inject
   public void setResourceSetFactory(final ResourceSetFactory resourceSetFactory) {
      this.resourceSetFactory = resourceSetFactory;
   }

   @Override
   public ResourceSet getResourceSet(final String modeluri) {
      URI uri = createURI(modeluri);
      resourceSetsLock.lock();

      try {
         return resourceSets.get(uri);
      } finally {
         resourceSetsLock.unlock();
      }
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
            URI modelURI = createURI(file.getAbsolutePath());
            resourceSets.put(modelURI, resourceSetFactory.createResourceSet(modelURI));
            loadResource(modelURI.toString());
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
      Matcher scheme = SCHEME_PATTERN.matcher(modeluri);
      if (scheme.find() && URI.validScheme(scheme.group(1))) {
         return URI.createURI(modeluri, true);
      }

      // It's a file path
      return URI.createFileURI(modeluri);
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
         ResourceSet rset = getResourceSet(modeluri);
         URI resourceURI = createURI(modeluri);
         Optional<Resource> loadedResource = Optional.ofNullable(rset.getResource(resourceURI, false))
            .filter(Resource::isLoaded);
         if (loadedResource.isPresent()) {
            return loadedResource;
         }
         // do load the resource and watch for modifications
         Resource resource = rset.getResource(resourceURI, true);
         resource.load(Collections.EMPTY_MAP);
         watchResourceModifications(resource);
         return Optional.of(resource);
      } catch (final Throwable e) {
         handleLoadError(modeluri, this.isInitializing, e);
         return Optional.empty();
      }
   }

   /**
    * Watch for resource modifications.
    *
    * @param resource the resource to watch for
    */
   @Override
   public void watchResourceModifications(final Resource resource) {
      watchersManager.watch(resource);
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
            safeUnload(resource);
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
      resourceSetsLock.lock();

      try {
         basicCloseResource(modeluri);
      } finally {
         resourceSetsLock.unlock();
      }
   }

   protected void basicCloseResource(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      if (resourceSet != null) {
         URI uri = createURI(modeluri);
         boolean resourceStillExists = resourceSet.getURIConverter().exists(uri, resourceSet.getLoadOptions());
         Resource resource = resourceSet.getResource(uri, false);
         if (resource != null) {
            // remove resource and clear resource set and editing domain when necessary
            boolean wasMainResource = safeUnloadWithResult(resource,
               res -> {
                  boolean result = resourceSet.getResources().indexOf(res) == 0;
                  resourceSet.getResources().remove(res);
                  return result;
               });

            /*
             * wasMainResource is generally true with this default implementation,
             * but we don't eliminate the case of a loaded library for extensibility.
             */
            if (wasMainResource) {
               // Unload the rest of the resources, too
               removeResourceSet(uri, resourceSet);
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
               ResourceSet created = resourceSetFactory.createResourceSet(u);
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
   public Collection<ResourceSet> getAllLoadedResourceSets() {
      resourceSetsLock.lock();

      try {
         return resourceSets.values();
      } finally {
         resourceSetsLock.unlock();
      }
   }

   @Override
   public Set<URI> getAllLoadedModelURIs() {
      resourceSetsLock.lock();

      try {
         return resourceSets.keySet();
      } finally {
         resourceSetsLock.unlock();
      }
   }

   @Override
   public void addResource(final String modeluri, final EObject model) throws IOException {
      final ResourceSet newResourceSet;
      final Resource resource;

      URI resourceURI = createURI(modeluri);

      resourceSetsLock.lock();

      try {
         resourceSets.put(resourceURI, resourceSetFactory.createResourceSet(resourceURI));
         newResourceSet = getResourceSet(modeluri);
         resource = newResourceSet.createResource(resourceURI);
         newResourceSet.getResources().add(resource);
         resource.getContents().add(model);
         createEditingDomain(newResourceSet);
      } finally {
         resourceSetsLock.unlock();
      }

      resource.save(null);
      watchResourceModifications(resource);
   }

   protected final void removeResourceSet(final URI modelURI, final ResourceSet resourceSet) {
      safeUnload(resourceSet);

      resourceSetsLock.lock();

      try {
         ModelServerEditingDomain domain = getEditingDomain(resourceSet);
         domain.dispose();
         editingDomains.remove(resourceSet);
         resourceSets.remove(modelURI);
      } finally {
         resourceSetsLock.unlock();
      }
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
      // Note: In API V2, when applying changes to the model via Json Patch, we don't have a client command

      Optional<CommandExecutionContext> context = undoCommand(domain, undoCommand.get(), clientCommand);
      // Context may also be empty in V2

      return context.map(ctx -> {
         CCommandExecutionResult result = createExecutionResult(ctx);
         ReadResourceSet readResourceSet = new ReadResourceSet(domain);
         readResourceSet.resolve(result, "$command.undo.res");
         return result;
      });
   }

   protected Optional<CommandExecutionContext> undoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand, final Optional<CCommand> clientCommand) {
      if (!domain.undo()) {
         return Optional.empty();
      }
      return Optional
         .of(new CommandExecutionContext(CommandExecutionType.UNDO, clientCommand.orElse(null), serverCommand));
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
      // Note: In API V2, when applying changes to the model via Json Patch, we don't have a client command

      Optional<CommandExecutionContext> context = redoCommand(domain, redoCommand.get(), clientCommand);
      // Context may also be empty in V2

      return context.map(ctx -> {
         CCommandExecutionResult result = createExecutionResult(ctx);
         ReadResourceSet readResourceSet = new ReadResourceSet(domain);
         readResourceSet.resolve(result, "$command.redo.res");
         return result;
      });
   }

   protected Optional<CommandExecutionContext> redoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand, final Optional<CCommand> clientCommand) {
      if (!domain.redo()) {
         return Optional.empty();
      }
      return Optional
         .of(new CommandExecutionContext(CommandExecutionType.REDO, clientCommand.orElse(null), serverCommand));
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

   @Override
   public CCommandExecutionResult execute(final String modeluri, final ArrayNode jsonPatch)
      throws JsonPatchTestException, JsonPatchException {
      ResourceSet resourceSet = getResourceSet(modeluri);
      ModelServerEditingDomain domain = getEditingDomain(resourceSet);

      Command command = jsonPatchHelper.get().getCommand(modeluri, resourceSet, jsonPatch);
      // execute command
      CommandExecutionContext context = executeCommand(domain, command, null);

      // create result
      ReadResourceSet readResourceSet = new ReadResourceSet(domain);
      CCommandExecutionResult result = createExecutionResult(context);
      readResourceSet.resolve(result, "$command.exec.res");
      return result;
   }

   protected CommandExecutionContext executeCommand(final ModelServerEditingDomain domain, final Command serverCommand,
      final CCommand clientCommand) {
      domain.execute(serverCommand);
      return new CommandExecutionContext(CommandExecutionType.EXECUTE, clientCommand, serverCommand);
   }

   protected CCommandExecutionResult createExecutionResult(final CommandExecutionContext context) {
      CCommandExecutionResult result = CCommandFactory.eINSTANCE.createCommandExecutionResult();
      result.setType(context.getType());

      // The client command will be null in the case of applying a JSON Patch
      if (context.getClientCommand() != null) {
         result.setSource(EcoreUtil.copy(context.getClientCommand()));
      }

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
      final Collection<ResourceSet> resourceSets = getAllLoadedResourceSets();
      for (ResourceSet rs : resourceSets) {
         boolean tempResult = rs.getResources().stream().allMatch(this::saveResource);
         if (tempResult) {
            ModelServerEditingDomain domain = (ModelServerEditingDomain) TransactionUtil.getEditingDomain(rs);
            domain.saveIsDone();
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
    *
    * @deprecated Resolution/normalization/etc. of incoming model URIs is the responsibility of the
    *             {@link ModelURIConverter} service
    */
   @Override
   @Deprecated
   public String adaptModelUri(final String modelUri) {
      URI uri = createURI(modelUri);

      if (uri.isRelative()) {
         if (serverConfiguration.getWorkspaceRootURI().isFile()) {
            return uri.resolve(serverConfiguration.getWorkspaceRootURI()).toString();
         }
         return URI.createFileURI(modelUri).toString();
      }

      // Create file URI from path if modelUri is already absolute path (file:/ or full path file:///)
      // to ensure consistent usage of org.eclipse.emf.common.util.URI
      if (uri.hasDevice() && !Strings.isNullOrEmpty(uri.device())) {
         String path = "/" + Paths.get(uri.device(), uri.path()).toFile().toString();
         return URI.createFileURI(path).toString();
      }

      return uri.toString();
   }

   @Override
   public void runResourceSetAction(final Runnable action) {
      resourceSetsLock.lock();

      try {
         action.run();
      } finally {
         resourceSetsLock.unlock();
      }
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
