/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.common.patch;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.command.Command;

/**
 * A Compound Command that uses lazily-instantiated commands. Instead of creating
 * all Commands when instantiating the {@link SimpleLazyCompoundCommand}, actual
 * commands will be supplied during execution. The first command of the list
 * may be created earlier, when testing for {@link #canExecute()}.
 */
public interface LazyCompoundCommand extends Command {

   List<Callable<Command>> getCommands();

   void append(Callable<Command> command);

   default void append(final Command command) {
      this.append(() -> command);
   }

}
