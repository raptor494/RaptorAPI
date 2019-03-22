package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.FireworkEffect;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class FireworkEffect$TypeTypeAdapter implements JsonDeserializer<FireworkEffect.Type>, JsonSerializer<FireworkEffect.Type> {

	@Override
	public FireworkEffect.Type deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String name = null;
		try {
			name = json.getAsString();
			if(name.equals("ball_large") || !name.equals(name.toLowerCase()))
				throw new JsonParseException("Unknown firework shape: " + name);
			if(name.equals("large_ball"))
				name = "ball_large";
			return FireworkEffect.Type.valueOf(name.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JsonParseException("Unknown firework shape: " + name);
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(FireworkEffect.Type src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
		if(src == FireworkEffect.Type.BALL_LARGE)
			return new JsonPrimitive("large_ball");
		return new JsonPrimitive(src.name().toLowerCase());
	}

}
