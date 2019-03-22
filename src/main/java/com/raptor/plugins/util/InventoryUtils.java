package com.raptor.plugins.util;

import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {
	private InventoryUtils() {}
	
	public static boolean hasItems(Player player, ItemStack... items) {
		for(ItemStack item : items) {
			if(!hasItem(player, item))
				return false;
		}
		return true;
	}
	
	public static boolean hasItem(Player player, ItemStack item) {
		PlayerInventory inv = player.getInventory();
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
		if(!hasItems(player, items))
			return false;
		PlayerInventory inv = player.getInventory();
		for(ItemStack item : items) {
			doRemove(inv, item);
		}
		return true;
	}
	
	public static boolean removeFromInventory(Player player, ItemStack item) {
		if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0)
			return true;
		item = item.clone();
		PlayerInventory inv = player.getInventory();
		/*ListIterator<ItemStack> iter = inv.iterator();
		int count_left = item.getAmount();
		while (iter.hasNext()) {
			ItemStack stack = iter.next();
			
			if (item.isSimilar(stack)) {
				if (count_left > 0) {
					count_left -= stack.getAmount();
				}
			}
		}*/
		if (hasItem(player, item)) {
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
	
}
