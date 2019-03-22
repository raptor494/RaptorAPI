package com.raptor.plugins.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Represents a {@linkplain JsonObject JSON Object} as an instance of {@linkplain FileConfiguration}.
 */
public class JsonConfiguration extends FileConfiguration {
	private static JsonParser jsonParser = new JsonParser();
	private static final Gson DEFAULT_GSON = new Gson();
	
	private Gson gson = DEFAULT_GSON;
	
	public JsonConfiguration() {}
	
	public JsonConfiguration(Gson gson) {		
		if(gson != null) {
			this.gson = gson;
		}
	}
	
	public JsonConfiguration(JsonObject obj) {
		this(obj, null);
	}
	
	public JsonConfiguration(JsonObject obj, Gson gson) {
		this(gson);
		loadFromJsonObject(obj);
	}
	
	public void setGson(Gson newgson) {
		Validate.notNull(newgson, "gson may not be null");
		
		gson = newgson;
	}
	
	@Override
	public ItemStack getItemStack(String path) {
		return getItemStack(path, null);
	}
	
	@Override
	public ItemStack getItemStack(String path, ItemStack def) {
		if(!isConfigurationSection(path))
			return def;
		ConfigurationSection data = getConfigurationSection(path);
		return gson.fromJson(toJsonObject(data, gson), ItemStack.class);
	}
	
	public JsonObject getJsonObject(String path) {
		return getJsonObject(path, null);
	}
	
	public JsonObject getJsonObject(String path, JsonObject def) {
		if(!isConfigurationSection(path))
			return def;
		ConfigurationSection data = getConfigurationSection(path);
		return toJsonObject(data, gson);
	}
	
	public <T> T deserialize(String path, Class<T> type) {
		JsonObject obj = getJsonObject(path);
		if(obj == null)
			return null;
		return gson.fromJson(obj, type);
	}
	
	public JsonObject toJsonObject() {
		return toJsonObject(this, gson);
	}
	
	public static JsonObject toJsonObject(ConfigurationSection data, final Gson gson) {
		Validate.notNull(data);
		
		JsonObject object = new JsonObject();
		
		for(String key : data.getKeys(false)) {
			if(data.isList(key)) {
				object.add(key, toJsonArray(data.getList(key), gson));
			} else if(data.isConfigurationSection(key)) {
				object.add(key, toJsonObject(data.getConfigurationSection(key), gson));
			} else {
				object.add(key, toJsonElement(data.get(key), gson));
			}
		}
		return object;
	}
	
	private static JsonObject toJsonObject(Map<?,?> map, final Gson gson) {
		Validate.notNull(map);
		
		JsonObject object = new JsonObject();
		
		for(Entry<?,?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			object.add(String.valueOf(key), toJsonElement(value, gson));
		}
		
		return object;
	}
	
	private static JsonArray toJsonArray(List<?> data, final Gson gson) {
		Validate.notNull(data);
		
		JsonArray list = new JsonArray();
		
		for(Object element : data) {
			list.add(toJsonElement(element, gson));
		}
		
		return list;
	}
	
	private static JsonElement toJsonElement(Object obj, final Gson gson) {
		if(obj == null)
			return JsonNull.INSTANCE;
		else if(obj instanceof JsonElement)
			return (JsonElement)obj;
		else if(obj instanceof List)
			return toJsonArray((List<?>)obj, gson);
		else if(obj instanceof Map)
			return toJsonObject((Map<?,?>)obj, gson);
		else if(obj instanceof Boolean)
			return new JsonPrimitive((Boolean)obj);
		else if(obj instanceof Byte
				|| obj instanceof Short
				|| obj instanceof Integer
				|| obj instanceof Long
				|| obj instanceof Float
				|| obj instanceof Double)
			return new JsonPrimitive((Number)obj);
		else if(obj instanceof Number)
			return new JsonPrimitive((Number)obj);
		else if(obj instanceof String)
			return new JsonPrimitive((String)obj);
		else if(obj instanceof Character)
			return new JsonPrimitive((Character)obj);
		else return gson.toJsonTree(obj);
	}
	
	@Override
	public String saveToString() {
		JsonObject object = toJsonObject();
		StringWriter stringWriter = new StringWriter();
		JsonWriter writer;
		try {
			writer = gson.newJsonWriter(stringWriter);
		} catch (IOException e1) {
			throw new AssertionError();
		}
		writer.setSerializeNulls(false);
		writer.setIndent("\t");
		try {
			Streams.write(object, writer);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return stringWriter.toString();
		
	}

	@Override
	public void loadFromString(String contents) throws InvalidConfigurationException {
		if(contents.isEmpty())
			return;
		
		try {
			JsonReader reader = gson.newJsonReader(new StringReader(contents));
			JsonObject object = (JsonObject)jsonParser.parse(reader);
			
			loadFromJsonObject(object);
		} catch(JsonParseException e) {
			throw new InvalidConfigurationException(e);
		}
	}
	
	public void loadFromJsonObject(JsonObject obj) {
		mergeWithJsonObject(this, obj);
	}
	
	private void mergeWithJsonObject(ConfigurationSection data, JsonObject object) {
		for(Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if(value.isJsonObject()) {
				mergeWithJsonObject(data.isConfigurationSection(key)? data.getConfigurationSection(key) : data.createSection(key), value.getAsJsonObject());
			} else  {
				data.set(key, unwrapJsonElement(value));
			}
		}
	}
	
	public static List<?> unwrapJsonArray(JsonArray array) {
		List<Object> list = new ArrayList<>();
		
		for(JsonElement element : array) {
			list.add(unwrapJsonElement(element));
		}
		
		return list;
	}
	
	public static Object unwrapJsonPrimitive(JsonPrimitive primitive) {
		if(primitive.isNumber())
			return primitive.getAsNumber();
		else if(primitive.isBoolean())
			return primitive.getAsBoolean();
		else return primitive.getAsString();
	}
	
	public static Map<String, ?> unwrapJsonObject(JsonObject object) {
		Map<String, Object> map = new HashMap<>();
		
		for(Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			map.put(key, unwrapJsonElement(value));
		}
		
		return map;
	}
	
	public static Object unwrapJsonElement(JsonElement element) {
		if(element.isJsonPrimitive())
			return unwrapJsonPrimitive(element.getAsJsonPrimitive());
		else if(element.isJsonArray())
			return unwrapJsonArray(element.getAsJsonArray());
		else if(element.isJsonObject())
			return unwrapJsonObject(element.getAsJsonObject());
		else return null;
	}

	@Override
	protected String buildHeader() {
		return "";
	}
	
	public static JsonConfiguration loadConfiguration(File file) {
		return loadConfiguration(file, DEFAULT_GSON);
	}
	
	public static JsonConfiguration loadConfiguration(File file, Gson gson) {
		Validate.notNull(file, "File cannot be null");

		JsonConfiguration config = new JsonConfiguration(gson);

		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return config;
	}

	public static JsonConfiguration loadConfiguration(Reader reader) {
		return loadConfiguration(reader, DEFAULT_GSON);
	}

	public static JsonConfiguration loadConfiguration(Reader reader, Gson gson) {
		Validate.notNull(reader, "Stream cannot be null");

		JsonConfiguration config = new JsonConfiguration(gson);

		try {
			config.load(reader);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		}

		return config;
	}

}
