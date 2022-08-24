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
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;

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
    * @return preferred format value. If {@code null}, then the preferred format is determined by the API version:
    *         <ul>
    *         <li>for API version 2 or later, {@code "json-v2"}</li>
    *         <li>for API version 1, {@code "json"}</li>
    *         </ul>
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
    * @param payload  the String payload holding EObject definition to decode
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(String modelUri, Context context, String payload) throws DecodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param context      the javalin http context
    * @param payload      the String payload holding EObject definition to decode
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
    * @param payload  the String payload holding EObject definition to decode
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(String modelUri, WsContext context, String payload) throws DecodingException;

   /**
    * Decode a JsonNode to an EObject.
    *
    * @param context      the javalin websocket context
    * @param payload      the String payload holding EObject definition to decode
    * @param workspaceURI the URI to access to the model from the workspace
    * @return the decoded EObject
    * @throws DecodingException when decoding failed
    */
   Optional<EObject> decode(WsContext context, String payload, URI workspaceURI)
      throws DecodingException;

   /**
    * Returns the format (perhaps implicit) of a {@code request}.
    *
    * @param context the javalin request context
    * @return format string
    */
   default String findFormat(final Context context) {
      String format = context.queryParam(ModelServerPathParametersV2.FORMAT);
      if (format != null) {
         return format;
      }
      return getPreferredFormat(context);
   }

   /**
    * Returns the format, for which the websocket subscribed for.
    *
    * @param context the javalin websocket context
    * @return format string
    */
   default String findFormat(final WsContext context) {
      String format = context.queryParam(ModelServerPathParametersV2.FORMAT);
      if (format != null) {
         return format;
      }
      return getPreferredFormat(context);
   }

   /**
    * Get the preferred format for request/response payloads in the given {@code context}.
    *
    * @param context a request context
    * @return the preferred format
    */
   default String getPreferredFormat(final Context context) {
      String result = this.getPreferredFormat();
      if (result == null) {
         result = ContextRequest.getAPIVersion(context).lessThan(APIVersion.API_V2)
            ? ModelServerPathParametersV2.FORMAT_JSON
            : ModelServerPathParametersV2.FORMAT_JSON_V2;
      }
      return result;
   }

   /**
    * Get the preferred format for messages in the given socket {@code context}.
    *
    * @param context a socket context
    * @return the preferred format
    */
   default String getPreferredFormat(final WsContext context) {
      String result = this.getPreferredFormat();
      if (result == null) {
         result = ContextRequest.getAPIVersion(context).lessThan(APIVersion.API_V2)
            ? ModelServerPathParametersV2.FORMAT_JSON
            : ModelServerPathParametersV2.FORMAT_JSON_V2;
      }
      return result;
   }

   /**
    * Obtains the codec that handles the format of the websocket.
    *
    * @param modelUri The ModelUri of the request, null if not available
    * @param context  the javalin websocket context
    * @return the codec for the websocket's format
    */
   Codec findCodec(String modelUri, WsContext context);

}
