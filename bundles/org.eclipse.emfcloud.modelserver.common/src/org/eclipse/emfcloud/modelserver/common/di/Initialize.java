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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation for a method to be invoked by the Guice Injector after the object has
 * been created and all member injections are performed. This provides a post-injection
 * initialization hook in which the object can be assured of all its dependencies being
 * available.
 * </p>
 * <p>
 * It is an error for an initialization method to have parameters. All dependencies
 * should be injected prior to invocation of the initializer. If the initializer has
 * parameters, an exception will be thrown at run-time.
 * </p>
 * <p>
 * Initializers are called in superclass order from top to bottom of the class hierarchy.
 * If a class overrides the initializer method of a superclass and the overriding method
 * is itself annotated with {@code @Initialize}, then the method is only called once.
 * But the overriding method needs not have the annotation because it will be invoked
 * via the superclass.
 * </p>
 * <p>
 * At most one method of a class, not considering* inheritance, may be designated as
 * an initializer.
 * </p>
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Initialize {
   // Empty annotation
}
