/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import javax.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class DefaultResourceSetFactory implements ResourceSetFactory {

   protected URIConverter uriConverter;

   public DefaultResourceSetFactory() {
      super();
   }

   @Inject
   public void setURIConverter(final URIConverter uriConverter) { this.uriConverter = uriConverter; }

   @Override
   public ResourceSet createResourceSet(final URI modelURI) {
      ResourceSet result = new ResourceSetImpl();

      result.setURIConverter(uriConverter);

      return result;
   }

}
