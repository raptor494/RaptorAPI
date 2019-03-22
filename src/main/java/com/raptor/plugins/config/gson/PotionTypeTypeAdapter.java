package com.raptor.plugins.config.gson;

import static org.bukkit.potion.PotionType.*;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PotionTypeTypeAdapter implements JsonDeserializer<PotionType>, JsonSerializer<PotionType> {
	private static final HashMap<String, PotionType> byName;
	private static final EnumMap<PotionType, String> byType;
	
	@Override
	public PotionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		NamespacedKey key = context.deserialize(json, NamespacedKey.class);
		try {
			String name = key.getKey();
			if(!key.getNamespace().equals("minecraft"))
				throw new JsonParseException("Unknown potion effect: " + key);
			PotionType type = byName.get(name);
			if(type == null)
				throw new JsonParseException("Unknown potion effect: " + key);
			return type;
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}
	
	@Override
	public JsonElement serialize(PotionType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive("minecraft:" + byType.get(src));
	}

	static {
		byName = new HashMap<>();
		
		byName.put("uncraftable", UNCRAFTABLE);
		byName.put("water", WATER);
		byName.put("mundane", MUNDANE);
		byName.put("thick", THICK);
		byName.put("awkward", AWKWARD);
		byName.put("night_vision", NIGHT_VISION);
		byName.put("invisibility", INVISIBILITY);
		byName.put("leaping", JUMP);
		byName.put("fire_resistance", FIRE_RESISTANCE);
		byName.put("swiftness", SPEED);
		byName.put("slowness", SLOWNESS);
		byName.put("water_breathing", WATER_BREATHING);
		byName.put("healing", INSTANT_HEAL);
		byName.put("harming", INSTANT_DAMAGE);
		byName.put("poison", POISON);
		byName.put("regeneration", REGEN);
		byName.put("strength", STRENGTH);
		byName.put("weakness", WEAKNESS);
		byName.put("luck", LUCK);
		byName.put("turtle_master", TURTLE_MASTER);
		byName.put("slow_falling", SLOW_FALLING);
		
		byType = new EnumMap<>(PotionType.class);
		
		byType.put(UNCRAFTABLE, "uncraftable");
		byType.put(WATER, "water");
		byType.put(MUNDANE, "mundane");
		byType.put(THICK, "thick");
		byType.put(AWKWARD, "awkward");
		byType.put(NIGHT_VISION, "night_vision");
		byType.put(INVISIBILITY, "invisibility");
		byType.put(JUMP, "leaping");
		byType.put(FIRE_RESISTANCE, "fire_resistance");
		byType.put(SPEED, "swiftness");
		byType.put(SLOWNESS, "slowness");
		byType.put(WATER_BREATHING, "water_breathing");
		byType.put(INSTANT_HEAL, "healing");
		byType.put(INSTANT_DAMAGE, "harming");
		byType.put(POISON, "poison");
		byType.put(REGEN, "regeneration");
		byType.put(STRENGTH, "strength");
		byType.put(WEAKNESS, "weakness");
		byType.put(LUCK, "luck");
		byType.put(TURTLE_MASTER, "turtle_master");
		byType.put(SLOW_FALLING, "slow_falling");
	}
}
