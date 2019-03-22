package com.raptor.plugins.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public final class TypedList<E> extends TypedCollection<E> implements List<E>, Cloneable {

	public TypedList(Class<E> type) {
		super(type, new ArrayList<>());
	}

	public TypedList(Class<E> type, List<? extends E> wrappedList) {
		super(type, wrappedList);
	}
	
	public TypedList(Class<E> type, Collection<? extends E> initialValues) {
		super(type, new ArrayList<>(initialValues.size()));
		addAll(initialValues);
	}
	
	public TypedList(TypedCollection<E> initialValues) {
		super(initialValues.type, new ArrayList<>(initialValues.size()));
		addAll(initialValues);
	}
	
	@Override
	protected List<E> getWrapped() {
		return (List<E>)values;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if(c.isEmpty())
			return false;
		for(E e : c) {
			add(index++, cast(e));
		}
		return true;
	}

	@Override
	public E get(int index) {
		return getWrapped().get(index);
	}

	@Override
	public E set(int index, E element) {
		return getWrapped().set(index, cast(element));
	}

	@Override
	public void add(int index, E element) {
		getWrapped().add(index, cast(element));
	}

	@Override
	public E remove(int index) {
		return getWrapped().remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return isInstance(o)? getWrapped().indexOf(o) : -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return isInstance(o)? getWrapped().lastIndexOf(o) : -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new TypedListIterator<>(type, getWrapped().listIterator());
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new TypedListIterator<>(type, getWrapped().listIterator(index));
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new TypedList<>(type, getWrapped().subList(fromIndex, toIndex));
	}
}
