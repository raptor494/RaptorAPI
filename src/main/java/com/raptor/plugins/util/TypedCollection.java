package com.raptor.plugins.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypedCollection<E> implements Collection<E> {
	protected final Class<E> type;
	protected final Collection<E> values;
	
	public TypedCollection(Class<E> type) {
		this.type = Objects.requireNonNull(type, "type may not be null");
		if (type.isPrimitive())
			throw new IllegalArgumentException("type may not be primitive");
		this.values = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	public TypedCollection(Class<E> type, Collection<? extends E> wrapped) {
		this.type = Objects.requireNonNull(type, "type may not be null");
		if (type.isPrimitive())
			throw new IllegalArgumentException("type may not be primitive");
		this.values = (Collection<E>)wrapped;
		if(!wrapped.isEmpty()) {
			for(E e : wrapped) {
				cast(e);
			}
		}
	}
	
	protected Collection<E> getWrapped() {
		return values;
	}
	
	protected E cast(Object obj) {
		return getComponentType().cast(obj);
	}
	
	protected boolean isInstance(Object obj) {
		return obj == null || getComponentType().isInstance(obj);
	}
	
	public Class<E> getComponentType() {
		return type;
	}

	@Override
	public int size() {
		return getWrapped().size();
	}

	@Override
	public boolean isEmpty() {
		return getWrapped().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return isInstance(o) && getWrapped().contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return getWrapped().iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		return getWrapped().toArray((E[])Array.newInstance(getComponentType(), size()));
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getWrapped().toArray(a);
	}

	@Override
	public boolean add(E e) {
		return getWrapped().add(cast(e));
	}

	@Override
	public boolean remove(Object o) {
		return getWrapped().remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getWrapped().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c.isEmpty())
			return false;
		boolean modified = false;
		for(E e : c) {
			modified |= add(e);
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return getWrapped().removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return getWrapped().retainAll(c);
	}

	@Override
	public void clear() {
		getWrapped().clear();
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		getWrapped().forEach(action);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return getWrapped().removeIf(filter);
	}

	@Override
	public Spliterator<E> spliterator() {
		return getWrapped().spliterator();
	}

	@Override
	public Stream<E> stream() {
		return getWrapped().stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return getWrapped().parallelStream();
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || getWrapped().equals(obj);
	}
	
	@Override
	public String toString() {
		return getWrapped().toString();
	}
}
