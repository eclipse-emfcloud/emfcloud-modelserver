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

import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;

import io.javalin.http.Context;

public interface ModelController {
   void create(Context ctx, String modeluri);

   void delete(Context ctx, String modeluri);

   void close(Context ctx, String modeluri);

   void getAll(Context ctx);

   void getOne(Context ctx, String modeluri);

   void getModelElementById(Context ctx, String modeluri, String elementid);

   void getModelElementByName(Context ctx, String modeluri, String elementname);

   void getModelUris(Context ctx);

   void update(Context ctx, String modeluri);

   void save(Context ctx, String modeluri);

   void saveAll(Context ctx);

   void validate(Context ctx, String modeluri);

   void getValidationConstraints(Context ctx, String modeluri);

   /**
    * V1 version of the executeCommand API. This method expects
    * a {@link CCommand} payload, serialized as Json or XMI (Or
    * any application-specific format).
    *
    * @param ctx
    * @param modelURI
    */
   void executeCommand(Context ctx, String modelURI);

   /**
    * V2 version of the executeCommand API. This methods expects
    * a Json Object with a "type" attribute, where type may be
    * either {@link ModelServerPathsV2#JSON_PATCH} or
    * {@link ModelServerPathsV2#EMF_COMMAND}, and a "data" attribute
    * representing a Command or a Patch, in a format that corresponds
    * to the specified type.
    *
    * @param ctx
    * @param modelURI
    */
   default void executeCommandV2(final Context ctx, final String modelURI) {
      throw new UnsupportedOperationException("V2 API is not supported by this implementation");
   }

   void undo(Context ctx, String modeluri);

   void redo(Context ctx, String modeluri);
}
