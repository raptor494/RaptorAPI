package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PatternTypeAdapter implements JsonDeserializer<Pattern>, JsonSerializer<Pattern> {

	@Override
	public JsonElement serialize(Pattern src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add("color", context.serialize(src.getColor()));
		obj.add("pattern", context.serialize(src.getPattern()));
		return obj;
	}

	@Override
	public Pattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			JsonObject obj = json.getAsJsonObject();
			if(!obj.has("color"))
				throw new JsonParseException("Cannot deserialize Pattern: missing 'color'");
			if(!obj.has("pattern"))
				throw new JsonParseException("Cannot deserialize Pattern: missing 'pattern'");
			DyeColor color = context.deserialize(obj.get("color"), DyeColor.class);
			PatternType pattern = context.deserialize(obj.get("pattern"), PatternType.class);
			return new Pattern(color, pattern);
		} catch(ClassCastException e) {
			throw new JsonParseException(e);
		}
	}

}
