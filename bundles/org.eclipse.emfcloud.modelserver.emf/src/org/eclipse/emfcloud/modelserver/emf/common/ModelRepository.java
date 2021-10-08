/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
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

   Optional<CCommandExecutionResult> undo(String modeluri);

   Optional<CCommandExecutionResult> redo(String modeluri);
}
