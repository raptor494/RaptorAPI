package com.raptor.plugins.config.gson;

import static org.bukkit.potion.PotionEffectType.*;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PotionEffectTypeTypeAdapter implements JsonDeserializer<PotionEffectType>, JsonSerializer<PotionEffectType> {
	private static final HashMap<String, PotionEffectType> byName;
	public static final HashMap<PotionEffectType, String> byType;
	
	@Override
	public PotionEffectType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		NamespacedKey key = context.deserialize(json, NamespacedKey.class);
		try {
			String name = key.getKey();
			if(!key.getNamespace().equals("minecraft"))
				throw new JsonParseException("Unknown potion effect: " + key);
			PotionEffectType type = byName.get(name);
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
	public JsonElement serialize(PotionEffectType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive("minecraft:" + byType.get(src));
	}

	static {
		byName = new HashMap<>();
		
		byName.put("speed", SPEED);
		byName.put("slowness", SLOW);
		byName.put("haste", FAST_DIGGING);
		byName.put("mining_fatigue", SLOW_DIGGING);
		byName.put("instant_health", HEAL);
		byName.put("instant_damage", HARM);
		byName.put("jump_boost", JUMP);
		byName.put("nausea", CONFUSION);
		byName.put("regeneration", REGENERATION);
		byName.put("resistance", DAMAGE_RESISTANCE);
		byName.put("fire_resistance", FIRE_RESISTANCE);
		byName.put("water_breathing", WATER_BREATHING);
		byName.put("invisibility", INVISIBILITY);
		byName.put("blindness", BLINDNESS);
		byName.put("night_vision", NIGHT_VISION);
		byName.put("hunger", HUNGER);
		byName.put("weakness", WEAKNESS);
		byName.put("posion", POISON);
		byName.put("wither", WITHER);
		byName.put("health_boost", HEALTH_BOOST);
		byName.put("absorption", ABSORPTION);
		byName.put("saturation", SATURATION);
		byName.put("glowing", GLOWING);
		byName.put("levitation", LEVITATION);
		byName.put("luck", LUCK);
		byName.put("bad_luck", UNLUCK);
		byName.put("slow_falling", SLOW_FALLING);
		byName.put("dolphins_grace", DOLPHINS_GRACE);
		
		byType = new HashMap<>();
		
		byType.put(SPEED, "speed");
		byType.put(SLOW, "slowness");
		byType.put(FAST_DIGGING, "haste");
		byType.put(SLOW_DIGGING, "mining_fatigue");
		byType.put(HEAL, "instant_health");
		byType.put(HARM, "instant_damage");
		byType.put(JUMP, "jump_boost");
		byType.put(CONFUSION, "nausea");
		byType.put(REGENERATION, "regeneration");
		byType.put(DAMAGE_RESISTANCE, "resistance");
		byType.put(FIRE_RESISTANCE, "fire_resistance");
		byType.put(WATER_BREATHING, "water_breathing");
		byType.put(INVISIBILITY, "invisibility");
		byType.put(BLINDNESS, "blindness");
		byType.put(NIGHT_VISION, "night_vision");
		byType.put(HUNGER, "hunger");
		byType.put(WEAKNESS, "weakness");
		byType.put(POISON, "posion");
		byType.put(WITHER, "wither");
		byType.put(HEALTH_BOOST, "health_boost");
		byType.put(ABSORPTION, "absorption");
		byType.put(SATURATION, "saturation");
		byType.put(GLOWING, "glowing");
		byType.put(LEVITATION, "levitation");
		byType.put(LUCK, "luck");
		byType.put(UNLUCK, "bad_luck");
		byType.put(SLOW_FALLING, "slow_falling");
		byType.put(DOLPHINS_GRACE, "dolphins_grace");
	}
}
