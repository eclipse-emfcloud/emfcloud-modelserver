/********************************************************************************
 * Copyright (c) 2020-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.utils.MapBinding;
import org.eclipse.emfcloud.modelserver.common.utils.MultiBinding;
import org.eclipse.emfcloud.modelserver.edit.CommandCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandContribution;
import org.eclipse.emfcloud.modelserver.edit.DICommandCodec;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultFacetConfig;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelValidator;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultResourceSetFactory;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSchemaRepository;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultServerController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultSessionController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultTransactionController;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultUriHelper;
import org.eclipse.emfcloud.modelserver.emf.common.ModelController;
import org.eclipse.emfcloud.modelserver.emf.common.ModelRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.ModelValidator;
import org.eclipse.emfcloud.modelserver.emf.common.RecordingModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ResourceSetFactory;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaRepository;
import org.eclipse.emfcloud.modelserver.emf.common.ServerController;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;
import org.eclipse.emfcloud.modelserver.emf.common.SingleThreadModelController;
import org.eclipse.emfcloud.modelserver.emf.common.TransactionController;
import org.eclipse.emfcloud.modelserver.emf.common.UriHelper;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.DICodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.DIModelWatchersManager;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatcher;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatchersManager;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ReconcilingStrategy;
import org.eclipse.emfcloud.modelserver.emf.configuration.DefaultServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.FacetConfig;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.launch.DefaultModelServerStartup;
import org.eclipse.emfcloud.modelserver.emf.launch.ModelServerStartup;
import org.eclipse.emfcloud.modelserver.emf.patch.EMFCommandHandler;
import org.eclipse.emfcloud.modelserver.emf.patch.JsonPatchHandler;
import org.eclipse.emfcloud.modelserver.emf.patch.PatchCommandHandler;
import org.eclipse.emfcloud.modelserver.jsonschema.DefaultJsonSchemaConverter;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonSchemaConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import io.javalin.Javalin;

public class DefaultModelServerModule extends ModelServerModule {

   @Override
   protected void configure() {
      super.configure();

      bind(String.class).annotatedWith(Names.named(CodecsManager.PREFERRED_FORMAT))
         .toInstance(ModelServerPathParametersV2.FORMAT_JSON_V2);

      // singletons
      bind(ModelRepository.class).to(bindModelRepository()).in(Singleton.class);
      bind(JsonSchemaConverter.class).to(bindJsonSchemaConverter()).in(Singleton.class);
      bind(SchemaController.class).to(bindSchemaController()).in(Singleton.class);
      bind(SchemaRepository.class).to(bindSchemaRepository()).in(Singleton.class);
      bind(SessionController.class).to(bindSessionController()).in(Singleton.class);
      bind(ModelController.class).to(bindThreadSafeModelController()).in(Singleton.class);
      bind(ServerController.class).to(bindServerController()).in(Singleton.class);
      bind(CommandCodec.class).to(bindCommandCodec()).in(Singleton.class);
      bind(ModelResourceManager.class).to(bindModelResourceManager()).in(Singleton.class);
      bind(ModelWatchersManager.class).to(bindModelWatchersManager()).in(Singleton.class);
      bind(ReconcilingStrategy.class).to(bindReconcilingStrategy()).in(Singleton.class);
      bind(CodecsManager.class).to(bindCodecsManager()).in(Singleton.class);
      bind(ModelValidator.class).to(bindModelValidator()).in(Singleton.class);
      bind(FacetConfig.class).to(bindFacetConfig()).in(Singleton.class);
      bind(UriHelper.class).to(bindUriHelper()).in(Singleton.class);
      bind(TransactionController.class).to(bindTransactionController()).in(Singleton.class);
      bind(ModelURIConverter.class).to(bindModelURIConverter()).in(Singleton.class);
      bind(ResourceSetFactory.class).to(bindResourceSetFactory()).in(Singleton.class);
      bind(URIConverter.class).to(Key.get(ModelURIConverter.class));

      // Configure instance bindings
      bind(ObjectMapper.class).toProvider(this::provideObjectMapper).in(Singleton.class);
      bind(Javalin.class).toProvider(this::provideJavalin).in(Singleton.class);
      bind(AdapterFactory.class).toProvider(this::provideAdapterFactory).in(Singleton.class);

      // configure multi-bindings
      configureMultiBindings();
   }

   protected Class<? extends CodecsManager> bindCodecsManager() {
      return DICodecsManager.class;
   }

   protected Class<? extends CommandCodec> bindCommandCodec() {
      return DICommandCodec.class;
   }

   protected Class<? extends FacetConfig> bindFacetConfig() {
      return DefaultFacetConfig.class;
   }

   protected Class<? extends JsonSchemaConverter> bindJsonSchemaConverter() {
      return DefaultJsonSchemaConverter.class;
   }

   protected Class<? extends ModelController> bindModelController() {
      return DefaultModelController.class;
   }

   protected Class<? extends ModelController> bindThreadSafeModelController() {
      bind(ModelController.class)
         .annotatedWith(Names.named(SingleThreadModelController.MODEL_CONTROLLER_DELEGATE))
         .to(bindModelController());
      return SingleThreadModelController.class;
   }

   protected Class<? extends ModelRepository> bindModelRepository() {
      return DefaultModelRepository.class;
   }

   protected Class<? extends ModelResourceManager> bindModelResourceManager() {
      return RecordingModelResourceManager.class;

   }

   protected Class<? extends ModelWatchersManager> bindModelWatchersManager() {
      return DIModelWatchersManager.class;
   }

   protected Class<? extends ReconcilingStrategy> bindReconcilingStrategy() {
      return ReconcilingStrategy.AlwaysReload.class;
   }

   @Override
   protected Class<? extends ModelServerStartup> bindModelServerStartup() {
      return DefaultModelServerStartup.class;
   }

   protected Class<? extends ModelValidator> bindModelValidator() {
      return DefaultModelValidator.class;
   }

   protected Class<? extends SchemaController> bindSchemaController() {
      return DefaultSchemaController.class;
   }

   protected Class<? extends SchemaRepository> bindSchemaRepository() {
      return DefaultSchemaRepository.class;
   }

   @Override
   protected Class<? extends ServerConfiguration> bindServerConfiguration() {
      return DefaultServerConfiguration.class;
   }

   protected Class<? extends ServerController> bindServerController() {
      return DefaultServerController.class;
   }

   protected Class<? extends SessionController> bindSessionController() {
      return DefaultSessionController.class;
   }

   protected Class<? extends UriHelper> bindUriHelper() {
      return DefaultUriHelper.class;
   }

   protected Class<? extends TransactionController> bindTransactionController() {
      return DefaultTransactionController.class;
   }

   protected Class<? extends ModelURIConverter> bindModelURIConverter() {
      return DefaultModelURIConverter.class;
   }

   protected Class<? extends ResourceSetFactory> bindResourceSetFactory() {
      return DefaultResourceSetFactory.class;
   }

   protected void configureAppEntryPoints(final MapBinding<EntryPointType, AppEntryPoint> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_APP_ENTRY_POINTS);
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
   protected void configureCodecs(final MapBinding<String, Codec> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_CODECS);
   }

   protected void configureCommandCodecs(final MapBinding<String, CommandContribution> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_COMMAND_CODECS);
   }

   protected void configureEPackages(final MultiBinding<EPackageConfiguration> binding) {
      binding.addAll(MultiBindingDefaults.DEFAULT_EPACKAGE_CONFIGURATIONS);
   }

   protected void configureModelURIResolvers(
      final MapBinding<APIVersionRange, Function<? super URI, Optional<URI>>> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_MODEL_URI_RESOLVERS);
   }

   protected void configureModelURIDeresolvers(
      final MapBinding<APIVersionRange, Function<? super URI, URI>> binding) {
      binding.putAll(MultiBindingDefaults.DEFAULT_MODEL_URI_DERESOLVERS);
   }

   protected void configureMultiBindings() {
      configure(MultiBinding.create(EPackageConfiguration.class), this::configureEPackages);
      configure(MultiBinding.create(Routing.class), this::configureRoutings);
      configure(MapBinding.create(EntryPointType.class, AppEntryPoint.class), this::configureAppEntryPoints);
      configure(MapBinding.create(String.class, Codec.class), this::configureCodecs);
      configure(MapBinding.create(String.class, CommandContribution.class), this::configureCommandCodecs);
      configure(MultiBinding.create(ModelWatcher.Factory.class), this::configureModelWatcherFactories);
      configure(MultiBinding.create(PatchCommandHandler.class), this::configurePatchCommandHandlers);
      configure(MapBinding.create(APIVersionRange.class, new TypeLiteral<Function<? super URI, Optional<URI>>>() {})
         .setAnnotationName(DefaultModelURIConverter.MODEL_URI_RESOLVERS), this::configureModelURIResolvers);
      configure(MapBinding.create(APIVersionRange.class, new TypeLiteral<Function<? super URI, URI>>() {})
         .setAnnotationName(DefaultModelURIConverter.MODEL_URI_DERESOLVERS), this::configureModelURIDeresolvers);
   }

   protected void configureRoutings(final MultiBinding<Routing> binding) {
      binding.addAll(MultiBindingDefaults.DEFAULT_ROUTINGS);
   }

   protected AdapterFactory provideAdapterFactory() {
      return ProviderDefaults.provideAdapterFactory();
   }

   protected Javalin provideJavalin() {
      return ProviderDefaults.provideJavalin();
   }

   protected ObjectMapper provideObjectMapper() {
      return ProviderDefaults.provideObjectMapper();
   }

   protected void configureModelWatcherFactories(final MultiBinding<ModelWatcher.Factory> binding) {
      binding.addAll(MultiBindingDefaults.DEFAULT_MODEL_WATCHER_FACTORIES);
   }

   protected void configurePatchCommandHandlers(final MultiBinding<PatchCommandHandler> binding) {
      binding.add(JsonPatchHandler.class);
      binding.add(EMFCommandHandler.class);
   }
}
