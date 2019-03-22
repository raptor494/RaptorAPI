package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WorldTypeAdapter implements JsonSerializer<World>, JsonDeserializer<World> {

	@Override
	public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Server server = Bukkit.getServer();
		try {
			if(json.isJsonPrimitive()) {
				String name = json.getAsString();
				World world = server.getWorld(name);
				if(world == null)
					throw new JsonParseException("Unknown world: " + name);
				return world;
			} else {
				if(!json.isJsonObject())
					throw new JsonParseException("Cannot deserialize org.bukkit.World from " + json);
				JsonObject obj = json.getAsJsonObject();
				if(obj.has("name")) {
					String name = obj.get("name").getAsString();
					World world = server.getWorld(name);
					if(world == null)
						throw new JsonParseException("Unknown world: " + name);
					return world;
				}
				if(!obj.has("uuid"))
					throw new JsonParseException("Cannot deserialize org.bukkit.World, object is missing both name and uuid keys");
				UUID uuid = context.<UUID>deserialize(obj.get("uuid"), UUID.class);
				World world = server.getWorld(uuid);
				if(world == null)
					throw new JsonParseException("Unknown world: " + uuid);
				return world;
			}
		} catch(ClassCastException | IllegalStateException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getName());
	}

}
