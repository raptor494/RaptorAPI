package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;
import java.time.Duration;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PotionEffectTypeAdapter implements JsonDeserializer<PotionEffect>, JsonSerializer<PotionEffect> {

	@Override
	public PotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			JsonObject obj = json.getAsJsonObject();
			if(obj.has("level")) {
				int level = obj.get("level").getAsInt();
				if(level <= 0)
					throw new JsonParseException("Invalid potion effect level");
			}
		} catch(ClassCastException | NullPointerException e) {
			throw new JsonParseException(e);
		}
		
		try {
			return context.<InternalPotionEffect>deserialize(json, InternalPotionEffect.class).getPotionEffect();
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(PotionEffect src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(new InternalPotionEffect(src), InternalPotionEffect.class);
	}

}

class InternalPotionEffect {
	PotionEffectType id;
	Duration duration;
	int level;
	boolean ambient;
	boolean showParticles;
	
	InternalPotionEffect(PotionEffect effect) {
		id = effect.getType();
		duration = Duration.ofSeconds(effect.getDuration() / 20);
		level = effect.getAmplifier() + 1;
		ambient = effect.isAmbient();
		showParticles = effect.hasParticles();
	}
	
	InternalPotionEffect() {
		ambient = true;
		showParticles = true;
	}
	
	PotionEffect getPotionEffect() {
		int durationInTicks = duration == null? 60 : (int) (20 * duration.getSeconds());
		
		return new PotionEffect(id, durationInTicks, level - 1, ambient, showParticles);
	}
}
