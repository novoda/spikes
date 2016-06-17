package com.novoda.data;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoValue
public abstract class ImmutableMapWithCopy<K, V> {

    public static <K, V> ImmutableMapWithCopy<K, V> empty() {
        return from(ImmutableMap.<K, V>of());
    }

    public static <K, V> ImmutableMapWithCopy<K, V> from(Map<K, V> map) {
        return new AutoValue_ImmutableMapWithCopy<>(ImmutableMap.copyOf(map));
    }

    ImmutableMapWithCopy() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    abstract ImmutableMap<K, V> internalMap();

    public int size() {
        return internalMap().size();
    }

    public boolean isEmpty() {
        return internalMap().isEmpty();
    }

    public boolean containsKey(Object key) {
        return internalMap().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return internalMap().containsValue(value);
    }

    public V get(Object key) {
        return internalMap().get(key);
    }

    public ImmutableMapWithCopy<K, V> put(K key, V value) {
        Map<K, V> mutableMap = extractMutableMap();
        mutableMap.put(key, value);
        return from(mutableMap);
    }

    public ImmutableMapWithCopy<K, V> remove(Object key) {
        Map<K, V> mutableMap = extractMutableMap();
        mutableMap.remove(key);
        return from(mutableMap);
    }

    public ImmutableMapWithCopy<K, V> putAll(Map<? extends K, ? extends V> m) {
        Map<K, V> mutableMap = extractMutableMap();
        mutableMap.putAll(m);
        return from(mutableMap);
    }

    public Set<K> keySet() {
        return internalMap().keySet();
    }

    public Collection<V> values() {
        return internalMap().values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return internalMap().entrySet();
    }

    public <K2, V2> ImmutableMapWithCopy<K2, V2> transformEntries(Function<Map.Entry<K, V>, Map.Entry<K2, V2>> transformer) {
        ImmutableMap.Builder<K2, V2> builder = ImmutableMap.builder();
        for (Map.Entry<K, V> entry : internalMap().entrySet()) {
            builder.put(transformer.apply(entry));
        }
        return from(builder.build());
    }

    private Map<K, V> extractMutableMap() {
        Map<K, V> mutableMap = new HashMap<>();
        for (Map.Entry<K, V> entry : internalMap().entrySet()) {
            mutableMap.put(entry.getKey(), entry.getValue());
        }
        return mutableMap;
    }

    public ImmutableMapWithCopy<K, V> filter(Predicate<Map.Entry<K, V>> predicate) {
        return from(Maps.filterEntries(internalMap(), predicate));
    }

}
