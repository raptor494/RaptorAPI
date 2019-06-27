package com.raptor.plugins.util;

import static org.bukkit.block.BlockFace.*;

import java.util.EnumSet;

import org.bukkit.block.BlockFace;

public final class BlockFaceUtils {
	/**
	 * The basic horizontal block faces: {@code NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}. 
	 */
	public static final EnumSet<BlockFace> HORIZONTALS = EnumSet.of(NORTH, NORTH_EAST, EAST, SOUTH_EAST);
	
	/**
	 * The axial direction block faces: {@code NORTH, EAST, SOUTH, WEST, UP, DOWN}.
	 */
	public static final EnumSet<BlockFace> AXIALS = EnumSet.of(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	
	/**
	 * The cardinal direction block faces: {@code NORTH, EAST, SOUTH, WEST}. 
	 */
	public static final EnumSet<BlockFace> CARDINALS = EnumSet.of(NORTH, EAST, SOUTH, WEST);
	
	/**
	 * <i>All</i> horizontal block faces: {@code NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST, EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST, SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST, WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST}.
	 */
	public static final EnumSet<BlockFace> ALL_HORIZONTALS = EnumSet.complementOf(EnumSet.of(UP, DOWN, SELF));
	
	private BlockFaceUtils() {
		throw new UnsupportedOperationException("BlockFaceUtils cannot be instantiated!");
	}
}
