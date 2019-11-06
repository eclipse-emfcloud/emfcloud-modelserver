/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.plugin.json.JavalinJackson;

public class JsonCodec extends DefaultJsonCodec {

   public static JsonNode encode(final Object obj) throws EncodingException {
      try {
         return JavalinJackson.getObjectMapper().valueToTree(obj);
      } catch (IllegalArgumentException ex) {
         throw new EncodingException(ex);
      }
   }

   @Override
   protected ObjectMapper getObjectMapper() { return JavalinJackson.getObjectMapper(); }

}
