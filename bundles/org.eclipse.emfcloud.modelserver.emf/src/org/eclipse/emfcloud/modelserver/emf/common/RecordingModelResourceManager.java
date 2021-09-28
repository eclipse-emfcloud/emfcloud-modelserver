/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.emf.configuration.ChangePackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;

import com.google.inject.Inject;

/**
 * A custom implementation of the resource manager that records the changes when executing commands on the command
 * stack and puts the result into the command execution result sent to the client.
 */
public class RecordingModelResourceManager extends DefaultModelResourceManager {

   @Inject
   public RecordingModelResourceManager(final Set<EPackageConfiguration> configurations,
      final AdapterFactory adapterFactory,
      final ServerConfiguration serverConfiguration) {
      super(configurations, adapterFactory, serverConfiguration);
   }

   @Override
   public void initialize() {
      this.configurations.add(new ChangePackageConfiguration());
      super.initialize();
   }

   @Override
   protected CommandExecutionContext executeCommand(final ModelServerEditingDomain domain, final Command serverCommand,
      final CCommand clientCommand) {
      ChangeRecorder recorder = new ChangeRecorder(domain.getResourceSet());
      CommandExecutionContext context = super.executeCommand(domain, serverCommand, clientCommand);
      ChangeDescription recording = recorder.endRecording();
      recorder.dispose();
      return new RecordingCommandExecutionContext(context, recording);
   }

   @Override
   protected Optional<CommandExecutionContext> undoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand,
      final CCommand clientCommand) {
      ChangeRecorder recorder = new ChangeRecorder(domain.getResourceSet());
      Optional<CommandExecutionContext> context = super.undoCommand(domain, serverCommand, clientCommand);
      ChangeDescription recording = recorder.endRecording();
      recorder.dispose();
      return context.map(existingContext -> new RecordingCommandExecutionContext(existingContext, recording));
   }

   @Override
   protected Optional<CommandExecutionContext> redoCommand(final ModelServerEditingDomain domain,
      final Command serverCommand,
      final CCommand clientCommand) {
      ChangeRecorder recorder = new ChangeRecorder(domain.getResourceSet());
      Optional<CommandExecutionContext> context = super.redoCommand(domain, serverCommand, clientCommand);
      ChangeDescription recording = recorder.endRecording();
      recorder.dispose();
      return context.map(existingContext -> new RecordingCommandExecutionContext(existingContext, recording));
   }

   @Override
   protected CCommandExecutionResult createExecutionResult(final CommandExecutionContext context) {
      CCommandExecutionResult result = super.createExecutionResult(context);
      if (context instanceof RecordingCommandExecutionContext) {
         ChangeDescription recording = ((RecordingCommandExecutionContext) context).getChangeDescription();
         result.setChangeDescription(recording);
      }
      return result;
   }

   public static class RecordingCommandExecutionContext extends CommandExecutionContext {

      private final ChangeDescription changeDescription;

      public RecordingCommandExecutionContext(final CommandExecutionContext context,
         final ChangeDescription recording) {
         this(context.getType(), context.getClientCommand(), context.getServerCommand(), recording);
      }

      public RecordingCommandExecutionContext(final String type, final CCommand clientCommand,
         final Command serverCommand, final ChangeDescription changeDescription) {
         super(type, clientCommand, serverCommand);
         this.changeDescription = changeDescription;
      }

      public ChangeDescription getChangeDescription() { return changeDescription; }
   }
}
