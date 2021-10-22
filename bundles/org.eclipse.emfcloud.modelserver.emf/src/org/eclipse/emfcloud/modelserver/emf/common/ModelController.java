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

   void executeCommand(Context ctx, String modelURI);

   void undo(Context ctx, String modeluri);

   void redo(Context ctx, String modeluri);
}
