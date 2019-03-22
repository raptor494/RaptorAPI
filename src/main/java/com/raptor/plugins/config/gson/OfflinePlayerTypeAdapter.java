package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OfflinePlayerTypeAdapter implements JsonDeserializer<OfflinePlayer>, JsonSerializer<OfflinePlayer> {

	@Override
	@SuppressWarnings("deprecation")
	public OfflinePlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		Server server = Bukkit.getServer();
		try {
			if(json.isJsonPrimitive()) {
				String name = json.getAsString();
				if(name.indexOf('-') == -1)
					return server.getOfflinePlayer(name);
			}
			return server.getOfflinePlayer(context.<UUID>deserialize(json, UUID.class));
		} catch(ClassCastException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(OfflinePlayer src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getUniqueId().toString());
	}

}
