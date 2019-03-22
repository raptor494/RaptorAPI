package com.raptor.plugins.config.gson;

import static java.lang.Long.parseLong;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DurationTypeAdapter implements JsonDeserializer<Duration>, JsonSerializer<Duration> {
	private static final Pattern PATTERN = Pattern.compile("^\\d+:\\d{2}(:\\d{2}){0,2}$");
	
	@Override
	public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if(primitive.isNumber()) {
				double amountInSeconds = primitive.getAsDouble();
				return Duration.ofNanos((long)(amountInSeconds * 1E9));
			} else {
				String str = primitive.getAsString();
				Matcher matcher = PATTERN.matcher(str);
				if(matcher.matches()) {
					String[] elems = str.split(":");
					Duration result = Duration.ZERO;
					long days = 0, hours = 0, minutes = 0, seconds = 0;
					switch(elems.length) {
					case 2:
						minutes = parseLong(elems[0]);
						seconds = parseLong(elems[1]);
						break;
					case 3:
						hours = parseLong(elems[0]);
						minutes = parseLong(elems[1]);
						seconds = parseLong(elems[2]);
						break;
					case 4:
						days = parseLong(elems[0]);
						hours = parseLong(elems[1]);
						minutes = parseLong(elems[2]);
						seconds = parseLong(elems[3]);
						break;
					}
					if(days != 0)
						result = result.plusDays(days);
					if(hours != 0)
						result = result.plusHours(hours);
					if(minutes != 0)
						result = result.plusMinutes(minutes);
					if(seconds != 0)
						result = result.plusSeconds(seconds);
					
					return result;
				} else {
					return Duration.parse(str);
				}
			}
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
