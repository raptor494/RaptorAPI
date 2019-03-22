package com.raptor.plugins.config.gson;

import java.lang.reflect.Type;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.ItemStackModel;
import com.raptor.plugins.api.RaptorAPI;

public class ItemStackTypeAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			if(json.isJsonPrimitive()) {
				String name = json.getAsString();
				ItemStack customItem = RaptorAPI.getInstance().getCustomItem(name);
				if(customItem == null)
					throw new JsonParseException("Unknown custom item: " + name);
				return customItem;
			}
			/*JsonObject object = json.getAsJsonObject();
			if(object.has("customItem")) {
				String name = object.get("customItem").getAsJsonPrimitive().getAsString();
				ItemStack customItem = RaptorAPI.getInstance().getCustomItem(name);
				if(customItem == null)
					throw new JsonParseException("Unknown custom item: " + name);
				if(object.has("count")) {
					customItem.setAmount(object.get("count").getAsJsonPrimitive().getAsInt());
				}
				return customItem;
			}*/
			return context.<ItemStackModel>deserialize(json, ItemStackModel.class).getItemStack();
		} catch(JsonParseException e) {
			throw e;
		} catch(RuntimeException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(new ItemStackModel(src), ItemStackModel.class);
	}

}
