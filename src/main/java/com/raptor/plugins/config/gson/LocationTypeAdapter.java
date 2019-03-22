package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

	@Override
	public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			JsonObject object = json.getAsJsonObject();
			if(!object.has("world"))
				throw new JsonParseException("Cannot deserialize location, missing 'world' key");
			if(!object.has("pos"))
				throw new JsonParseException("Cannot deserialize location, missing 'pos' key");
			World world = context.deserialize(object.get("world"), World.class);
			double[] pos = context.deserialize(object.get("pos"), double[].class);
			if(pos.length != 3)
				throw new JsonParseException("Cannot deserialize location, 'pos' key has invalid length (must be 3)");
			float pitch = 0, yaw = 0;
			if(object.has("pitch")) {
				pitch = object.get("pitch").getAsJsonPrimitive().getAsFloat();
			}
			if(object.has("yaw")) {
				yaw = object.get("yaw").getAsJsonPrimitive().getAsFloat();
			}
			return new Location(world, pos[0], pos[1], pos[2], yaw, pitch);
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		JsonArray pos = new JsonArray();
		pos.add(src.getX());
		pos.add(src.getY());
		pos.add(src.getZ());
		obj.add("pos", pos);
		obj.addProperty("world", src.getWorld().getName());
		obj.addProperty("pitch", src.getPitch());
		obj.addProperty("yaw", src.getYaw());
		return obj;
	}

}
