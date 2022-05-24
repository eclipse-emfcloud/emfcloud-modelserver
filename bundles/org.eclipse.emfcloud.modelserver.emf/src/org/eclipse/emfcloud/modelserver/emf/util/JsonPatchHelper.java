/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics..
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.patch.AbstractJsonPatchHelper;
import org.eclipse.emfcloud.modelserver.common.patch.LazyCompoundCommand;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerEditingDomain;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

/**
 * A Helper to create EMF Commands from a Json Patch.
 */
public class JsonPatchHelper extends AbstractJsonPatchHelper {

   protected static final Logger LOG = LogManager.getLogger(JsonPatchHelper.class);

   protected static final Pattern PATH_SUFFIX_PATTERN = Pattern.compile("/[^/]+(?:/[0-9]+|/-)?$");

   private final ModelResourceManager modelManager;
   private final ServerConfiguration serverConfiguration;
   private final Map<String, Codec> codecs;
   private final Codec fallback = new JsonCodecV2();
   private final ModelURIConverter modelURIConverter;

   /**
    * An externalized mapping of JSON Patches to maps of EMF Object URIs for each of the operations in the patch.
    * The patch helper may not be a singleton, so all helpers need to share this.
    */
   private static final Map<JsonNode, Map<JsonNode, URI>> URI_PATH_MAPPINGS = new MapMaker().weakKeys().weakValues()
      .makeMap();

   /**
    * Flag controlling whether to compute object URIs for patches.
    * The patch helper may not be a singleton, so all helpers need to share this.
    * If any client wants these mappings, they will be enabled. There is no going back.
    */
   private static final AtomicBoolean NEED_OBJECT_URI_MAPPINGS = new AtomicBoolean();

   @Inject
   public JsonPatchHelper(final ModelResourceManager modelManager, final ServerConfiguration serverConfiguration,
      final Map<String, Codec> codecs, final ModelURIConverter modelURIConverter) {

      this.modelManager = modelManager;
      this.serverConfiguration = serverConfiguration;
      this.codecs = Map.copyOf(codecs);
      this.modelURIConverter = modelURIConverter;
   }

   @Override
   protected TransactionalEditingDomain getEditingDomain(final EObject eObject) {
      ResourceSet resourceSet = eObject.eResource().getResourceSet();
      return modelManager.getEditingDomain(resourceSet);
   }

   @Override
   protected LazyCompoundCommand createCompoundCommand(final String modelURI, final String label) {
      ResourceSet resourceSet = getResource(modelURI).getResourceSet();
      TransactionalEditingDomain domain = modelManager.getEditingDomain(resourceSet);
      return new LazyTransactionalCompoundCommand(domain, label);
   }

   @Override
   protected Resource getResource(final String modelURI, final URI resourceURI) {
      // FIXME We should make sure that modelURI and resourceURI are part of the same
      // editing domain (resourceSet). For single-resource use cases, they should be identical;
      // but for cross-resources models, they might differ.
      // Currently, trying to access a resource from a different resourceSet will cause
      // a write transaction exception
      // Issue: https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/159
      URI uri = modelURIConverter.normalize(resourceURI == null ? URI.createURI(modelURI) : resourceURI);
      if (uri.isRelative()) {
         uri = uri.resolve(serverConfiguration.getWorkspaceRootURI());
      }
      return modelManager.loadResource(uri.toString()).orElse(null);
   }

   /**
    * Create a JsonPatch by diffing 2 versions of the same model.
    *
    * @param oldModel
    *                    The old model, before applying changes
    * @param newModel
    *                    The new model, after applying changes
    * @return
    *         A JsonPatch representing the differences between oldModel and newModel
    * @throws EncodingException
    */
   public JsonNode diffModel(final JsonNode oldModel, final JsonNode newModel)
      throws EncodingException {
      return JsonDiff.asJson(oldModel, newModel);
   }

