package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.OfflinePlayer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.PlayerSkullOwner;
import com.raptor.plugins.SkullOwner;
import com.raptor.plugins.TexturedSkullOwner;

public class SkullOwnerTypeAdapter implements JsonDeserializer<SkullOwner>, JsonSerializer<SkullOwner> {

	@Override
	public SkullOwner deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			if(json.isJsonPrimitive()) {
				return new PlayerSkullOwner(context.<OfflinePlayer>deserialize(json, OfflinePlayer.class));
			} else {
				return context.<TexturedSkullOwner>deserialize(json, TexturedSkullOwner.class);
			}
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(SkullOwner src, Type typeOfSrc, JsonSerializationContext context) {
		if(src instanceof PlayerSkullOwner) {
			return new JsonPrimitive(((PlayerSkullOwner)src).getOwningPlayer().getUniqueId().toString());
		} else {
			TexturedSkullOwner skullOwner = (TexturedSkullOwner)src;
			JsonObject obj = new JsonObject();
			obj.addProperty("uuid", skullOwner.getUniqueId().toString());
			obj.addProperty("texture", skullOwner.getTexture());
			return obj;
		}
	}

}
