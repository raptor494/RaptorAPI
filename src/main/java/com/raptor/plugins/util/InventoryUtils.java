package com.raptor.plugins.util;

import java.util.Collection;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {
	private InventoryUtils() {}
	
	/**
	 * Adds all the items into the player's inventory. If some items could
	 * not be added, drop them at the player's location.
	 * 
	 * @param player the player to give the items to
	 * @param items the items to add
	 * @return {@code true} if all items were added to the player's inventory,
	 * 		{@code false} if some were dropped.
	 */
	public static boolean addOrDropItems(Player player, ItemStack... items) {
		return doAddOrDropItems(player.getInventory(), player.getLocation(), items);
	}
	
	/**
	 * Adds all the items into the player's inventory. If some items could
	 * not be added, drop them at the player's location.
	 * 
	 * @param player the player to give the items to
	 * @param items the items to add
	 * @return {@code true} if all items were added to the player's inventory,
	 * 		{@code false} if some were dropped.
	 */
	public static boolean addOrDropItems(Player player, Collection<ItemStack> items) {
		return doAddOrDropItems(player.getInventory(), player.getLocation(), toItemStackArray(items));
	}
	
	/**
	 * Adds all the items into the player's inventory. If some items could
	 * not be added, drop them at the player's location.
	 * 
	 * @param inv the player inventory to add the items to
	 * @param items the items to add
	 * @return {@code true} if all items were added to the player's inventory,
	 * 		{@code false} if some were dropped.
	 */
	public static boolean addOrDropItems(PlayerInventory inv, ItemStack... items) {
		return doAddOrDropItems(inv, inv.getLocation(), items);
	}
	
	/**
	 * Adds all the items into the player's inventory. If some items could
	 * not be added, drop them at the player's location.
	 * 
	 * @param inv the player inventory to add the items to
	 * @param items the items to add
	 * @return {@code true} if all items were added to the player's inventory,
	 * 		{@code false} if some were dropped.
	 */
	public static boolean addOrDropItems(PlayerInventory inv, Collection<ItemStack> items) {
		return doAddOrDropItems(inv, inv.getLocation(), toItemStackArray(items));
	}
	
	private static boolean doAddOrDropItems(PlayerInventory inv, Location location, ItemStack[] items) {
		World world = location.getWorld();
		Collection<ItemStack> notAdded = inv.addItem(items).values();
		if(notAdded.isEmpty())
			return true;
		for(ItemStack item : notAdded) {
			world.dropItem(location, item);
		}
		return false;
	}
	
	public static boolean hasItems(Player player, ItemStack... items) {
		return hasItems(player.getInventory(), items);
	}
	
	public static boolean hasItems(Player player, Collection<ItemStack> items) {
		return hasItems(player.getInventory(), toItemStackArray(items));
	}
	
	public static boolean hasItems(PlayerInventory inv, ItemStack... items) {
		for(ItemStack item : items) {
			if(!hasItem(inv, item))
				return false;
		}
		return true;
	}
	
	public static boolean hasItems(PlayerInventory inv, Collection<ItemStack> items) {
		return hasItems(inv, toItemStackArray(items));
	}
	
	public static boolean hasItem(Player player, ItemStack item) {
		return hasItem(player.getInventory(), item);
	}
	
	public static boolean hasItem(PlayerInventory inv, ItemStack item) {
		ListIterator<ItemStack> iter = inv.iterator();
		int count_left = item.getAmount();
		while (iter.hasNext()) {
			ItemStack stack = iter.next();
			
			if (item.isSimilar(stack)) {
				if (count_left > 0) {
					count_left -= stack.getAmount();
				}
			}
		}
		return count_left <= 0;
	}
	
	public static boolean removeFromInventory(Player player, ItemStack... items) {
		return removeFromInventory(player.getInventory(), items);
	}
	
	public static boolean removeFromInventory(Player player, Collection<ItemStack> items) {
		return removeFromInventory(player.getInventory(), toItemStackArray(items));
	}
	
	public static boolean removeFromInventory(PlayerInventory inv, ItemStack... items) {
		if(!hasItems(inv, items))
			return false;
		for(ItemStack item : items) {
			doRemove(inv, item);
		}
		return true;
	}
	
	public static boolean removeFromInventory(PlayerInventory inv, Collection<ItemStack> items) {
		return removeFromInventory(inv, toItemStackArray(items));
	}
	
	public static boolean removeFromInventory(Player player, ItemStack item) {
		return removeFromInventory(player.getInventory(), item);
	}
	
	public static boolean removeFromInventory(PlayerInventory inv, ItemStack item) {
		if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0)
			return true;
		item = item.clone();
		if (hasItem(inv, item)) {
			doRemove(inv, item);
			return true;
		}
		return false;
	}
	
	/**
	 * Assumes the inventory already has the item and removes it.
	 * @param inv
	 * @param item
	 */
	private static void doRemove(PlayerInventory inv, ItemStack item) {
		int count_left = item.getAmount();
		ListIterator<ItemStack> iter = inv.iterator();
		while (iter.hasNext() && count_left > 0) {
			ItemStack stack = iter.next();
			if (item.isSimilar(stack)) {
				if (stack.getAmount() > count_left) {
					stack.setAmount(stack.getAmount() - count_left);
					count_left = 0;
				} else if (stack.getAmount() == count_left) {
					stack.setAmount(count_left = 0);
				} else {
					count_left -= stack.getAmount();
					stack.setAmount(0);
				}
			}
		}
	}
	
	private static ItemStack[] toItemStackArray(Collection<ItemStack> items) {
		return items.toArray(new ItemStack[items.size()]);
	}
}