   public JsonNode getCurrentModel(final EObject root) throws EncodingException {
      Codec codec = codecs.getOrDefault(ModelServerPathParametersV2.FORMAT_JSON_V2, fallback);

      if (codec instanceof Codec.Internal) {
         // Do not make a copy in a one-off resource for serialization as Codec.encode(EObject) does
         return ((Codec.Internal) codec).basicEncode(root);
      }

      return codec.encode(root);
   }

   public Map<URI, JsonNode> getJsonPatches(final EObject root, final CCommandExecutionResult result)
      throws EncodingException {

      if (root.eResource() == null || root.eResource().getResourceSet() == null) {
         LOG.error("Can't generate Json patches; the specified object doesn't belong to a resourceSet");
         return null;
      }

      // Collect new state for all resources
      ResourceSet resourceSet = root.eResource().getResourceSet();
      List<Resource> resources = resourceSet.getResources();

      Map<Resource, JsonNode> newState = collectStates(resources);
      Map<Resource, JsonNode> initialState;

      // Revert the changes and collect the initial state for all resources
      initialState = revertChangesAndGetState(resourceSet, result);
      if (initialState == null) {
         return null;
      }

      // Create a diff patch between the initial and new state for each resource
      Map<URI, JsonNode> patches = new HashMap<>();
      for (Resource resource : resources) {
         JsonNode newModel = newState.get(resource);
         JsonNode oldModel = initialState.get(resource);

         JsonNode resourcePatch = diffModel(oldModel, newModel);
         if (!resourcePatch.isEmpty()) {
            URI normalizedURI = modelURIConverter.normalize(resource.getURI());
            URI_PATH_MAPPINGS.put(resourcePatch, mapObjectURIs(resourcePatch, oldModel, resource.getURI()));
            patches.put(normalizedURI, resourcePatch);
         }
      }

      return patches;
   }

   private Map<Resource, JsonNode> revertChangesAndGetState(final ResourceSet resourceSet,
      final CCommandExecutionResult result) throws EncodingException {

      ChangeDescription cd = (ChangeDescription) result.getChangeDescription();
      ModelServerEditingDomain editingDomain = modelManager.getEditingDomain(resourceSet);
      List<Resource> resources = resourceSet.getResources();

      Map<Resource, JsonNode> states;

      try {
         // Compute the old model by reverting the current change. We need to do that in a write-transaction.
         // Note: we could also apply it on a read-only copy of the model, but this could potentially modify
         // the IDs (which are part of the Resource; not of the EObjects), causing unexpected patch changes.
         InternalTransaction transaction = editingDomain.startTransaction(false, null);
         try {
            cd.applyAndReverse();
            states = collectStates(resources);
            cd.applyAndReverse();
            transaction.commit();
         } catch (RollbackException e) {
            LOG.error("Failed to generate JsonPatch", e);
            return null;
         } finally {
            if (transaction != null && transaction.isActive()) {
               rollback(transaction, editingDomain);
            }
         }
      } catch (InterruptedException e) {
         LOG.error("Failed to generate JsonPatch", e);
         return null;
      }
      return states;
   }

   private Map<Resource, JsonNode> collectStates(final List<Resource> resources) throws EncodingException {
      Map<Resource, JsonNode> states = new HashMap<>();
      for (Resource resource : resources) {
         JsonNode newModel = getCurrentModel(resource.getContents().get(0));
         states.put(resource, newModel);
      }
      return states;
   }

   protected void rollback(final Transaction tx, final ModelServerEditingDomain domain) {
      while (tx.isActive()) {
         Transaction active = domain.getActiveTransaction();
         active.rollback();
      }
   }

