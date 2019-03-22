package com.raptor.plugins.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TypedSet<E> extends TypedCollection<E> implements Set<E> {

	public TypedSet(Class<E> type) {
		super(type, new HashSet<>());
	}
	
	public TypedSet(Class<E> type, Set<E> initialValues) {
		super(type, initialValues);
	}
	
	public TypedSet(Class<E> type, Collection<? extends E> initialValues) {
		super(type, new HashSet<>());
		addAll(initialValues);
	}
	
	public TypedSet(TypedCollection<E> initialValues) {
		super(initialValues.type, new HashSet<>());
		addAll(initialValues);
	}
	
	@Override
	protected Set<E> getWrapped() {
		return (Set<E>)values;
	}

}
