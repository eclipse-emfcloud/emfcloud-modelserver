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
package org.eclipse.emfcloud.modelserver.edit;

import java.util.Map;

import org.eclipse.emfcloud.modelserver.edit.command.AddCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.CompoundCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.RemoveCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandContribution;

public class EMFCommandCodec extends DICommandCodec {
   public EMFCommandCodec() {
      super();
      setCommandCodecs(Map.of(
         EMFCommandType.ADD, new AddCommandContribution(),
         EMFCommandType.SET, new SetCommandContribution(),
         EMFCommandType.REMOVE, new RemoveCommandContribution(),
         EMFCommandType.COMPOUND, new CompoundCommandContribution(this)));
   }

}
