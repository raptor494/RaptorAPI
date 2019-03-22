package com.raptor.plugins.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class TypedMap<K, V> implements Map<K, V> {
	private final Class<K> keyType;
	private final Class<V> valueType;
	private TypedSet<K> keySet;
	private TypedCollection<V> values;
	private EntrySet entrySet;
	private final Map<K, V> map;
	
	public TypedMap(Class<K> keyType, Class<V> valueType) {
		this.keyType = Objects.requireNonNull(keyType, "key type may not be null");
		this.valueType = Objects.requireNonNull(valueType, "value type may not be null");
		if(keyType.isPrimitive())
			throw new IllegalArgumentException("key type may not be primitive");
		if(valueType.isPrimitive())
			throw new IllegalArgumentException("value type may not be primitive");
		this.map = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public TypedMap(Class<K> keyType, Class<V> valueType, Map<? extends K, ? extends V> initialValues) {
		this.keyType = Objects.requireNonNull(keyType, "key type may not be null");
		this.valueType = Objects.requireNonNull(valueType, "value type may not be null");
		if(keyType.isPrimitive())
			throw new IllegalArgumentException("key type may not be primitive");
		if(valueType.isPrimitive())
			throw new IllegalArgumentException("value type may not be primitive");
		this.map = (Map<K, V>) initialValues;
		for(Map.Entry<K, V> entry : map.entrySet()) {
			castKey(entry.getKey());
			castValue(entry.getValue());
		}
	}
	
	public TypedMap(TypedMap<K, V> map) {
		this.keyType = map.keyType;
		this.valueType = map.valueType;
		this.map = new HashMap<>(map.size());
		putAll(map);
	}
	
	private K castKey(Object key) {
		return keyType.cast(Objects.requireNonNull(key, "keys may not be null"));
	}
	
	private V castValue(Object value) {
		return valueType.cast(Objects.requireNonNull(value, "values may not be null"));
	}
	
	@Override
	public V get(Object key) {
		return map.get(castKey(key));
	}
	
	@Override
	public boolean containsKey(Object key) {
		return keyType.isInstance(key) && map.containsKey(key);
	}
	
	@Override
	public V put(K key, V value) {
		return map.put(castKey(key), castValue(value));
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if(m.isEmpty())
			return;
		for(Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public V remove(Object key) {
		return keyType.isInstance(key)? map.remove(key) : null;
	}
	
	@Override
	public boolean containsValue(Object value) {
		return valueType.isInstance(value) && map.containsValue(value);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		if(keySet == null) {
			keySet = new TypedSet<>(keyType, map.keySet());
		}
		return keySet;
	}

	@Override
	public Collection<V> values() {
		if(values == null) {
			values = new TypedCollection<>(valueType, map.values());
		}
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		if(entrySet == null) {
			entrySet = new EntrySet();
		}
		return entrySet;
	}
	
	private class EntrySet implements Set<Entry<K, V>> {
		private Set<Entry<K, V>> set = map.entrySet();

		@Override
		public int size() {
			return set.size();
		}

		@Override
		public boolean isEmpty() {
			return set.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return set.contains(o);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return set.iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Entry<K, V>[] toArray() {
			return set.toArray(new Entry[size()]);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return set.toArray(a);
		}

		@Override
		public boolean add(Entry<K, V> e) {
			castKey(e.getKey());
			castValue(e.getValue());
			return set.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return o instanceof Entry && set.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return set.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			if(c.isEmpty())
				return false;
			boolean modified = false;
			for(Entry<K, V> e : c) {
				modified |= add(e);
			}
			return modified;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return set.retainAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return set.removeAll(c);
		}

		@Override
		public void clear() {
			set.clear();
		}
	}
}
