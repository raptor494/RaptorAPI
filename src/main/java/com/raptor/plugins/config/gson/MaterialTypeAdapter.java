package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MaterialTypeAdapter implements JsonDeserializer<Material>, JsonSerializer<Material> {

	@Override
	public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		NamespacedKey key = context.deserialize(json, NamespacedKey.class);
		try {
			
			String name = key.getKey();
			if(!key.getNamespace().equals("minecraft")
					|| !name.equals(name.toLowerCase()))
				throw new JsonParseException("Unknown material: " + key);
			return Material.valueOf(name.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JsonParseException("Unknown material: " + key, e);
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Material src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.getKey(), NamespacedKey.class);
	}

}
