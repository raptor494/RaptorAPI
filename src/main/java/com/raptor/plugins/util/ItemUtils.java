package com.raptor.plugins.util;

import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.chat.TranslationRegistry;

/**
 * Various utilities for testing properties of Materials
 * and ItemStacks. <br>
 * No method in this class uses or supports legacy Materials.
 */
public final class ItemUtils {
		
	/**
	 * Get whether or not the given tool can harvest the block.
	 * To get whether a tool is <i>effective</i> against a block or not,
	 * use {@link #isEffective(Material, Material)}.
	 * To get whether a tool can break the block at <i>all</i>,
	 * use {@link #canBreak(Material, Material)}.
	 * 
	 * @param tool the tool to use, or {@code null} for hand
	 * @param block the block to break
	 * @return {@code true} if the tool can mine the block with drops
	 */
	public static boolean canBreakAndDrop(Material tool, Material block) {
		switch(block) {
		case STONE:
		case STONE_SLAB:
		case SMOOTH_STONE:
		case COBBLESTONE:
		case MOSSY_COBBLESTONE:
		case COBBLESTONE_SLAB:
		case COBBLESTONE_STAIRS:
		case COBBLESTONE_WALL:
		case MOSSY_COBBLESTONE_WALL:
		case ANDESITE:	case POLISHED_ANDESITE:
		case DIORITE:	case POLISHED_DIORITE:
		case GRANITE:	case POLISHED_GRANITE:
		case SANDSTONE:
		case SMOOTH_SANDSTONE:
		case CHISELED_SANDSTONE:
		case SANDSTONE_SLAB:
		case SANDSTONE_STAIRS:
		case RED_SANDSTONE:
		case SMOOTH_RED_SANDSTONE:
		case CHISELED_RED_SANDSTONE:
		case RED_SANDSTONE_SLAB:
		case RED_SANDSTONE_STAIRS:
		case COAL_BLOCK:
		case REDSTONE_BLOCK:
		case QUARTZ_BLOCK:
		case QUARTZ_PILLAR:
		case CHISELED_QUARTZ_BLOCK:
		case SMOOTH_QUARTZ:
		case QUARTZ_SLAB:
		case QUARTZ_STAIRS:
		case BRICKS:
		case BRICK_SLAB:
		case BRICK_STAIRS:
		case STONE_BRICKS:
		case MOSSY_STONE_BRICKS:
		case CRACKED_STONE_BRICKS:
		case CHISELED_STONE_BRICKS:
		case STONE_BRICK_SLAB:
		case STONE_BRICK_STAIRS:
		case NETHERRACK:
		case NETHER_BRICKS:
		case NETHER_BRICK_SLAB:
		case NETHER_BRICK_STAIRS:
		case NETHER_BRICK_FENCE:
		case NETHER_QUARTZ_ORE:
		case END_STONE:
		case END_STONE_BRICKS:
		case PURPUR_BLOCK:
		case PURPUR_PILLAR:
		case PURPUR_SLAB:
		case PURPUR_STAIRS:
		case PRISMARINE:
		case PRISMARINE_SLAB:
		case PRISMARINE_STAIRS:
		case DARK_PRISMARINE:
		case DARK_PRISMARINE_SLAB:
		case DARK_PRISMARINE_STAIRS:
		case PRISMARINE_BRICKS:
		case PRISMARINE_BRICK_SLAB:
		case PRISMARINE_BRICK_STAIRS:
		case ENCHANTING_TABLE:
		case ANVIL:
		case CHIPPED_ANVIL:
		case DAMAGED_ANVIL:
		case FURNACE:
		case DISPENSER:
		case DROPPER:
		case BREWING_STAND:
		case CAULDRON:
		case IRON_BARS:
		case IRON_DOOR:
		case IRON_TRAPDOOR:
		case HOPPER:
		case STONE_PRESSURE_PLATE:
		case STONE_BUTTON:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case ENDER_CHEST:
			if(tool == WOODEN_PICKAXE)
				return true;
		case IRON_ORE:
		case LAPIS_ORE:
		case IRON_BLOCK:
		case LAPIS_BLOCK:
			if(tool == STONE_PICKAXE)
				return true;
		case REDSTONE_ORE:
		case GOLD_ORE:
		case EMERALD_ORE:
		case DIAMOND_ORE:
		case DIAMOND_BLOCK:
		case GOLD_BLOCK:
		case EMERALD_BLOCK:
			if(tool == IRON_PICKAXE)
				return true;
		case OBSIDIAN:
			return tool == DIAMOND_PICKAXE; 
		case BEDROCK:
		case SPAWNER:
		case ICE:
		case PACKED_ICE:
		case BLUE_ICE:
		case FROSTED_ICE:
		case DRAGON_EGG:
		case CAKE:
		case COMMAND_BLOCK:
		case BARRIER:
		case END_PORTAL:
		case NETHER_PORTAL:
		case END_PORTAL_FRAME:
		case AIR:
		case CAVE_AIR:
		case VOID_AIR:
			return false;
		case LAVA:
		case WATER:
			return tool == BUCKET;
		case COBWEB:
			if(isSword(tool))
				return true;
		case VINE:
		case GRASS:
		case TALL_GRASS:
		case FERN:
		case LARGE_FERN:
			return tool == SHEARS;
		case SNOW:
		case SNOW_BLOCK:
			return isShovel(tool);
		default:
			if(isTerracotta(block) 
			|| isGlazedTerracotta(block)
			|| isConcrete(block))
				return isPickaxe(tool);
			if(isLeaves(block))
				return tool == SHEARS;
			if(isGlass(block))
				return false;
			return true;
		}
	}
	
	/**
	 * Note: silk touch is not considered.
	 * 
	 * @see #canBreakAndDrop(Material, Material)
	 */
	public static boolean canBreakAndDrop(ItemStack tool, Block block) {
		return canBreakAndDrop(tool.getType(), block.getType());
	}
	
	/**
	 * Get whether or not the given tool is <i>effective</i> against the given block.
	 * <br>
	 * Note: this method returns {@code true} if the block is an air block, regardless of tool.
	 * 
	 * @param tool the tool to use, or {@code null} for hand
	 * @param block the block to break
	 * @return {@code true} if the tool is effective at mining the block
	 */
	public static boolean isEffective(Material tool, Material block) {
		switch(block) {
		case STONE:
		case STONE_SLAB:
		case SMOOTH_STONE:
		case COBBLESTONE:
		case MOSSY_COBBLESTONE:
		case COBBLESTONE_SLAB:
		case COBBLESTONE_STAIRS:
		case COBBLESTONE_WALL:
		case MOSSY_COBBLESTONE_WALL:
		case ANDESITE:	case POLISHED_ANDESITE:
		case DIORITE:	case POLISHED_DIORITE:
		case GRANITE:	case POLISHED_GRANITE:
		case SANDSTONE:
		case SMOOTH_SANDSTONE:
		case CHISELED_SANDSTONE:
		case SANDSTONE_SLAB:
		case SANDSTONE_STAIRS:
		case RED_SANDSTONE:
		case SMOOTH_RED_SANDSTONE:
		case CHISELED_RED_SANDSTONE:
		case RED_SANDSTONE_SLAB:
		case RED_SANDSTONE_STAIRS:
		case COAL_BLOCK:
		case REDSTONE_BLOCK:
		case QUARTZ_BLOCK:
		case QUARTZ_PILLAR:
		case CHISELED_QUARTZ_BLOCK:
		case SMOOTH_QUARTZ:
		case QUARTZ_SLAB:
		case QUARTZ_STAIRS:
		case BRICKS:
		case BRICK_SLAB:
		case BRICK_STAIRS:
		case STONE_BRICKS:
		case MOSSY_STONE_BRICKS:
		case CRACKED_STONE_BRICKS:
		case CHISELED_STONE_BRICKS:
		case STONE_BRICK_SLAB:
		case STONE_BRICK_STAIRS:
		case NETHERRACK:
		case NETHER_BRICKS:
		case NETHER_BRICK_SLAB:
		case NETHER_BRICK_STAIRS:
		case NETHER_BRICK_FENCE:
		case NETHER_QUARTZ_ORE:
		case END_STONE:
		case END_STONE_BRICKS:
		case PURPUR_BLOCK:
		case PURPUR_PILLAR:
		case PURPUR_SLAB:
		case PURPUR_STAIRS:
		case PRISMARINE:
		case PRISMARINE_SLAB:
		case PRISMARINE_STAIRS:
		case DARK_PRISMARINE:
		case DARK_PRISMARINE_SLAB:
		case DARK_PRISMARINE_STAIRS:
		case PRISMARINE_BRICKS:
		case PRISMARINE_BRICK_SLAB:
		case PRISMARINE_BRICK_STAIRS:
		case ENCHANTING_TABLE:
		case ANVIL:
		case CHIPPED_ANVIL:
		case DAMAGED_ANVIL:
		case FURNACE:
		case DISPENSER:
		case DROPPER:
		case BREWING_STAND:
		case CAULDRON:
		case IRON_BARS:
		case IRON_DOOR:
		case IRON_TRAPDOOR:
		case HOPPER:
		case STONE_PRESSURE_PLATE:
		case STONE_BUTTON:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case SPAWNER:
		case ICE:
		case PACKED_ICE:
		case BLUE_ICE:
		case FROSTED_ICE:
		case ENDER_CHEST:
		case RAIL:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
		case ACTIVATOR_RAIL:
			if(tool == WOODEN_PICKAXE)
				return true;
		case IRON_ORE:
		case LAPIS_ORE:
		case IRON_BLOCK:
		case LAPIS_BLOCK:
			if(tool == STONE_PICKAXE)
				return true;
		case REDSTONE_ORE:
		case GOLD_ORE:
		case EMERALD_ORE:
		case DIAMOND_ORE:
		case DIAMOND_BLOCK:
		case GOLD_BLOCK:
		case EMERALD_BLOCK:
			if(tool == IRON_PICKAXE)
				return true;
		case OBSIDIAN:
			return tool == DIAMOND_PICKAXE; 
		case BEDROCK:
		case DRAGON_EGG:
		case COMMAND_BLOCK:
		case BARRIER:
		case END_PORTAL:
		case NETHER_PORTAL:
		case END_PORTAL_FRAME:
			return false;
		case LAVA:
		case WATER:
			return tool == BUCKET;
		case COBWEB:
			if(isSword(tool))
				return true;
		case GRASS:
		case TALL_GRASS:
		case FERN:
		case LARGE_FERN:
			return tool == SHEARS;
		case DIRT:
		case GRASS_BLOCK:
		case SNOW:
		case SNOW_BLOCK:
		case CLAY:
		case COARSE_DIRT:
		case FARMLAND:
		case GRAVEL:
		case SAND:
		case RED_SAND:
		case MYCELIUM:
		case PODZOL:
		case SOUL_SAND:
			return isShovel(tool);
		case VINE:
			if(tool == SHEARS)
				return true;
		case COCOA:
		case PUMPKIN:
		case CARVED_PUMPKIN:
		case JACK_O_LANTERN:
		case MELON:
			if(isSword(tool))
				return true;
		case BOOKSHELF:
		case CHEST:
		case TRAPPED_CHEST:
		case CRAFTING_TABLE:
		case DAYLIGHT_DETECTOR:
		case BROWN_MUSHROOM_BLOCK:
		case RED_MUSHROOM_BLOCK:
		case MUSHROOM_STEM:
		case JUKEBOX:
		case LADDER:
		case NOTE_BLOCK:
			return isAxe(tool);
		default:
			if(isTerracotta(block) 
			|| isGlazedTerracotta(block)
			|| isConcrete(block)
			|| isGlass(block))
				return isPickaxe(tool);
			if(isLeaves(block))
				return tool == SHEARS || isSword(tool);
			if(isWool(block))
				return tool == SHEARS;
			if(isSign(block)
			|| isBannerBlock(block)
			|| isLog(block)
			|| isStrippedLog(block)
			|| isBarkBlock(block)
			|| isStrippedWood(block)
			|| isPlanks(block)
			|| isWoodenPressurePlate(block)
			|| isWoodenSlab(block)
			|| isWoodenDoor(block)
			|| isWoodenButton(block)
			|| isWoodenStairs(block)
			|| isWoodenTrapdoor(block)
			|| isWoodenFencePost(block)
			|| isWoodenFenceGate(block))
				return isAxe(tool);
			if(isConcretePowder(block))
				return isShovel(tool);
			return true;
		}
	}
	
