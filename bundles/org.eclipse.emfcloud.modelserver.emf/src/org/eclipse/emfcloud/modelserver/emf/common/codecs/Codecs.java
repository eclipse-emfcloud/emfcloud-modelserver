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
package org.eclipse.emfcloud.modelserver.emf.common.codecs;

import java.util.Map;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParameters;
import org.eclipse.emfcloud.modelserver.common.codecs.XmiCodec;

/**
 * Legacy class that will be removed soon. Inject and use the CodecsManager instead.
 *
 * @deprecated
 */
@Deprecated
public class Codecs extends DICodecsManager {
   public Codecs() {
      super(Map.of(
         ModelServerPathParameters.FORMAT_XMI, new XmiCodec(),
         ModelServerPathParameters.FORMAT_JSON, new JsonCodec()));
   }

}
