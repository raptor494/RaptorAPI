package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RegexTypeAdapter implements JsonDeserializer<Pattern>, JsonSerializer<Pattern> {

	@Override
	public Pattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return Pattern.compile(json.getAsJsonPrimitive().getAsString());
		} catch(PatternSyntaxException | IllegalStateException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Pattern src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.pattern());
	}

}
