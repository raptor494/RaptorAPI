package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EnchantmentTypeAdapter implements JsonDeserializer<Enchantment>, JsonSerializer<Enchantment> {

	@Override
	public Enchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			NamespacedKey key = context.deserialize(json, NamespacedKey.class);
			Enchantment result = Enchantment.getByKey(key);
			if(result == null)
				throw new JsonParseException("No such enchantment: " + key);
			return result;
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Enchantment src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.getKey(), NamespacedKey.class);
	}

}
