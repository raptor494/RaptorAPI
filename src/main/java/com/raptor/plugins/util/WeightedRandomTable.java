package com.raptor.plugins.util;

import static com.raptor.plugins.util.WeightedRandom.countTotalWeight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

public class WeightedRandomTable<E extends WeightedItem> implements Collection<E> {
	private ArrayList<E> items;
	private int totalWeight;
	private WeightedRandom rand;
	
	/**
	 * Creates a new WeightedRandomTable. The underlying random generator
	 * uses the default seed, and the underlying collection is an empty {@linkplain ArrayList}.
	 */
	public WeightedRandomTable() {
		items = new ArrayList<>();
		totalWeight = 0;
		rand = new WeightedRandom();
	}
	
	/**
	 * Creates a new WeightedRandomTable with initial elements and a given
	 * random generator. The initial elements are copied into a 
	 * new {@linkplain ArrayList} instance.
	 * 
	 * @param itemsIn the initial elements of this table
	 * @param randIn the random generator
	 * 
	 * @throws IllegalArgumentException if {@code itemsIn} or {@code randIn} are {@code null}, 
	 * 			or {@code itemsIn} contains any null elements. 
	 */
	public WeightedRandomTable(Collection<? extends E> itemsIn, WeightedRandom randIn) {
		Validate.notNull(randIn, "random generator may not be null");
		Validate.noNullElements(itemsIn, "items may not be null/contain null elements");
		
		items = new ArrayList<>(itemsIn);
		totalWeight = countTotalWeight(itemsIn);
		rand = randIn;
	}
	
	/**
	 * Creates a new WeightedRandomTable with initial elements. 
	 * The initial elements are copied into a new {@linkplain ArrayList} instance.
	 * 
	 * @param itemsIn the initial elements of this table
	 * 
	 * @throws IllegalArgumentException if {@code itemsIn} is {@code null}, 
	 * 			or {@code itemsIn} contains any null elements. 
	 */
	public WeightedRandomTable(Collection<? extends E> itemsIn) {
		Validate.noNullElements(itemsIn, "items may not be null/contain null elements");
		
		items = new ArrayList<>(itemsIn);
		totalWeight = countTotalWeight(itemsIn);
		rand = new WeightedRandom();
	}
	
	/**
	 * Creates a new WeightedRandomTable with initial elements and 
	 * a random generator initialized with a given seed. 
	 * The initial elements are copied into a new {@linkplain ArrayList} instance.
	 * 
	 * @param itemsIn the initial elements of this table
	 * @param seed the seed to create the random generator with
	 * 
	 * @throws IllegalArgumentException if {@code itemsIn} is {@code null}, 
	 * 			or {@code itemsIn} contains any null elements. 
	 */
	public WeightedRandomTable(Collection<? extends E> itemsIn, long seed) {
		Validate.noNullElements(itemsIn, "items may not be null/contain null elements");
		
		items = new ArrayList<>(itemsIn);
		totalWeight = countTotalWeight(itemsIn);
		rand = new WeightedRandom(seed);
	}
	
	/**
	 * Creates a new WeightedRandmTable with a given random generator.
	 * 
	 * @param randIn the random generator
	 */
	public WeightedRandomTable(WeightedRandom randIn) {
		Validate.notNull(randIn, "random generator may not be null");
		
		items = new ArrayList<>();
		totalWeight = 0;
		rand = randIn;
	}
	
	/**
	 * Creates a new WeightedRandomTable with a random generator
	 * initialized with a given seed.
	 * 
	 * @param seed the seed to create the random generator with
	 */
	public WeightedRandomTable(long seed) {
		items = new ArrayList<>();
		totalWeight = 0;
		rand = new WeightedRandom(seed);
	}
	
	/**
	 * Sets the seed used by the internal random number generator.
	 */
	public void setSeed(long seed) {
		rand.setSeed(seed);
	}

	/**
	 * @return an element of this table selected randomly by weight
	 */
	public E chooseRandomElement() {
		return rand.choose(items, totalWeight);
	}
	
	@Override
	public int size() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return items.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> iter = items.iterator();
		return new Iterator<E>() {
			E last;
			
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public E next() {
				return last = iter.next();
			}

			@Override
			public void remove() {
				totalWeight -= last.weight();
				iter.remove();
			}
			
		};
	}

	@Override
	public Object[] toArray() {
		return items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return items.toArray(a);
	}

	@Override
	public boolean add(E e) {
		if(items.add(e)) {
			totalWeight += e.weight();
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(Object obj) {
		if(items.remove(obj)) {
			totalWeight -= ((E) obj).weight();
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(items.addAll(c)) {
			totalWeight += countTotalWeight(c);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(items.removeAll(c)) {
			totalWeight = countTotalWeight(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(items.retainAll(c)) {
			totalWeight = countTotalWeight(items);
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		totalWeight = 0;
		items.clear();
	}
	
	/**
	 * This method returns true only if {@code this == obj} is true.
	 * It does not consider the contents of this table or the current
	 * seed of the random generator.
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public String toString() {
		return super.toString() + items.toString();
	}

}
