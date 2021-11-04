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
package org.eclipse.emfcloud.modelserver.emf.common.watchers;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.inject.Inject;

/**
 * Watches for changes on model resource to adopt a strategy to update models.
 * This abstract implementation provides basic functionalities to poll for resource modifications in a {@link Runnable}
 * loop.
 *
 * @author vhemery
 */
public abstract class AbstractModelWatcher implements ModelWatcher, Runnable {

   /** The thread running this watcher */
   private final Thread worker;

   /** Whether still on watch */
   protected boolean running = false;

   /** The EMF resource */
   protected Resource resource;

   /** The EMF resource */
   @Inject
   protected ReconcilingStrategy strategy;

   /**
    * Reconcile the model applying the injected strategy
    *
    * @param resource
    */
   public void reconcile(final Resource resource) {
      strategy.reconcileModel(resource);
   }

   /**
    * Creates a new watcher to listen to model resource changes.
    */
   protected AbstractModelWatcher() {
      this.worker = new Thread(this);
      this.worker.setDaemon(true);
   }

   @Override
   public void watch(final Resource resource) {
      this.resource = resource;
      this.running = true;
      this.worker.start();
      addStopAdapter();
   }

   /**
    * Add an adapter to the resource to stop watching the resource on closure
    */
   private void addStopAdapter() {
      // stop watcher automatically when resource is unloaded
      resource.eAdapters().add(new AdapterImpl() {
         @Override
         public void notifyChanged(final Notification msg) {
            if (resource.equals(msg.getNotifier())
               && Resource.RESOURCE__IS_LOADED == msg.getFeatureID(Resource.class) && !msg.getNewBooleanValue()) {
               // resource is now unloaded, stop watching
               AbstractModelWatcher.this.stop();
               resource.eAdapters().remove(this);
            }
         }
      });
   }

   /**
    * Tell this watcher to stop polling
    */
   public void stop() {
      this.running = false;
      this.worker.interrupt();
   }

}
