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

package org.eclipse.emfcloud.modelserver.edit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;

import com.google.inject.Inject;

/**
 * Default implementation of {@link Command} codec.
 */
public class DICommandCodec implements CommandCodec {

   private final Map<String, CommandContribution> typeToCodec = new LinkedHashMap<>();

   /**
    * Initializes me.
    */
   public DICommandCodec() {
      super();
   }

   /**
    * Injected constructor.
    *
    * @param codecs the command codecs per command type
    */
   @Inject(optional = true)
   public void setCommandCodecs(final Map<String, CommandContribution> codecs) {
      typeToCodec.putAll(codecs);
   }

   @Override
   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS" })
   public Command clientToServer(final URI modelUri, final EditingDomain domain, final CCommand clientCommand)
      throws DecodingException {
      String commandType = clientCommand.getType();
      CommandContribution codecContribution = typeToCodec.get(commandType);
      if (codecContribution == null) {
         throw new DecodingException("Unknown command type: " + commandType);
      }
      Command result = codecContribution.clientToServer(modelUri, domain, clientCommand);
      return ModelServerCommand.wrap(result, clientCommand);
   }

   @Override
   @SuppressWarnings({ "checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS" })
   public CCommand serverToClient(final Command command) throws EncodingException {
      Command actualCommand = ModelServerCommand.unwrap(command);
      Optional<CCommand> userCommand = ModelServerCommand.getClientCommand(command);
      if (!userCommand.isPresent()) {
         throw new EncodingException("Cannot determine origin of command: " + command);
      }

      String commandType = userCommand.get().getType();
      CommandContribution codecContribution = typeToCodec.get(commandType);
      if (codecContribution == null) {
         throw new EncodingException("Unknown command type: " + commandType);
      }
      return codecContribution.serverToClient(actualCommand, userCommand.get());
   }
}
