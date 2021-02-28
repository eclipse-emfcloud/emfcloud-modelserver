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
package org.eclipse.emfcloud.modelserver.edit.command;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.utils.GenericsUtil;
import org.eclipse.emfcloud.modelserver.edit.CommandContribution;

public abstract class BasicCommandContribution<T extends Command> implements CommandContribution {

   protected final Class<T> commandClass;

   public BasicCommandContribution() {
      this.commandClass = deriveCommandClass();
   }

   @SuppressWarnings("unchecked")
   protected Class<T> deriveCommandClass() {
      return (Class<T>) GenericsUtil.getGenericTypeParameterClass(getClass(), BasicCommandContribution.class);
   }

   @Override
   public CCommand serverToClient(final Command command, final CCommand origin) throws EncodingException {
      if (commandClass.isInstance(command)) {
         return toClient(commandClass.cast(command), origin);
      }
      throw new EncodingException("Unexpected command of type " + command.getClass().getSimpleName());
   }

   protected CCommand toClient(final T command, final CCommand origin) throws EncodingException {
      return origin;
   }

   @Override
   public T clientToServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {
      return toServer(modelUri, domain, command);
   }

   protected abstract T toServer(URI modelUri, EditingDomain domain, CCommand command) throws DecodingException;

}
