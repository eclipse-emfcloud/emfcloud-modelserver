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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Watches for changes on model files to adopt a strategy to update models
 *
 * @author vhemery
 */
public class FileModelWatcher extends AbstractModelWatcher {

   /**
    * The factory for {@link FileModelWatcher}
    */
   public static class Factory implements ModelWatcher.Factory {

      @Inject
      private Injector injector;

      @Override
      public boolean handles(final Resource resource) {
         // we only support file resources
         URI uri = resource.getURI();
         uri = resource.getResourceSet().getURIConverter().normalize(uri);
         File file = toFile(uri);
         return file != null && file.exists();
      }

      @Override
      public ModelWatcher createWatcher(final Resource resource) {
         return injector.getInstance(FileModelWatcher.class);
      }

   }

   /** logger */
   protected static final Logger LOG = Logger.getLogger(FileModelWatcher.class.getSimpleName());

   /** The file to watch for */
   private File fileToWatch;

   /**
    * Creates a new watcher to listen to model file changes.
    */
   public FileModelWatcher() {
      super();
   }

   @Override
   public void watch(final Resource resource) {
      URI uri = resource.getURI();
      uri = resource.getResourceSet().getURIConverter().normalize(uri);
      File file = toFile(uri);
      if (file != null) {
         this.fileToWatch = file;
         super.watch(resource);
      }
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

   @Override
   public void run() {
      try (WatchService ws = FileSystems.getDefault().newWatchService()) {
         Path path = Paths.get(fileToWatch.getParent());
         path.register(ws, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
         while (running) {
            WatchKey key = ws.take();
            /*
             * Sleep to prevent receiving two separate ENTRY_MODIFY events:
             * one for file modified and one for timestamp updated
             * Instead, receive one event with two counts.
             */
            Thread.sleep(50);
            List<WatchEvent<?>> events = key.pollEvents();
            for (WatchEvent<?> event : events) {
               Object ctx = event.context();
               boolean changeOnWatchedFile = ctx instanceof Path
                  && ((Path) ctx).getFileName().toString().equals(fileToWatch.getName());
               if (changeOnWatchedFile) {
                  if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())
                     || fileToWatch.lastModified() > resource.getTimeStamp()) {
                     // reconcile model on file change
                     reconcile(this.resource);
                  }
               }
            }
            if (!key.reset()) {
               running = false;
            }
         }
      } catch (IOException e) {
         String msg = MessageFormat.format("Failed while watching for file {0}", this.fileToWatch.toURI());
         LOG.error(msg, e);
      } catch (InterruptedException e) {
         // assume it is to stop, nothing to do
      }
   }

}
