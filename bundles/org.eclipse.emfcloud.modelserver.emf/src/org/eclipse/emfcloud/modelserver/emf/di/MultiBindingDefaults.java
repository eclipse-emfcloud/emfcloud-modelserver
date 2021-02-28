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

import java.util.List;
import java.util.Map;

import org.eclipse.emfcloud.modelserver.common.AppEntryPoint;
import org.eclipse.emfcloud.modelserver.common.EntryPointType;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.Routing;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;
import org.eclipse.emfcloud.modelserver.edit.CommandContribution;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandType;
import org.eclipse.emfcloud.modelserver.edit.command.AddCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.CompoundCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.RemoveCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.UpdateModelCommandContribution;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerRoutingV1;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;
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
      ModelServerRoutingV1.class);

   public static final Map<EntryPointType, Class<? extends AppEntryPoint>> DEFAULT_APP_ENTRY_POINTS = Map.of(
      EntryPointType.REST, ModelServerEntryPoint.class);

   public static final Map<String, Class<? extends Codec>> DEFAULT_CODECS = Map.of(
      ModelServerPathParameters.FORMAT_XMI, XmiCodec.class,
      ModelServerPathParameters.FORMAT_JSON, JsonCodec.class);

   public static final Map<String, Class<? extends CommandContribution>> DEFAULT_COMMAND_CODECS = Map.of(
      EMFCommandType.ADD, AddCommandContribution.class,
      EMFCommandType.SET, SetCommandContribution.class,
      EMFCommandType.REMOVE, RemoveCommandContribution.class,
      EMFCommandType.COMPOUND, CompoundCommandContribution.class,
      UpdateModelCommandContribution.TYPE, UpdateModelCommandContribution.class);
}
