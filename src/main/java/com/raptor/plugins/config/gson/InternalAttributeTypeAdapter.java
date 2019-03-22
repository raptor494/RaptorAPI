package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.erethon.commons.item.InternalAttribute;

public class InternalAttributeTypeAdapter implements JsonDeserializer<InternalAttribute>, JsonSerializer<InternalAttribute> {

	@Override
	public InternalAttribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			String name = json.getAsString();
			InternalAttribute result = InternalAttribute.fromInternal(name);
			if(result == null)
				throw new JsonParseException("Unknown attribute: " + name);
			return result;
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(InternalAttribute src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getInternal());
	}

}
