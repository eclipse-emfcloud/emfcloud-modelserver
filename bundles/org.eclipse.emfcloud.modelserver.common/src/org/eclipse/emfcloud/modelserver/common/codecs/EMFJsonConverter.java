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
package org.eclipse.emfcloud.modelserver.common.codecs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.jackson.module.EMFModule;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class EMFJsonConverter {
   private static Logger LOG = Logger.getLogger(EMFJsonConverter.class.getSimpleName());

   public static ObjectMapper setupDefaultMapper() {
      return EMFJsonConverter.setupDefaultMapper(null);
   }

   public static ObjectMapper setupDefaultMapper(final JsonFactory factory) {
      final ObjectMapper mapper = new ObjectMapper(factory);
      // same as emf
      final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
      dateFormat.setTimeZone(TimeZone.getDefault());

      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      mapper.setDateFormat(dateFormat);
      mapper.setTimeZone(TimeZone.getDefault());
      mapper.registerModule(new EMFModule());
      return mapper;
   }

   private ObjectMapper mapper;

   public EMFJsonConverter() {
      this.mapper = setupDefaultMapper();
   }

   public Optional<String> toJson(final EObject object) {
      try {
         return Optional.of(mapper.writeValueAsString(object));
      } catch (JsonProcessingException e) {
         LOG.warn("Could not convert object to json String: " + object);
         return Optional.empty();
      }
   }

   public <T extends EObject> Optional<T> fromJson(final String json, final Class<T> clazz) {
      try {
         final T t = mapper.readValue(json, clazz);
         return Optional.of(t).map(clazz::cast);
      } catch (IOException | ClassCastException e) {
         LOG.warn(String.format("The json input \"%s\" could not be converted to an EObject of type \"%s\"", json,
            clazz.getSimpleName()));
         return Optional.empty();
      }
   }

   public Optional<EObject> fromJson(final String json) {
      return fromJson(json, EObject.class);
   }

   public ObjectMapper getMapper() { return mapper; }

   public void setMapper(final ObjectMapper mapper) { this.mapper = mapper; }
}
