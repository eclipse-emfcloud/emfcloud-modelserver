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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
         // TODO support custom labels. See https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/160
         String label = "Json Patch";

         // CompoundCommands and multi-operation Json Patches
         // are not equivalent. Json Patch operations should be applied
         // sequentially. For this reason, we need to create a command, execute
         // it, and then create the next command. We use LazyCompoundCommand for this.
         SimpleLazyCompoundCommand command = new SimpleLazyCompoundCommand(label);
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
      throw new UnsupportedOperationException(
         "JsonPatch 'move' Operation is not supported yet. Use 'remove' and 'add' instead");
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
      Command result = null;
      if (setting.getEStructuralFeature() instanceof EReference) {
         // References
         EObject objectToAdd = getObjectToAdd(modelURI, resourceSet, value);
         if (objectToAdd == null) {
            throw new JsonPatchException("Invalid value specified for 'add' operation");
         }

         EStructuralFeature feature = setting.getEStructuralFeature();

         if (feature instanceof EReference) {
            EReference reference = (EReference) feature;
            if (reference.isContainment() && getResourceSet(objectToAdd) == resourceSet) {
               // This is actually a move. First, remove the Object from its original parent.
               // This is not required to actually apply the change (EMF will do it automatically),
               // however this is necessary to properly undo the change.
               result = RemoveCommand.create(getEditingDomain(objectToAdd), objectToAdd.eContainer(),
                  objectToAdd.eContainingFeature(),
                  objectToAdd);
            }
         }

         Command create = AddCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
            setting.getEStructuralFeature(), Collections.singleton(objectToAdd));
         result = result == null ? create : result.chain(create);
      } else {
         // Attributes
         Object emfValue = getEMFValue(modelURI, resourceSet, setting.getEStructuralFeature(), value);
         result = AddCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
            setting.getEStructuralFeature(), Collections.singleton(emfValue));
      }

      return result;
   }

   protected final ResourceSet getResourceSet(final EObject objectToAdd) {
      return objectToAdd == null || objectToAdd.eResource() == null ? null : objectToAdd.eResource().getResourceSet();
   }

   @SuppressWarnings("checkstyle:CyclomaticComplexity")
   protected EObject getObjectToAdd(final String modelURI, final ResourceSet resourceSet, final JsonNode value)
      throws JsonPatchException {
      JsonNode idAttr = value.get(JsonConstants.ID_ATTR);
      if (idAttr != null) {
         // If the ID is specified, we're adding an existing object to a list (or a different parent).
         // Find the existing object (or return null if the $id is invalid).
         URI eObjectURI = URI.createURI(idAttr.asText());
         return getEObject(modelURI, eObjectURI);
      }
      // Otherwise, create a new Object of the specified $type (or return null if the $type is invalid)
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

   /**
    * Return the EMF Value represented by the given JsonNode value.
    *
    * @param modelURI
    * @param resourceSet
    * @param feature
    * @param value
    * @return
    * @throws JsonPatchException
    */
   protected Object getEMFValue(final String modelURI, final ResourceSet resourceSet, final EStructuralFeature feature,
      final JsonNode value) throws JsonPatchException {
      if (feature instanceof EAttribute) {
         // Attributes
         if (feature.getEType() instanceof EEnum) {
            EEnum enumType = (EEnum) feature.getEType();
            EEnumLiteral literal = enumType.getEEnumLiteral(value.asText());
            if (literal != null) {
               return literal;
            }
         } else {
            return getPrimitiveEMFValue(value);
         }
      } else {
         // References
         JsonNode refNode = value.get(JsonConstants.REF_ATTR);
         JsonNode idNode = value.get(JsonConstants.ID_ATTR);
         if (refNode == null && idNode == null) {
            throw new JsonPatchException("Reference values should include a $ref or an $id attribute");
         }
         String objectId = refNode == null ? idNode.asText() : refNode.asText();
         return getEObject(modelURI, URI.createURI(objectId));
      }
      // TODO Support datatypes?
      // TODO Support multiple values? (value: [{value1}, {value2}] instead of value: {value1} )
      return null;
   }

   protected Object getPrimitiveEMFValue(final JsonNode value) {
      if (value == null || value.isNull()) {
         return null;
      } else if (value.isTextual()) {
         return value.asText();
      } else if (value.isBoolean()) {
         return value.asBoolean();
      } else if (value.isNumber()) {
         return value.asInt();
      }
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

   protected Resource getResource(final String modelURI) {
      return getResource(modelURI, null);
   }

   protected abstract Resource getResource(String modelURI, URI resourceURI);

   protected EObject getEObject(final String modelURI, final URI eObjectURI) {
      Resource resource = getResource(modelURI, eObjectURI.trimFragment());
      return resource.getEObject(eObjectURI.fragment());
   }

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