	/**
	 * Note: silk touch is not considered.
	 * 
	 * @see #isEffective(Material, Material)
	 */
	public static boolean isEffective(ItemStack tool, Block block) {
		return isEffective(tool.getType(), block.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is an arrow
	 */
	public static boolean isArrow(Material material) {
		switch(material) {
		case ARROW:
		case SPECTRAL_ARROW:
		case TIPPED_ARROW:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isArrow(ItemStack item) {
		return isArrow(item.getType());
	}
	
	public static boolean isArrow(Item item) {
		return isArrow(item.getItemStack());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material has durability (can be broken)
	 */
	public static boolean hasDurability(Material material) {
		switch(material) {
		case WOODEN_PICKAXE:
		case WOODEN_AXE:
		case WOODEN_HOE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case STONE_PICKAXE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_SHOVEL:
		case STONE_SWORD:
		case IRON_PICKAXE:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_SHOVEL:
		case IRON_SWORD:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case GOLDEN_PICKAXE:
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
		case GOLDEN_HELMET:
		case GOLDEN_CHESTPLATE:
		case GOLDEN_LEGGINGS:
		case GOLDEN_BOOTS:
		case DIAMOND_PICKAXE:
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
		case TURTLE_HELMET:
		case ELYTRA:
		case FISHING_ROD:
		case SHEARS:
		case FLINT_AND_STEEL:
		case CARROT_ON_A_STICK:
		case SHIELD:
		case BOW:
		case TRIDENT:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * @see #hasDurability(Material)
	 */
	public static boolean hasDurability(ItemStack item) {
		return hasDurability(item.getType());
	}
	
	/**
	 * Tests whether a material can be repaired with another given material in
	 * an anvil. This method does not return {@code true} if the material in the
	 * right slot is a tool of the same type as the left material. 
	 *  
	 * @param itemToBeRepaired the item in the left anvil slot
	 * @param repairItem the item in the right anvil slot
	 * @return {@code true} if the item in the right anvil slot will result in
	 * 	the item in the left anvil slot getting some durability back (i.e. the 
	 *  item in the right anvil slot is a unit material for the item in the left
	 *  anvil slot)
	 */
	public static boolean isRepairItem(Material itemToBeRepaired, Material repairItem) {
		switch(itemToBeRepaired) {
		case WOODEN_PICKAXE:
		case WOODEN_AXE:
		case WOODEN_HOE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
		case SHIELD:
			return isPlanks(repairItem);
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			return repairItem == LEATHER;
		case STONE_PICKAXE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_SHOVEL:
		case STONE_SWORD:
			return repairItem == COBBLESTONE;
		case IRON_PICKAXE:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_SHOVEL:
		case IRON_SWORD:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
			return repairItem == IRON_INGOT;
		case GOLDEN_PICKAXE:
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
		case GOLDEN_HELMET:
		case GOLDEN_CHESTPLATE:
		case GOLDEN_LEGGINGS:
		case GOLDEN_BOOTS:
			return repairItem == GOLD_INGOT;
		case DIAMOND_PICKAXE:
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
			return repairItem == DIAMOND;
		case TURTLE_HELMET:
			return repairItem == SCUTE;
		case ELYTRA:
			return repairItem == PHANTOM_MEMBRANE;
		default:
			return false;
		}
	}
	
	/**
	 * @see #isRepairItem(Material, Material)
	 */
	public static boolean isRepairItem(ItemStack item1, ItemStack item2) {
		return isRepairItem(item1.getType(), item2.getType());
	}
	
	/**
	 * Get a material that can be combined with the input material in an anvil
	 * to repair the input. 
	 * <br>
	 * <br>
	 * Note: in the case of wooden tools and shields, this method returns {@link Material#OAK_PLANKS OAK_PLANKS},
	 * but any wooden planks can be used.
	 * 
	 * @param material the material that can be repaired
	 * @return the material that could be combined with the input material
	 * 		to repair it or {@code null} if it does not have a unit repair material
	 */
	public static Material getPreferredRepairMaterial(Material material) {
		switch(material) {
		case WOODEN_PICKAXE:
		case WOODEN_AXE:
		case WOODEN_HOE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
		case SHIELD:
			return OAK_PLANKS;
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			return LEATHER;
		case STONE_PICKAXE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_SHOVEL:
		case STONE_SWORD:
			return COBBLESTONE;
		case IRON_PICKAXE:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_SHOVEL:
		case IRON_SWORD:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
			return IRON_INGOT;
		case GOLDEN_PICKAXE:
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
		case GOLDEN_HELMET:
		case GOLDEN_CHESTPLATE:
		case GOLDEN_LEGGINGS:
		case GOLDEN_BOOTS:
			return GOLD_INGOT;
		case DIAMOND_PICKAXE:
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
			return DIAMOND;
		case TURTLE_HELMET:
			return SCUTE;
		case ELYTRA:
			return PHANTOM_MEMBRANE;
		default:
			return null;
		}
	}
	
	/**
	 * @see #getPreferredRepairMaterial(Material)
	 */
	public static Material getPreferredRepairMaterial(ItemStack item) {
		return getPreferredRepairMaterial(item.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a helmet
	 */
	public static boolean isHelmet(Material material) {
		switch(material) {
		case LEATHER_HELMET:
		case IRON_HELMET:
		case CHAINMAIL_HELMET:
		case GOLDEN_HELMET:
		case DIAMOND_HELMET:
		case TURTLE_HELMET:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * @see #isHelmet(Material)
	 */
	public static boolean isHelmet(ItemStack item) {
		return isHelmet(item.getType());
	}

	public static boolean isHelmet(Item item) {
		return isHelmet(item.getItemStack());
	}

	
	/**
	 * Test whether a material can be put into the helmet slot.
	 * 
	 * @param material the material to test
	 * @return {@code true} if the material is a helmet, carved pumpkin, or head block
	 */
	public static boolean isWearableOnHead(Material material) {
		switch(material) {
		case CARVED_PUMPKIN:
		case PLAYER_HEAD:
		case CREEPER_HEAD:
		case DRAGON_HEAD:
		case ZOMBIE_HEAD:
		case SKELETON_SKULL:
		case WITHER_SKELETON_SKULL:
			return true;
		default:
			return isHelmet(material);
		}
	}
	
	/**
	 * @see #isWearableOnHead(Material)
	 */
	public static boolean isWearableOnHead(ItemStack item) {
		return isWearableOnHead(item.getType());
	}

	public static boolean isWearableOnHead(Item item) {
		return isWearableOnHead(item.getItemStack());
	}
	
	public static boolean isWearableOnHead(Block block) {
		return isWearableOnHead(block.getType());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a chestplate
	 */
	public static boolean isChestplate(Material material) {
		switch(material) {
		case LEATHER_CHESTPLATE:
		case IRON_CHESTPLATE:
		case CHAINMAIL_CHESTPLATE:
		case GOLDEN_CHESTPLATE:
		case DIAMOND_CHESTPLATE:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * @see #isChestplate(Material)
	 */
	public static boolean isChestplate(ItemStack item) {
		return isChestplate(item.getType());
	}

	public static boolean isChestplate(Item item) {
		return isChestplate(item.getItemStack());
	}

	
	/**
	 * Test whether a material can be put into the chestplate slot.
	 * @param material the Material to test
	 * @return {@code true} if the material is a chestplate or elytra.
	 */
	public static boolean isWearableOnChest(Material material) {
		switch(material) {
		case ELYTRA:
			return true;
		default:
			return isChestplate(material);
		}
	}
	
	/**
	 * @see #isWearableOnChest(Material)
	 */
	public static boolean isWearableOnChest(ItemStack item) {
		return isWearableOnChest(item.getType());
	}

	public static boolean isWearableOnChest(Item item) {
		return isWearableOnChest(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is leggings
	 */
	public static boolean isLeggings(Material material) {
		switch(material) {
		case LEATHER_LEGGINGS:
		case IRON_LEGGINGS:
		case CHAINMAIL_LEGGINGS:
		case GOLDEN_LEGGINGS:
		case DIAMOND_LEGGINGS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isLeggings(ItemStack item) {
		return isLeggings(item.getType());
	}

	public static boolean isLeggings(Item item) {
		return isLeggings(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is boots
	 */
	public static boolean isBoots(Material material) {
		switch(material) {
		case LEATHER_BOOTS:
		case IRON_BOOTS:
		case CHAINMAIL_BOOTS:
		case GOLDEN_BOOTS:
		case DIAMOND_BOOTS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBoots(ItemStack item) {
		return isBoots(item.getType());
	}

	public static boolean isBoots(Item item) {
		return isBoots(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a pickaxe
	 */
	public static boolean isPickaxe(Material material) {
		switch(material) {
		case WOODEN_PICKAXE:
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLDEN_PICKAXE:
		case DIAMOND_PICKAXE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isPickaxe(ItemStack item) {
		return isPickaxe(item.getType());
	}

	public static boolean isPickaxe(Item item) {
		return isPickaxe(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is an axe
	 */
	public static boolean isAxe(Material material) {
		switch(material) {
		case WOODEN_AXE:
		case STONE_AXE:
		case IRON_AXE:
		case GOLDEN_AXE:
		case DIAMOND_AXE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isAxe(ItemStack item) {
		return isAxe(item.getType());
	}

	public static boolean isAxe(Item item) {
		return isAxe(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a shovel
	 */
	public static boolean isShovel(Material material) {
		switch(material) {
		case WOODEN_SHOVEL:
		case STONE_SHOVEL:
		case IRON_SHOVEL:
		case GOLDEN_SHOVEL:
		case DIAMOND_SHOVEL:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isShovel(ItemStack item) {
		return isShovel(item.getType());
	}

	public static boolean isShovel(Item item) {
		return isShovel(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a sword
	 */
	public static boolean isSword(Material material) {
		switch(material) {
		case WOODEN_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLDEN_SWORD:
		case DIAMOND_SWORD:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSword(ItemStack item) {
		return isSword(item.getType());
	}

	public static boolean isSword(Item item) {
		return isSword(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a hoe
	 */
	public static boolean isHoe(Material material) {
		switch(material) {
		case WOODEN_HOE:
		case STONE_HOE:
		case IRON_HOE:
		case GOLDEN_HOE:
		case DIAMOND_HOE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isHoe(ItemStack item) {
		return isHoe(item.getType());
	}

	public static boolean isHoe(Item item) {
		return isHoe(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is wooden planks
	 */
	public static boolean isPlanks(Material material) {
		switch(material) {
		case OAK_PLANKS:
		case SPRUCE_PLANKS:
		case BIRCH_PLANKS:
		case JUNGLE_PLANKS:
		case DARK_OAK_PLANKS:
		case ACACIA_PLANKS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isPlanks(ItemStack item) {
		return isPlanks(item.getType());
	}

	public static boolean isPlanks(Item item) {
		return isPlanks(item.getItemStack());
	}

	public static boolean isPlanks(Block block) {
		return isPlanks(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the planks of the given wood type
	 */
	public static Material getPlanks(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_PLANKS;
		case REDWOOD:	return SPRUCE_PLANKS;
		case BIRCH:		return BIRCH_PLANKS;
		case JUNGLE:	return JUNGLE_PLANKS;
		case ACACIA:	return ACACIA_PLANKS;
		case DARK_OAK:	return DARK_OAK_PLANKS;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is wooden stairs
	 */
	public static boolean isWoodenStairs(Material material) {
		switch(material) {
		case OAK_STAIRS:
		case SPRUCE_STAIRS:
		case BIRCH_STAIRS:
		case JUNGLE_STAIRS:
		case DARK_OAK_STAIRS:
		case ACACIA_STAIRS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenStairs(ItemStack item) {
		return isWoodenStairs(item.getType());
	}

	public static boolean isWoodenStairs(Item item) {
		return isWoodenStairs(item.getItemStack());
	}

	public static boolean isWoodenStairs(Block block) {
		return isWoodenStairs(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the stairs of the given wood type
	 */
	public static Material getWoodenStairs(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_STAIRS;
		case REDWOOD:	return SPRUCE_STAIRS;
		case BIRCH:		return BIRCH_STAIRS;
		case JUNGLE:	return JUNGLE_STAIRS;
		case ACACIA:	return ACACIA_STAIRS;
		case DARK_OAK:	return DARK_OAK_STAIRS;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * Note: this method also returns {@code true} if the material
	 * is {@linkplain Material#PETRIFIED_OAK_SLAB PETRIFIED_OAK_SLAB}.
	 * 
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden slab
	 */
	public static boolean isWoodenSlab(Material material) {
		switch(material) {
		case OAK_SLAB:
		case SPRUCE_SLAB:
		case BIRCH_SLAB:
		case JUNGLE_SLAB:
		case DARK_OAK_SLAB:
		case ACACIA_SLAB:
		case PETRIFIED_OAK_SLAB:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenSlab(ItemStack item) {
		return isWoodenSlab(item.getType());
	}

	public static boolean isWoodenSlab(Item item) {
		return isWoodenSlab(item.getItemStack());
	}

	public static boolean isWoodenSlab(Block block) {
		return isWoodenSlab(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the slab of the given wood type
	 */
	public static Material getWoodenSlab(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_SLAB;
		case REDWOOD:	return SPRUCE_SLAB;
		case BIRCH:		return BIRCH_SLAB;
		case JUNGLE:	return JUNGLE_SLAB;
		case ACACIA:	return ACACIA_SLAB;
		case DARK_OAK:	return DARK_OAK_SLAB;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a log block, excluding bark blocks
	 */
	public static boolean isLog(Material material) {
		switch(material) {
		case OAK_LOG:
		case SPRUCE_LOG:
		case BIRCH_LOG:
		case JUNGLE_LOG:
		case DARK_OAK_LOG:
		case ACACIA_LOG:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isLog(ItemStack item) {
		return isLog(item.getType());
	}

	public static boolean isLog(Item item) {
		return isLog(item.getItemStack());
	}

	public static boolean isLog(Block block) {
		return isLog(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the log of the given wood type
	 */
	public static Material getLog(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_LOG;
		case REDWOOD:	return SPRUCE_LOG;
		case BIRCH:		return BIRCH_LOG;
		case JUNGLE:	return JUNGLE_LOG;
		case ACACIA:	return ACACIA_LOG;
		case DARK_OAK:	return DARK_OAK_LOG;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a bark block
	 */
	public static boolean isBarkBlock(Material material) {
		switch(material) {
		case OAK_WOOD:
		case SPRUCE_WOOD:
		case BIRCH_WOOD:
		case JUNGLE_WOOD:
		case DARK_OAK_WOOD:
		case ACACIA_WOOD:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBarkBlock(ItemStack item) {
		return isBarkBlock(item.getType());
	}

	public static boolean isBarkBlock(Item item) {
		return isBarkBlock(item.getItemStack());
	}

	public static boolean isBarkBlock(Block block) {
		return isBarkBlock(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the bark block of the given wood type
	 */
	public static Material getBarkBlock(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_WOOD;
		case REDWOOD:	return SPRUCE_WOOD;
		case BIRCH:		return BIRCH_WOOD;
		case JUNGLE:	return JUNGLE_WOOD;
		case ACACIA:	return ACACIA_WOOD;
		case DARK_OAK:	return DARK_OAK_WOOD;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a stripped log block, excluding bark blocks
	 */
	public static boolean isStrippedLog(Material material) {
		switch(material) {
		case STRIPPED_OAK_LOG:
		case STRIPPED_SPRUCE_LOG:
		case STRIPPED_BIRCH_LOG:
		case STRIPPED_JUNGLE_LOG:
		case STRIPPED_DARK_OAK_LOG:
		case STRIPPED_ACACIA_LOG:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isStrippedLog(ItemStack item) {
		return isStrippedLog(item.getType());
	}

	public static boolean isStrippedLog(Item item) {
		return isStrippedLog(item.getItemStack());
	}

	public static boolean isStrippedLog(Block block) {
		return isStrippedLog(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the stripped log of the given wood type
	 */
	public static Material getStrippedLog(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return STRIPPED_OAK_LOG;
		case REDWOOD:	return STRIPPED_SPRUCE_LOG;
		case BIRCH:		return STRIPPED_BIRCH_LOG;
		case JUNGLE:	return STRIPPED_JUNGLE_LOG;
		case ACACIA:	return STRIPPED_ACACIA_LOG;
		case DARK_OAK:	return STRIPPED_DARK_OAK_LOG;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a stripped bark block block, excluding bark blocks
	 */
	public static boolean isStrippedWood(Material material) {
		switch(material) {
		case OAK_LOG:
		case SPRUCE_LOG:
		case BIRCH_LOG:
		case JUNGLE_LOG:
		case DARK_OAK_LOG:
		case ACACIA_LOG:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isStrippedWood(ItemStack item) {
		return isStrippedWood(item.getType());
	}

	public static boolean isStrippedWood(Item item) {
		return isStrippedWood(item.getItemStack());
	}

	public static boolean isStrippedWood(Block block) {
		return isStrippedWood(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the stripped bark block of the given wood type
	 */
	public static Material getStrippedWood(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return STRIPPED_OAK_WOOD;
		case REDWOOD:	return STRIPPED_SPRUCE_WOOD;
		case BIRCH:		return STRIPPED_BIRCH_WOOD;
		case JUNGLE:	return STRIPPED_JUNGLE_WOOD;
		case ACACIA:	return STRIPPED_ACACIA_WOOD;
		case DARK_OAK:	return STRIPPED_DARK_OAK_WOOD;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a sapling
	 */
	public static boolean isSapling(Material material) {
		switch(material) {
		case OAK_SAPLING:
		case SPRUCE_SAPLING:
		case BIRCH_SAPLING:
		case JUNGLE_SAPLING:
		case DARK_OAK_SAPLING:
		case ACACIA_SAPLING:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSapling(ItemStack item) {
		return isSapling(item.getType());
	}

	public static boolean isSapling(Item item) {
		return isSapling(item.getItemStack());
	}

	public static boolean isSapling(Block block) {
		return isSapling(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the sapling of the given wood type
	 */
	public static Material getSapling(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_SAPLING;
		case REDWOOD:	return SPRUCE_SAPLING;
		case BIRCH:		return BIRCH_SAPLING;
		case JUNGLE:	return JUNGLE_SAPLING;
		case ACACIA:	return ACACIA_SAPLING;
		case DARK_OAK:	return DARK_OAK_SAPLING;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is leaves
	 */
	public static boolean isLeaves(Material material) {
		switch(material) {
		case OAK_LEAVES:
		case SPRUCE_LEAVES:
		case BIRCH_LEAVES:
		case JUNGLE_LEAVES:
		case DARK_OAK_LEAVES:
		case ACACIA_LEAVES:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isLeaves(ItemStack item) {
		return isLeaves(item.getType());
	}

	public static boolean isLeaves(Item item) {
		return isLeaves(item.getItemStack());
	}

	public static boolean isLeaves(Block block) {
		return isLeaves(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the leaves of the given wood type
	 */
	public static Material getLeaves(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_LEAVES;
		case REDWOOD:	return SPRUCE_LEAVES;
		case BIRCH:		return BIRCH_LEAVES;
		case JUNGLE:	return JUNGLE_LEAVES;
		case ACACIA:	return ACACIA_LEAVES;
		case DARK_OAK:	return DARK_OAK_LEAVES;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden fence post
	 */
	public static boolean isWoodenFencePost(Material material) {
		switch(material) {
		case OAK_FENCE:
		case SPRUCE_FENCE:
		case BIRCH_FENCE:
		case JUNGLE_FENCE:
		case DARK_OAK_FENCE:
		case ACACIA_FENCE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenFencePost(ItemStack item) {
		return isWoodenFencePost(item.getType());
	}

	public static boolean isWoodenFencePost(Item item) {
		return isWoodenFencePost(item.getItemStack());
	}

	public static boolean isWoodenFencePost(Block block) {
		return isWoodenFencePost(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the fence post of the given wood type
	 */
	public static Material getWoodenFencePost(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_FENCE;
		case REDWOOD:	return SPRUCE_FENCE;
		case BIRCH:		return BIRCH_FENCE;
		case JUNGLE:	return JUNGLE_FENCE;
		case ACACIA:	return ACACIA_FENCE;
		case DARK_OAK:	return DARK_OAK_FENCE;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a fence post
	 */
	public static boolean isFencePost(Material material) {
		switch(material) {
		case OAK_FENCE:
		case SPRUCE_FENCE:
		case BIRCH_FENCE:
		case JUNGLE_FENCE:
		case DARK_OAK_FENCE:
		case ACACIA_FENCE:
		case NETHER_BRICK_FENCE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isFencePost(ItemStack item) {
		return isFencePost(item.getType());
	}

	public static boolean isFencePost(Item item) {
		return isFencePost(item.getItemStack());
	}

	public static boolean isFencePost(Block block) {
		return isFencePost(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden fence gate
	 */
	public static boolean isWoodenFenceGate(Material material) {
		switch(material) {
		case OAK_FENCE_GATE:
		case SPRUCE_FENCE_GATE:
		case BIRCH_FENCE_GATE:
		case JUNGLE_FENCE_GATE:
		case DARK_OAK_FENCE_GATE:
		case ACACIA_FENCE_GATE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenFenceGate(ItemStack item) {
		return isWoodenFenceGate(item.getType());
	}

	public static boolean isWoodenFenceGate(Item item) {
		return isWoodenFenceGate(item.getItemStack());
	}

	public static boolean isWoodenFenceGate(Block block) {
		return isWoodenFenceGate(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the fence gate of the given wood type
	 */
	public static Material getWoodenFenceGate(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_FENCE_GATE;
		case REDWOOD:	return SPRUCE_FENCE_GATE;
		case BIRCH:		return BIRCH_FENCE_GATE;
		case JUNGLE:	return JUNGLE_FENCE_GATE;
		case ACACIA:	return ACACIA_FENCE_GATE;
		case DARK_OAK:	return DARK_OAK_FENCE_GATE;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a fence gate
	 */
	public static boolean isFenceGate(Material material) {
		switch(material) {
		case OAK_FENCE_GATE:
		case SPRUCE_FENCE_GATE:
		case BIRCH_FENCE_GATE:
		case JUNGLE_FENCE_GATE:
		case DARK_OAK_FENCE_GATE:
		case ACACIA_FENCE_GATE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isFenceGate(ItemStack item) {
		return isFenceGate(item.getType());
	}

	public static boolean isFenceGate(Item item) {
		return isFenceGate(item.getItemStack());
	}

	public static boolean isFenceGate(Block block) {
		return isFenceGate(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden door
	 */
	public static boolean isWoodenDoor(Material material) {
		switch(material) {
		case OAK_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case DARK_OAK_DOOR:
		case ACACIA_DOOR:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenDoor(ItemStack item) {
		return isWoodenDoor(item.getType());
	}

	public static boolean isWoodenDoor(Item item) {
		return isWoodenDoor(item.getItemStack());
	}

	public static boolean isWoodenDoor(Block block) {
		return isWoodenDoor(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the door of the given wood type
	 */
	public static Material getWoodenDoor(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_DOOR;
		case REDWOOD:	return SPRUCE_DOOR;
		case BIRCH:		return BIRCH_DOOR;
		case JUNGLE:	return JUNGLE_DOOR;
		case ACACIA:	return ACACIA_DOOR;
		case DARK_OAK:	return DARK_OAK_DOOR;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a door
	 */
	public static boolean isDoor(Material material) {
		switch(material) {
		case OAK_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case DARK_OAK_DOOR:
		case ACACIA_DOOR:
		case IRON_DOOR:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isDoor(ItemStack item) {
		return isDoor(item.getType());
	}

	public static boolean isDoor(Item item) {
		return isDoor(item.getItemStack());
	}

	public static boolean isDoor(Block block) {
		return isDoor(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden trapdoor
	 */
	public static boolean isWoodenTrapdoor(Material material) {
		switch(material) {
		case OAK_TRAPDOOR:
		case SPRUCE_TRAPDOOR:
		case BIRCH_TRAPDOOR:
		case JUNGLE_TRAPDOOR:
		case DARK_OAK_TRAPDOOR:
		case ACACIA_TRAPDOOR:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenTrapdoor(ItemStack item) {
		return isWoodenTrapdoor(item.getType());
	}

	public static boolean isWoodenTrapdoor(Item item) {
		return isWoodenTrapdoor(item.getItemStack());
	}

	public static boolean isWoodenTrapdoor(Block block) {
		return isWoodenTrapdoor(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the trapdoor of the given wood type
	 */
	public static Material getWoodenTrapdoor(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_TRAPDOOR;
		case REDWOOD:	return SPRUCE_TRAPDOOR;
		case BIRCH:		return BIRCH_TRAPDOOR;
		case JUNGLE:	return JUNGLE_TRAPDOOR;
		case ACACIA:	return ACACIA_TRAPDOOR;
		case DARK_OAK:	return DARK_OAK_TRAPDOOR;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a trapdoor
	 */
	public static boolean isTrapdoor(Material material) {
		switch(material) {
		case OAK_TRAPDOOR:
		case SPRUCE_TRAPDOOR:
		case BIRCH_TRAPDOOR:
		case JUNGLE_TRAPDOOR:
		case DARK_OAK_TRAPDOOR:
		case ACACIA_TRAPDOOR:
		case IRON_TRAPDOOR:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isTrapdoor(ItemStack item) {
		return isTrapdoor(item.getType());
	}

	public static boolean isTrapdoor(Item item) {
		return isTrapdoor(item.getItemStack());
	}

	public static boolean isTrapdoor(Block block) {
		return isTrapdoor(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden button
	 */
	public static boolean isWoodenButton(Material material) {
		switch(material) {
		case OAK_BUTTON:
		case SPRUCE_BUTTON:
		case BIRCH_BUTTON:
		case JUNGLE_BUTTON:
		case DARK_OAK_BUTTON:
		case ACACIA_BUTTON:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenButton(ItemStack item) {
		return isWoodenButton(item.getType());
	}

	public static boolean isWoodenButton(Item item) {
		return isWoodenButton(item.getItemStack());
	}

	public static boolean isWoodenButton(Block block) {
		return isWoodenButton(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the button of the given wood type
	 */
	public static Material getWoodenButton(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_BUTTON;
		case REDWOOD:	return SPRUCE_BUTTON;
		case BIRCH:		return BIRCH_BUTTON;
		case JUNGLE:	return JUNGLE_BUTTON;
		case ACACIA:	return ACACIA_BUTTON;
		case DARK_OAK:	return DARK_OAK_BUTTON;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a button
	 */
	public static boolean isButton(Material material) {
		switch(material) {
		case OAK_BUTTON:
		case SPRUCE_BUTTON:
		case BIRCH_BUTTON:
		case JUNGLE_BUTTON:
		case DARK_OAK_BUTTON:
		case ACACIA_BUTTON:
		case STONE_BUTTON:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isButton(ItemStack item) {
		return isButton(item.getType());
	}

	public static boolean isButton(Item item) {
		return isButton(item.getItemStack());
	}

	public static boolean isButton(Block block) {
		return isButton(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wooden pressure plate
	 */
	public static boolean isWoodenPressurePlate(Material material) {
		switch(material) {
		case OAK_PRESSURE_PLATE:
		case SPRUCE_PRESSURE_PLATE:
		case BIRCH_PRESSURE_PLATE:
		case JUNGLE_PRESSURE_PLATE:
		case DARK_OAK_PRESSURE_PLATE:
		case ACACIA_PRESSURE_PLATE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWoodenPressurePlate(ItemStack item) {
		return isWoodenPressurePlate(item.getType());
	}

	public static boolean isWoodenPressurePlate(Item item) {
		return isWoodenPressurePlate(item.getItemStack());
	}

	public static boolean isWoodenPressurePlate(Block block) {
		return isWoodenPressurePlate(block.getType());
	}


	
	/**
	 * @param type the type of wood
	 * @return the Material corresponding to the pressure plate of the given wood type
	 */
	public static Material getWoodenPressurePlate(TreeSpecies type) {
		switch(type) {
		case GENERIC:	return OAK_PRESSURE_PLATE;
		case REDWOOD:	return SPRUCE_PRESSURE_PLATE;
		case BIRCH:		return BIRCH_PRESSURE_PLATE;
		case JUNGLE:	return JUNGLE_PRESSURE_PLATE;
		case ACACIA:	return ACACIA_PRESSURE_PLATE;
		case DARK_OAK:	return DARK_OAK_PRESSURE_PLATE;
		default:
			throw new AssertionError("TreeSpecies definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a pressure plate
	 */
	public static boolean isPressurePlate(Material material) {
		switch(material) {
		case OAK_PRESSURE_PLATE:
		case SPRUCE_PRESSURE_PLATE:
		case BIRCH_PRESSURE_PLATE:
		case JUNGLE_PRESSURE_PLATE:
		case DARK_OAK_PRESSURE_PLATE:
		case ACACIA_PRESSURE_PLATE:
		case STONE_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isPressurePlate(ItemStack item) {
		return isPressurePlate(item.getType());
	}

	public static boolean isPressurePlate(Item item) {
		return isPressurePlate(item.getItemStack());
	}

	public static boolean isPressurePlate(Block block) {
		return isPressurePlate(block.getType());
	}


	
	/**
	 * @param material the wooden Material
	 * @return the corresponding {@linkplain TreeSpecies} or {@code null} if
	 * 	the material is not a wooden material with a corresponding wood type
	 */
	public static TreeSpecies getWoodType(Material material) {
		switch(material) {
		case OAK_PLANKS:
		case OAK_STAIRS:
		case OAK_SLAB:
		case OAK_LOG:
		case OAK_WOOD:
		case STRIPPED_OAK_LOG:
		case STRIPPED_OAK_WOOD:
		case OAK_SAPLING:
		case OAK_LEAVES:
		case OAK_FENCE:
		case OAK_FENCE_GATE:
		case OAK_DOOR:
		case OAK_TRAPDOOR:
		case OAK_BUTTON:
		case OAK_PRESSURE_PLATE:
			return TreeSpecies.GENERIC;
		case SPRUCE_PLANKS:
		case SPRUCE_STAIRS:
		case SPRUCE_SLAB:
		case SPRUCE_LOG:
		case SPRUCE_WOOD:
		case STRIPPED_SPRUCE_LOG:
		case STRIPPED_SPRUCE_WOOD:
		case SPRUCE_SAPLING:
		case SPRUCE_LEAVES:
		case SPRUCE_FENCE:
		case SPRUCE_FENCE_GATE:
		case SPRUCE_DOOR:
		case SPRUCE_TRAPDOOR:
		case SPRUCE_BUTTON:
		case SPRUCE_PRESSURE_PLATE:
			return TreeSpecies.REDWOOD;
		case BIRCH_PLANKS:
		case BIRCH_STAIRS:
		case BIRCH_SLAB:
		case BIRCH_LOG:
		case BIRCH_WOOD:
		case STRIPPED_BIRCH_LOG:
		case STRIPPED_BIRCH_WOOD:
		case BIRCH_SAPLING:
		case BIRCH_LEAVES:
		case BIRCH_FENCE:
		case BIRCH_FENCE_GATE:
		case BIRCH_DOOR:
		case BIRCH_TRAPDOOR:
		case BIRCH_BUTTON:
		case BIRCH_PRESSURE_PLATE:
			return TreeSpecies.BIRCH;
		case JUNGLE_PLANKS:
		case JUNGLE_STAIRS:
		case JUNGLE_SLAB:
		case JUNGLE_LOG:
		case JUNGLE_WOOD:
		case STRIPPED_JUNGLE_LOG:
		case STRIPPED_JUNGLE_WOOD:
		case JUNGLE_SAPLING:
		case JUNGLE_LEAVES:
		case JUNGLE_FENCE:
		case JUNGLE_FENCE_GATE:
		case JUNGLE_DOOR:
		case JUNGLE_TRAPDOOR:
		case JUNGLE_BUTTON:
		case JUNGLE_PRESSURE_PLATE:
			return TreeSpecies.JUNGLE;
		case ACACIA_PLANKS:
		case ACACIA_STAIRS:
		case ACACIA_SLAB:
		case ACACIA_LOG:
		case ACACIA_WOOD:
		case STRIPPED_ACACIA_LOG:
		case STRIPPED_ACACIA_WOOD:
		case ACACIA_SAPLING:
		case ACACIA_LEAVES:
		case ACACIA_FENCE:
		case ACACIA_FENCE_GATE:
		case ACACIA_DOOR:
		case ACACIA_TRAPDOOR:
		case ACACIA_BUTTON:
		case ACACIA_PRESSURE_PLATE:	
			return TreeSpecies.ACACIA;
		case DARK_OAK_PLANKS:
		case DARK_OAK_STAIRS:
		case DARK_OAK_SLAB:
		case DARK_OAK_LOG:
		case DARK_OAK_WOOD:
		case STRIPPED_DARK_OAK_LOG:
		case STRIPPED_DARK_OAK_WOOD:
		case DARK_OAK_SAPLING:
		case DARK_OAK_LEAVES:
		case DARK_OAK_FENCE:
		case DARK_OAK_FENCE_GATE:
		case DARK_OAK_DOOR:
		case DARK_OAK_TRAPDOOR:
		case DARK_OAK_BUTTON:
		case DARK_OAK_PRESSURE_PLATE:
			return TreeSpecies.DARK_OAK;
		default:
			return null;
		}
	}
	
	public static TreeSpecies getWoodType(ItemStack item) {
		return getWoodType(item.getType());
	}
	
	public static TreeSpecies getWoodType(Block block) {
		return getWoodType(block.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is made of wood
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWooden(Material material) {
		switch(material) {
		case OAK_PLANKS:
		case OAK_STAIRS:
		case OAK_SLAB:
		case OAK_LOG:
		case OAK_WOOD:
		case STRIPPED_OAK_LOG:
		case STRIPPED_OAK_WOOD:
		case OAK_SAPLING:
		case OAK_LEAVES:
		case OAK_FENCE:
		case OAK_FENCE_GATE:
		case OAK_DOOR:
		case OAK_TRAPDOOR:
		case OAK_BUTTON:
		case OAK_PRESSURE_PLATE:
		case OAK_SIGN:
		case OAK_WALL_SIGN:
		case SPRUCE_PLANKS:
		case SPRUCE_STAIRS:
		case SPRUCE_SLAB:
		case SPRUCE_LOG:
		case SPRUCE_WOOD:
		case STRIPPED_SPRUCE_LOG:
		case STRIPPED_SPRUCE_WOOD:
		case SPRUCE_SAPLING:
		case SPRUCE_LEAVES:
		case SPRUCE_FENCE:
		case SPRUCE_FENCE_GATE:
		case SPRUCE_DOOR:
		case SPRUCE_TRAPDOOR:
		case SPRUCE_BUTTON:
		case SPRUCE_PRESSURE_PLATE:
		case SPRUCE_SIGN:
		case SPRUCE_WALL_SIGN:
		case BIRCH_PLANKS:
		case BIRCH_STAIRS:
		case BIRCH_SLAB:
		case BIRCH_LOG:
		case BIRCH_WOOD:
		case STRIPPED_BIRCH_LOG:
		case STRIPPED_BIRCH_WOOD:
		case BIRCH_SAPLING:
		case BIRCH_LEAVES:
		case BIRCH_FENCE:
		case BIRCH_FENCE_GATE:
		case BIRCH_DOOR:
		case BIRCH_TRAPDOOR:
		case BIRCH_BUTTON:
		case BIRCH_PRESSURE_PLATE:
		case BIRCH_SIGN:
		case BIRCH_WALL_SIGN:
		case JUNGLE_PLANKS:
		case JUNGLE_STAIRS:
		case JUNGLE_SLAB:
		case JUNGLE_LOG:
		case JUNGLE_WOOD:
		case STRIPPED_JUNGLE_LOG:
		case STRIPPED_JUNGLE_WOOD:
		case JUNGLE_SAPLING:
		case JUNGLE_LEAVES:
		case JUNGLE_FENCE:
		case JUNGLE_FENCE_GATE:
		case JUNGLE_DOOR:
		case JUNGLE_TRAPDOOR:
		case JUNGLE_BUTTON:
		case JUNGLE_PRESSURE_PLATE:
		case JUNGLE_SIGN:
		case JUNGLE_WALL_SIGN:
		case ACACIA_PLANKS:
		case ACACIA_STAIRS:
		case ACACIA_SLAB:
		case ACACIA_LOG:
		case ACACIA_WOOD:
		case STRIPPED_ACACIA_LOG:
		case STRIPPED_ACACIA_WOOD:
		case ACACIA_SAPLING:
		case ACACIA_LEAVES:
		case ACACIA_FENCE:
		case ACACIA_FENCE_GATE:
		case ACACIA_DOOR:
		case ACACIA_TRAPDOOR:
		case ACACIA_BUTTON:
		case ACACIA_PRESSURE_PLATE:
		case ACACIA_SIGN:
		case ACACIA_WALL_SIGN:
		case DARK_OAK_PLANKS:
		case DARK_OAK_STAIRS:
		case DARK_OAK_SLAB:
		case DARK_OAK_LOG:
		case DARK_OAK_WOOD:
		case STRIPPED_DARK_OAK_LOG:
		case STRIPPED_DARK_OAK_WOOD:
		case DARK_OAK_SAPLING:
		case DARK_OAK_LEAVES:
		case DARK_OAK_FENCE:
		case DARK_OAK_FENCE_GATE:
		case DARK_OAK_DOOR:
		case DARK_OAK_TRAPDOOR:
		case DARK_OAK_BUTTON:
		case DARK_OAK_PRESSURE_PLATE:
		case DARK_OAK_SIGN:
		case DARK_OAK_WALL_SIGN:
		case CHEST:
		case TRAPPED_CHEST:
		case CRAFTING_TABLE:
			return true;
		case LEGACY_SIGN:
		case LEGACY_WALL_SIGN:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWooden(ItemStack item) {
		return isWooden(item.getType());
	}

	public static boolean isWooden(Item item) {
		return isWooden(item.getItemStack());
	}

	public static boolean isWooden(Block block) {
		return isWooden(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a sign
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSign(Material material) {
		switch(material) {
		case OAK_SIGN:
		case OAK_WALL_SIGN:
		case SPRUCE_SIGN:
		case SPRUCE_WALL_SIGN:
		case BIRCH_SIGN:
		case BIRCH_WALL_SIGN:
		case JUNGLE_SIGN:
		case JUNGLE_WALL_SIGN:
		case ACACIA_SIGN:
		case ACACIA_WALL_SIGN:
		case DARK_OAK_SIGN:
		case DARK_OAK_WALL_SIGN:
			return true;
		case LEGACY_SIGN:
		case LEGACY_WALL_SIGN:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSign(ItemStack item) {
		return isSign(item.getType());
	}

	public static boolean isSign(Item item) {
		return isSign(item.getItemStack());
	}

	public static boolean isSign(Block block) {
		return isSign(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a wall sign
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWallSign(Material material) {
		switch(material) {
		case OAK_WALL_SIGN:
		case SPRUCE_WALL_SIGN:
		case BIRCH_WALL_SIGN:
		case JUNGLE_WALL_SIGN:
		case ACACIA_WALL_SIGN:
		case DARK_OAK_WALL_SIGN:
			return true;
		case LEGACY_WALL_SIGN:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWallSign(Block block) {
		return isWallSign(block.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a standing sign
	 */
	@SuppressWarnings("deprecation")
	public static boolean isStandingSign(Material material) {
		switch(material) {
		case OAK_SIGN:
		case SPRUCE_SIGN:
		case BIRCH_SIGN:
		case JUNGLE_SIGN:
		case ACACIA_SIGN:
		case DARK_OAK_SIGN:
			return true;
		case LEGACY_SIGN:
			return true;
		default:
			return false;
		}
	}

	public static boolean isStandingSign(Block block) {
		return isStandingSign(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a 2-block-tall plant
	 */
	public static boolean isDoublePlant(Material material) {
		switch(material) {
		case ROSE_BUSH:
		case PEONY:
		case LILAC:
		case TALL_GRASS:
		case LARGE_FERN:
		case SUNFLOWER:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isDoublePlant(ItemStack item) {
		return isDoublePlant(item.getType());
	}

	public static boolean isDoublePlant(Item item) {
		return isDoublePlant(item.getItemStack());
	}

	public static boolean isDoublePlant(Block block) {
		return isDoublePlant(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a flower (single- or double-block tall)
	 */
	public static boolean isFlower(Material material) {
		switch(material) {
		case ROSE_BUSH:
		case PEONY:
		case LILAC:
		case SUNFLOWER:
		case DANDELION:
		case POPPY:
		case ORANGE_TULIP:
		case PINK_TULIP:
		case RED_TULIP:
		case WHITE_TULIP:
		case BLUE_ORCHID:
		case ALLIUM:
		case AZURE_BLUET:
		case OXEYE_DAISY:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isFlower(ItemStack item) {
		return isFlower(item.getType());
	}

	public static boolean isFlower(Item item) {
		return isFlower(item.getItemStack());
	}

	public static boolean isFlower(Block block) {
		return isFlower(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a single-block-tall flower
	 */
	public static boolean isSmallFlower(Material material) {
		switch(material) {
		case DANDELION:
		case POPPY:
		case ORANGE_TULIP:
		case PINK_TULIP:
		case RED_TULIP:
		case WHITE_TULIP:
		case BLUE_ORCHID:
		case ALLIUM:
		case AZURE_BLUET:
		case OXEYE_DAISY:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSmallFlower(ItemStack item) {
		return isSmallFlower(item.getType());
	}

	public static boolean isSmallFlower(Item item) {
		return isSmallFlower(item.getItemStack());
	}

	public static boolean isSmallFlower(Block block) {
		return isSmallFlower(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is a flower pot
	 */
	public static boolean isFlowerPot(Material material) {
		switch(material) {
		case FLOWER_POT:
		case POTTED_ACACIA_SAPLING:
		case POTTED_ALLIUM:
		case POTTED_AZURE_BLUET:
		case POTTED_BIRCH_SAPLING:
		case POTTED_BLUE_ORCHID:
		case POTTED_BROWN_MUSHROOM:
		case POTTED_CACTUS:
		case POTTED_DANDELION:
		case POTTED_DARK_OAK_SAPLING:
		case POTTED_DEAD_BUSH:
		case POTTED_FERN:
		case POTTED_JUNGLE_SAPLING:
		case POTTED_OAK_SAPLING:
		case POTTED_ORANGE_TULIP:
		case POTTED_OXEYE_DAISY:
		case POTTED_PINK_TULIP:
		case POTTED_POPPY:
		case POTTED_RED_MUSHROOM:
		case POTTED_RED_TULIP:
		case POTTED_SPRUCE_SAPLING:
		case POTTED_WHITE_TULIP:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isFlowerPot(ItemStack item) {
		return isFlowerPot(item.getType());
	}

	public static boolean isFlowerPot(Item item) {
		return isFlowerPot(item.getItemStack());
	}

	public static boolean isFlowerPot(Block block) {
		return isFlowerPot(block.getType());
	}


	
	public static boolean isPottable(Material material) {
		switch(material) {
		case ACACIA_SAPLING:
		case ALLIUM:
		case AZURE_BLUET:
		case BIRCH_SAPLING:
		case BLUE_ORCHID:
		case BROWN_MUSHROOM:
		case CACTUS:
		case DANDELION:
		case DARK_OAK_SAPLING:
		case DEAD_BUSH:
		case FERN:
		case JUNGLE_SAPLING:
		case OAK_SAPLING:
		case ORANGE_TULIP:
		case OXEYE_DAISY:
		case PINK_TULIP:
		case POPPY:
		case RED_MUSHROOM:
		case RED_TULIP:
		case SPRUCE_SAPLING:
		case WHITE_TULIP:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isPottable(ItemStack item) {
		return isPottable(item.getType());
	}

	public static boolean isPottable(Item item) {
		return isPottable(item.getItemStack());
	}

	public static boolean isPottable(Block block) {
		return isPottable(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is foliage (i.e. leaves, grass, tall grass, flowers, ferns, etc.)
	 *  (NOT dirt, mycelium, mushrooms)
	 */
	public static boolean isFoliage(Material material) {
		switch(material) {
		case ROSE_BUSH:
		case PEONY:
		case LILAC:
		case SUNFLOWER:
		case TALL_GRASS:
		case LARGE_FERN:
		case DANDELION:
		case POPPY:
		case ORANGE_TULIP:
		case PINK_TULIP:
		case RED_TULIP:
		case WHITE_TULIP:
		case BLUE_ORCHID:
		case ALLIUM:
		case AZURE_BLUET:
		case OXEYE_DAISY:
		case GRASS:
		case FERN:
		case CACTUS:
		case DEAD_BUSH:
		case GRASS_BLOCK:
		case PODZOL:
			return true;
		default:
			return isLeaves(material) 
					|| isLog(material) 
					|| isBarkBlock(material)
					|| isSapling(material);
		}
	}
	
	public static boolean isFoliage(ItemStack item) {
		return isFoliage(item.getType());
	}

	public static boolean isFoliage(Item item) {
		return isFoliage(item.getItemStack());
	}

	public static boolean isFoliage(Block block) {
		return isFoliage(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is mushrooms or mycelium
	 */
	public static boolean isFungal(Material material) {
		switch(material) {
		case RED_MUSHROOM:
		case BROWN_MUSHROOM:
		case RED_MUSHROOM_BLOCK:
		case BROWN_MUSHROOM_BLOCK:
		case MUSHROOM_STEM:
		case MYCELIUM:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isFungal(ItemStack item) {
		return isFungal(item.getType());
	}

	public static boolean isFungal(Item item) {
		return isFungal(item.getItemStack());
	}

	public static boolean isFungal(Block block) {
		return isFungal(block.getType());
	}


	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is foliage or fungal
	 */
	public static boolean isFlora(Material material) {
		return isFoliage(material) || isFungal(material);
	}
	
	public static boolean isFlora(ItemStack item) {
		return isFlora(item.getType());
	}

	public static boolean isFlora(Item item) {
		return isFlora(item.getItemStack());
	}

	public static boolean isFlora(Block block) {
		return isFlora(block.getType());
	}


	
	/**
	 * @param material the grass material
	 * @return the {@linkplain GrassSpecies} of the material,
	 * 	or {@code null} if it is not grass
	 */
	public static GrassSpecies getGrassType(Material material) {
		switch(material) {
		case DEAD_BUSH:
			return GrassSpecies.DEAD;
		case GRASS:
		case TALL_GRASS:
			return GrassSpecies.NORMAL;
		case FERN:
		case LARGE_FERN:
			return GrassSpecies.FERN_LIKE;
		default:
			return null;
		}
	}
	
	public static GrassSpecies getGrassType(ItemStack item) {
		return getGrassType(item.getType());
	}
	
	public static GrassSpecies getGrassType(Item item) {
		return getGrassType(item.getItemStack());
	}
	
	public static GrassSpecies getGrassType(Block block) {
		return getGrassType(block.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a spawn egg
	 */
	public static boolean isSpawnEgg(Material material) {
		switch(material) {
		case BAT_SPAWN_EGG:
		case BLAZE_SPAWN_EGG:
		case CAVE_SPIDER_SPAWN_EGG:
		case CHICKEN_SPAWN_EGG:
		case COD_SPAWN_EGG:
		case COW_SPAWN_EGG:
		case CREEPER_SPAWN_EGG:
		case DOLPHIN_SPAWN_EGG:
		case DONKEY_SPAWN_EGG:
		case DROWNED_SPAWN_EGG:
		case ELDER_GUARDIAN_SPAWN_EGG:
		case ENDERMAN_SPAWN_EGG:
		case ENDERMITE_SPAWN_EGG:
		case EVOKER_SPAWN_EGG:
		case GHAST_SPAWN_EGG:
		case GUARDIAN_SPAWN_EGG:
		case HORSE_SPAWN_EGG:
		case HUSK_SPAWN_EGG:
		case LLAMA_SPAWN_EGG:
		case MAGMA_CUBE_SPAWN_EGG:
		case MOOSHROOM_SPAWN_EGG:
		case MULE_SPAWN_EGG:
		case OCELOT_SPAWN_EGG:
		case PARROT_SPAWN_EGG:
		case PHANTOM_SPAWN_EGG:
		case PIG_SPAWN_EGG:
		case POLAR_BEAR_SPAWN_EGG:
		case PUFFERFISH_SPAWN_EGG:
		case RABBIT_SPAWN_EGG:
		case SALMON_SPAWN_EGG:
		case SHEEP_SPAWN_EGG:
		case SHULKER_SPAWN_EGG:
		case SILVERFISH_SPAWN_EGG:
		case SKELETON_HORSE_SPAWN_EGG:
		case SKELETON_SPAWN_EGG:
		case SLIME_SPAWN_EGG:
		case SPIDER_SPAWN_EGG:
		case SQUID_SPAWN_EGG:
		case STRAY_SPAWN_EGG:
		case TROPICAL_FISH_SPAWN_EGG:
		case TURTLE_SPAWN_EGG:
		case VEX_SPAWN_EGG:
		case VILLAGER_SPAWN_EGG:
		case VINDICATOR_SPAWN_EGG:
		case WITCH_SPAWN_EGG:
		case WITHER_SKELETON_SPAWN_EGG:
		case WOLF_SPAWN_EGG:
		case ZOMBIE_HORSE_SPAWN_EGG:
		case ZOMBIE_PIGMAN_SPAWN_EGG:
		case ZOMBIE_SPAWN_EGG:
		case ZOMBIE_VILLAGER_SPAWN_EGG:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSpawnEgg(ItemStack item) {
		return isSpawnEgg(item.getType());
	}

	public static boolean isSpawnEgg(Item item) {
		return isSpawnEgg(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is a dye
	 */
	public static boolean isDye(Material material) {
		switch(material) {
//		case INK_SAC:
		case BLACK_DYE:
//		case LAPIS_LAZULI:
		case BLUE_DYE:
//		case COCOA_BEANS:
		case BROWN_DYE:
		case CYAN_DYE:
		case GRAY_DYE:
//		case CACTUS_GREEN:
		case GREEN_DYE:
		case LIGHT_BLUE_DYE:
		case LIGHT_GRAY_DYE:
		case LIME_DYE:
		case MAGENTA_DYE:
		case ORANGE_DYE:
		case PINK_DYE:
		case PURPLE_DYE:
//		case ROSE_RED:
		case RED_DYE:
//		case BONE_MEAL:
		case WHITE_DYE:
//		case DANDELION_YELLOW:
		case YELLOW_DYE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isDye(ItemStack item) {
		return isDye(item.getType());
	}

	public static boolean isDye(Item item) {
		return isDye(item.getItemStack());
	}

	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the dye of the given color
	 */
	public static Material getDye(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_DYE;
		case BLUE:			return BLUE_DYE;
		case BROWN:			return BROWN_DYE;
		case CYAN:			return CYAN_DYE;
		case GRAY:			return GRAY_DYE;
		case GREEN:			return GREEN_DYE;
		case LIGHT_BLUE:	return LIGHT_BLUE_DYE;
		case LIGHT_GRAY:	return LIGHT_GRAY_DYE;
		case LIME:			return LIME_DYE;
		case MAGENTA:		return MAGENTA_DYE;
		case ORANGE:		return ORANGE_DYE;
		case PINK:			return PINK_DYE;
		case PURPLE:		return PURPLE_DYE;
		case RED:			return RED_DYE;
		case WHITE:			return WHITE_DYE;
		case YELLOW:		return YELLOW_DYE;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}

	/**
	 * @param material the Material to test
	 * @return {@code true} if it is shulker box
	 */
	public static boolean isShulkerBox(Material material) {
		switch(material) {
		case SHULKER_BOX:
		case BLACK_SHULKER_BOX:
		case BLUE_SHULKER_BOX:
		case BROWN_SHULKER_BOX:
		case CYAN_SHULKER_BOX:
		case GRAY_SHULKER_BOX:
		case GREEN_SHULKER_BOX:
		case LIGHT_BLUE_SHULKER_BOX:
		case LIGHT_GRAY_SHULKER_BOX:
		case LIME_SHULKER_BOX:
		case MAGENTA_SHULKER_BOX:
		case ORANGE_SHULKER_BOX:
		case PINK_SHULKER_BOX:
		case PURPLE_SHULKER_BOX:
		case RED_SHULKER_BOX:
		case WHITE_SHULKER_BOX:
		case YELLOW_SHULKER_BOX:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isShulkerBox(ItemStack item) {
		return isShulkerBox(item.getType());
	}

	public static boolean isShulkerBox(Item item) {
		return isShulkerBox(item.getItemStack());
	}

	public static boolean isShulkerBox(Block block) {
		return isShulkerBox(block.getType());
	}


	
	/**
	 * Note: this is one of the only {@code getXXXX(DyeColor)} method to support
	 * a case where the input color is {@code null}.
	 * 
	 * @param color the dye color
	 * @return the Material corresponding to the shulker box of the given color,
	 *  or {@linkplain Material#SHULKER_BOX SHULKER_BOX} if {@code color} is {@code null} 
	 */
	public static Material getShulkerBox(DyeColor color) {
		if(color == null) return SHULKER_BOX;
		switch(color) {
		case BLACK:			return BLACK_SHULKER_BOX;
		case BLUE:			return BLUE_SHULKER_BOX;
		case BROWN:			return BROWN_SHULKER_BOX;
		case CYAN:			return CYAN_SHULKER_BOX;
		case GRAY:			return GRAY_SHULKER_BOX;
		case GREEN:			return GREEN_SHULKER_BOX;
		case LIGHT_BLUE:	return LIGHT_BLUE_SHULKER_BOX;
		case LIGHT_GRAY:	return LIGHT_GRAY_SHULKER_BOX;
		case LIME:			return LIME_SHULKER_BOX;
		case MAGENTA:		return MAGENTA_SHULKER_BOX;
		case ORANGE:		return ORANGE_SHULKER_BOX;
		case PINK:			return PINK_SHULKER_BOX;
		case PURPLE:		return PURPLE_SHULKER_BOX;
		case RED:			return RED_SHULKER_BOX;
		case WHITE:			return WHITE_SHULKER_BOX;
		case YELLOW:		return YELLOW_SHULKER_BOX;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is wool
	 */
	public static boolean isWool(Material material) {
		switch(material) {
		case BLACK_WOOL:
		case BLUE_WOOL:
		case BROWN_WOOL:
		case CYAN_WOOL:
		case GRAY_WOOL:
		case GREEN_WOOL:
		case LIGHT_BLUE_WOOL:
		case LIGHT_GRAY_WOOL:
		case LIME_WOOL:
		case MAGENTA_WOOL:
		case ORANGE_WOOL:
		case PINK_WOOL:
		case PURPLE_WOOL:
		case RED_WOOL:
		case WHITE_WOOL:
		case YELLOW_WOOL:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWool(ItemStack item) {
		return isWool(item.getType());
	}

	public static boolean isWool(Item item) {
		return isWool(item.getItemStack());
	}

	public static boolean isWool(Block block) {
		return isWool(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the wool of the given color
	 */
	public static Material getWool(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_WOOL;
		case BLUE:			return BLUE_WOOL;
		case BROWN:			return BROWN_WOOL;
		case CYAN:			return CYAN_WOOL;
		case GRAY:			return GRAY_WOOL;
		case GREEN:			return GREEN_WOOL;
		case LIGHT_BLUE:	return LIGHT_BLUE_WOOL;
		case LIGHT_GRAY:	return LIGHT_GRAY_WOOL;
		case LIME:			return LIME_WOOL;
		case MAGENTA:		return MAGENTA_WOOL;
		case ORANGE:		return ORANGE_WOOL;
		case PINK:			return PINK_WOOL;
		case PURPLE:		return PURPLE_WOOL;
		case RED:			return RED_WOOL;
		case WHITE:			return WHITE_WOOL;
		case YELLOW:		return YELLOW_WOOL;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is carpet
	 */
	public static boolean isCarpet(Material material) {
		switch(material) {
		case BLACK_CARPET:
		case BLUE_CARPET:
		case BROWN_CARPET:
		case CYAN_CARPET:
		case GRAY_CARPET:
		case GREEN_CARPET:
		case LIGHT_BLUE_CARPET:
		case LIGHT_GRAY_CARPET:
		case LIME_CARPET:
		case MAGENTA_CARPET:
		case ORANGE_CARPET:
		case PINK_CARPET:
		case PURPLE_CARPET:
		case RED_CARPET:
		case WHITE_CARPET:
		case YELLOW_CARPET:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isCarpet(ItemStack item) {
		return isCarpet(item.getType());
	}

	public static boolean isCarpet(Item item) {
		return isCarpet(item.getItemStack());
	}

	public static boolean isCarpet(Block block) {
		return isCarpet(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the carpet of the given color
	 */
	public static Material getCarpet(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_CARPET;
		case BLUE:			return BLUE_CARPET;
		case BROWN:			return BROWN_CARPET;
		case CYAN:			return CYAN_CARPET;
		case GRAY:			return GRAY_CARPET;
		case GREEN:			return GREEN_CARPET;
		case LIGHT_BLUE:	return LIGHT_BLUE_CARPET;
		case LIGHT_GRAY:	return LIGHT_GRAY_CARPET;
		case LIME:			return LIME_CARPET;
		case MAGENTA:		return MAGENTA_CARPET;
		case ORANGE:		return ORANGE_CARPET;
		case PINK:			return PINK_CARPET;
		case PURPLE:		return PURPLE_CARPET;
		case RED:			return RED_CARPET;
		case WHITE:			return WHITE_CARPET;
		case YELLOW:		return YELLOW_CARPET;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is a bed
	 */
	public static boolean isBed(Material material) {
		switch(material) {
		case BLACK_BED:
		case BLUE_BED:
		case BROWN_BED:
		case CYAN_BED:
		case GRAY_BED:
		case GREEN_BED:
		case LIGHT_BLUE_BED:
		case LIGHT_GRAY_BED:
		case LIME_BED:
		case MAGENTA_BED:
		case ORANGE_BED:
		case PINK_BED:
		case PURPLE_BED:
		case RED_BED:
		case WHITE_BED:
		case YELLOW_BED:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBed(ItemStack item) {
		return isBed(item.getType());
	}

	public static boolean isBed(Item item) {
		return isBed(item.getItemStack());
	}

	public static boolean isBed(Block block) {
		return isBed(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the bed of the given color
	 */
	public static Material getBed(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_BED;
		case BLUE:			return BLUE_BED;
		case BROWN:			return BROWN_BED;
		case CYAN:			return CYAN_BED;
		case GRAY:			return GRAY_BED;
		case GREEN:			return GREEN_BED;
		case LIGHT_BLUE:	return LIGHT_BLUE_BED;
		case LIGHT_GRAY:	return LIGHT_GRAY_BED;
		case LIME:			return LIME_BED;
		case MAGENTA:		return MAGENTA_BED;
		case ORANGE:		return ORANGE_BED;
		case PINK:			return PINK_BED;
		case PURPLE:		return PURPLE_BED;
		case RED:			return RED_BED;
		case WHITE:			return WHITE_BED;
		case YELLOW:		return YELLOW_BED;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is terracotta
	 */
	public static boolean isTerracotta(Material material) {
		switch(material) {
		case TERRACOTTA:
		case BLACK_TERRACOTTA:
		case BLUE_TERRACOTTA:
		case BROWN_TERRACOTTA:
		case CYAN_TERRACOTTA:
		case GRAY_TERRACOTTA:
		case GREEN_TERRACOTTA:
		case LIGHT_BLUE_TERRACOTTA:
		case LIGHT_GRAY_TERRACOTTA:
		case LIME_TERRACOTTA:
		case MAGENTA_TERRACOTTA:
		case ORANGE_TERRACOTTA:
		case PINK_TERRACOTTA:
		case PURPLE_TERRACOTTA:
		case RED_TERRACOTTA:
		case WHITE_TERRACOTTA:
		case YELLOW_TERRACOTTA:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isTerracotta(ItemStack item) {
		return isTerracotta(item.getType());
	}

	public static boolean isTerracotta(Item item) {
		return isTerracotta(item.getItemStack());
	}

	public static boolean isTerracotta(Block block) {
		return isTerracotta(block.getType());
	}


	
	/**
	 * Note: this is one of the only {@code getXXXX(DyeColor)} method to support
	 * a case where the input color is {@code null}.
	 * 
	 * @param color the dye color
	 * @return the Material corresponding to the terracotta of the given color,
	 *  or {@linkplain Material#TERRACOTTA TERRACOTTA} if {@code color} is {@code null} 
	 */
	public static Material getTerracotta(DyeColor color) {
		if(color == null) return TERRACOTTA;
		switch(color) {
		case BLACK:			return BLACK_TERRACOTTA;
		case BLUE:			return BLUE_TERRACOTTA;
		case BROWN:			return BROWN_TERRACOTTA;
		case CYAN:			return CYAN_TERRACOTTA;
		case GRAY:			return GRAY_TERRACOTTA;
		case GREEN:			return GREEN_TERRACOTTA;
		case LIGHT_BLUE:	return LIGHT_BLUE_TERRACOTTA;
		case LIGHT_GRAY:	return LIGHT_GRAY_TERRACOTTA;
		case LIME:			return LIME_TERRACOTTA;
		case MAGENTA:		return MAGENTA_TERRACOTTA;
		case ORANGE:		return ORANGE_TERRACOTTA;
		case PINK:			return PINK_TERRACOTTA;
		case PURPLE:		return PURPLE_TERRACOTTA;
		case RED:			return RED_TERRACOTTA;
		case WHITE:			return WHITE_TERRACOTTA;
		case YELLOW:		return YELLOW_TERRACOTTA;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is glazed terracotta
	 */
	public static boolean isGlazedTerracotta(Material material) {
		switch(material) {
		case BLACK_GLAZED_TERRACOTTA:
		case BLUE_GLAZED_TERRACOTTA:
		case BROWN_GLAZED_TERRACOTTA:
		case CYAN_GLAZED_TERRACOTTA:
		case GRAY_GLAZED_TERRACOTTA:
		case GREEN_GLAZED_TERRACOTTA:
		case LIGHT_BLUE_GLAZED_TERRACOTTA:
		case LIGHT_GRAY_GLAZED_TERRACOTTA:
		case LIME_GLAZED_TERRACOTTA:
		case MAGENTA_GLAZED_TERRACOTTA:
		case ORANGE_GLAZED_TERRACOTTA:
		case PINK_GLAZED_TERRACOTTA:
		case PURPLE_GLAZED_TERRACOTTA:
		case RED_GLAZED_TERRACOTTA:
		case WHITE_GLAZED_TERRACOTTA:
		case YELLOW_GLAZED_TERRACOTTA:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isGlazedTerracotta(ItemStack item) {
		return isGlazedTerracotta(item.getType());
	}

	public static boolean isGlazedTerracotta(Item item) {
		return isGlazedTerracotta(item.getItemStack());
	}

	public static boolean isGlazedTerracotta(Block block) {
		return isGlazedTerracotta(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the glazed terracotta of the given color
	 */
	public static Material getGlazedTerracotta(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_GLAZED_TERRACOTTA;
		case BLUE:			return BLUE_GLAZED_TERRACOTTA;
		case BROWN:			return BROWN_GLAZED_TERRACOTTA;
		case CYAN:			return CYAN_GLAZED_TERRACOTTA;
		case GRAY:			return GRAY_GLAZED_TERRACOTTA;
		case GREEN:			return GREEN_GLAZED_TERRACOTTA;
		case LIGHT_BLUE:	return LIGHT_BLUE_GLAZED_TERRACOTTA;
		case LIGHT_GRAY:	return LIGHT_GRAY_GLAZED_TERRACOTTA;
		case LIME:			return LIME_GLAZED_TERRACOTTA;
		case MAGENTA:		return MAGENTA_GLAZED_TERRACOTTA;
		case ORANGE:		return ORANGE_GLAZED_TERRACOTTA;
		case PINK:			return PINK_GLAZED_TERRACOTTA;
		case PURPLE:		return PURPLE_GLAZED_TERRACOTTA;
		case RED:			return RED_GLAZED_TERRACOTTA;
		case WHITE:			return WHITE_GLAZED_TERRACOTTA;
		case YELLOW:		return YELLOW_GLAZED_TERRACOTTA;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is concrete
	 */
	public static boolean isConcrete(Material material) {
		switch(material) {
		case BLACK_CONCRETE:
		case BLUE_CONCRETE:
		case BROWN_CONCRETE:
		case CYAN_CONCRETE:
		case GRAY_CONCRETE:
		case GREEN_CONCRETE:
		case LIGHT_BLUE_CONCRETE:
		case LIGHT_GRAY_CONCRETE:
		case LIME_CONCRETE:
		case MAGENTA_CONCRETE:
		case ORANGE_CONCRETE:
		case PINK_CONCRETE:
		case PURPLE_CONCRETE:
		case RED_CONCRETE:
		case WHITE_CONCRETE:
		case YELLOW_CONCRETE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isConcrete(ItemStack item) {
		return isConcrete(item.getType());
	}

	public static boolean isConcrete(Item item) {
		return isConcrete(item.getItemStack());
	}

	public static boolean isConcrete(Block block) {
		return isConcrete(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the concrete of the given color
	 */
	public static Material getConcrete(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_CONCRETE;
		case BLUE:			return BLUE_CONCRETE;
		case BROWN:			return BROWN_CONCRETE;
		case CYAN:			return CYAN_CONCRETE;
		case GRAY:			return GRAY_CONCRETE;
		case GREEN:			return GREEN_CONCRETE;
		case LIGHT_BLUE:	return LIGHT_BLUE_CONCRETE;
		case LIGHT_GRAY:	return LIGHT_GRAY_CONCRETE;
		case LIME:			return LIME_CONCRETE;
		case MAGENTA:		return MAGENTA_CONCRETE;
		case ORANGE:		return ORANGE_CONCRETE;
		case PINK:			return PINK_CONCRETE;
		case PURPLE:		return PURPLE_CONCRETE;
		case RED:			return RED_CONCRETE;
		case WHITE:			return WHITE_CONCRETE;
		case YELLOW:		return YELLOW_CONCRETE;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is concrete powder
	 */
	public static boolean isConcretePowder(Material material) {
		switch(material) {
		case BLACK_CONCRETE_POWDER:
		case BLUE_CONCRETE_POWDER:
		case BROWN_CONCRETE_POWDER:
		case CYAN_CONCRETE_POWDER:
		case GRAY_CONCRETE_POWDER:
		case GREEN_CONCRETE_POWDER:
		case LIGHT_BLUE_CONCRETE_POWDER:
		case LIGHT_GRAY_CONCRETE_POWDER:
		case LIME_CONCRETE_POWDER:
		case MAGENTA_CONCRETE_POWDER:
		case ORANGE_CONCRETE_POWDER:
		case PINK_CONCRETE_POWDER:
		case PURPLE_CONCRETE_POWDER:
		case RED_CONCRETE_POWDER:
		case WHITE_CONCRETE_POWDER:
		case YELLOW_CONCRETE_POWDER:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isConcretePowder(ItemStack item) {
		return isConcretePowder(item.getType());
	}

	public static boolean isConcretePowder(Item item) {
		return isConcretePowder(item.getItemStack());
	}

	public static boolean isConcretePowder(Block block) {
		return isConcretePowder(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the concrete powder of the given color
	 */
	public static Material getConcretePowder(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_CONCRETE_POWDER;
		case BLUE:			return BLUE_CONCRETE_POWDER;
		case BROWN:			return BROWN_CONCRETE_POWDER;
		case CYAN:			return CYAN_CONCRETE_POWDER;
		case GRAY:			return GRAY_CONCRETE_POWDER;
		case GREEN:			return GREEN_CONCRETE_POWDER;
		case LIGHT_BLUE:	return LIGHT_BLUE_CONCRETE_POWDER;
		case LIGHT_GRAY:	return LIGHT_GRAY_CONCRETE_POWDER;
		case LIME:			return LIME_CONCRETE_POWDER;
		case MAGENTA:		return MAGENTA_CONCRETE_POWDER;
		case ORANGE:		return ORANGE_CONCRETE_POWDER;
		case PINK:			return PINK_CONCRETE_POWDER;
		case PURPLE:		return PURPLE_CONCRETE_POWDER;
		case RED:			return RED_CONCRETE_POWDER;
		case WHITE:			return WHITE_CONCRETE_POWDER;
		case YELLOW:		return YELLOW_CONCRETE_POWDER;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is stained glass block
	 */
	public static boolean isStainedGlassBlock(Material material) {
		switch(material) {
		case BLACK_STAINED_GLASS:
		case BLUE_STAINED_GLASS:
		case BROWN_STAINED_GLASS:
		case CYAN_STAINED_GLASS:
		case GRAY_STAINED_GLASS:
		case GREEN_STAINED_GLASS:
		case LIGHT_BLUE_STAINED_GLASS:
		case LIGHT_GRAY_STAINED_GLASS:
		case LIME_STAINED_GLASS:
		case MAGENTA_STAINED_GLASS:
		case ORANGE_STAINED_GLASS:
		case PINK_STAINED_GLASS:
		case PURPLE_STAINED_GLASS:
		case RED_STAINED_GLASS:
		case WHITE_STAINED_GLASS:
		case YELLOW_STAINED_GLASS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isStainedGlassBlock(ItemStack item) {
		return isStainedGlassBlock(item.getType());
	}

	public static boolean isStainedGlassBlock(Item item) {
		return isStainedGlassBlock(item.getItemStack());
	}

	public static boolean isStainedGlassBlock(Block block) {
		return isStainedGlassBlock(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the stained glass block of the given color
	 */
	public static Material getStainedGlassBlock(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_STAINED_GLASS;
		case BLUE:			return BLUE_STAINED_GLASS;
		case BROWN:			return BROWN_STAINED_GLASS;
		case CYAN:			return CYAN_STAINED_GLASS;
		case GRAY:			return GRAY_STAINED_GLASS;
		case GREEN:			return GREEN_STAINED_GLASS;
		case LIGHT_BLUE:	return LIGHT_BLUE_STAINED_GLASS;
		case LIGHT_GRAY:	return LIGHT_GRAY_STAINED_GLASS;
		case LIME:			return LIME_STAINED_GLASS;
		case MAGENTA:		return MAGENTA_STAINED_GLASS;
		case ORANGE:		return ORANGE_STAINED_GLASS;
		case PINK:			return PINK_STAINED_GLASS;
		case PURPLE:		return PURPLE_STAINED_GLASS;
		case RED:			return RED_STAINED_GLASS;
		case WHITE:			return WHITE_STAINED_GLASS;
		case YELLOW:		return YELLOW_STAINED_GLASS;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if it is stained glass pane
	 */
	public static boolean isStainedGlassPane(Material material) {
		switch(material) {
		case BLACK_STAINED_GLASS_PANE:
		case BLUE_STAINED_GLASS_PANE:
		case BROWN_STAINED_GLASS_PANE:
		case CYAN_STAINED_GLASS_PANE:
		case GRAY_STAINED_GLASS_PANE:
		case GREEN_STAINED_GLASS_PANE:
		case LIGHT_BLUE_STAINED_GLASS_PANE:
		case LIGHT_GRAY_STAINED_GLASS_PANE:
		case LIME_STAINED_GLASS_PANE:
		case MAGENTA_STAINED_GLASS_PANE:
		case ORANGE_STAINED_GLASS_PANE:
		case PINK_STAINED_GLASS_PANE:
		case PURPLE_STAINED_GLASS_PANE:
		case RED_STAINED_GLASS_PANE:
		case WHITE_STAINED_GLASS_PANE:
		case YELLOW_STAINED_GLASS_PANE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isStainedGlassPane(ItemStack item) {
		return isStainedGlassPane(item.getType());
	}

	public static boolean isStainedGlassPane(Item item) {
		return isStainedGlassPane(item.getItemStack());
	}

	public static boolean isStainedGlassPane(Block block) {
		return isStainedGlassPane(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the stained glass pane of the given color
	 */
	public static Material getStainedGlassPane(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_STAINED_GLASS_PANE;
		case BLUE:			return BLUE_STAINED_GLASS_PANE;
		case BROWN:			return BROWN_STAINED_GLASS_PANE;
		case CYAN:			return CYAN_STAINED_GLASS_PANE;
		case GRAY:			return GRAY_STAINED_GLASS_PANE;
		case GREEN:			return GREEN_STAINED_GLASS_PANE;
		case LIGHT_BLUE:	return LIGHT_BLUE_STAINED_GLASS_PANE;
		case LIGHT_GRAY:	return LIGHT_GRAY_STAINED_GLASS_PANE;
		case LIME:			return LIME_STAINED_GLASS_PANE;
		case MAGENTA:		return MAGENTA_STAINED_GLASS_PANE;
		case ORANGE:		return ORANGE_STAINED_GLASS_PANE;
		case PINK:			return PINK_STAINED_GLASS_PANE;
		case PURPLE:		return PURPLE_STAINED_GLASS_PANE;
		case RED:			return RED_STAINED_GLASS_PANE;
		case WHITE:			return WHITE_STAINED_GLASS_PANE;
		case YELLOW:		return YELLOW_STAINED_GLASS_PANE;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	

	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a glass block (stained or unstained)
	 */
	public static boolean isGlassBlock(Material material) {
		return material == GLASS || isStainedGlassBlock(material);
	}
	
	public static boolean isGlassBlock(ItemStack item) {
		return isGlassBlock(item.getType());
	}

	public static boolean isGlassBlock(Item item) {
		return isGlassBlock(item.getItemStack());
	}

	public static boolean isGlassBlock(Block block) {
		return isGlassBlock(block.getType());
	}


	
	/**
	 * Note: this is one of the only {@code getXXXX(DyeColor)} method to support
	 * a case where the input color is {@code null}.
	 * 
	 * @param color the dye color
	 * @return the Material corresponding to the stained glass block of the given color,
	 *  or {@linkplain Material#GLASS GLASS} if {@code color} is {@code null} 
	 */
	public static Material getGlassBlock(DyeColor color) {
		if(color == null)
			return GLASS;
		else return getStainedGlassBlock(color);
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a glass pane (stained or unstained)
	 */
	public static boolean isGlassPane(Material material) {
		return material == GLASS_PANE || isStainedGlassPane(material);
	}
	
	public static boolean isGlassPane(ItemStack item) {
		return isGlassPane(item.getType());
	}

	public static boolean isGlassPane(Item item) {
		return isGlassPane(item.getItemStack());
	}

	public static boolean isGlassPane(Block block) {
		return isGlassPane(block.getType());
	}


	
	/**
	 * Note: this is one of the only {@code getXXXX(DyeColor)} method to support
	 * a case where the input color is {@code null}.
	 * 
	 * @param color the dye color
	 * @return the Material corresponding to the stained glass pane of the given color,
	 *  or {@linkplain Material#GLASS_PANE GLASS_PANE} if {@code color} is {@code null} 
	 */
	public static Material getGlassPane(DyeColor color) {
		if(color == null)
			return GLASS_PANE;
		else return getStainedGlassPane(color);
	}
	
	/**
	 * Tests if a material is a block made of glass.
	 * <br>
	 * <br>
	 * Note 1: this method returns {@code true} if the material
	 * is a glass block <i>or</i> a glass pane. To just do either one
	 * or the other, use {@link #isGlassBlock(Material)} or {@link #isGlassPane(Material)}.
	 * <br>
	 * Note 2: this method returns {@code true} <i><b>only</i></b> if the material
	 * is a glass block or pane. Items which are made of glass or other blocks
	 * that have glass in them do not qualify.
	 * 
	 * @param material the material to test
	 * @return {@code true} if it is a glass pane or block
	 */
	public static boolean isGlass(Material material) {
		switch(material) {
		case GLASS:
		case BLACK_STAINED_GLASS:
		case BLUE_STAINED_GLASS:
		case BROWN_STAINED_GLASS:
		case CYAN_STAINED_GLASS:
		case GRAY_STAINED_GLASS:
		case GREEN_STAINED_GLASS:
		case LIGHT_BLUE_STAINED_GLASS:
		case LIGHT_GRAY_STAINED_GLASS:
		case LIME_STAINED_GLASS:
		case MAGENTA_STAINED_GLASS:
		case ORANGE_STAINED_GLASS:
		case PINK_STAINED_GLASS:
		case PURPLE_STAINED_GLASS:
		case RED_STAINED_GLASS:
		case WHITE_STAINED_GLASS:
		case YELLOW_STAINED_GLASS:
		case GLASS_PANE:
		case BLACK_STAINED_GLASS_PANE:
		case BLUE_STAINED_GLASS_PANE:
		case BROWN_STAINED_GLASS_PANE:
		case CYAN_STAINED_GLASS_PANE:
		case GRAY_STAINED_GLASS_PANE:
		case GREEN_STAINED_GLASS_PANE:
		case LIGHT_BLUE_STAINED_GLASS_PANE:
		case LIGHT_GRAY_STAINED_GLASS_PANE:
		case LIME_STAINED_GLASS_PANE:
		case MAGENTA_STAINED_GLASS_PANE:
		case ORANGE_STAINED_GLASS_PANE:
		case PINK_STAINED_GLASS_PANE:
		case PURPLE_STAINED_GLASS_PANE:
		case RED_STAINED_GLASS_PANE:
		case WHITE_STAINED_GLASS_PANE:
		case YELLOW_STAINED_GLASS_PANE:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isGlass(ItemStack item) {
		return isGlass(item.getType());
	}

	public static boolean isGlass(Item item) {
		return isGlass(item.getItemStack());
	}

	public static boolean isGlass(Block block) {
		return isGlass(block.getType());
	}


	
	/**
	 * Test whether a material is a banner.
	 * <br>
	 * <br>
	 * Note: this method does not return {@code true} if the material
	 * is a wall banner. To do that, use {@link #isBannerBlock(Material)}.
	 * 
	 * @param material the Material to test
	 * @return {@code true} if the material is a banner item
	 */
	public static boolean isBanner(Material material) {
		switch(material) {
		case BLACK_BANNER:
		case BLUE_BANNER:
		case BROWN_BANNER:
		case CYAN_BANNER:
		case GRAY_BANNER:
		case GREEN_BANNER:
		case LIGHT_BLUE_BANNER:
		case LIGHT_GRAY_BANNER:
		case LIME_BANNER:
		case MAGENTA_BANNER:
		case ORANGE_BANNER:
		case PINK_BANNER:
		case PURPLE_BANNER:
		case RED_BANNER:
		case WHITE_BANNER:
		case YELLOW_BANNER:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBanner(ItemStack item) {
		return isBanner(item.getType());
	}

	public static boolean isBanner(Item item) {
		return isBanner(item.getItemStack());
	}

	public static boolean isBanner(Block block) {
		return isBannerBlock(block.getType()); // [sic]
	}
	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the banner of the given color
	 */
	public static Material getBanner(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_BANNER;
		case BLUE:			return BLUE_BANNER;
		case BROWN:			return BROWN_BANNER;
		case CYAN:			return CYAN_BANNER;
		case GRAY:			return GRAY_BANNER;
		case GREEN:			return GREEN_BANNER;
		case LIGHT_BLUE:	return LIGHT_BLUE_BANNER;
		case LIGHT_GRAY:	return LIGHT_GRAY_BANNER;
		case LIME:			return LIME_BANNER;
		case MAGENTA:		return MAGENTA_BANNER;
		case ORANGE:		return ORANGE_BANNER;
		case PINK:			return PINK_BANNER;
		case PURPLE:		return PURPLE_BANNER;
		case RED:			return RED_BANNER;
		case WHITE:			return WHITE_BANNER;
		case YELLOW:		return YELLOW_BANNER;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * Test whether a material is a wall banner block.
	 * <br>
	 * <br>
	 * Note: this method does not return {@code true} if the material
	 * is a standing banner. To do that, use {@link #isBannerBlock(Material)}.
	 * 
	 * @param material the Material to test
	 * @return {@code true} if the material is a banner item
	 */
	public static boolean isWallBanner(Material material) {
		switch(material) {
		case BLACK_WALL_BANNER:
		case BLUE_WALL_BANNER:
		case BROWN_WALL_BANNER:
		case CYAN_WALL_BANNER:
		case GRAY_WALL_BANNER:
		case GREEN_WALL_BANNER:
		case LIGHT_BLUE_WALL_BANNER:
		case LIGHT_GRAY_WALL_BANNER:
		case LIME_WALL_BANNER:
		case MAGENTA_WALL_BANNER:
		case ORANGE_WALL_BANNER:
		case PINK_WALL_BANNER:
		case PURPLE_WALL_BANNER:
		case RED_WALL_BANNER:
		case WHITE_WALL_BANNER:
		case YELLOW_WALL_BANNER:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWallBanner(ItemStack item) {
		return isWallBanner(item.getType());
	}

	public static boolean isWallBanner(Item item) {
		return isWallBanner(item.getItemStack());
	}

	public static boolean isWallBanner(Block block) {
		return isWallBanner(block.getType());
	}


	
	/**
	 * @param color the dye color
	 * @return the Material corresponding to the wall banner of the given color
	 */
	public static Material getWallBanner(DyeColor color) {
		switch(color) {
		case BLACK:			return BLACK_WALL_BANNER;
		case BLUE:			return BLUE_WALL_BANNER;
		case BROWN:			return BROWN_WALL_BANNER;
		case CYAN:			return CYAN_WALL_BANNER;
		case GRAY:			return GRAY_WALL_BANNER;
		case GREEN:			return GREEN_WALL_BANNER;
		case LIGHT_BLUE:	return LIGHT_BLUE_WALL_BANNER;
		case LIGHT_GRAY:	return LIGHT_GRAY_WALL_BANNER;
		case LIME:			return LIME_WALL_BANNER;
		case MAGENTA:		return MAGENTA_WALL_BANNER;
		case ORANGE:		return ORANGE_WALL_BANNER;
		case PINK:			return PINK_WALL_BANNER;
		case PURPLE:		return PURPLE_WALL_BANNER;
		case RED:			return RED_WALL_BANNER;
		case WHITE:			return WHITE_WALL_BANNER;
		case YELLOW:		return YELLOW_WALL_BANNER;
		default:
			throw new AssertionError("DyeColor definition has changed during runtime");
		}
	}
	
	/**
	 * Test whether a material is a banner block.
	 * 
	 * @param material the Material to test
	 * @return {@code true} if the material is a banner block (standing or wall)
	 */
	public static boolean isBannerBlock(Material material) {
		switch(material) {
		case BLACK_BANNER:
		case BLUE_BANNER:
		case BROWN_BANNER:
		case CYAN_BANNER:
		case GRAY_BANNER:
		case GREEN_BANNER:
		case LIGHT_BLUE_BANNER:
		case LIGHT_GRAY_BANNER:
		case LIME_BANNER:
		case MAGENTA_BANNER:
		case ORANGE_BANNER:
		case PINK_BANNER:
		case PURPLE_BANNER:
		case RED_BANNER:
		case WHITE_BANNER:
		case YELLOW_BANNER:
		case BLACK_WALL_BANNER:
		case BLUE_WALL_BANNER:
		case BROWN_WALL_BANNER:
		case CYAN_WALL_BANNER:
		case GRAY_WALL_BANNER:
		case GREEN_WALL_BANNER:
		case LIGHT_BLUE_WALL_BANNER:
		case LIGHT_GRAY_WALL_BANNER:
		case LIME_WALL_BANNER:
		case MAGENTA_WALL_BANNER:
		case ORANGE_WALL_BANNER:
		case PINK_WALL_BANNER:
		case PURPLE_WALL_BANNER:
		case RED_WALL_BANNER:
		case WHITE_WALL_BANNER:
		case YELLOW_WALL_BANNER:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBannerBlock(Block block) {
		return isBannerBlock(block.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is a banner item or a shield
	 */
	public static boolean isBannerOrShield(Material material) {
		return material == SHIELD || isBanner(material);
	}
	
	public static boolean isBannerOrShield(ItemStack item) {
		return isBannerOrShield(item.getType());
	}

	public static boolean isBannerOrShield(Item item) {
		return isBannerOrShield(item.getItemStack());
	}

	
	/**
	 * @param material the dyed Material
	 * @return the corresponding {@linkplain DyeColor} or {@code null} if
	 * 	the material is not a dyed material
	 */
	public static DyeColor getDyeColor(Material material) {
		switch(material) {
		case WHITE_BANNER:
		case WHITE_WALL_BANNER:
		case WHITE_WOOL:
		case WHITE_CARPET:
		case WHITE_TERRACOTTA:
		case WHITE_GLAZED_TERRACOTTA:
		case WHITE_BED:
		case WHITE_CONCRETE:
		case WHITE_CONCRETE_POWDER:
		case WHITE_SHULKER_BOX:
		case WHITE_STAINED_GLASS:
		case WHITE_STAINED_GLASS_PANE:
		case WHITE_DYE:
			return DyeColor.WHITE;
		case ORANGE_BANNER:
		case ORANGE_WALL_BANNER:
		case ORANGE_WOOL:
		case ORANGE_CARPET:
		case ORANGE_TERRACOTTA:
		case ORANGE_GLAZED_TERRACOTTA:
		case ORANGE_BED:
		case ORANGE_CONCRETE:
		case ORANGE_CONCRETE_POWDER:
		case ORANGE_SHULKER_BOX:
		case ORANGE_STAINED_GLASS:
		case ORANGE_STAINED_GLASS_PANE:
		case ORANGE_DYE:
			return DyeColor.ORANGE;
		case MAGENTA_BANNER:
		case MAGENTA_WALL_BANNER:
		case MAGENTA_WOOL:
		case MAGENTA_CARPET:
		case MAGENTA_TERRACOTTA:
		case MAGENTA_GLAZED_TERRACOTTA:
		case MAGENTA_BED:
		case MAGENTA_CONCRETE:
		case MAGENTA_CONCRETE_POWDER:
		case MAGENTA_SHULKER_BOX:
		case MAGENTA_STAINED_GLASS:
		case MAGENTA_STAINED_GLASS_PANE:
		case MAGENTA_DYE:
			return DyeColor.MAGENTA;
		case LIGHT_BLUE_BANNER:
		case LIGHT_BLUE_WALL_BANNER:
		case LIGHT_BLUE_WOOL:
		case LIGHT_BLUE_CARPET:
		case LIGHT_BLUE_TERRACOTTA:
		case LIGHT_BLUE_GLAZED_TERRACOTTA:
		case LIGHT_BLUE_BED:
		case LIGHT_BLUE_CONCRETE:
		case LIGHT_BLUE_CONCRETE_POWDER:
		case LIGHT_BLUE_SHULKER_BOX:
		case LIGHT_BLUE_STAINED_GLASS:
		case LIGHT_BLUE_STAINED_GLASS_PANE:
		case LIGHT_BLUE_DYE:
			return DyeColor.LIGHT_BLUE;
		case YELLOW_BANNER:
		case YELLOW_WALL_BANNER:
		case YELLOW_WOOL:
		case YELLOW_CARPET:
		case YELLOW_TERRACOTTA:
		case YELLOW_GLAZED_TERRACOTTA:
		case YELLOW_BED:
		case YELLOW_CONCRETE:
		case YELLOW_CONCRETE_POWDER:
		case YELLOW_SHULKER_BOX:
		case YELLOW_STAINED_GLASS:
		case YELLOW_STAINED_GLASS_PANE:
		case YELLOW_DYE:
			return DyeColor.YELLOW;
		case LIME_BANNER:
		case LIME_WALL_BANNER:
		case LIME_WOOL:
		case LIME_CARPET:
		case LIME_TERRACOTTA:
		case LIME_GLAZED_TERRACOTTA:
		case LIME_BED:
		case LIME_CONCRETE:
		case LIME_CONCRETE_POWDER:
		case LIME_SHULKER_BOX:
		case LIME_STAINED_GLASS:
		case LIME_STAINED_GLASS_PANE:
		case LIME_DYE:
			return DyeColor.LIME;
		case PINK_BANNER:
		case PINK_WALL_BANNER:
		case PINK_WOOL:
		case PINK_CARPET:
		case PINK_TERRACOTTA:
		case PINK_GLAZED_TERRACOTTA:
		case PINK_BED:
		case PINK_CONCRETE:
		case PINK_CONCRETE_POWDER:
		case PINK_SHULKER_BOX:
		case PINK_STAINED_GLASS:
		case PINK_STAINED_GLASS_PANE:
		case PINK_DYE:
			return DyeColor.PINK;
		case GRAY_BANNER:
		case GRAY_WALL_BANNER:
		case GRAY_WOOL:
		case GRAY_CARPET:
		case GRAY_TERRACOTTA:
		case GRAY_GLAZED_TERRACOTTA:
		case GRAY_BED:
		case GRAY_CONCRETE:
		case GRAY_CONCRETE_POWDER:
		case GRAY_SHULKER_BOX:
		case GRAY_STAINED_GLASS:
		case GRAY_STAINED_GLASS_PANE:
		case GRAY_DYE:
			return DyeColor.GRAY;
		case LIGHT_GRAY_BANNER:
		case LIGHT_GRAY_WALL_BANNER:
		case LIGHT_GRAY_WOOL:
		case LIGHT_GRAY_CARPET:
		case LIGHT_GRAY_TERRACOTTA:
		case LIGHT_GRAY_GLAZED_TERRACOTTA:
		case LIGHT_GRAY_BED:
		case LIGHT_GRAY_CONCRETE:
		case LIGHT_GRAY_CONCRETE_POWDER:
		case LIGHT_GRAY_SHULKER_BOX:
		case LIGHT_GRAY_STAINED_GLASS:
		case LIGHT_GRAY_STAINED_GLASS_PANE:
		case LIGHT_GRAY_DYE:
			return DyeColor.LIGHT_GRAY;
		case CYAN_BANNER:
		case CYAN_WALL_BANNER:
		case CYAN_WOOL:
		case CYAN_CARPET:
		case CYAN_TERRACOTTA:
		case CYAN_GLAZED_TERRACOTTA:
		case CYAN_BED:
		case CYAN_CONCRETE:
		case CYAN_CONCRETE_POWDER:
		case CYAN_SHULKER_BOX:
		case CYAN_STAINED_GLASS:
		case CYAN_STAINED_GLASS_PANE:
		case CYAN_DYE:
			return DyeColor.CYAN;
		case PURPLE_BANNER:
		case PURPLE_WALL_BANNER:
		case PURPLE_WOOL:
		case PURPLE_CARPET:
		case PURPLE_TERRACOTTA:
		case PURPLE_GLAZED_TERRACOTTA:
		case PURPLE_BED:
		case PURPLE_CONCRETE:
		case PURPLE_CONCRETE_POWDER:
		case PURPLE_SHULKER_BOX:
		case PURPLE_STAINED_GLASS:
		case PURPLE_STAINED_GLASS_PANE:
		case PURPLE_DYE:
			return DyeColor.PURPLE;
		case BLUE_BANNER:
		case BLUE_WALL_BANNER:
		case BLUE_WOOL:
		case BLUE_CARPET:
		case BLUE_TERRACOTTA:
		case BLUE_GLAZED_TERRACOTTA:
		case BLUE_BED:
		case BLUE_CONCRETE:
		case BLUE_CONCRETE_POWDER:
		case BLUE_SHULKER_BOX:
		case BLUE_STAINED_GLASS:
		case BLUE_STAINED_GLASS_PANE:
		case BLUE_DYE:
			return DyeColor.BLUE;
		case BROWN_BANNER:
		case BROWN_WALL_BANNER:
		case BROWN_WOOL:
		case BROWN_CARPET:
		case BROWN_TERRACOTTA:
		case BROWN_GLAZED_TERRACOTTA:
		case BROWN_BED:
		case BROWN_CONCRETE:
		case BROWN_CONCRETE_POWDER:
		case BROWN_SHULKER_BOX:
		case BROWN_STAINED_GLASS:
		case BROWN_STAINED_GLASS_PANE:
		case BROWN_DYE:
			return DyeColor.BROWN;
		case GREEN_BANNER:
		case GREEN_WALL_BANNER:
		case GREEN_WOOL:
		case GREEN_CARPET:
		case GREEN_TERRACOTTA:
		case GREEN_GLAZED_TERRACOTTA:
		case GREEN_BED:
		case GREEN_CONCRETE:
		case GREEN_CONCRETE_POWDER:
		case GREEN_SHULKER_BOX:
		case GREEN_STAINED_GLASS:
		case GREEN_STAINED_GLASS_PANE:
		case GREEN_DYE:
			return DyeColor.GREEN;
		case RED_BANNER:
		case RED_WALL_BANNER:
		case RED_WOOL:
		case RED_CARPET:
		case RED_TERRACOTTA:
		case RED_GLAZED_TERRACOTTA:
		case RED_BED:
		case RED_CONCRETE:
		case RED_CONCRETE_POWDER:
		case RED_SHULKER_BOX:
		case RED_STAINED_GLASS:
		case RED_STAINED_GLASS_PANE:
		case RED_DYE:
			return DyeColor.RED;
		case BLACK_BANNER:
		case BLACK_WALL_BANNER:
		case BLACK_WOOL:
		case BLACK_CARPET:
		case BLACK_TERRACOTTA:
		case BLACK_GLAZED_TERRACOTTA:
		case BLACK_BED:
		case BLACK_CONCRETE:
		case BLACK_CONCRETE_POWDER:
		case BLACK_SHULKER_BOX:
		case BLACK_STAINED_GLASS:
		case BLACK_STAINED_GLASS_PANE:
		case BLACK_DYE:
			return DyeColor.BLACK;
	/*	case TERRACOTTA:
		case SHULKER_BOX:
		case GLASS:
		case GLASS_PANE: */
		default:
			return null;
		}
	}
	
	public static DyeColor getDyeColor(ItemStack item) {
		return getDyeColor(item.getType());
	}
	
	public static DyeColor getDyeColor(Item item) {
		return getDyeColor(item.getItemStack());
	}
	
	public static DyeColor getDyeColor(Block block) {
		return getDyeColor(block.getType());
	}
	
	/**
	 * Tests if a material is seeds.
	 * <br>
	 * <br>
	 * Note: this method does not return {@code true} for
	 * {@linkplain Material#COCOA_BEANS COCOA_BEANS}, 
	 * {@linkplain Material#CARROT CARROT}, or
	 * {@linkplain Material#POTATO POTATO}. To test for that
	 * as well, use {@link #isPlantable(Material)}.
	 * 
	 * @param material the Material to test
	 * @return {@code true} if the material is seeds
	 */
	public static boolean isSeeds(Material material) {
		switch(material) {
		case WHEAT_SEEDS:
		case PUMPKIN_SEEDS:
		case MELON_SEEDS:
		case BEETROOT_SEEDS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSeeds(ItemStack item) {
		return isSeeds(item.getType());
	}

	public static boolean isSeeds(Item item) {
		return isSeeds(item.getItemStack());
	}

	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material is plantable in some way
	 */
	public static boolean isPlantable(Material material) {
		switch(material) {
		case WHEAT_SEEDS:
		case PUMPKIN_SEEDS:
		case MELON_SEEDS:
		case BEETROOT_SEEDS:
		case COCOA_BEANS:
		case CARROT:
		case POTATO:
		case OAK_SAPLING:
		case SPRUCE_SAPLING:
		case BIRCH_SAPLING:
		case JUNGLE_SAPLING:
		case DARK_OAK_SAPLING:
		case ACACIA_SAPLING:
		case NETHER_WART:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isPlantable(ItemStack item) {
		return isPlantable(item.getType());
	}

	public static boolean isPlantable(Item item) {
		return isPlantable(item.getItemStack());
	}

	
	/**
	 * Get the material that results when the input material is planted appropriately.
	 * <br>
	 * <br>
	 * Note: in the case of saplings, this method returns the log material of the
	 * corresponding tree.
	 * 
	 * @param material the {@linkplain #isPlantable(Material) plantable} item material
	 * @return the block Material or {@code null} if it is not plantable
	 */
	public static Material getCropBlockType(Material material) {
		switch(material) {
		case WHEAT_SEEDS:		return WHEAT;
		case PUMPKIN_SEEDS:		return PUMPKIN_STEM;
		case MELON_SEEDS:		return MELON_STEM;
		case BEETROOT_SEEDS:	return BEETROOTS;
		case COCOA_BEANS:		return COCOA;
		case POTATO:			return POTATOES;
		case CARROT:			return CARROTS;
		case OAK_SAPLING:		return OAK_LOG;
		case SPRUCE_SAPLING:	return SPRUCE_LOG;
		case BIRCH_SAPLING:		return BIRCH_LOG;
		case JUNGLE_SAPLING:	return JUNGLE_LOG;
		case DARK_OAK_SAPLING:	return DARK_OAK_LOG;
		case ACACIA_SAPLING:	return ACACIA_LOG;
		case NETHER_WART:		return NETHER_WART;
		default:				return null;
		}
	}
	
	public static Material getCropBlockType(ItemStack item) {
		return getCropBlockType(item.getType());
	}
	
	/**
	 * @param material the Material to test
	 * @return {@code true} if the material can be thrown
	 */
	public static boolean isThrowable(Material material) {
		switch(material) {
		case SNOWBALL:
		case ENDER_PEARL:
		case ENDER_EYE:
		case SPLASH_POTION:
		case LINGERING_POTION:
		case EXPERIENCE_BOTTLE:
		case EGG:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isThrowable(ItemStack item) {
		return isThrowable(item.getType());
	}

	public static boolean isThrowable(Item item) {
		return isThrowable(item.getItemStack());
	}


	public static boolean isAnvil(Material material) {
		switch(material) {
		case ANVIL:
		case DAMAGED_ANVIL:
		case CHIPPED_ANVIL:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isAnvil(ItemStack item) {
		return isAnvil(item.getType());
	}

	public static boolean isAnvil(Item item) {
		return isAnvil(item.getItemStack());
	}

	public static boolean isAnvil(Block block) {
		return isAnvil(block.getType());
	}

	public static boolean isAir(Material material) {
		switch(material) {
		case AIR:
		case CAVE_AIR:
		case VOID_AIR:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isAir(Block block) {
		return isAir(block.getType());
	}
	
	public static void updateEnchantments(ItemStack output) {
		ItemMeta meta = output.getItemMeta();
		if(meta.hasEnchants()) {
			boolean requiresCustomLore = false;
			for(Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
				final int lvl = entry.getValue();
				//int max = lvl;
				//Enchantment ench = entry.getKey();
				/*if(meta.hasEnchant(ench)) {
					int lvl2 = meta.getEnchantLevel(ench);
					if(lvl2 > max && lvl2 > ench.getMaxLevel())
						max = lvl2;
				} else if(meta instanceof EnchantmentStorageMeta) {
					EnchantmentStorageMeta ebook = (EnchantmentStorageMeta)meta;
					if(ebook.hasStoredEnchant(ench)) {
						int lvl2 = ebook.getStoredEnchantLevel(ench);
						if(lvl2 > max && lvl2 > ench.getMaxLevel())
							max = lvl2;
					}
				}*/
				
				if(lvl > 10 || lvl == 0)
					requiresCustomLore = true;
				/*			
				if(max > ench.getMaxLevel() && lvl != max)
					meta.addEnchant(ench, max, true);*/
			}
			if(requiresCustomLore) {
				List<String> lore = meta.hasLore()? meta.getLore() : new ArrayList<>();
				if(!lore.isEmpty()) {
					// remove existing lore
					for(Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
						// if (entry.getValue() > entry.getKey().getMaxLevel()) {
						StringBuilder enchName = new StringBuilder();
						enchName.append(enchantmentColor(entry.getKey()));
						enchName.append(TranslationRegistry.INSTANCE.translate(
								"enchantment.minecraft." + entry.getKey().getKey().getKey().toLowerCase()));
						String test = enchName.toString();
						for(int i = lore.size() - 1; i >= 0; i--) {
							String loreLine = lore.get(i);
							if(loreLine.startsWith(test)) {
								lore.remove(i);
							}
						}
					}
				}
				// Add lore
				List<Map.Entry<Enchantment, Integer>> entries = new ArrayList<>(meta.getEnchants().entrySet());
				entries.sort((entry1, entry2) -> entry2.getKey().getKey().getKey().compareTo(entry1.getKey().getKey().getKey()));
				for(Map.Entry<Enchantment, Integer> entry : entries) {
					// if (entry.getValue() > entry.getKey().getMaxLevel()) {
					if(entry.getValue() > 0) {
						StringBuilder enchName = new StringBuilder();
						enchName.append(enchantmentColor(entry.getKey()));
						enchantmentName(entry.getKey(), entry.getValue(), enchName);
					
						lore.add(0, enchName.toString());
					}
				}
				
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				meta.setLore(lore);
				output.setItemMeta(meta);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static ChatColor enchantmentColor(Enchantment ench) {
		return ench.isCursed()? ChatColor.RED : ChatColor.GRAY;
	}
	
	public static void enchantmentName(Enchantment ench, int level, StringBuilder enchName) {
		enchName.append(TranslationRegistry.INSTANCE.translate("enchantment.minecraft." + ench.getKey().getKey().toLowerCase()));
		if(ench.getMaxLevel() != 1)
			enchName.append(' ').append(StringUtils.toRomanNumerals(level));
	}
	
	/**
	 * Damages an item stack by 1 point. If the damage is too great, the item stack's
	 * count get set to 0.
	 * @param stack the stack to damage
	 * @return {@code true} if the stack got damaged
	 */
	public static boolean damage(ItemStack stack) {
		return damage(stack, 1);
	}
	
	/**
	 * Damages an item stack. If the damage is too great, the item stack's
	 * count get set to 0.
	 * @param stack the stack to damage
	 * @param amount the number of points to damage the durability by
	 * @return {@code true} if the stack got damaged
	 */
	public static boolean damage(ItemStack stack, int amount) {
		ItemMeta meta = stack.getItemMeta();
		if(meta instanceof Damageable) {
			Damageable damageable = (Damageable)meta;
			int dmg = damageable.getDamage()+amount;
			damageable.setDamage(dmg);
			if(dmg > stack.getType().getMaxDurability()) {
				stack.setAmount(0);
				stack.setType(AIR);
			}
			return true;
		}
		return false;
	}
	
	private ItemUtils() {
		throw new UnsupportedOperationException("ItemUtils cannot be instantiated!");
	}
}
