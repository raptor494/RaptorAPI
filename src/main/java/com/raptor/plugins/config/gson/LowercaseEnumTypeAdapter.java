package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LowercaseEnumTypeAdapter<E extends Enum<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
	private final Class<E> type;
	private final String typeName;
	
	public LowercaseEnumTypeAdapter(Class<E> type, String name) {
		this.type = type;
		this.typeName = name;
	}
	
	public LowercaseEnumTypeAdapter(Class<E> type) {
		this(type, type.getSimpleName());
	}
	
	@Override
	public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String name = null;
		try {
			name = json.getAsString();
			if(!name.equals(name.toLowerCase()))
				throw new JsonParseException("Unknown " + typeName + ": " + name);
			return Enum.valueOf(type, name.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JsonParseException("Unknown " + typeName + ": " + name);
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(E src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.name().toLowerCase());
	}

}
