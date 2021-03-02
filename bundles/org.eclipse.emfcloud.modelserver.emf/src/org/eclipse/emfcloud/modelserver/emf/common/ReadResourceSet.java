/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.util.TransactionUtil;

public class ReadResourceSet extends ResourceSetImpl {
   private final ModelServerEditingDomain domain;

   ReadResourceSet(final ModelServerEditingDomain domain) {
      this.domain = domain;
   }

   @Override
   protected Resource delegatedGetResource(final URI uri, final boolean loadOnDemand) {
      return domain.getResourceSet().getResource(uri, loadOnDemand);
   }

   @Override
   public EObject getEObject(final URI uri, final boolean loadOnDemand) {
      try {
         return TransactionUtil.runExclusive(domain,
            new RunnableWithResult.Impl<EObject>() {
               @Override
               public void run() {
                  setResult(domain.getResourceSet().getEObject(uri, loadOnDemand));
               }
            });
      } catch (InterruptedException exception) {
         exception.printStackTrace();
      }
      return super.getEObject(uri, loadOnDemand);
   }

   public Resource resolve(final EObject eObject, final String tempUri) {
      Resource createdResource = createResource(URI.createURI(tempUri));
      createdResource.getContents().add(eObject);
      EcoreUtil.resolveAll(createdResource);
      return createdResource;
   }
}
