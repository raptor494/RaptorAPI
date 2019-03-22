package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.erethon.commons.item.InternalSlot;

public class InternalSlotTypeAdapter implements JsonDeserializer<InternalSlot>, JsonSerializer<InternalSlot> {

	@Override
	public InternalSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			String name = json.getAsString();
			InternalSlot result = InternalSlot.fromInternal(name);
			if(result == null)
				throw new JsonParseException("Unknown slot: " + name);
			return result;
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(InternalSlot src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getInternal());
	}

}
