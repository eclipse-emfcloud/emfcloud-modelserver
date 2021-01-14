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

import static org.eclipse.emfcloud.modelserver.jsonschema.Json.prop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit tests for the {@link SchemaRepository} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSchemaRepositoryTest {

   private SchemaRepository schemaRepository;
   private ServerConfiguration serverConfiguration;

   @Before
   public void before() {
      serverConfiguration = mock(ServerConfiguration.class);
      when(serverConfiguration.getUiSchemaFolderURI())
         .thenReturn(URI.createFileURI(getCWD().getAbsolutePath() + "/ui-schemas/"));
      schemaRepository = new DefaultSchemaRepository(serverConfiguration);
   }

   @Test
   public void loadUiSchema_schemaAvailable() throws IOException {
      final JsonNode expectedUiSchema = Json.object(
         prop("type", Json.text("VerticalLayout")),
         prop("elements", Json.array(
            Json.object(
               prop("type", Json.text("Label")),
               prop("text", Json.text("Machine"))),
            Json.object(
               prop("type", Json.text("Control")),
               prop("label", Json.text("Name")),
               prop("scope", Json.text("#/properties/name"))))));

      Optional<JsonNode> actualUiSchema = schemaRepository.loadUiSchema("machine");

      assertTrue(actualUiSchema.isPresent());
      assertEquals(expectedUiSchema, actualUiSchema.get());
   }

   @Test
   public void loadUiSchema_schemaUnvailable() throws IOException {
      assertTrue(schemaRepository.loadUiSchema("machine2").isEmpty());
   }

   //
   // Test framework
   //

   static File getCWD() { return new File(System.getProperty("user.dir")); }

}
