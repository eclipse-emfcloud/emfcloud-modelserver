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
package org.eclipse.emfcloud.modelserver.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

public final class MapBinding<K, V> {
   public static <K, V> MapBinding<K, V> create(final Class<K> keyType, final Class<V> valueType) {
      return create(TypeLiteral.get(keyType), TypeLiteral.get(valueType));
   }

   public static <K, V> MapBinding<K, V> create(final TypeLiteral<K> keyType, final Class<V> valueType) {
      return create(keyType, TypeLiteral.get(valueType));
   }

   public static <K, V> MapBinding<K, V> create(final Class<K> keyType, final TypeLiteral<V> valueType) {
      return create(TypeLiteral.get(keyType), valueType);
   }

   public static <K, V> MapBinding<K, V> create(final TypeLiteral<K> keyType, final TypeLiteral<V> valueType) {
      return new MapBinding<>(keyType, valueType);
   }

   private final Map<K, Class<? extends V>> bindings;

   private final TypeLiteral<K> keyType;
   private final TypeLiteral<V> valueType;

   private String annotationName;

   private MapBinding(final TypeLiteral<K> keyType, final TypeLiteral<V> valueType) {
      this.keyType = keyType;
      this.valueType = valueType;
      bindings = new LinkedHashMap<>();
   }

   public MapBinding<K, V> setAnnotationName(final String annotationName) {
      this.annotationName = annotationName;
      return this;
   }

   public TypeLiteral<K> getKeyType() { return keyType; }

   public TypeLiteral<V> getValueType() { return valueType; }

   public String getAnnotationName() { return annotationName; }

   /**
    * Applies the stored bindings to the given binder in form of a set binding.
    *
    * @param binder binder
    */
   public void applyBinding(final Binder binder) {
      MapBinder<K, V> mapBinder = this.annotationName == null
         ? MapBinder.newMapBinder(binder, getKeyType(), getValueType())
         : MapBinder.newMapBinder(binder, getKeyType(), getValueType(), Names.named(annotationName));
      bindings.forEach((key, value) -> mapBinder.addBinding(key).to(value));
   }

   public Class<? extends V> put(final K key, final Class<? extends V> value) {
      return bindings.put(key, value);
   }

   public void putAll(final Map<K, Class<? extends V>> newBindings) {
      bindings.putAll(newBindings);
   }

   public Class<? extends V> remove(final K key) {
      return bindings.remove(key);
   }

   public boolean rebind(final K key, final Class<? extends V> newBinding) {
      if (remove(key) == null) {
         put(key, newBinding);
         return true;
      }
      return false;
   }

   public Map<K, Class<? extends V>> getAll() { return this.bindings; }

   public boolean containsKey(final Class<? extends K> key) {
      return bindings.containsKey(key);
   }

}
