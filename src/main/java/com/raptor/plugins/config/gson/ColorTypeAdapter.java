package com.raptor.plugins.config.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Color;
import org.bukkit.DyeColor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ColorTypeAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {
	protected static final Pattern RGB_REGEX = Pattern.compile("^rgb\\s*\\(\\s*(?<r>\\d+(_+\\d+)*)\\s*,\\s*(?<g>\\d+(_+\\d+)*)\\s*,\\s*(?<b>\\d+(_+\\d+)*)\\s*\\)$"),
			   COLOR_HEX_REGEX = Pattern.compile("^#?(?<hex>(\\p{XDigit}_*){5}\\p{XDigit})$");	
	
	@Override
	public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if(primitive.isString()) {
				String str = primitive.getAsString().trim();
				
				Matcher m;
				
				m = RGB_REGEX.matcher(str);
				if(m.matches()) {
					int r = Integer.parseUnsignedInt(m.group("r").replace("_", ""));
					int g = Integer.parseUnsignedInt(m.group("g").replace("_", ""));
					int b = Integer.parseUnsignedInt(m.group("b").replace("_", ""));
					return Color.fromRGB(r, g, b);
				}
				m = COLOR_HEX_REGEX.matcher(str);
				if(m.matches()) {
					int rgb = Integer.parseUnsignedInt(m.group("hex").replace("_", ""));
					return Color.fromRGB(rgb);
				}
				String origStr = str;
				str = str.toUpperCase();
				if(!origStr.equals(origStr.toLowerCase())) {
					throw new JsonParseException("Invalid color: " + origStr);
				}
				
				try {
					Field field = Color.class.getField(str);
					if(Modifier.isStatic(field.getModifiers()) && Color.class == field.getType()) {
						return (Color)field.get(null);
					}
				} catch(NoSuchFieldException | IllegalAccessException e) {}
				
				DyeColor color2;
				try {
					color2 = DyeColor.valueOf(str);
				} catch(IllegalArgumentException e) {
					throw new JsonParseException("Invalid color: " + origStr);
				}
				return color2.getColor();
			} else {
				return Color.fromRGB(primitive.getAsInt());
			}
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.asRGB());
	}

}
