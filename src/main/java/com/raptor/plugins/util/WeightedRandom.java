package com.raptor.plugins.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.Validate;

public class WeightedRandom {
	public static final WeightedRandom DEFAULT = new WeightedRandom();
	
	private final Random rand;
	
	public WeightedRandom() {
		rand = new Random();
	}
	
	public WeightedRandom(long seed) {
		rand = new Random(seed);
	}
	
	public WeightedRandom(Random randIn) {
		Validate.notNull(randIn, "random generator was null");
		rand = randIn;
	}
	
	/**
	 * Sets the seed used by the internal random number generator.
	 */
	public void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	
	/**
	 * Chooses a random item from {@code items} by weight. If {@code items}
	 * is empty, {@code null} is returned. If the total weight of all
	 * the items in {@code items} is {@code 0}, a random element from
	 * {@code items} is returned.
	 * 
	 * @param items the items to choose from
	 * @return a random item from {@code items}
	 * @throws NullPointerException if {@code items} contains any null elements
	 */
	public <T extends WeightedItem> T choose(Collection<? extends T> items) {
		return choose(items, rand);
	}
	
	<T extends WeightedItem> T choose(Collection<? extends T> items, int totalWeight) {
		return choose(items, totalWeight, rand);
	}
	
	/**
	 * Chooses a random item from {@code items} by weight. If {@code items}
	 * is empty, {@code null} is returned. If the total weight of all
	 * the items in {@code items} is {@code 0}, {@code defaultValue} is returned.
	 * 
	 * @param items the items to choose from
	 * @param defaultValue the default value if {@code items} is empty or total weight is {@code 0}.
	 * @return a random item from {@code items}
	 * @throws NullPointerException if {@code items} contains any null elements
	 */
	public <T extends WeightedItem> T choose(Collection<? extends T> items, T defaultValue) {
		return choose(items, defaultValue, rand);
	}
	
	/**
	 * Chooses a random item from {@code items} by weight. If {@code items}
	 * is empty, {@code null} is returned. If the total weight of all
	 * the items in {@code items} is {@code 0}, a random element from
	 * {@code items} is returned.
	 * 
	 * @param items the items to choose from
	 * @param rand the random generator to use. If it is {@code null}, then {@link #DEFAULT}'s random generator will be used.
	 * @return a random item from {@code items}
	 * @throws NullPointerException if {@code items} contains any null elements
	 */
	public static <T extends WeightedItem> T choose(Collection<? extends T> items, Random rand) {
		return choose(items, countTotalWeight(items), rand);
	}
	
	static <T extends WeightedItem> T choose(Collection<? extends T> items, int totalWeight, Random rand) {
		if(items.isEmpty())
			return null;
		
		if(totalWeight == 0) {
			int index = (rand == null? DEFAULT.rand : rand).nextInt(items.size());
			if(items instanceof List) {
				return ((List<? extends T>)items).get(index);
			} else {
				Iterator<? extends T> iter = items.iterator();
				for(int i = 0; i < index; i++)
					iter.next();
				return iter.next();
			}
		}
		
		return choose(items, totalWeight, null, rand);
	}
	
	/**
	 * Chooses a random item from {@code items} by weight. If {@code items}
	 * is empty, {@code defaultValue} is returned. If the total weight of all
	 * the items in {@code items} is {@code 0}, {@code defaultValue} is returned.
	 * 
	 * @param items the items to choose from
	 * @param defaultValue the default value if {@code items} is empty or total weight is {@code 0}.
	 * @param rand the random generator to use. If it is {@code null}, then {@link #DEFAULT}'s random generator will be used.
	 * @return a random item from {@code items}
	 * @throws NullPointerException if {@code items} contains any null elements
	 */
	public static <T extends WeightedItem> T choose(Collection<? extends T> items, T defaultValue, Random rand) {
		return choose(items, countTotalWeight(items), defaultValue, rand);
	}
	
	static int countTotalWeight(Collection<? extends WeightedItem> items) {
		int totalWeight = 0;
		for(WeightedItem item : items) {
			if(item.weight() >= 0)
				totalWeight += item.weight();
		}
		return totalWeight;
	}

	static <T extends WeightedItem> T choose(Collection<? extends T> items, int totalWeight, T defaultValue, Random rand) {
		if(items.isEmpty())
			return defaultValue;
		
		if(rand == null)
			rand = DEFAULT.rand;
		
		int selected = rand.nextInt(totalWeight) + 1;
		for(T item : items) {
			if(item.weight() > 0) {
				selected -= item.weight();
				if(selected <= 0)
					return item;
			}
		}
		
		return defaultValue;
	}
	
}
