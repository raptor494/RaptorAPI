package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NamespacedKeyTypeAdapter implements JsonDeserializer<NamespacedKey>, JsonSerializer<NamespacedKey> {

	@Override
	@SuppressWarnings("deprecation")
	public NamespacedKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			String str = json.getAsString();
			int i = str.indexOf(':');
			if(i == -1) {
				return NamespacedKey.minecraft(str);
			} else {
				String namespace, name;
				namespace = str.substring(0, i);
				name = str.substring(i+1);
				return new NamespacedKey(namespace, name);
			}
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(NamespacedKey src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
