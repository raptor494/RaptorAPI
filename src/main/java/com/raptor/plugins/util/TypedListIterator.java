package com.raptor.plugins.util;

import java.util.ListIterator;
import java.util.Objects;

class TypedListIterator<E> implements ListIterator<E> {
	protected final Class<E> type;
	protected final ListIterator<E> iter;
	
	@SuppressWarnings("unchecked")
	public TypedListIterator(Class<E> type, ListIterator<? extends E> iter) {
		this.type = Objects.requireNonNull(type, "type may not be null");
		if (type.isPrimitive())
			throw new IllegalArgumentException("type may not be primitive");
		this.iter = (ListIterator<E>)iter;
	}
	
	protected ListIterator<E> getWrapped() {
		return iter;
	}
	
	protected E cast(Object obj) {
		return type.cast(obj);
	}
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public E next() {
		return cast(iter.next());
	}

	@Override
	public boolean hasPrevious() {
		return iter.hasPrevious();
	}

	@Override
	public E previous() {
		return cast(iter.previous());
	}

	@Override
	public int nextIndex() {
		return iter.nextIndex();
	}

	@Override
	public int previousIndex() {
		return iter.previousIndex();
	}

	@Override
	public void remove() {
		iter.remove();
	}

	@Override
	public void set(E e) {
		iter.set(cast(e));
	}

	@Override
	public void add(E e) {
		iter.add(cast(e));
	}

	@Override
	public int hashCode() {
		return iter.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || iter.equals(obj);
	}
	
	@Override
	public String toString() {
		return iter.toString();
	}
}
