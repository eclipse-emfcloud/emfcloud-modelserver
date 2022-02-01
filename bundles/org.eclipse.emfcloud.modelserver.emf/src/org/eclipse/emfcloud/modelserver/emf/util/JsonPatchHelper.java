/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics..
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.util;

import org.apache.log4j.Logger;
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
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.patch.AbstractJsonPatchHelper;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerEditingDomain;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.diff.JsonDiff;

/**
 * A Helper to create EMF Commands from a Json Patch.
 */
public class JsonPatchHelper extends AbstractJsonPatchHelper {

   protected static final Logger LOG = Logger.getLogger(JsonPatchHelper.class.getSimpleName());

   private final ModelResourceManager modelManager;
   private final ServerConfiguration serverConfiguration;

   public JsonPatchHelper(final ModelResourceManager modelManager, final ServerConfiguration serverConfiguration) {
      this.modelManager = modelManager;
      this.serverConfiguration = serverConfiguration;
   }

   @Override
   protected TransactionalEditingDomain getEditingDomain(final EObject eObject) {
      ResourceSet resourceSet = eObject.eResource().getResourceSet();
      return modelManager.getEditingDomain(resourceSet);
   }

   @Override
   protected Resource getResource(final String modelURI, final URI resourceURI) {
      // FIXME We should make sure that modelURI and resourceURI are part of the same
      // editing domain (resourceSet). For single-resource use cases, they should be identical;
      // but for cross-resources models, they might differ.
      // Currently, trying to access a resource from a different resourceSet will cause
      // a write transaction exception
      URI uri = resourceURI.resolve(serverConfiguration.getWorkspaceRootURI());
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
      // TODO Support multiple resource patches
      return JsonDiff.asJson(oldModel, newModel);
   }

   // public JsonNode getCurrentModel(final String modeluri, final Optional<ArrayNode> jsonPatch)
   // throws EncodingException {
   // Set<Resource> affectedResources = new HashSet<>();
   // modelManager.loadResource(modeluri).ifPresent(affectedResources::add);
   //
   // // TODO Support multiple resource models
   // // jsonPatch.ifPresent(patch -> {
   // // // Determine the affected resources by looking at the path of each patch operation
   // // for (JsonNode patchElement : patch) {
   // // String element = patchElement.get("path").asText();
   // // int index = element.indexOf('#');
   // // if (index >= 0) {
   // // String resourceURI = element.substring(0, index);
   // // Resource resource = getResource(modeluri, URI.createURI(resourceURI));
   // // affectedResources.add(resource);
   // // }
   // // }
   // // });
   // // if (affectedResources.size() > 1) {
   // // System.err.println("!!Multiple resources affected by patch!!");
   // // }
   //
   // Resource resource = affectedResources.iterator().next();
   // Codec codec = new JsonCodecV2();
   // return codec.encode(resource.getContents().get(0));
   // }

   public JsonNode getCurrentModel(final EObject root) throws EncodingException {
      Codec codec = new JsonCodecV2();
      return codec.encode(root);
   }

   public JsonNode getJsonPatch(final EObject root, final CCommandExecutionResult result) throws EncodingException {
      JsonNode newModel = getCurrentModel(root);
      ChangeDescription cd = (ChangeDescription) result.getChangeDescription();
      ModelServerEditingDomain editingDomain = modelManager.getEditingDomain(root.eResource().getResourceSet());
      JsonNode oldModel = null;
      try {
         // Compute the old model by reverting the current change. We need to do that in a write-transaction.
         // Note: we could also apply it on a read-only copy of the model, but this could potentially modify
         // the IDs (which are part of the Resource; not of the EObjects), causing unexpected patch changes.
         InternalTransaction transaction = editingDomain.startTransaction(false, null);
         try {
            cd.applyAndReverse();
            oldModel = getCurrentModel(root);
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
      return diffModel(oldModel, newModel);
   }

   protected void rollback(final Transaction tx, final ModelServerEditingDomain domain) {
      while (tx.isActive()) {
         Transaction active = domain.getActiveTransaction();
         active.rollback();
      }
   }

}