   /**
    * Create a mapping of operation node (bearing a {@code path} property) in the given {@code patch}
    * to EMF URI of the model object in the old state of the model to which the patch is logically applicable.
    * This mapping is maintained "on the side" to be retrieved later if necessary to rewrite {@code path}
    * properties of the patch in terms of EMF URIs.
    *
    * @param patch       a JSON patch for which to create a mapping of Operation &rarr; Object-URI
    * @param oldModel    the previous state of the model to which the {@code patch} is applicable
    * @param resourceURI the URI of the resource containing the old model
    *
    * @see <a href="https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/205">Issue 205: API v2: Subscription
    *      option for URI fragments in update notifications </a>
    * @see <a href="https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/218">Issue 218: API V2:
    *      rewritePathsAsURIFragments doesn&apos;t (always?) work for deleted elements</a>
    */
   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   public Map<JsonNode, URI> mapObjectURIs(final JsonNode patch, final JsonNode oldModel, final URI resourceURI) {
      Map<JsonNode, URI> result = Maps.newHashMap();

      // What is the name of the unique identifier property for our codec that we used to create the old model?
      String idKey = codecs.containsKey(ModelServerPathParametersV2.FORMAT_JSON_V2) ? "$id" : "id";

      Iterator<JsonNode> operations = patch.isArray() ? patch.iterator() : Iterators.singletonIterator(patch);
      while (operations.hasNext()) {
         JsonNode op = operations.next();
         JsonNode path = op.get("path");
         if (path != null && path.isTextual()) {
            String pointer = path.textValue();

            // The 'pointer' includes a path to the owner object and the property (with possible array index)
            // of the replaced/added/removed/etc. value. Take that part off to get the owner object URI
            Matcher propertyMatcher = PATH_SUFFIX_PATTERN.matcher(pointer);
            if (propertyMatcher.find()) {
               String suffix = propertyMatcher.group();
               pointer = pointer.substring(0, propertyMatcher.start());
               JsonNode target = oldModel.at(pointer);
               if (!target.isMissingNode() && target.has(idKey)) {
                  URI objectURI = resourceURI.appendFragment(target.get(idKey).asText() + suffix);
                  result.put(op, objectURI);
               }
            }
         }
      }

      return result;
   }

   /**
    * For a given {@code patch}, obtain a function that retrieves the URI path of the EMF model object and property
    * (in the old state of the model) to which some operation in that {@code patch} applies. This mapping is only
    * available for patches created by the {@link #getJsonPatches(EObject, CCommandExecutionResult)} API.
    *
    * @param patch a patch for which to get an Object URI function
    * @return a function that returns the original object URI, extend as appropriate by a property name, of the object
    *         to which an input operation is applicable, that it identifies in the old state of the model by the JSON
    *         pointer in its {@code path} property. The function will return {@code null} for operations that don't
    *         trace to any model object
    *
    * @see #getJsonPatches(EObject, CCommandExecutionResult)
    * @see #setNeedObjectURIMappings(boolean)
    * @see #isNeedObjectURIMappings()
    */
   public Function<JsonNode, URI> getObjectURIFunction(final JsonNode patch) {
      Map<JsonNode, URI> mapping = URI_PATH_MAPPINGS.getOrDefault(patch, Collections.emptyMap());
      return mapping::get;
   }

   /**
    * Set whether to provide {@linkplain #getObjectURIFunction(JsonNode) object URI mappings} for patches
    * that I compute from command execution results.
    *
    * @param needObjectURIMappings whether object URI mappings will be needed
    *
    * @see #isNeedObjectURIMappings()
    * @see #getObjectURIFunction(JsonNode)
    */
   public void setNeedObjectURIMappings(final boolean needObjectURIMappings) {
      NEED_OBJECT_URI_MAPPINGS.compareAndSet(false, needObjectURIMappings);
   }

   /**
    * Query whether I provide {@linkplain #getObjectURIFunction(JsonNode) object URI mappings} for patches
    * that I compute from command execution results.
    *
    * @return whether I compute object URI mappings
    *
    * @see #setNeedObjectURIMappings(boolean)
    * @see #getObjectURIFunction(JsonNode)
    */
   public boolean isNeedObjectURIMappings() { return NEED_OBJECT_URI_MAPPINGS.get(); }

}
