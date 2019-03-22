package com.raptor.plugins;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.erethon.commons.item.ItemUtil;

public class TexturedSkullOwner implements SkullOwner {
	UUID uuid;
	String texture;
	
	TexturedSkullOwner() {}
	
	public TexturedSkullOwner(UUID uuid, String texture) {
		Validate.notNull(uuid, "UUID may not be null");
		Validate.notNull(texture, "texture may not be null");
		this.uuid = uuid;
		this.texture = texture;
	}
	
	@Override
	public ItemStack applyTo(ItemStack item, SkullMeta meta) {
		item.setItemMeta(meta);
		item = ItemUtil.setSkullOwner(item, uuid, texture);
		return item;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getTexture() {
		return texture;
	}
	
	@Override
	public String toString() {
		return String.format("TexturedSkullOwner{uuid=%s,texture=%s}", uuid, texture);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof TexturedSkullOwner))
			return false;
		TexturedSkullOwner other = (TexturedSkullOwner) obj;
		return Objects.equals(uuid, other.uuid)
				&& Objects.equals(texture, other.texture);
	}
}