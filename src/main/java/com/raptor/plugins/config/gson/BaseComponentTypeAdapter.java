package com.raptor.plugins.config.gson;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.raptor.plugins.config.JsonConfiguration;
import com.raptor.plugins.util.StringUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.Keybinds;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class BaseComponentTypeAdapter implements JsonDeserializer<BaseComponent> { 
	public static final Pattern SELECTOR_PATTERN = Pattern.compile(
			"^@[parse]($|\\[.*\\]$)");
	
	@Override
	public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			
			if(json.isJsonPrimitive()) {
				TextComponent text = new TextComponent(json.getAsString());
				return text;
			} else if(json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				TextComponent text = new TextComponent();
				for(JsonElement element : array) {
					text.addExtra(deserialize(element, BaseComponent.class, context));
				}
				return text;
			} else {
				JsonObject obj = json.getAsJsonObject();
				
				BaseComponent text;
				
				if(obj.has("text")) {
					text = new TextComponent(obj.get("text").getAsJsonPrimitive().getAsString()); 
					
				} else if(obj.has("translate")) {
					Object[] with;
					
					if(obj.has("with")) {
						with = JsonConfiguration.unwrapJsonArray(obj.get("with").getAsJsonArray()).toArray();
					} else {
						with = new Object[0];
					}
					
					text = new TranslatableComponent(obj.get("translate").getAsJsonPrimitive().getAsString(), with);
				} else if(obj.has("score")) {
					JsonObject score = obj.get("score").getAsJsonObject();
					
					if(score.has("objective")) {
						String objective = score.get("objective").getAsJsonPrimitive().getAsString();
						
						String name = score.has("name")? score.get("name").getAsJsonPrimitive().getAsString() : "*";
						
						String value = score.has("value")? score.get("value").getAsJsonPrimitive().getAsString() : "";
						
						text = new ScoreComponent(name, objective, value);
					} else {
						Bukkit.getLogger().log(Level.SEVERE, "Missing 'objective' in 'score'");
						text = new TextComponent();
					}
				} else if(obj.has("selector")) {
					String selector = obj.get("selector").getAsJsonPrimitive().getAsString();
					if(!isSelector(selector)) {
						Bukkit.getLogger().log(Level.WARNING, "The string '" + selector + "' may not be a proper selector argument for the selector text component");
					}
					text = new SelectorComponent(selector);
				} else if(obj.has("keybind")) {
					String keybind = obj.get("keybind").getAsJsonPrimitive().getAsString();
					if(!isKeybindName(keybind)) {
						Bukkit.getLogger().log(Level.WARNING, "The string '" + keybind + "' may not be a proper keybind argument for the keybind text component");
					}
					text = new KeybindComponent(keybind);
				} else {
					Bukkit.getLogger().log(Level.SEVERE, "Missing 'text', 'translate', 'score, 'selector', and/or 'keybind' values");
					text = new TextComponent();
				}
				
				if(obj.has("color")) {
					String colorName = obj.get("color").getAsString();
					if(StringUtils.isAllLowerCase(colorName)) {
						try {
							text.setColor(ChatColor.valueOf(colorName.toUpperCase()));
						} catch(IllegalArgumentException e) {
							Bukkit.getLogger().log(Level.WARNING, "Invalid color: '" + colorName + "'");
						}
					} else {
						 Bukkit.getLogger().log(Level.WARNING, "Invalid color: '" + colorName + "'");
					}					
				}
				if(obj.has("bold")) {
					text.setBold(obj.get("bold").getAsJsonPrimitive().getAsBoolean());
				}
				if(obj.has("italic")) {
					text.setItalic(obj.get("italic").getAsJsonPrimitive().getAsBoolean());
				}
				if(obj.has("underlined")) {
					text.setUnderlined(obj.get("underlined").getAsJsonPrimitive().getAsBoolean());
				}
				if(obj.has("strikethrough")) {
					text.setStrikethrough(obj.get("strikethrough").getAsJsonPrimitive().getAsBoolean());
				}
				if(obj.has("obfuscated")) {
					text.setObfuscated(obj.get("obfuscated").getAsJsonPrimitive().getAsBoolean());
				}
				if(obj.has("insertion")) {
					text.setInsertion(obj.get("insertion").getAsJsonPrimitive().getAsString());
				}
				if(obj.has("clickEvent")) {
					JsonObject clickEvent = obj.get("clickEvent").getAsJsonObject();
					if(clickEvent.has("action") && clickEvent.has("value")) {
						String value = clickEvent.get("value").getAsJsonPrimitive().getAsString();
						String actionName = clickEvent.get("action").getAsJsonPrimitive().getAsString();
						if(StringUtils.isAllLowerCase(actionName)) {
							try {
								ClickEvent.Action action = ClickEvent.Action.valueOf(actionName.toUpperCase());
								text.setClickEvent(new ClickEvent(action, value));
							} catch(IllegalArgumentException e) {
								Bukkit.getLogger().log(Level.SEVERE, "Invalid 'action' for 'clickEvent': '" + actionName + "'");
							}
						} else {
							Bukkit.getLogger().log(Level.SEVERE, "Invalid 'action' for 'clickEvent': '" + actionName + "'");
						}
					} else {
						Bukkit.getLogger().log(Level.SEVERE, "Missing 'action' or 'value' in 'clickEvent'");
					}
				}
				if(obj.has("hoverEvent")) {
					JsonObject hoverEvent = obj.get("hoverEvent").getAsJsonObject();
					if(hoverEvent.has("action") && hoverEvent.has("value")) {
						String actionName = hoverEvent.get("action").getAsJsonPrimitive().getAsString();
						if(StringUtils.isAllLowerCase(actionName)) {
							try {
								HoverEvent.Action action = HoverEvent.Action.valueOf(actionName.toUpperCase());
								BaseComponent[] value;
								JsonElement element = hoverEvent.get("value");
								switch(action) {
								case SHOW_ACHIEVEMENT:
									value = new BaseComponent[] { new TextComponent(element.getAsJsonPrimitive().getAsString()) };
									break;
								case SHOW_ENTITY:
									if(element.isJsonPrimitive()) {									
										value = new BaseComponent[] { new TextComponent(element.getAsJsonPrimitive().getAsString()) };
									} else {
										JsonObject valueObj = element.getAsJsonObject();
										String str;
										try {
											StringWriter stringWriter = new StringWriter();
											JsonWriter jsonWriter = new JsonWriter(stringWriter);
											jsonWriter.setIndent("");
											jsonWriter.setSerializeNulls(false);
											jsonWriter.setLenient(false);
											Streams.write(valueObj, jsonWriter);
											str = stringWriter.toString();
										} catch(IOException e) {
											throw new AssertionError(e);
										}
										value = new BaseComponent[] { new TextComponent(str) };
									}
									break;
								case SHOW_ITEM:
									if(element.isJsonPrimitive()) {									
										value = new BaseComponent[] { new TextComponent(element.getAsJsonPrimitive().getAsString()) };
									} else {
										JsonObject valueObj = element.getAsJsonObject();
										String str;
										try {
											StringWriter stringWriter = new StringWriter();
											JsonWriter jsonWriter = new JsonWriter(stringWriter);
											jsonWriter.setIndent("");
											jsonWriter.setSerializeNulls(false);
											jsonWriter.setLenient(false);
											Streams.write(valueObj, jsonWriter);
											str = stringWriter.toString();
										} catch(IOException e) {
											throw new AssertionError(e);
										}
										value = new BaseComponent[] { new TextComponent(str) };
									}
									break;
								case SHOW_TEXT:
									if(element.isJsonArray()) {
										JsonArray array = element.getAsJsonArray();
										value = new BaseComponent[array.size()];
										for(int i = 0; i < array.size(); i++) {
											value[i] = deserialize(array.get(i), BaseComponent.class, context);
										}
									} else {
										value = new BaseComponent[] { deserialize(element, BaseComponent.class, context) };
									}
									break;
								default:
									throw new AssertionError();
								}
								text.setHoverEvent(new HoverEvent(action, value));
							} catch(IllegalArgumentException e) {
								Bukkit.getLogger().log(Level.SEVERE, "Invalid 'action' for 'hoverEvent': '" + actionName + "'");
							}
						} else {
							Bukkit.getLogger().log(Level.SEVERE, "Invalid 'action' for 'hoverEvent': '" + actionName + "'");
						}
					} else {
						Bukkit.getLogger().log(Level.SEVERE, "Missing 'action' and/or 'value' in 'hoverEvent'");
					}
				}
				if(obj.has("extra")) {
					JsonArray extra = obj.get("extra").getAsJsonArray();
					for(JsonElement element : extra) {
						text.addExtra(deserialize(element, BaseComponent.class, context));
					}
				}
				return text;
			}
		} catch(ClassCastException | NullPointerException | IllegalStateException e) {
			throw new JsonParseException(e);
		}
	}

	public static boolean isSelector(String selector) {
		return SELECTOR_PATTERN.matcher(selector).matches();
	}
	
	public static boolean isKeybindName(String keybind) {
		if(StringUtils.isEmpty(keybind))
			return false;
		
		for(Field f : Keybinds.class.getFields()) {
			if(f.getType() == String.class) {
				try {
					if(keybind.equals(f.get(null)))
						return true;
				} catch(IllegalArgumentException | IllegalAccessException e) {
					Bukkit.getLogger().log(Level.SEVERE, "Error testing keybind on field " + f, e);
				}
			}
		}
		
		return false;
	}
}
