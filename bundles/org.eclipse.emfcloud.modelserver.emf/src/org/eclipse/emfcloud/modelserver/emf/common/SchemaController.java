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

import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;

import com.google.inject.Inject;

import io.javalin.http.Context;

public class SchemaController {

   private final ModelRepository modelRepository;
   private final SchemaRepository schemaRepository;
   private final JsonSchemaConverter jsonSchemaconverter;

   @Inject
   public SchemaController(final ModelRepository modelRepository, final SchemaRepository schemaRepository,
      final JsonSchemaConverter jsonSchemaCreator) {
      this.modelRepository = modelRepository;
      this.schemaRepository = schemaRepository;
      this.jsonSchemaconverter = jsonSchemaCreator;
   }

   public void getTypeSchema(final Context ctx, final String modeluri) {
      this.modelRepository.getModel(modeluri).ifPresentOrElse(
         instance -> ctx.json(JsonResponse.success(this.jsonSchemaconverter.from(instance))),
         () -> {
            ctx.status(404);
            ctx.json(JsonResponse.error(String.format("Type schema for '%s' not found!", ctx.queryParam("modeluri"))));
         });
   }

   public void getUISchema(final Context ctx, final String schemaname) {
      this.schemaRepository.loadUISchema(schemaname).ifPresentOrElse(
         jsonNode -> ctx.json(JsonResponse.success(jsonNode)),
         () -> {
            ctx.status(404);
            ctx.json(JsonResponse.error(String.format("UI schema '%s' not found!", ctx.queryParam("schemaname"))));
         });
   }

}
