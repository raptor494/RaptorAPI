package com.raptor.plugins;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface SkullOwner {
	ItemStack applyTo(ItemStack item, SkullMeta meta);
}
