package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PotionDataTypeAdapter implements JsonDeserializer<PotionData>, JsonSerializer<PotionData> {

	@Override
	public PotionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return context.<InternalPotionData>deserialize(json, InternalPotionData.class).getPotionData();
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(PotionData src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(new InternalPotionData(src), InternalPotionData.class);
	}

}

class InternalPotionData {
	PotionType type;
	boolean extended, upgraded;
	
	InternalPotionData() {}
	
	InternalPotionData(PotionData data) {
		type = data.getType();
		extended = data.isExtended();
		upgraded = data.isUpgraded();
	}
	
	PotionData getPotionData() {
		return new PotionData(type, extended, upgraded);
	}
}