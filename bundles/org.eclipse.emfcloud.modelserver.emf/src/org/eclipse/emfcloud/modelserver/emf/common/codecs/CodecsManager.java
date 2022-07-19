/********************************************************************************
 * Copyright (c) 2020-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

import com.fasterxml.jackson.databind.JsonNode;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

public interface CodecsManager {

   /**
    * The constant to tell which is the preferred formats in codecs.
    * This constant can be used for binding the value of the preferred format in DI module.
    */
   String PREFERRED_FORMAT = "PreferredFormat";

   /**
    * Get the format we prefer to use.
    * This format should have a corresponding Codec in the manager, but it's not guaranteed when binding is overridden.
    * This is generally implemented using DI and {@link #PREFERRED_FORMAT}.
    *
    * @return preferred format value
    */
   String getPreferredFormat();

   /**
    * Encode an EObject to JsonNodes in all available formats.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param eObject  The EObject to encode
    * @return a map containing all format strings and their encoded JsonNodes
    * @throws EncodingException
    */
   Map<String, JsonNode> encode(String modelUri, EObject eObject) throws EncodingException;

   /**
    * Encode an EObject to a JsonNode.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin http context
    * @param eObject  EObject to encode
    * @return JsonNode
    * @throws EncodingException when encoding failed
    */
   JsonNode encode(String modelUri, Context context, EObject eObject) throws EncodingException;

   /**
    * Encode an EObject to a JsonNode.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin websocket context
    * @param eObject  EObject to encode
    * @return JsonNode
    * @throws EncodingException when encoding failed
    */
   JsonNode encode(String modelUri, WsContext context, EObject eObject) throws EncodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin http context
    * @param payload  tthe String payload holding EObject definition to decode
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(String modelUri, Context context, String payload) throws DecodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param context      the javalin http context
    * @param payload      tthe String payload holding EObject definition to decode
    * @param workspaceURI the URI to access to the model from the workspace
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(Context context, String payload, URI workspaceURI)
      throws DecodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin websocket context
    * @param payload  tthe String payload holding EObject definition to decode
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(String modelUri, WsContext context, String payload) throws DecodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param context      the javalin websocket context
    * @param payload      tthe String payload holding EObject definition to decode
    * @param workspaceURI the URI to access to the model from the workspace
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(WsContext context, String payload, URI workspaceURI)
      throws DecodingException;

   /**
    * Returns the format, for which the websocket subscribed for.
    *
    * @param context the javalin websocket context
    * @return format string
    */
   String findFormat(WsContext context);

   /**
    * Obtains the codec that handles the format of the websocket.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin websocket context
    * @return the codec for the websocket's format
    */
   Codec findCodec(String modelUri, WsContext context);

}
