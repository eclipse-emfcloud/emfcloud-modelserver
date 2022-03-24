/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.jsonpatch.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.jsonpatch.util.JsonPatchCodec.Factory;

/**
 * The factory of the default codec implementation.
 */
public class DefaultJsonPatchCodecFactory implements Factory {

   public DefaultJsonPatchCodecFactory() {
      super();
   }

   @Override
   public JsonPatchCodec createCodec(final Codec codec, final EClass modelType) {
      return new DefaultJsonPatchCodec(codec, modelType);
   }

}
