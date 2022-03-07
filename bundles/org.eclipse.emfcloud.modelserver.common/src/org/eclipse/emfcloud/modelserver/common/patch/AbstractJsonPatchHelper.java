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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
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

   private static final Logger LOG = LogManager.getLogger(AbstractJsonPatchHelper.class);

   static final String TEST = "test";
   static final String MOVE = "move";
   static final String REMOVE = "remove";
   static final String ADD = "add";
   static final String OP = "op";
   static final String REPLACE = "replace";
   static final String ANY_INDEX = "-";
   static final int NO_INDEX = CommandParameter.NO_INDEX;

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
         LazyCompoundCommand command = createCompoundCommand(modelURI, label);
         for (JsonNode patchAction : jsonPatch) {
            command.append(() -> getCommand(modelURI, resourceSet, patchAction));
         }
         return command;
      }
   }

   /**
    * <p>
    * Return a new empty LazyCompoundCommand.
    * </p>
    *
    * <p>
    * Default implementation is not undoable. Subclasses should override.
    * </p>
    *
    * @param modelURI
    * @param label
    * @return
    */
   protected LazyCompoundCommand createCompoundCommand(final String modelURI, final String label) {
      return new SimpleLazyCompoundCommand(label);
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
      String jsonPath = path.asText();
      SettingValue setting = getSetting(modelURI, resourceSet, jsonPath);

      // If the index is present, we remove a value from a list
      if (setting.getIndex().isPresent()) {
         int index = setting.getIndex().get();
         if (index == CommandParameter.NO_INDEX) {
            // If the index is "-", remove the last value from the list
            Object currentValue = setting.getEObject().eGet(setting.getFeature());
            if (currentValue instanceof Collection) {
               index = ((Collection<?>) currentValue).size() - 1;
            }
         }
         return RemoveCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
            setting.getFeature(), index);
      }

      // If the feature is present, but index is absent, we unset the value (i.e. set it to default value)
      if (setting.getFeature() != null) {
         Object defaultValue = getDefaultValue(setting.getFeature());
         return SetCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(), setting.getFeature(),
            defaultValue);
      }

      // If neither the index nor the feature are present, we delete an Object
      EObject eObjectToDelete = setting.getEObject();
      EObject parent = eObjectToDelete.eContainer();
      EStructuralFeature feature = eObjectToDelete.eContainingFeature();
      return RemoveCommand.create(getEditingDomain(eObjectToDelete), parent, feature,
         Collections.singleton(eObjectToDelete));
   }

   protected Object getDefaultValue(final EStructuralFeature feature) {
      Object defaultValue = feature.getDefaultValue();
      if (defaultValue == null && feature.isMany()) {
         // Special case for lists default value.
         // If the feature represents a collection, and 'null' is specified as the default
         // value, we need to use an empty collection instead. Otherwise, SetCommand(object, feature, null)
         // will cause a NPE.
         return Collections.emptyList();
      }
      return defaultValue;
   }

   protected Command getAddCommand(final String modelURI, final ResourceSet resourceSet, final JsonNode patchAction)
      throws JsonPatchException {
      JsonNode path = patchAction.get("path");
      String jsonPath = path.asText();
      SettingValue setting = getSetting(modelURI, resourceSet, jsonPath);
      if (setting.getFeature() == null) {
         throw new JsonPatchException(
            "Invalid operation: cannot add element without specifying a feature. Path: " + jsonPath);
      }

      JsonNode value = patchAction.get("value");
      Command result;
      if (setting.getFeature() instanceof EReference) {
         // References
         result = getAddReferenceCommand(modelURI, resourceSet, setting, value);
      } else {
         // Attributes
         Object emfValue = getEMFValue(modelURI, resourceSet, setting.getFeature(), value);
         result = AddCommand.create(getEditingDomain(setting.getEObject()), setting.getEObject(),
            setting.getFeature(), Collections.singleton(emfValue));
      }

      return result;
   }

   /**
    * Create an Add Command for references.
    *
    * @param modelURI
    *                       The URI of the model being edited.
    * @param resourceSet
    *                       The resourceSet containing the edited model.
    * @param setting
    *                       The setting representing the object/reference to edit.
    * @param value
    *                       The value to set.
    * @return
    *         The Add command to apply the changes to the model.
    * @throws JsonPatchException
    */
   protected Command getAddReferenceCommand(final String modelURI, final ResourceSet resourceSet,
      final SettingValue setting,
      final JsonNode value) throws JsonPatchException {
      EObject objectToAdd = getObjectToAdd(modelURI, resourceSet, value);
      if (objectToAdd == null) {
         throw new JsonPatchException("Invalid value specified for 'add' operation");
      }

      EStructuralFeature feature = setting.getFeature();

      Command result = null;
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
         setting.getFeature(), Collections.singleton(objectToAdd));
      result = result == null ? create : result.chain(create);
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
      SettingValue setting = getSetting(modelURI, resourceSet, path.asText());
      Object emfValue = getEMFValue(modelURI, resourceSet, setting.getFeature(), value);
      if (setting.getIndex().isPresent()) {
         // TODO Support replace in lists
         // https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/162
         throw new UnsupportedOperationException("Index-based 'replace' operations are not supported yet");
      }
      Command command = SetCommand.create(getEditingDomain(setting.getEObject()),
         setting.getEObject(), setting.getFeature(), emfValue);
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
            getEMFEnumValue((EEnum) feature.getEType(), value);
         } else {
            return getPrimitiveEMFValue((EAttribute) feature, value);
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
      // TODO Support custom datatypes & array-values. See
      // https://github.com/eclipse-emfcloud/emfcloud-modelserver/issues/162
      return null;
   }

   protected EEnumLiteral getEMFEnumValue(final EEnum enumType, final JsonNode value) throws JsonPatchException {
      if (value.isTextual()) {
         EEnumLiteral literal = enumType.getEEnumLiteral(value.asText());
         if (literal != null) {
            return literal;
         }
         throw new JsonPatchException("Invalid enum literal value for EEnum property: " + value.toString());
      }
      throw new JsonPatchException("Unexpected value for EEnum property: " + value.toString());
   }

   protected Object getPrimitiveEMFValue(final EAttribute feature, final JsonNode value) {
      if (value == null || value.isNull()) {
         return null;
      } else if (value.isTextual()) {
         return value.asText();
      } else if (value.isBoolean()) {
         return value.asBoolean();
      } else if (value.isNumber()) {
         return getJavaNumberValue(feature, value);
      }
      return null;
   }

   protected Object getJavaNumberValue(final EAttribute feature, final JsonNode value) {
      if (feature.getEType() instanceof EDataType) {
         EDataType eType = (EDataType) feature.getEType();
         switch (eType.getInstanceTypeName()) {
            case "int":
               return value.asInt();
            case "long":
               return value.asLong();
            case "float":
               return (float) value.asDouble();
            case "double":
               return value.asDouble();
            default:
               return null;
         }
      }
      return null;
   }

   /**
    * <p>
    * Remove the EMF {@link Setting} corresponding to the specified Json Path.
    * </p>
    * <p>
    * This method supports custom Json path that include the EMF Resource and Object ID,
    * as well as standard Json Pointers.
    * </p>
    *
    * @param modelURI
    * @param resourceSet
    * @param jsonPath
    *                       <ul>
    *                       <li>Custom Format:
    *                       <code>&lt;resource&gt;#&lt;objectfragment&gt;/&lt;featureName&gt;</code></li>
    *                       <li>Standard Format: <code>path/to/object/featureName</code></li>
    *                       </ul>
    * @return
    * @throws JsonPatchException
    */
   protected SettingValue getSetting(final String modelURI, final ResourceSet resourceSet,
      final String jsonPath)
      throws JsonPatchException {
      if (jsonPath.contains("#")) {
         // EMF-like Path
         return getSettingFromCustomPath(modelURI, jsonPath);
      }
      // Json Pointer
      return getSettingFromJsonPointer(modelURI, resourceSet, jsonPath);
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
   protected SettingValue getSettingFromJsonPointer(final String modelURI, final ResourceSet resourceSet,
      final String jsonPath) throws JsonPatchException {
      String[] segments = jsonPath.split("/");

      Resource resource = getResource(modelURI);

      final EObject rootElement = resource.getContents().get(0);
      Object currentValue = rootElement;
      EStructuralFeature featureToEdit = null;
      EObject eObjectToEdit = null;
      for (String segment : segments) {
         if (segment.isBlank()) {
            // We typically expect the first segment
            // to be blank, and sometimes the last one as well (e.g. "/")
            continue;
         }

         Optional<Integer> indexSegment = getIndexSegment(segment);
         if (indexSegment.isPresent()) {
            // Index-based value
            if (currentValue instanceof List) {
               currentValue = getValueFromList(jsonPath, segments, (List<?>) currentValue, indexSegment.get());
            } else {
               throw new JsonPatchException();
            }
         } else {
            // Feature-based value
            String featureName = segment;
            if (currentValue instanceof EObject) {
               eObjectToEdit = (EObject) currentValue;
               featureToEdit = getFeature(eObjectToEdit, featureName);
               currentValue = eObjectToEdit.eGet(featureToEdit);
            } else {
               throw new JsonPatchException();
            }
         }
      }

      String lastSegment = segments[segments.length - 1];
      Optional<Integer> index = getIndexSegment(lastSegment);
      return new SettingValue(eObjectToEdit, featureToEdit, index);
   }

   /**
    * Parse a Json Path segment, and return the corresponding Integer value if it is
    * valid, or {@link Optional#empty()} if the segment doesn't represent an index
    * value.
    *
    * @param jsonPathSegment
    *                           The Json Path segment to parse.
    * @return
    */
   protected Optional<Integer> getIndexSegment(final String jsonPathSegment) {
      if (ANY_INDEX.equals(jsonPathSegment)) {
         return Optional.of(CommandParameter.NO_INDEX);
      }
      try {
         int index = Integer.parseInt(jsonPathSegment);
         return Optional.of(index);
      } catch (NumberFormatException ex) {
         // Not a number; ignore the exception and return an empty index
         return Optional.empty();
      }
   }

   protected Object getValueFromList(final String jsonPath, final String[] segments, final List<?> currentValue,
      final int index) throws JsonPatchException {
      if (index == NO_INDEX) {
         return currentValue.get(currentValue.size() - 1);
      }
      return currentValue.get(index);
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
    *                       Format: &lt;resource&gt;#&lt;objectfragment&gt;/&lt;featureName&gt;[/&lt;index&gt;]
    * @return
    * @throws JsonPatchException
    */
   protected SettingValue getSettingFromCustomPath(final String modelURI, final String jsonPath)
      throws JsonPatchException {
      int lastSegmentPos = jsonPath.lastIndexOf('/');
      if (lastSegmentPos < 0 || lastSegmentPos >= jsonPath.length()) {
         throw new JsonPatchException("Failed to parse Json Path: " + jsonPath);
      }

      // The path may represent an EObject (without feature or index). In that case,
      // directly return it.
      EObject eObject = getEObject(modelURI, URI.createURI(jsonPath));
      if (eObject != null) {
         return new SettingValue(eObject);
      }

      // XXX if the edited object is the root element, its positional URI will be '/'
      // Since we expect path as <id>/<feature>, the expected path in that case is
      // "//feature" and not "/feature" (Although "/feature" would be a valid Json Pointer path)

      String lastSegment = jsonPath.substring(lastSegmentPos + 1);

      // First, check if the path ends with an index.
      Optional<Integer> index = getIndexSegment(lastSegment);

      final String objectURI, featureName;

      if (index.isPresent()) {
         // Index is present, the path is in the form modeluri#objectfragment/featureName/index
         String objectAndFeaturePath = jsonPath.substring(0, lastSegmentPos);
         lastSegmentPos = objectAndFeaturePath.lastIndexOf('/');
         objectURI = objectAndFeaturePath.substring(0, lastSegmentPos);
         featureName = objectAndFeaturePath.substring(lastSegmentPos + 1);
      } else {
         // Else: no index, we have a path representing Object + Feature
         objectURI = jsonPath.substring(0, lastSegmentPos);
         featureName = jsonPath.substring(lastSegmentPos + 1);
      }

      URI eObjectURI = URI.createURI(objectURI);
      eObject = getEObject(modelURI, eObjectURI);

      if (eObject == null) {
         throw new JsonPatchException("Invalid Object path: " + objectURI);
      }

      EStructuralFeature feature = null;
      if (featureName != null) {
         feature = getFeature(eObject, featureName);
      }

      return new SettingValue(eObject, feature, index);
   }

   protected EStructuralFeature getFeature(final EObject eObject, final String featureName) throws JsonPatchException {
      EStructuralFeature feature = eObject.eClass().getEStructuralFeature(featureName);
      if (feature == null) {
         throw new JsonPatchException("Invalid Object property: " + featureName);
      }
      return feature;
   }

   protected EditingDomain getEditingDomain(final EObject eObject) {
      return AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
   }

   protected Resource getResource(final String modelURI) {
      return getResource(modelURI, null);
   }

   protected abstract Resource getResource(String modelURI, URI resourceURI);

   @SuppressWarnings("checkstyle:IllegalExceptionCatch")
   protected EObject getEObject(final String modelURI, final URI eObjectURI) {
      Resource resource = getResource(modelURI, eObjectURI.trimFragment());
      if (resource == null) {
         return null;
      }
      try {
         return resource.getEObject(eObjectURI.fragment());
      } catch (Exception ex) {
         // The object URI is not valid. Ignore the exception and
         // return null.
         return null;
      }
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

   /**
    * Represents a value to edit, in the form EObject + Feature, with
    * an optional Index for list items, or a referenced element (e.g.
    * an EObject to remove, in which case there will be no Feature or Index)
    */
   public static class SettingValue {

      private final EObject eObject;

      private final EStructuralFeature feature;

      private final Optional<Integer> index;

      public SettingValue(final EObject eObject) {
         this(eObject, null);
      }

      public SettingValue(final EObject eObject, final EStructuralFeature feature) {
         this(eObject, feature, Optional.empty());
      }

      public SettingValue(final EObject eObject, final EStructuralFeature feature, final int index) {
         this(eObject, feature, Optional.of(index));
      }

      public SettingValue(final EStructuralFeature.Setting setting) {
         this(setting.getEObject(), setting.getEStructuralFeature());
      }

      public SettingValue(final EStructuralFeature.Setting setting, final int index) {
         this(setting.getEObject(), setting.getEStructuralFeature(), index);
      }

      public SettingValue(final EObject eObject, final EStructuralFeature feature,
         final Optional<Integer> index) {
         if (eObject == null) {
            throw new IllegalArgumentException("EObject must be specified");
         }
         this.eObject = eObject;
         this.feature = feature;
         this.index = index;
      }

      /**
       * @return
       *         The {@link EObject} represented by a JsonPointer path or
       *         a custom EMF-based Json Path.
       */
      public EObject getEObject() { return eObject; }

      /**
       * @return
       *         The {@link EStructuralFeature} to edit.
       */
      public EStructuralFeature getFeature() { return feature; }

      /**
       *
       * @return
       *         For list-operations, the index to edit.
       */
      public Optional<Integer> getIndex() { return index; }
   }

}
