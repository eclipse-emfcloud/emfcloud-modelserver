/********************************************************************************
 * Copyright (c) 2019-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.di;

import static org.eclipse.emfcloud.modelserver.common.APIVersion.API_V2;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.common.APIVersionRange;
import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.edit.CommandContribution;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.command.AddCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.CompoundCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.RemoveCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.UpdateModelCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.common.DefaultModelURIConverter;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerRoutingV1;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerRoutingV2;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecProvider;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.DefaultCodecsProvider;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.FileModelWatcher;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatcher;
import org.eclipse.emfcloud.modelserver.emf.configuration.CommandPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EcorePackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.launch.ModelServerEntryPoint;

public final class MultiBindingDefaults {
   private MultiBindingDefaults() {}

   public static final List<Class<? extends EPackageConfiguration>> DEFAULT_EPACKAGE_CONFIGURATIONS = List.of(
      EcorePackageConfiguration.class,
      CommandPackageConfiguration.class);

   public static final List<Class<? extends Routing>> DEFAULT_ROUTINGS = List.of(
      ModelServerRoutingV2.class,
      ModelServerRoutingV1.class);

   public static final Map<EntryPointType, Class<? extends AppEntryPoint>> DEFAULT_APP_ENTRY_POINTS = Map.of(
      EntryPointType.REST, ModelServerEntryPoint.class);

   public static final List<Class<? extends CodecProvider>> DEFAULT_CODECS = List.of(
      DefaultCodecsProvider.class);

   public static final Map<String, Class<? extends CommandContribution>> DEFAULT_COMMAND_CODECS = Map.of(
      EMFCommandType.ADD, AddCommandContribution.class,
      EMFCommandType.SET, SetCommandContribution.class,
      EMFCommandType.REMOVE, RemoveCommandContribution.class,
      EMFCommandType.COMPOUND, CompoundCommandContribution.class,
      UpdateModelCommandContribution.TYPE, UpdateModelCommandContribution.class);

   public static final List<Class<? extends ModelWatcher.Factory>> DEFAULT_MODEL_WATCHER_FACTORIES = List.of(
      FileModelWatcher.Factory.class);

   public static final Map<APIVersionRange, Class<? extends Function<? super URI, Optional<URI>>>> DEFAULT_MODEL_URI_RESOLVERS = Map
      .of(
         APIVersion.ZERO.range(API_V2), DefaultModelURIConverter.APIV1Resolver.class,
         API_V2.range(), DefaultModelURIConverter.APIV2Resolver.class);

   public static final Map<APIVersionRange, Class<? extends Function<? super URI, URI>>> DEFAULT_MODEL_URI_DERESOLVERS = Map
      .of(
         APIVersion.ZERO.range(API_V2), DefaultModelURIConverter.APIV1Deresolver.class,
         API_V2.range(), DefaultModelURIConverter.APIV2Deresolver.class);

}
