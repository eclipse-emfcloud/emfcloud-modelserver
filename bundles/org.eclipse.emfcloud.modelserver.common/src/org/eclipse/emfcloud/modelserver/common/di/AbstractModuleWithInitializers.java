/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * An abstract module to subclass to get support for post-injection invocation
 * of {@link Initialize @Initialize} methods.
 */
public class AbstractModuleWithInitializers extends AbstractModule {

   @Override
   protected void configure() {
      bindListener(Matchers.any(), new InitializerSupport());
   }

}
