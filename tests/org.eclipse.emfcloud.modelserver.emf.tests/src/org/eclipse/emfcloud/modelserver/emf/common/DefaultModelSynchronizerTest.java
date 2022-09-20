/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class DefaultModelSynchronizerTest {

   @Rule
   public final Timeout timeout = new Timeout(500L, TimeUnit.MILLISECONDS);

   private DefaultModelSynchronizer synchronizer;

   private final AtomicInteger data = new AtomicInteger();

   public DefaultModelSynchronizerTest() {
      super();
   }

   @Test
   public void syncExec() {
      synchronizer.syncExec(data::incrementAndGet);
      assertThat(data.get(), is(1));
   }

   @Test
   public void asyncExec() throws InterruptedException, ExecutionException, TimeoutException {
      Future<?> done = synchronizer.asyncExec(data::incrementAndGet);
      done.get(1L, TimeUnit.SECONDS);
      assertThat(data.get(), is(1));
   }

   @Test
   public void syncCall() {
      Integer result = synchronizer.syncCall(data::incrementAndGet);
      assertThat(result, is(1));
   }

   @Test
   public void asyncCall() throws InterruptedException, ExecutionException, TimeoutException {
      Future<Integer> result = synchronizer.asyncCall(data::incrementAndGet);
      assertThat(result.get(1L, TimeUnit.SECONDS), is(1));
   }

   @Test
   public void reentrantSyncExec() {
      Runnable[] run = { null };
      run[0] = () -> {
         if (data.incrementAndGet() < 3) {
            synchronizer.syncExec(run[0]);
         }
      };
      synchronizer.syncExec(run[0]);
      assertThat(data.get(), is(3));
   }

   @Test
   public void reentrantSyncCall() {
      AtomicReference<Callable<Integer>> call = new AtomicReference<>();
      call.set(() -> {
         int result = data.incrementAndGet();
         if (result < 3) {
            result = synchronizer.syncCall(call.get());
         }
         return result;
      });
      Integer result = synchronizer.syncCall(call.get());
      assertThat(result, is(3));
   }

   //
   // Test framework
   //

   @Before
   public void setup() {
      this.synchronizer = new DefaultModelSynchronizer();
   }

   @After
   public void tearDown() {
      this.synchronizer.dispose();
   }

}
