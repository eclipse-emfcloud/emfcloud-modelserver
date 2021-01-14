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
package org.eclipse.emfcloud.modelserver.emf.di;

import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSchemaRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultServerController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSessionController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultUriHelper;
import org.eclipse.emfcloud.modelserver.emf.common.ModelController;
import org.eclipse.emfcloud.modelserver.emf.common.ModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelValidator;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ServerController;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;
import org.eclipse.emfcloud.modelserver.emf.common.UriHelper;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.DICodecsManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.DefaultServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.FacetConfig;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.launch.DefaultModelServerStartup;
import org.eclipse.emfcloud.modelserver.emf.launch.ModelServerStartup;
import org.eclipse.emfcloud.modelserver.jsonschema.DefaultJsonSchemaConverter;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;
import org.emfjson.jackson.module.EMFModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.javalin.Javalin;

public abstract class ModelServerModule extends AbstractModule {

   protected static final Logger LOG = Logger.getLogger(ModelServerModule.class.getSimpleName());

   @Override
   protected void configure() {
      // configure singletons
      bind(ModelServerStartup.class).to(bindModelServerStartup()).in(Singleton.class);
      bind(ModelRepository.class).to(bindModelRepository()).in(Singleton.class);
      bind(JsonSchemaConverter.class).to(bindJsonSchemaConverter()).in(Singleton.class);
      bind(SchemaController.class).to(bindSchemaController()).in(Singleton.class);
      bind(SchemaRepository.class).to(bindSchemaRepository()).in(Singleton.class);
      bind(SessionController.class).to(bindSessionController()).in(Singleton.class);
      bind(ServerConfiguration.class).to(bindServerConfiguration()).in(Singleton.class);
      bind(ModelController.class).to(bindModelController()).in(Singleton.class);
      bind(ServerController.class).to(bindServerController()).in(Singleton.class);

      bind(CommandCodec.class).to(bindCommandCodec()).in(Singleton.class);
      bind(ModelResourceManager.class).to(bindModelResourceManager()).in(Singleton.class);
      bind(CodecsManager.class).to(bindCodecsManager()).in(Singleton.class);
      bind(ModelValidator.class).to(bindModelValidator()).in(Singleton.class);
      bind(FacetConfig.class).to(bindFacetConfig()).in(Singleton.class);
      bind(UriHelper.class).to(bindUriHelper()).in(Singleton.class);

      // Configure instance bindings
      bind(ObjectMapper.class).toProvider(this::provideObjectMapper);
      bind(Javalin.class).toProvider(this::provideJavalin).in(Singleton.class);
      bind(AdapterFactory.class).toProvider(this::provideAdapterFactory).in(Singleton.class);

      // configure multi-bindings
      configureMultiBindings();
   }

   protected void configureMultiBindings() {
      configure(MultiBinding.create(EPackageConfiguration.class), this::configureEPackages);
      configure(MultiBinding.create(Routing.class), this::configureRoutings);
      configure(MapBinding.create(EntryPointType.class, AppEntryPoint.class), this::configureAppEntryPoints);
      configure(MapBinding.create(String.class, Codec.class), this::configureCodecs);
   }

   protected <T> void configure(final MultiBinding<T> binding, final Consumer<MultiBinding<T>> configurator) {
      configurator.accept(binding);
      binding.applyBinding(binder());
   }

   protected <K, V> void configure(final MapBinding<K, V> binding, final Consumer<MapBinding<K, V>> configurator) {
      configurator.accept(binding);
      binding.applyBinding(binder());
   }

   protected ObjectMapper provideObjectMapper() {
      return EMFModule.setupDefaultMapper();
   }

   protected Javalin provideJavalin() {
      return Javalin.create(config -> {
         config.enableCorsForAllOrigins();
         config.requestLogger((ctx, ms) -> {
            String requestPath = ctx.path() + (ctx.queryString() == null ? "" : "?" + ctx.queryString());
            LOG.info(ctx.method() + " " + requestPath + " -> Status: " + ctx.status() + " (took " + ms + " ms)");
         });
         config.asyncRequestTimeout = 5000L;
         config.wsLogger(ws -> {
            ws.onConnect(ctx -> LOG.info("WS Connected: " + ctx.getSessionId()));
            ws.onMessage(ctx -> LOG.info("WS Received: " + ctx.message() + " by " + ctx.getSessionId()));
            ws.onClose(ctx -> LOG.info("WS Closed: " + ctx.getSessionId()));
            ws.onError(ctx -> LOG.info("WS Errored: " + ctx.getSessionId()));
         });
      });
   }

   protected Class<? extends ServerConfiguration> bindServerConfiguration() {
      return DefaultServerConfiguration.class;
   }

   protected Class<? extends ModelServerStartup> bindModelServerStartup() {
      return DefaultModelServerStartup.class;
   }

   protected Class<? extends ModelRepository> bindModelRepository() {
      return DefaultModelRepository.class;
   }

   protected Class<? extends JsonSchemaConverter> bindJsonSchemaConverter() {
      return DefaultJsonSchemaConverter.class;
   }

   protected Class<? extends SchemaController> bindSchemaController() {
      return DefaultSchemaController.class;
   }

   protected Class<? extends SchemaRepository> bindSchemaRepository() {
      return DefaultSchemaRepository.class;
   }

   protected Class<? extends SessionController> bindSessionController() {
      return DefaultSessionController.class;
   }

   protected Class<? extends ServerController> bindServerController() {
      return DefaultServerController.class;
   }

   protected Class<? extends ModelController> bindModelController() {
      return DefaultModelController.class;
   }

   protected Class<? extends CodecsManager> bindCodecsManager() {
      return DICodecsManager.class;
   }

   protected Class<? extends UriHelper> bindUriHelper() {
      return DefaultUriHelper.class;
   }

   /**
    * Bind the EObject codecs to support various formats encoding and decoding.
    * <p>
    * Note that in case you don't support Json or don't want it as the default format, you may bind an alternative one
    * to the key {@link CodecsManager#PREFERRED_FORMAT}.
    * </p>
    * <p>
    * Note this binding may also no longer be relevant in case you override the {@link #bindCodecsManager()} method.
    * </>
    *
    * @param binding map binding from format to codec
    */
   protected abstract void configureCodecs(MapBinding<String, Codec> binding);

   protected abstract void configureEPackages(MultiBinding<EPackageConfiguration> binding);

   protected abstract void configureRoutings(MultiBinding<Routing> binding);

   protected abstract void configureAppEntryPoints(MapBinding<EntryPointType, AppEntryPoint> binding);

   protected abstract AdapterFactory provideAdapterFactory();

   protected abstract Class<? extends CommandCodec> bindCommandCodec();

   protected abstract Class<? extends ModelResourceManager> bindModelResourceManager();

   protected abstract Class<? extends ModelValidator> bindModelValidator();

   protected abstract Class<? extends FacetConfig> bindFacetConfig();
}
