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

import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.notFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.success;

import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;

import com.google.inject.Inject;

import io.javalin.http.Context;

public class DefaultSchemaController implements SchemaController {

   private final ModelRepository modelRepository;
   private final SchemaRepository schemaRepository;
   private final JsonSchemaConverter jsonSchemaConverter;

   @Inject
   public DefaultSchemaController(final ModelRepository modelRepository, final SchemaRepository schemaRepository,
      final JsonSchemaConverter jsonSchemaCreator) {
      this.modelRepository = modelRepository;
      this.schemaRepository = schemaRepository;
      this.jsonSchemaConverter = jsonSchemaCreator;
   }

   @Override
   public void getTypeSchema(final Context ctx, final String modeluri) {
      this.modelRepository.getModel(modeluri).ifPresentOrElse(
         instance -> success(ctx, this.jsonSchemaConverter.from(instance)),
         () -> notFound(ctx, "Type schema for '%s' not found!", modeluri));
   }

   @Override
   public void getUiSchema(final Context ctx, final String schemaname) {
      this.schemaRepository.loadUiSchema(schemaname).ifPresentOrElse(
         jsonNode -> success(ctx, jsonNode),
         () -> notFound(ctx, "UI schema for '%s' not found!", schemaname));
   }

}
