package com.raptor.plugins;

import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerSkullOwner implements SkullOwner {
	OfflinePlayer owningPlayer;
	
	PlayerSkullOwner() {}
	
	public PlayerSkullOwner(OfflinePlayer playerIn){
		Validate.notNull(playerIn, "Owning player may not be null");
		owningPlayer = playerIn;
	}
	
	public OfflinePlayer getOwningPlayer() {
		return owningPlayer;
	}

	@Override
	public ItemStack applyTo(ItemStack item, SkullMeta meta) {
		meta.setOwningPlayer(owningPlayer);
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public String toString() {
		return String.format("PlayerSkullOwner{owningPlayer=%s}", owningPlayer == null? "null" : String.format("(name=%s,uuid=%s)", owningPlayer.getName(), owningPlayer.getUniqueId()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof PlayerSkullOwner))
			return false;
		PlayerSkullOwner other = (PlayerSkullOwner) obj;
		if(owningPlayer == null)
			return other.owningPlayer == null;
		if(other.owningPlayer == null)
			return false;
		return owningPlayer.getUniqueId().equals(other.owningPlayer.getUniqueId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(owningPlayer);
	}
}