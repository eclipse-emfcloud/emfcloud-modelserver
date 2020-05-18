/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchema;

import com.google.inject.Inject;

import io.javalin.http.Context;

public class SchemaController {

   @Inject
   private ModelRepository modelRepository;

   @Inject
   private SchemaRepository schemaRepository;

   public void getSchema(final Context ctx, final String modeluri) {
      this.modelRepository.getModel(modeluri).ifPresentOrElse(
         instance -> ctx.json(JsonResponse.success(JsonSchema.from(instance.eClass()))),
         () -> ctx.status(404)
            .json(JsonResponse.error(String.format("Schema for '%s' not found!", ctx.queryParam("modeluri")))));
   }

   public void getTypeSchema(final Context ctx, final String modeluri) {
      this.modelRepository.getModel(modeluri).ifPresentOrElse(
         instance -> ctx.json(JsonResponse.success(JsonSchema.from(instance))),
         () -> ctx.status(404)
            .json(JsonResponse.error(String.format("Type schema for '%s' not found!", ctx.queryParam("modeluri")))));
   }

   public void getJsonFormsUISchema(final Context ctx, final String schemaname) {
      this.schemaRepository.loadUISchema(schemaname).ifPresentOrElse(
         jsonNode -> {
            ctx.json(JsonResponse.success(jsonNode));
         },
         () -> ctx.status(404)
            .json(JsonResponse.error(String.format("UI schema '%s' not found!", ctx.queryParam("schemaname")))));
   }

}
