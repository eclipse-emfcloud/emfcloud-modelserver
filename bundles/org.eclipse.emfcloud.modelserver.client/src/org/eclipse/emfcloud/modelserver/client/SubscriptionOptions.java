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
package org.eclipse.emfcloud.modelserver.client;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.Operation;

import com.google.common.collect.ImmutableMap;

/**
 * An specification of subscription options.
 */
public interface SubscriptionOptions extends Serializable {
   /**
    * Query the subscription format.
    *
    * @return the subscription format, for example {@link ModelServerPathParametersV2#FORMAT_JSON_V2}
    */
   String getFormat();

   /**
    * Query whether the subscription includes live validation results.
    *
    * @return whether live validation is included in the subscription
    */
   boolean isLiveValidation();

   /**
    * Query the subscription timeout.
    *
    * @return the subscription timeout, in milliseconds
    */
   long getTimeout();

   /**
    * Query the subscription's scheme for the {@code path} property in JSON Patch operations
    * in incremental update notifications.
    *
    * @return the operation path scheme, for example {@link ModelServerPathParametersV2#PATHS_JSON_POINTER}
    */
   String getPathScheme();

   /**
    * Query additional subscription options supported by the server.
    *
    * @return additional subscription options
    */
   Map<String, String> getAdditionalOptions();

   /**
    * Query whether the options include additional options not explicitly supported.
    *
    * @return whether any additional options are specified
    */
   default boolean hasAdditionalOptions() {
      return !getAdditionalOptions().isEmpty();
   }

   /**
    * Obtain a builder to create subscription options with suitable defaults, where applicable.
    *
    * @return a subscription options builder
    */
   static Builder builder() {
      return new Impl.Builder();
   }

   //
   // Nested types
   //

   /**
    * A builder of {@link SubscriptionOptions} with suitable defaults, where applicable.
    */
   interface Builder {
      /**
       * Encode subscription messages in the given {@code format}.
       *
       * @param format the subscription format, for example {@link ModelServerPathParametersV2#FORMAT_JSON_V2}
       * @return myself, for convenience of call chaining
       */
      Builder withFormat(String format);

      /**
       * Include live validation results in the subscription. Equivalent to
       * {@link #withLiveValidation(boolean) withLiveValidation(true)}.
       *
       * @return myself, for convenience of call chaining
       */
      default Builder withLiveValidation() {
         return withLiveValidation(true);
      }

      /**
       * Optionally include live validation results in the subscription.
       *
       * @param validation whether to include live validation results
       * @return myself, for convenience of call chaining
       */
      Builder withLiveValidation(boolean validation);

      /**
       * Time the subscription out after the given amount of idle time.
       *
       * @param timeout the timeout, measured in milliseconds
       * @return myself, for convenience of call chaining
       */
      default Builder withTimeout(final long timeout) {
         return withTimeout(timeout, TimeUnit.MILLISECONDS);
      }

      /**
       * Time the subscription out after the given amount of idle time.
       *
       * @param timeout the timeout
       * @param unit    the unit of measure of the {@code timeout}
       * @return myself, for convenience of call chaining
       */
      Builder withTimeout(long timeout, TimeUnit unit);

      /**
       * Encode the {@code path} of {@link Operation}s in {@link JsonPatch} incremental update messages in the given
       * scheme.
       *
       * @param pathScheme the path scheme, for example {@link ModelServerPathParametersV2#PATHS_JSON_POINTER}
       * @return myself, for convenience of call chaining
       */
      Builder withPathScheme(String pathScheme);

      /**
       * Add a generic query parameter that is supported by the server's subscription URL.
       *
       * @param key   the parameter key
       * @param value the parameter value
       * @return myself, for convenience of call chaining
       *
       * @throws IllegalArgumentException if the {@code key} is a pre-defined subscription option
       */
      Builder withOption(String key, String value);

      /**
       * Create the subscription options.
       *
       * @return the subscription options
       */
      SubscriptionOptions build();
   }

   /**
    * The immutable subscription options implementation.
    */
   final class Impl implements SubscriptionOptions {

      private static final long serialVersionUID = 7218439084247958619L;

      private String format;
      private boolean liveValidation;
      private long timeout;
      private String pathScheme;
      private Map<String, String> additionalOptions = Map.of();

      private Impl() {
         super();
      }

      @Override
      public String getFormat() { return format; }

      @Override
      public boolean isLiveValidation() { return liveValidation; }

      @Override
      public long getTimeout() { return timeout; }

      @Override
      public String getPathScheme() { return pathScheme; }

      @Override
      public Map<String, String> getAdditionalOptions() { return additionalOptions; }

      //
      // Nested types
      //

      /**
       * The subscription options builder.
       */
      static final class Builder implements SubscriptionOptions.Builder {
         private Impl product = new Impl();
         private ImmutableMap.Builder<String, String> additionalOptions;

         private Impl finishProduct() {
            Impl result = product();

            if (additionalOptions != null) {
               result.additionalOptions = additionalOptions.build();
               additionalOptions = null;
            }

            return result;
         }

         private Impl product() {
            if (product == null) {
               product = new Impl();
            }
            return product;
         }

         @Override
         public Builder withFormat(final String format) {
            product().format = format;
            return this;
         }

         @Override
         public Builder withLiveValidation(
            final boolean validation) {

            product().liveValidation = validation;
            return this;
         }

         @Override
         public Builder withTimeout(final long timeout,
            final TimeUnit unit) {
            product().timeout = unit.toMillis(timeout);
            return this;
         }

         @Override
         public Builder withPathScheme(
            final String pathScheme) {
            product().pathScheme = pathScheme;
            return this;
         }

         @Override
         public Builder withOption(final String key,
            final String value) {

            switch (key) {
               case ModelServerPathParametersV2.FORMAT:
               case ModelServerPathParametersV2.LIVE_VALIDATION:
               case ModelServerPathParametersV2.TIMEOUT:
               case ModelServerPathParametersV2.PATHS:
                  throw new IllegalArgumentException(key);
               default:
                  if (additionalOptions == null) {
                     additionalOptions = ImmutableMap.builder();
                  }

                  additionalOptions.put(key, value);
                  break;
            }

            return this;
         }

         @Override
         public SubscriptionOptions build() {
            Impl result = finishProduct();
            product = null;
            return result;
         }
      }

   }
}
