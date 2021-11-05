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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.AbstractResourceTest;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EcorePackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * This is an integration test to ensure that the whole model watcher mechanism works with :
 * <ul>
 * <li>{@link DIModelWatchersManager}</li>
 * <li>{@link FileModelWatcher}</li>
 * <li>{@link ReconcilingStrategy.AlwaysReload}</li>
 * <li>{@link DefaultModelResourceManager}</li>
 * <li>{@link DefaultModelRepository}</li>
 * </ul>
 *
 * @author vhemery
 */
@RunWith(MockitoJUnitRunner.class)
public class FileModelWatcherIntegrationTest extends AbstractResourceTest {

   /** tells us whether expected reconciliations occurred */
   private static AtomicReference<CountDownLatch> latch = new AtomicReference<>();

   /**
    * A custom strategy, which reloads, but also notifies us when reconciliation has occurred...
    *
    * @author vhemery
    */
   private static class ReconcilingStrategyWithNotification extends ReconcilingStrategy.AlwaysReload {
      @Override
      public void reconcileModel(final Resource modelResource) {
         super.reconcileModel(modelResource);
         Optional.ofNullable(latch.get()).ifPresent(CountDownLatch::countDown);
      }
   }

   /**
    * A file watcher with higher priority for integration builds...
    *
    * @author vhemery
    */
   public static class HigherPriorityFileModelWatcher extends FileModelWatcher {

      /**
       * The factory for {@link FileModelWatcher}
       */
      public static class Factory extends FileModelWatcher.Factory {

         @Inject
         private Injector injector;

         @Override
         public ModelWatcher createWatcher(final Resource resource) {
            return injector.getInstance(HigherPriorityFileModelWatcher.class);
         }

      }

      public HigherPriorityFileModelWatcher() {
         super();
         // set it as non-daemon, or linux (not using thread priority by default) will still give it low priority
         this.worker.setDaemon(false);
         this.worker.setPriority(Thread.MAX_PRIORITY);
      }
   }

   private static ModelResourceManager modelResourceManager;

   @Mock
   private ServerConfiguration serverConfig;
   @Mock
   private CommandCodec commandCodec;

   @Mock
   private SessionController session;

   public FileModelWatcherIntegrationTest() {
      super();
   }

   @Before
   public void beforeTests() throws DecodingException {
      when(serverConfig.getWorkspaceRootURI())
         .thenReturn(URI.createFileURI(getCWD().getAbsolutePath() + "/" + RESOURCE_PATH));
      Injector injector = Guice.createInjector(new AbstractModule() {

         private Multibinder<ModelWatcher.Factory> watcherFactoryBinder;
         private Multibinder<EPackageConfiguration> ePackageConfigurationBinder;

         @Override
         protected void configure() {
            ePackageConfigurationBinder = Multibinder.newSetBinder(binder(), EPackageConfiguration.class);
            ePackageConfigurationBinder.addBinding().to(EcorePackageConfiguration.class);

            watcherFactoryBinder = Multibinder.newSetBinder(binder(), ModelWatcher.Factory.class);
            watcherFactoryBinder.addBinding().to(HigherPriorityFileModelWatcher.Factory.class);

            bind(ServerConfiguration.class).toInstance(serverConfig);
            bind(CommandCodec.class).toInstance(commandCodec);
            bind(SessionController.class).toInstance(session);
            bind(AdapterFactory.class).toInstance(new EcoreAdapterFactory());

            // for the reconciling strategy to work, we need the actual ModelRepository and ModelResourceManager
            bind(ReconcilingStrategy.class).to(ReconcilingStrategyWithNotification.class).in(Singleton.class);
            bind(ModelWatchersManager.class).to(DIModelWatchersManager.class).in(Singleton.class);
            bind(ModelRepository.class).to(DefaultModelRepository.class).in(Singleton.class);
            bind(ModelResourceManager.class).to(DefaultModelResourceManager.class).in(Singleton.class);
         }
      });
      modelResourceManager = injector.getInstance(ModelResourceManager.class);

   }

   // Test framework
   private static File getCWD() { return new File(System.getProperty("user.dir")); }

   private static String adaptModelUri(final String modelUri) {
      return URI.createFileURI(getCWD().getAbsolutePath() + "/" + RESOURCE_PATH + modelUri).toString();
   }

   /**
    * Convert a uri to a concrete file
    *
    * @param uri a uri
    * @return corresponding file or <code>null</code>
    */
   private static File toFile(final URI uri) {
      File file = null;
      if (uri.isPlatformResource()) {
         IPath path = org.eclipse.core.runtime.Path.fromPortableString(uri.toPlatformString(true));
         IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
         if (res != null) {
            file = res.getLocation().toFile();
         }
      } else if (uri.isFile()) {
         String path = uri.toFileString();
         file = new File(path);
      }
      return file;
   }

   @Test
   public void testReconciliation() throws IOException {
      String modelUri = adaptModelUri("TestReconcile.ecore").toString();
      // initialize the resource with an EClass
      modelResourceManager.addResource(modelUri, EcoreFactory.eINSTANCE.createEClass());
      Resource outsiderResource = loadResource("TestReconcile.ecore");
      try {
         // load initial resource from model server
         Optional<EModelElement> loadedModelElement = modelResourceManager.loadModel(modelUri, EModelElement.class);
         assertTrue(loadedModelElement.filter(EClass.class::isInstance).isPresent());

         // check both resources are different and listen to the model server resource
         Resource modelServerResource = loadedModelElement.get().eResource();
         assertNotNull(modelServerResource);
         assertNotEquals(modelServerResource, outsiderResource);
         assertEquals(toFile(modelServerResource.getURI()).getAbsoluteFile(),
            toFile(outsiderResource.getURI()).getAbsoluteFile());

         // replace resource content with an EPackage, outside of ModelServer framework
         outsiderResource.getContents().replaceAll(c -> EcoreFactory.eINSTANCE.createEPackage());
         latch.set(new CountDownLatch(1));
         outsiderResource.save(Collections.emptyMap());

         // ensure reconciliation has occurred
         assertTrue(latch.get().await(5, TimeUnit.MINUTES));
         // reload and check the model server resource has been updated
         loadedModelElement = modelResourceManager.loadModel(modelUri, EModelElement.class);
         assertTrue(loadedModelElement.filter(EPackage.class::isInstance).isPresent());

         // delete resource
         latch.set(new CountDownLatch(1));
         outsiderResource.delete(Collections.emptyMap());

         // ensure reconciliation has occurred
         assertTrue(latch.get().await(5, TimeUnit.MINUTES));
         // reload and check the model server resource has been deleted too
         loadedModelElement = modelResourceManager.loadModel(modelUri, EModelElement.class);
         assertFalse(loadedModelElement.isPresent());
      } catch (InterruptedException e) {
         fail(e.getMessage());
      } finally {
         // force resource deletion to leave resource clean
         outsiderResource.delete(Collections.emptyMap());
      }
   }
}
