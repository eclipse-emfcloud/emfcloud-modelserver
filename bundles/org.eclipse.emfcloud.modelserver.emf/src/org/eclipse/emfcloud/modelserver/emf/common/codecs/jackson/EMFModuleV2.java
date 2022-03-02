/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common.codecs.jackson;

import org.eclipse.emfcloud.jackson.annotations.EcoreIdentityInfo;
import org.eclipse.emfcloud.jackson.annotations.EcoreTypeInfo;
import org.eclipse.emfcloud.jackson.module.EMFModule;
import org.eclipse.emfcloud.modelserver.jsonschema.JsonConstants;

/**
 * Extension of the default {@link EMFModule}, to support
 * "Json API V2" serialization.
 */
public class EMFModuleV2 extends EMFModule {

   private static final long serialVersionUID = 8071480170957809612L;

   /**
    * Use a "$type" property instead of the default "eClass" property,
    * to be more consistent with Json conventions.
    */
   static final String TYPE_PROPERTY = JsonConstants.TYPE_ATTR;

   /**
    * Use an "$id" property instead of the default "@id" property,
    * to be more consistent with Json conventions.
    */
   static final String ID_PROPERTY = JsonConstants.ID_ATTR;

   @Override
   public void setupModule(final SetupContext context) {
      setTypeInfo(new EcoreTypeInfo(TYPE_PROPERTY));
      setIdentityInfo(new EcoreIdentityInfo(ID_PROPERTY));
      // Always serialize $id: attributes
      configure(Feature.OPTION_USE_ID, true);
      super.setupModule(context);
   }

}
