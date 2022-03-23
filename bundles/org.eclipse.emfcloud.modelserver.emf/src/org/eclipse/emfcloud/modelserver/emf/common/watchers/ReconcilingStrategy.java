/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.watchers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.modelserver.emf.common.ModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;

import com.google.inject.Inject;

/**
 * A strategy for reconciling model resources with the underlying persistence.
 *
 * @author vhemery
 */
public interface ReconcilingStrategy {

   /**
    * Reconcile the model resource with the underlying persistence when a divergence occurs.
    * This does not necessarily reload the model resource, depending on the adopted strategy.
    *
    * @param modelResource the model resource to reconcile
    */
   void reconcileModel(Resource modelResource);

   /**
    * A strategy to always reload the model from persisted resource, loosing local modifications.
    *
    * @author vhemery
    */
   class AlwaysReload implements ReconcilingStrategy {
      protected static final Logger LOG = LogManager.getLogger(AlwaysReload.class);

      /** The injected model repository which can be used to reload models. */
      @Inject
      private ModelRepository repository;

      /** The session controller to inform about model reconciliation. */
      @Inject
      private SessionController sessionController;

      /**
       * Reconcile by reloading the model resource. Reconciliation is implemented in
       * an exclusive compound action on the model repository to synchronize
       * appropriately with other threads that may close/open/etc. model resource sets.
       *
       * @param modelResource the model resource to reconcile
       */
      @Override
      public void reconcileModel(final Resource modelResource) {
         repository.runResourceSetAction(() -> {
            basicReconcileModel(modelResource);
         });
      }

      /**
       * The basic implementation of reconciliation by reloading the model resource.
       * Extend this method instead of {@link #reconcileModel(Resource)} to use the
       * default exclusive-transactional context.
       *
       * @param modelResource the model resource to reconcile
       */
      protected void basicReconcileModel(final Resource modelResource) {
         // close and reload the resource
         String modelUri = modelResource.getURI().toString();
         LOG.debug("Reconciling model resource: " + modelUri);
         repository.closeModel(modelUri);
         boolean reloaded = false;
         if (repository.hasModel(modelUri)) {
            Optional<Resource> loadedResource = repository.loadResource(modelUri);
            reloaded = loadedResource.isPresent();
            if (!reloaded) {
               // incorrect load: model has probably been corrupted, re-close just in case...
               LOG.warn("Failed to reconcile '" + modelUri + "'. Closing it.");
               repository.closeModel(modelUri);
            }
         } else {
            LOG.info("Model resource '" + modelUri + "' no longer exists.");
         }

         // dispatch the message
         if (reloaded) {
            // model was reloaded, trigger a full updated
            sessionController.modelUpdated(modelUri);
         } else {
            // model was deleted, trigger a closed
            sessionController.modelClosed(modelUri);
         }
      }

   }

   /**
    * A strategy to always ignore persisted resource modifications and keep the loaded model as is.
    *
    * @author vhemery
    */
   class Ignore implements ReconcilingStrategy {

      /**
       * Reconcile by doing nothing.
       *
       * @param modelResource the model resource to reconcile
       */
      @Override
      public void reconcileModel(final Resource modelResource) {
         // do absolutely nothing!
      }

   }

}
