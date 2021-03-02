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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class XmiCodec implements Codec {

   private static Logger LOG = Logger.getLogger(XmiCodec.class.getSimpleName());

   @Override
   public JsonNode encode(final EObject eObject) throws EncodingException {
      final Resource resource = createResource();
      resource.getContents().add(EcoreUtil.copy(eObject));
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         resource.save(outputStream,
            Map.of(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD));
      } catch (IOException e) {
         throw new EncodingException(e);
      }

      return Json.text(outputStream.toString());
   }

   @Override
   public Optional<EObject> decode(final String payload) throws DecodingException {
      return decode(payload, null);
   }

   @Override
   public Optional<EObject> decode(final String payload, final URI workspaceURI) throws DecodingException {
      ResourceSet resourceSet = new ResourceSetImpl();
      Optional<Resource> resource = decode(resourceSet, "virtual.xmi", workspaceURI, payload);
      return resource.map(r -> r.getContents().isEmpty() ? null : r.getContents().get(0));
   }

   private Resource createResource() {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(null, new XMIResourceFactoryImpl());
      return resourceSet.createResource(URI.createURI("virtual.xmi"));
   }

   public Optional<Resource> decode(final ResourceSet resourceSet, final String modelURI, final URI workspaceURI,
      final String payload)
      throws DecodingException {

      URI uri = URI.createURI(modelURI);
      if (workspaceURI != null) {
         uri = uri.resolve(workspaceURI);
      }

      Resource result = resourceSet.getResource(uri, false);
      if (result != null && !(result instanceof XMIResource)) {
         // Replace it
         LOG.warn(String.format("Replacing resource '%s' with a XMIResource", modelURI));
         result.unload();
         resourceSet.getResources().remove(result);
         result = null;
      }
      if (result == null) {
         result = new XMIResourceFactoryImpl().createResource(URI.createURI(modelURI));
         resourceSet.getResources().add(result);
      }
      try {
         result.load(new ByteArrayInputStream(payload.getBytes()), resourceSet.getLoadOptions());
      } catch (IOException e) {
         throw new DecodingException(e);
      }

      return Optional.of(result);
   }

}
