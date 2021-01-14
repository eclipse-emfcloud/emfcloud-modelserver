/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
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
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * Injectable singleton class represents a repository to load UI schemas from the currently set path.
 *
 */
public class DefaultSchemaRepository implements SchemaRepository {
   private static Logger LOG = Logger.getLogger(DefaultSchemaRepository.class.getSimpleName());
   private final ServerConfiguration serverConfiguration;

   @Inject
   public DefaultSchemaRepository(final ServerConfiguration serverConfiguration) {
      this.serverConfiguration = serverConfiguration;
   }

   @Override
   public Optional<JsonNode> loadUiSchema(final String schemaname) {
      String schemaFilePath = serverConfiguration.getUiSchemaFolderURI().toFileString().concat(schemaname + ".json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = null;
      try {
         jsonNode = mapper.readValue(Paths.get(schemaFilePath).toFile(), JsonNode.class);
      } catch (JsonParseException e) {} catch (JsonMappingException e) {
         LOG.error(String.format("Error while parsing UI schema from \'%s'", schemaFilePath));
      } catch (IOException e) {
         LOG.error(String.format("Error while reading UI schema from \'%s'", schemaFilePath));
      }
      return Optional.ofNullable(jsonNode);
   }
}
