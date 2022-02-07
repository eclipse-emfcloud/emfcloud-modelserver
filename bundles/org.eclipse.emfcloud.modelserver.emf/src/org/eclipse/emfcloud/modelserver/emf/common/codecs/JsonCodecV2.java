/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV2;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.jackson.EMFModuleV2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link JsonCodec}, adapted to support {@link ModelServerPathsV2} API V2 results.
 */
public class JsonCodecV2 extends DefaultJsonCodec {

   public JsonCodecV2() {
      super(createEMFJsonConverterV2());
   }

   private static EMFJsonConverter createEMFJsonConverterV2() {
      EMFJsonConverter emfJsonConverter = new EMFJsonConverter();
      emfJsonConverter.setMapper(setupObjectMapperV2());
      return emfJsonConverter;
   }

   private static ObjectMapper setupObjectMapperV2() {
      final ObjectMapper mapper = new ObjectMapper();
      // same as emf
      final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
      dateFormat.setTimeZone(TimeZone.getDefault());

      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      mapper.setDateFormat(dateFormat);
      mapper.setTimeZone(TimeZone.getDefault());
      mapper.registerModule(new EMFModuleV2());
      return mapper;
   }

   @Override
   public JsonNode encode(final EObject obj) throws EncodingException {
      // FIXME: The super implementation creates a JsonResource, which doesn't (seem to) support
      // IDs at all. When used with the $id: feature, it results in $id:null for all elements.
      // Directly serialize the object, without moving it to a separate resource.
      // However, by doing this, we might break href-references in some cases? TO BE INVESTIGATED
      // Alternatively, we could use a custom ID-Provider (Or delegate to the original resource for IDs?)
      // return super.encode(obj);
      return super.encode(obj, getObjectMapper());
   }

}
