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
package org.eclipse.emfcloud.modelserver.client;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.DefaultJsonCodec;

/**
 * <p>
 * An {@link EObject} subscription listener using the {@link ModelServerPathParametersV1#FORMAT_JSON json} format.
 * </p>
 * <p>
 * For API v2 or later, especially using the {@link ModelServerPathParametersV2#FORMAT_JSON_V2 json-v2} format,
 * use the {@link EObjectSubscriptionListener} class with the appropriate {@link Codec}.
 * </p>
 */
public class JsonToEObjectSubscriptionListener extends EObjectSubscriptionListener {
   public JsonToEObjectSubscriptionListener() {
      super(new DefaultJsonCodec());
   }
}
