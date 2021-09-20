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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.http.Context;

/**
 * A {@link ModelController} that executes all requests in the same thread, to
 * ensure we use a consistent resource set state.
 */
public class SingleThreadModelController implements ModelController {

   private static final Logger LOG = Logger.getLogger(SingleThreadModelController.class);

   private static final AtomicInteger COUNT = new AtomicInteger(0);

   protected final ModelController delegate;

   protected final Thread thread;

   protected final BlockingQueue<Runnable> actionsQueue = new ArrayBlockingQueue<>(100, true);

   protected final String name;

   @Inject
   public SingleThreadModelController(final @Named("ModelControllerDelegate") ModelController delegate) {
      this.name = getClass().getSimpleName() + " " + COUNT.incrementAndGet();
      this.delegate = delegate;
      this.thread = new Thread(this::runThread);
      this.thread.setName(this.name);
      this.thread.setDaemon(true);
      this.thread.start();
   }

   private void runThread() {
      while (true) {
         try {
            handleNextAction();
         } catch (final InterruptedException e) {
            LOG.info(
               String.format("Terminating SingleThreadModelController thread %s", Thread.currentThread().getName()));
            break;
         }
      }
      LOG.info("Terminating SingleThreadModelController");
   }

   private void handleNextAction()
      throws InterruptedException {
      final Runnable runnable = actionsQueue.take();
      if (runnable != null) {
         handleAction(runnable);
      }
   }

   protected void handleAction(final Runnable runnable) {
      checkThread();
      runnable.run();
   }

   protected final void checkThread() {
      if (Thread.currentThread() != thread) {
         throw new IllegalStateException(
            "This method should only be invoked from the ModelControllers's thread: " + name);
      }
   }

   //
   // Delegate Methods
   //

   @Override
   public void create(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.create(ctx, modeluri));
   }

   @Override
   public void delete(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.delete(ctx, modeluri));
   }

   @Override
   public void getAll(final Context ctx) {
      runAndWait(() -> delegate.getAll(ctx));
   }

   @Override
   public void getOne(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.getOne(ctx, modeluri));
   }

   @Override
   public void getModelElementById(final Context ctx, final String modeluri, final String elementid) {
      runAndWait(() -> delegate.getModelElementById(ctx, modeluri, elementid));
   }

   @Override
   public void getModelElementByName(final Context ctx, final String modeluri, final String elementname) {
      runAndWait(() -> delegate.getModelElementByName(ctx, modeluri, elementname));
   }

   @Override
   public void getModelUris(final Context ctx) {
      runAndWait(() -> delegate.getModelUris(ctx));
   }

   @Override
   public void update(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.update(ctx, modeluri));
   }

   @Override
   public void save(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.save(ctx, modeluri));
   }

   @Override
   public void saveAll(final Context ctx) {
      runAndWait(() -> delegate.saveAll(ctx));
   }

   @Override
   public void validate(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.validate(ctx, modeluri));
   }

   @Override
   public void getValidationConstraints(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.getValidationConstraints(ctx, modeluri));
   }

   @Override
   public void executeCommand(final Context ctx, final String modelURI) {
      runAndWait(() -> delegate.executeCommand(ctx, modelURI));
   }

   @Override
   public void undo(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.undo(ctx, modeluri));
   }

   @Override
   public void redo(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.redo(ctx, modeluri));
   }

   @Override
   public void close(final Context ctx, final String modeluri) {
      runAndWait(() -> delegate.close(ctx, modeluri));
   }

   /**
    * Executes the given action in the Model thread, and wait for it to complete.
    *
    * @param action
    *                  The action to execute.
    */
   private void runAndWait(final Runnable action) {
      FutureTask<Void> task = new FutureTask<>(action, null);
      boolean success = actionsQueue.offer(task);
      while (!success) {
         if (!thread.isAlive() || thread.isInterrupted()) {
            // This may happen if e.g. some background tasks were still running when the client disconnected.
            // This (probably) isn't critical and can be safely ignored.
            LOG.warn(String.format(
               "Received an action after the ModelController was stopped. Ignoring action: %s", action));
            return;
         }
         try {
            // The queue may be temporarily full because we receive a lot of messages (e.g. during initialization),
            // but if this keeps failing for a long time, it might indicate a deadlock
            success = actionsQueue.offer(action, 1, TimeUnit.SECONDS);
            if (!success) {
               LOG.warn(String.format("Actions queue is currently full for ModelController %s ; retrying...", name));
            }
         } catch (final InterruptedException ex) {
            break;
         }
      }

      // We need to wait for the task to complete, because the context
      // will be closed/disposed when this method returns.
      waitComplete(task);
   }

   private void waitComplete(final FutureTask<Void> task) {
      while (true) {
         try {
            task.get(1, TimeUnit.SECONDS);
            return;
         } catch (TimeoutException ex) {
            // FIXME: Should we add a specific timeout? Most actions shouldn't take too long to execute,
            // but if something goes wrong, we may wait forever.
            LOG.warn("A ModelController action is taking a long time to execute. Keep waiting... " + ex.getMessage());
         } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
            return;
         } catch (ExecutionException e) {
            LOG.error(e);
            return;
         }
      }
   }
}
