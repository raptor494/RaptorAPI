package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.util.TypedList;

import de.erethon.commons.item.AttributeWrapper;
import de.erethon.commons.item.InternalAttribute;
import de.erethon.commons.item.InternalOperation;
import de.erethon.commons.item.InternalSlot;

public class AttributeWrapperTypeAdapter implements JsonDeserializer<AttributeWrapper>, JsonSerializer<AttributeWrapper> {

	@Override
	public AttributeWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return context.<InternalAttributeWrapper>deserialize(json, InternalAttributeWrapper.class).getAttributeWrapper();
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(AttributeWrapper src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(new InternalAttributeWrapper(src), InternalAttributeWrapper.class);
	}
	
}

class InternalAttributeWrapper {
	String name;
	InternalAttribute attribute;
	InternalOperation operation;
	double amount;
	TypedList<InternalSlot> slots;
	
	InternalAttributeWrapper() {}
	
	InternalAttributeWrapper(AttributeWrapper wrapper) {
		name = wrapper.getName();
		attribute = wrapper.getAttribute();
		operation = wrapper.getOperation();
		amount = wrapper.getAmount();
		slots = new TypedList<>(InternalSlot.class, wrapper.getSlots());
	}
	
	AttributeWrapper getAttributeWrapper() {
		return new AttributeWrapper(attribute, name, amount, operation, slots);
	}
}
