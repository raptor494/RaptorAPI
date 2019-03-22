package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.util.TypedList;

public class FireworkEffectTypeAdapter implements JsonDeserializer<FireworkEffect>, JsonSerializer<FireworkEffect> {

	@Override
	public FireworkEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return context.<InternalFireworkEffect>deserialize(json, InternalFireworkEffect.class).getFireworkEffect();
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(FireworkEffect src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(new InternalFireworkEffect(src), InternalFireworkEffect.class);
	}

}

class InternalFireworkEffect {
	TypedList<Color> colors, fadeColors;
	boolean flicker, trail;
	FireworkEffect.Type type;
	
	InternalFireworkEffect() {}
	
	InternalFireworkEffect(FireworkEffect effect) {
		colors = new TypedList<>(Color.class, effect.getColors());
		fadeColors = new TypedList<>(Color.class, effect.getFadeColors());
		flicker = effect.hasFlicker();
		trail = effect.hasTrail();
		type = effect.getType();
	}
	
	FireworkEffect getFireworkEffect() {
		FireworkEffect.Builder b = FireworkEffect.builder();
		if(colors != null)
			b.withColor(colors);
		if(fadeColors != null)
			b.withFade(fadeColors);
		return b.with(type)
				.flicker(flicker)
				.trail(trail)
				.build();
	}
}
