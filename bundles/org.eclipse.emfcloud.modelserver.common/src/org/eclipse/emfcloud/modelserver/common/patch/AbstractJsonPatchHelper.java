/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * A Helper to create EMF Commands from a Json Patch.
 */
public abstract class AbstractJsonPatchHelper {
   private static final String TEST = "test";
   private static final String MOVE = "move";
   private static final String REMOVE = "remove";
   private static final String ADD = "add";
   private static final String OP = "op";
   private static final String REPLACE = "replace";

   /**
    * Return an EMF Command that is equivalent to the specified jsonPatch.
    *
    * @param modelURI
    *                       The modeluri on which the patch will be applied.
    * @param resourceSet
    *                       The resourceSet on which the patch should be applied.
    * @param jsonPatch
    *                       The json patch to apply.
    * @return
    *         The EMF Command corresponding to the specified jsonPatch.
    * @throws JsonPatchTestException
    *                                   If a json patch 'test' operation fails.
    * @throws JsonPatchException
    *                                   If the jsonPatch object is invalid (e.g. unsupported operation, missing
    *                                   attribute...)
    */
   public Command getCommand(final String modelURI, final ResourceSet resourceSet, final ArrayNode jsonPatch)
      throws JsonPatchTestException, JsonPatchException {
      if (jsonPatch.isEmpty()) {
         return IdentityCommand.INSTANCE;
      } else if (jsonPatch.size() == 1) {
         return getCommand(modelURI, resourceSet, jsonPatch.get(0));
      } else {
         // CompoundCommands and multi-operation Json Patches
         // are not equivalent. Json Patch operations should be applied
         // sequentially. For this reason, we need to create a command, execute
         // it, and then create the next command. We use LazyCompoundCommand for this.
         SimpleLazyCompoundCommand command = new SimpleLazyCompoundCommand("Json Patch"); // TODO We should add a label
                                                                                          // to Json Patches
         for (JsonNode patchAction : jsonPatch) {
            command.append(() -> getCommand(modelURI, resourceSet, patchAction));
         }
         return command;
      }
   }

   protected Command getCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchTestException, JsonPatchException {
      String op = patchAction.get(OP).asText();
      switch (op) {
         case REPLACE:
            return getReplaceCommand(modelURI, resourceSet, patchAction);
         case ADD:
            return getAddCommand(resourceSet, patchAction);
         case REMOVE:
            return getRemoveCommand(resourceSet, patchAction);
         case MOVE:
            return getMoveCommand(resourceSet, patchAction);
         case TEST:
            return testAction(resourceSet, patchAction);
         default:
            throw new JsonPatchException("Unsupported Json Patch operation: " + op);
      }
   }

   protected Command getMoveCommand(final ResourceSet resourceSet, final JsonNode patchAction) {
      // TODO Auto-generated method stub
      return null;
   }

   protected Command getRemoveCommand(final ResourceSet resourceSet, final JsonNode patchAction) {
      // TODO Auto-generated method stub
      return null;
   }

   protected Command getAddCommand(final ResourceSet resourceSet, final JsonNode patchAction) {
      // TODO Auto-generated method stub
      return null;
   }

   protected Command getReplaceCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      JsonNode path = patchAction.get("path");
      JsonNode value = patchAction.get("value");
      EStructuralFeature.Setting setting = getSetting(modelURI, resourceSet, path.asText());
      Object emfValue = getEMFValue(value);
      Command command = SetCommand.create(getEditingDomain(setting.getEObject()),
         setting.getEObject(), setting.getEStructuralFeature(), emfValue);
      return command;
   }

   protected Object getEMFValue(final JsonNode value) {
      if (value == null || value.isNull()) {
         return null;
      } else if (value.isTextual()) {
         return value.asText();
      } else if (value.isBoolean()) {
         return value.asBoolean();
      } else if (value.isNumber()) {
         return value.asInt();
      }
      // TODO Support Objects
      // Objects are defined as {$type: "eClassURI"} (with additional attributes? But without nested nodes)
      // Further changes will be applied with Replace operations
      return null;
   }

   /**
    *
    * @param modelURI
    * @param resourceSet
    * @param jsonPath
    *                       Format: <resource>#<objectfragment>/<featureName>
    * @return
    * @throws JsonPatchException
    */
   protected EStructuralFeature.Setting getSetting(final String modelURI, final ResourceSet resourceSet,
      final String jsonPath)
      throws JsonPatchException {
      int lastSegment = jsonPath.lastIndexOf('/');
      if (lastSegment < 0 || lastSegment >= jsonPath.length()) {
         throw new JsonPatchException("Failed to parse Json Path: " + jsonPath);
      }
      // XXX if the edited object is the root element, its positional URI will be '/'
      // Since we expect path as <id>/<feature>, the expected path in that case is
      // "//feature" and not "/feature"
      String objectURI = jsonPath.substring(0, lastSegment);
      String featureName = jsonPath.substring(lastSegment + 1);
      URI eObjectURI = URI.createURI(objectURI);
      EObject eObject = null;
      if (eObjectURI.isRelative()) {
         URI resourceURI = eObjectURI.trimFragment();
         Resource resource = getResource(modelURI, resourceURI);
         eObject = resource.getEObject(eObjectURI.fragment());
      }
      if (eObject == null) {
         throw new JsonPatchException("Invalid Object path: " + objectURI);
      }
      EStructuralFeature feature = eObject.eClass().getEStructuralFeature(featureName);
      if (feature == null) {
         throw new JsonPatchException("Invalid Object property: " + featureName);
      }

      if (eObject instanceof InternalEObject) {
         return ((InternalEObject) eObject).eSetting(feature);
      }

      throw new JsonPatchException();
   }

   protected EditingDomain getEditingDomain(final EObject eObject) {
      return AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
   }

   protected abstract Resource getResource(final String modelURI, final URI resourceURI);

   protected Command testAction(final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchTestException {
      boolean isValid = true;
      // TODO ... implement actual test
      // Should we throw an exception, or return an UnexecutableCommand?
      if (!isValid) {
         throw new JsonPatchTestException(patchAction);
      }
      return IdentityCommand.INSTANCE;
   }
}
