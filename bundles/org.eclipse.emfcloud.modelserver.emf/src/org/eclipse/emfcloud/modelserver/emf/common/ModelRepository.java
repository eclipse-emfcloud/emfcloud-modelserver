/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ModelRepository {
   void initialize();

   Optional<Resource> loadResource(String modeluri);

   Set<String> getRelativeModelUris();

   Set<String> getAbsoluteModelUris();

   Map<URI, EObject> getAllModels() throws IOException;

   boolean hasModel(String modeluri);

   Optional<EObject> getModel(String modeluri);

   Optional<EObject> getModelElementById(String modeluri, String elementid);

   Optional<EObject> getModelElementByName(String modeluri, String elementname);

   void addModel(String modeluri, EObject model) throws IOException;

   void deleteModel(String modeluri) throws IOException;

   /**
    * Closes a model, forgetting about its content and any current modification.
    * Model can be reopened later from its persisted state with a simple {@link #loadResource(String)} call.
    *
    * @param modeluri the URI of the model resource
    */
   void closeModel(String modeluri);

   boolean saveModel(String modeluri);

   boolean saveAllModels();

   boolean getDirtyState(String modeluri);

   /**
    * Replace a model with an update.
    *
    * @param modeluri the URI of the model to replace
    * @param model    the replacement
    * @return the {@code resource} that was replaced, or an empty optional if it
    *         does not exist
    */
   Optional<Resource> updateModel(String modeluri, EObject model);

   CCommandExecutionResult executeCommand(String modeluri, CCommand command) throws DecodingException;

   /**
    * Execute a command on the specified model. The command is specified using
    * the Json Patch syntax.
    *
    * @param modeluri
    *                     The URI of the model on which the command should be executed
    * @param jsonPatch
    *                     The command (Json Patch) to execute
    * @return
    * @throws JsonPatchException
    * @throws JsonPatchTestException
    */
   default CCommandExecutionResult executeCommand(final String modeluri, final ArrayNode jsonPatch)
      throws JsonPatchTestException, JsonPatchException {
      // TODO Return type for Json Patch should be a Json Patch?
      throw new UnsupportedOperationException("V2 API is not supported by this implementation");
   }

   Optional<CCommandExecutionResult> undo(String modeluri);

   Optional<CCommandExecutionResult> redo(String modeluri);
}
