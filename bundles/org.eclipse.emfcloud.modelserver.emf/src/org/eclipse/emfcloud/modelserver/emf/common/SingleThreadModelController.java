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
package org.eclipse.emfcloud.modelserver.emf.common;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;

/**
 * A {@link ModelController} that executes all requests in the same thread, to
 * ensure we use a consistent resource set state.
 */
public class SingleThreadModelController implements ModelController {

   /**
    * Dependency injection name for the actual Model Controller implementation,
    * to which the {@link SingleThreadModelController} will delegate calls (after
    * switching to the correct Thread).
    *
    * @see Named
    */
   public static final String MODEL_CONTROLLER_DELEGATE = "ModelControllerDelegate";

   protected final ModelController delegate;

   protected final ModelSynchronizer synchronizer;

   @Inject
   public SingleThreadModelController(final @Named(MODEL_CONTROLLER_DELEGATE) ModelController delegate,
      final ModelSynchronizer synchronizer) {
      this.delegate = delegate;
      this.synchronizer = synchronizer;
   }

   /** @deprecated this method is no longer used */
   @Deprecated
   protected void handleAction(final Runnable runnable) {
      // No-op
   }

   /** @deprecated this method is no longer used */
   @Deprecated
   protected final void checkThread() {
      // No-op
   }

   //
   // Delegate Methods
   //

   @Override
   public void create(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.create(ctx, modeluri));
   }

   @Override
   public void delete(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.delete(ctx, modeluri));
   }

   @Override
   public void getAll(final Context ctx) {
      synchronizer.syncExec(() -> delegate.getAll(ctx));
   }

   @Override
   public void getOne(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.getOne(ctx, modeluri));
   }

   @Override
   public void getModelElementById(final Context ctx, final String modeluri, final String elementid) {
      synchronizer.syncExec(() -> delegate.getModelElementById(ctx, modeluri, elementid));
   }

   @Override
   public void getModelElementByName(final Context ctx, final String modeluri, final String elementname) {
      synchronizer.syncExec(() -> delegate.getModelElementByName(ctx, modeluri, elementname));
   }

   @Override
   public void getModelUris(final Context ctx) {
      synchronizer.syncExec(() -> delegate.getModelUris(ctx));
   }

   @Override
   public void update(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.update(ctx, modeluri));
   }

   @Override
   public void save(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.save(ctx, modeluri));
   }

   @Override
   public void saveAll(final Context ctx) {
      synchronizer.syncExec(() -> delegate.saveAll(ctx));
   }

   @Override
   public void validate(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.validate(ctx, modeluri));
   }

   @Override
   public void getValidationConstraints(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.getValidationConstraints(ctx, modeluri));
   }

   @Override
   public void executeCommand(final Context ctx, final String modelURI) {
      synchronizer.syncExec(() -> delegate.executeCommand(ctx, modelURI));
   }

   @Override
   public void executeCommandV2(final Context ctx, final String modelURI) {
      synchronizer.syncExec(() -> delegate.executeCommandV2(ctx, modelURI));
   }

   @Override
   public void undo(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.undo(ctx, modeluri));
   }

   @Override
   public void redo(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.redo(ctx, modeluri));
   }

   @Override
   public void close(final Context ctx, final String modeluri) {
      synchronizer.syncExec(() -> delegate.close(ctx, modeluri));
   }

}
