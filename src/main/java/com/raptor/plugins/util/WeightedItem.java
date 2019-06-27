package com.raptor.plugins.util;

public interface WeightedItem {
	/**
	 * Should never be less than 0.
	 * @return this item's weight
	 * @see WeightedRandom
	 */
	int weight();
}
