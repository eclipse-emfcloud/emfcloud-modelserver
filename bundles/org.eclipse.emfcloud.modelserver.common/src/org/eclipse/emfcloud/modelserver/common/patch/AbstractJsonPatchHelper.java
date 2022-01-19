/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics..
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * A Helper to create EMF Commands from a Json Patch.
 */
public abstract class AbstractJsonPatchHelper {

   protected static final Logger LOG = Logger.getLogger(AbstractJsonPatchHelper.class.getSimpleName());

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
            return getAddCommand(modelURI, resourceSet, patchAction);
         case REMOVE:
            return getRemoveCommand(modelURI, resourceSet, patchAction);
         case MOVE:
            return getMoveCommand(modelURI, resourceSet, patchAction);
         case TEST:
            return testAction(resourceSet, patchAction);
         default:
            throw new JsonPatchException("Unsupported Json Patch operation: " + op);
      }
   }

   protected Command getMoveCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      // TODO Auto-generated method stub
      return null;
   }

   protected Command getRemoveCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      JsonNode path = patchAction.get("path");
      // If the last segment of the path is an integer, we remove a value from a list (modelURI#objectID/feature/index).
      // Otherwise, we treat the path as an ObjectURI and remove this object from the model (modelURI#objectID).
      String jsonPath = path.asText();
      int lastSegment = jsonPath.lastIndexOf('/');
      Optional<Integer> index;
      try {
         index = Optional.of(Integer.parseInt(jsonPath.substring(lastSegment + 1)));
      } catch (NumberFormatException ex) {
         index = Optional.empty();
      }

      if (index.isPresent()) {
         String objectPath = jsonPath.substring(0, lastSegment);
         EStructuralFeature.Setting setting = getSetting(modelURI, resourceSet, objectPath);
         int intValue = index.get();
         return RemoveCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
            setting.getEStructuralFeature(), intValue);
      }

      URI objectURI = URI.createURI(jsonPath);
      Resource resource = getResource(modelURI, objectURI.trimFragment());
      EObject eObjectToDelete = resource.getEObject(objectURI.fragment());
      if (eObjectToDelete == null) {
         return UnexecutableCommand.INSTANCE;
      }

      EObject parent = eObjectToDelete.eContainer();
      EStructuralFeature feature = eObjectToDelete.eContainingFeature();
      return RemoveCommand.create(getEditingDomain(eObjectToDelete), parent, feature,
         Collections.singleton(eObjectToDelete));
   }

   protected Command getAddCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      JsonNode path = patchAction.get("path");
      EStructuralFeature.Setting setting = getSetting(modelURI, resourceSet, path.asText());
      JsonNode value = patchAction.get("value");
      EObject toCreate = getObjectToCreate(modelURI, resourceSet, value);
      if (toCreate == null) {
         throw new JsonPatchException("Invalid value specified for 'add' operation");
      }

      return AddCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
         setting.getEStructuralFeature(), Collections.singleton(toCreate));
   }

   protected EObject getObjectToCreate(final String modelURI, final ResourceSet resourceSet, final JsonNode value) {
      String type = value.get(JsonConstants.TYPE_ATTR).asText();
      if (type == null) {
         return null;
      }
      EClassifier eClassifier = getEClass(modelURI, resourceSet, type);
      if (eClassifier == null) {
         return null;
      }

      if (eClassifier instanceof EClass) {
         EClass eClass = (EClass) eClassifier;
         EFactory eFactoryInstance = eClass.getEPackage().getEFactoryInstance();
         EObject newObject = eFactoryInstance.create(eClass);
         Iterator<Entry<String, JsonNode>> fields = value.fields();
         while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            if (field.getKey().startsWith(JsonConstants.METADATA_PREFIX)) {
               continue;
            }
            EStructuralFeature feature = eClass.getEStructuralFeature(field.getKey());
            if (feature == null) {
               LOG.warn("Ignored unknown field: " + field.getKey());
               continue;
            }

            Object emfValue = getEMFValue(modelURI, resourceSet, feature, field.getValue());
            newObject.eSet(feature, emfValue);
         }
         return newObject;
      }

      // Other EClassifiers: Enum and Datatype can't be instantiated (But they may be used to specify values with
      // 'replace')

      return null;
   }

   protected EClassifier getEClass(final String modelURI, final ResourceSet resourceSet, final String type) {
      EObject eObject = resourceSet.getEObject(URI.createURI(type), false);
      if (eObject instanceof EClassifier) {
         return (EClassifier) eObject;
      }
      return null;
   }

   protected Command getReplaceCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      JsonNode path = patchAction.get("path");
      JsonNode value = patchAction.get("value");
      EStructuralFeature.Setting setting = getSetting(modelURI, resourceSet, path.asText());
      Object emfValue = getEMFValue(modelURI, resourceSet, setting.getEStructuralFeature(), value);
      Command command = SetCommand.create(getEditingDomain(setting.getEObject()),
         setting.getEObject(), setting.getEStructuralFeature(), emfValue);
      return command;
   }

   protected Object getEMFValue(final String modelURI, final ResourceSet resourceSet, final EStructuralFeature feature,
      final JsonNode value) {
      if (value == null || value.isNull()) {
         return null;
      } else if (value.isTextual()) {
         return value.asText();
      } else if (value.isBoolean()) {
         return value.asBoolean();
      } else if (value.isNumber()) {
         return value.asInt();
      }
      // TODO Check the feature type for consistency with the value type
      // TODO Support object references {"$type": "...", "$ref": "uri"}
      // TODO Support datatypes and enums
      return null;
   }

   /**
    * <p>
    * Remove the EMF {@link Setting} corresponding to the specified Json Path.
    * </p>
    * <p>
    * The Json Path should include the modelURI and the ObjectID (as specified
    * by the $id attribute in the Json model): <code>path/to/model#my/object/id/feature</code>.
    * </p>
    * <p>
    * Standard Json Paths are currently not supported, but may be added in the future.
    * </p>
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

   protected abstract Resource getResource(String modelURI, URI resourceURI);

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
