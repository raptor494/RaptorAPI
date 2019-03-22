package com.raptor.plugins;

import static java.util.logging.Level.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.raptor.plugins.config.JsonConfiguration;
import com.raptor.plugins.config.PropertiesConfiguration;
import com.raptor.plugins.config.gson.AttributeWrapperTypeAdapter;
import com.raptor.plugins.config.gson.BaseComponentTypeAdapter;
import com.raptor.plugins.config.gson.ColorTypeAdapter;
import com.raptor.plugins.config.gson.DurationTypeAdapter;
import com.raptor.plugins.config.gson.EnchantmentTypeAdapter;
import com.raptor.plugins.config.gson.EnumMapTypeAdapterFactory;
import com.raptor.plugins.config.gson.FireworkEffect$TypeTypeAdapter;
import com.raptor.plugins.config.gson.FireworkEffectTypeAdapter;
import com.raptor.plugins.config.gson.InternalAttributeTypeAdapter;
import com.raptor.plugins.config.gson.InternalSlotTypeAdapter;
import com.raptor.plugins.config.gson.ItemStackTypeAdapter;
import com.raptor.plugins.config.gson.LocationTypeAdapter;
import com.raptor.plugins.config.gson.LowercaseEnumTypeAdapter;
import com.raptor.plugins.config.gson.MaterialTypeAdapter;
import com.raptor.plugins.config.gson.NamespacedKeyTypeAdapter;
import com.raptor.plugins.config.gson.OfflinePlayerTypeAdapter;
import com.raptor.plugins.config.gson.PatternTypeAdapter;
import com.raptor.plugins.config.gson.PotionDataTypeAdapter;
import com.raptor.plugins.config.gson.PotionEffectTypeAdapter;
import com.raptor.plugins.config.gson.PotionEffectTypeTypeAdapter;
import com.raptor.plugins.config.gson.PotionTypeTypeAdapter;
import com.raptor.plugins.config.gson.RegexTypeAdapter;
import com.raptor.plugins.config.gson.SkullOwnerTypeAdapter;
import com.raptor.plugins.config.gson.SoundTypeAdapter;
import com.raptor.plugins.config.gson.TypedListTypeAdapter;
import com.raptor.plugins.config.gson.TypedMapTypeAdapter;
import com.raptor.plugins.config.gson.TypedSetTypeAdapter;
import com.raptor.plugins.config.gson.WorldTypeAdapter;
import com.raptor.plugins.util.TypedList;
import com.raptor.plugins.util.TypedMap;
import com.raptor.plugins.util.TypedSet;

import de.erethon.commons.item.AttributeWrapper;
import de.erethon.commons.item.InternalAttribute;
import de.erethon.commons.item.InternalOperation;
import de.erethon.commons.item.InternalSlot;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * @author flyin
 *
 */
public abstract class RaptorPlugin extends JavaPlugin {
	public static final UUID uuidOfRaptor__ = UUID.fromString("c3e6871e-8e60-490a-8a8d-2bbe35ad1604");
	private FileConfiguration config;
	private Object configObject;
	private Type configClass;
	private File configFile;
	private final ConfigType configType;
	protected final Gson gson;
	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	protected final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);
	protected YamlConfiguration plugin_yml;
	public final Server server = null;
	public final BukkitScheduler scheduler = null;
	
	private static final Field field_server, field_scheduler;
	
	static {
		try {
			field_server = RaptorPlugin.class.getDeclaredField("server");
			field_scheduler = RaptorPlugin.class.getDeclaredField("scheduler");
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_server.setAccessible(true);
			field_scheduler.setAccessible(true);
			field_modifiers.setAccessible(true);
			field_modifiers.set(field_server, field_server.getModifiers() & ~Modifier.FINAL);
			field_modifiers.set(field_scheduler, field_scheduler.getModifiers() & ~Modifier.FINAL);
		} catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
	
	private void setServer() {
		try {
			field_server.set(this, getServer());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
	
	private void setScheduler() {
		try {
			field_scheduler.set(this, getServer().getScheduler());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e);
		} catch(NullPointerException e) {}
	}
	
	protected enum ConfigType {
		JSON, YAML, PROPERTIES, CLASS
	}

	public RaptorPlugin() {
		this(ConfigType.YAML);
	}

	public RaptorPlugin(ConfigType configType) {
		this.configType = configType;
		switch(configType) {
		case JSON:
			this.configFile = getFile("config.json");
			break;
		case YAML:
			this.configFile = getFile("config.yml");
			break;
		case PROPERTIES:
			this.configFile = getFile("config.properties");
			break;
		case CLASS:
			throw new IllegalArgumentException("in order for config type to be CLASS, a Class must be given");
		default:
			throw new AssertionError();
		}
		setServer();
		setScheduler();
		gson = createGson();
		plugin_yml = loadPluginYml();
	}

	/**
	 * @param configType the type of the object loaded by the internal GSON and
	 *                       returned by {@link #getConfigObject()}
	 */
	public RaptorPlugin(Type configType) {
		Validate.notNull(configType, "configType may not be null");
		this.configFile = getFile("config.json");
		this.configType = ConfigType.CLASS;
		this.configClass = configType;
		setServer();
		setScheduler();
		gson = createGson();
		plugin_yml = loadPluginYml();
	}
	
	protected final YamlConfiguration loadPluginYml() {
		try(Reader reader = new InputStreamReader(getClass().getResourceAsStream("/plugin.yml"))) {
			return YamlConfiguration.loadConfiguration(reader);
		} catch(IOException e) {
			return null;
		}
	}

	public Gson getGson() {
		return gson;
	}

	public static GsonBuilder addGsonTypeAdapters(GsonBuilder builder) {
		return builder // @formatter:off
				.registerTypeAdapter(
						AttributeWrapper.class, 
						new AttributeWrapperTypeAdapter())
				.registerTypeAdapter(
						BaseComponent.class, 
						new BaseComponentTypeAdapter())
				.registerTypeAdapter(
						BookMeta.Generation.class, 
						new LowercaseEnumTypeAdapter<>(BookMeta.Generation.class, "book generation"))
				.registerTypeAdapter(
						net.md_5.bungee.api.ChatColor.class, 
						new LowercaseEnumTypeAdapter<>(net.md_5.bungee.api.ChatColor.class))
				.registerTypeAdapter(
						org.bukkit.ChatColor.class, 
						new LowercaseEnumTypeAdapter<>(org.bukkit.ChatColor.class))
				.registerTypeAdapter(
						Color.class,
						new ColorTypeAdapter())
				.registerTypeAdapter(
						Duration.class, 
						new DurationTypeAdapter())
				.registerTypeAdapter(
						DyeColor.class, 
						new LowercaseEnumTypeAdapter<>(DyeColor.class, "dye color"))
				.registerTypeAdapter(
						Enchantment.class,
						new EnchantmentTypeAdapter())
				.registerTypeAdapter(
						FireworkEffect.class, 
						new FireworkEffectTypeAdapter())
				.registerTypeAdapter(
						FireworkEffect.Type.class,
						new FireworkEffect$TypeTypeAdapter())
				.registerTypeAdapter(
						InternalAttribute.class, 
						new InternalAttributeTypeAdapter())
				.registerTypeAdapter(
						InternalOperation.class, 
						new LowercaseEnumTypeAdapter<>(InternalOperation.class, "operation"))
				.registerTypeAdapter(
						InternalSlot.class, 
						new InternalSlotTypeAdapter())
				.registerTypeAdapter(
						ItemStack.class, 
						new ItemStackTypeAdapter())
				.registerTypeAdapter(
						Location.class, 
						new LocationTypeAdapter())
				.registerTypeAdapter(
						Material.class, 
						new MaterialTypeAdapter())
				.registerTypeAdapter(
						NamespacedKey.class,
						new NamespacedKeyTypeAdapter())
				.registerTypeAdapter(
						OfflinePlayer.class, 
						new OfflinePlayerTypeAdapter())
				.registerTypeAdapter(
						org.bukkit.block.banner.Pattern.class, 
						new PatternTypeAdapter())
				.registerTypeAdapter(
						java.util.regex.Pattern.class, 
						new RegexTypeAdapter())
				.registerTypeAdapter(
						PatternType.class, 
						new LowercaseEnumTypeAdapter<>(PatternType.class))
				.registerTypeAdapter(
						PotionData.class, 
						new PotionDataTypeAdapter())
				.registerTypeAdapter(
						PotionEffect.class, 
						new PotionEffectTypeAdapter())
				.registerTypeAdapter(
						PotionEffectType.class, 
						new PotionEffectTypeTypeAdapter())
				.registerTypeAdapter(
						PotionType.class, 
						new PotionTypeTypeAdapter())
				.registerTypeAdapter(
						SkullOwner.class,
						new SkullOwnerTypeAdapter())
				.registerTypeAdapter(
						Sound.class, 
						new SoundTypeAdapter())
				.registerTypeAdapter(
						TropicalFish.Pattern.class,
						new LowercaseEnumTypeAdapter<>(TropicalFish.Pattern.class, "tropical fish pattern"))
				.registerTypeAdapter(
						TypedList.class, 
						new TypedListTypeAdapter())
				.registerTypeAdapter(
						TypedSet.class, 
						new TypedSetTypeAdapter())
				.registerTypeAdapter(
						TypedMap.class, 
						new TypedMapTypeAdapter())
				.registerTypeAdapter(
						World.class, 
						new WorldTypeAdapter())
				.registerTypeAdapterFactory(new EnumMapTypeAdapterFactory())
				.registerTypeAdapterFactory(new ItemStackModelTypeAdapterFactory());
		// @formatter:on
	}

	private Gson createGson() {
		return addGsonTypeAdapters(setupGson(new GsonBuilder())).create();
	}

	protected GsonBuilder setupGson(GsonBuilder builder) {
		return builder
				.disableHtmlEscaping()
				.serializeSpecialFloatingPointValues()
				.setPrettyPrinting()
				.setLenient();
	}

	@Override
	public void onLoad() {
		setServer();
		setScheduler();
	}

	@Override
	public void onEnable() {
		setServer();
		setScheduler();
		saveDefaultConfig();
		reloadConfig();
		for(Entry<String, Supplier<CommandExecutor>> entry : commands.entrySet()) {
			String name = entry.getKey();
			CommandExecutor executor = entry.getValue().get();
			PluginCommand command = getCommand(name);
			Validate.notNull(command, "No such command: " + name);
			command.setExecutor(executor);

			if(executor instanceof TabCompleter)
				command.setTabCompleter((TabCompleter) executor);
		}
		for(Supplier<Listener> listenerSupplier : listeners) {
			getPluginManager().registerEvents(listenerSupplier.get(), this);
		}
	}

	public final PluginManager getPluginManager() {
		return getServer().getPluginManager();
	}

	public final BukkitScheduler getScheduler() {
		return getServer().getScheduler();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

	private Set<Supplier<Listener>> listeners = new HashSet<>();

	public final void registerListener(Class<? extends Listener> listenerClass) {
		listeners.add(new ListenerLoader(listenerClass));
	}

	public final void registerListener(Listener l) {
		listeners.add(new ListenerSupplier(l));
	}

	@SuppressWarnings("unchecked")
	public final void registerListener(Supplier<? extends Listener> listenerSupplier) {
		Validate.notNull(listenerSupplier, "listener supplier may not be null");
		listeners.add((Supplier<Listener>) listenerSupplier);
	}

	private static class ListenerSupplier implements Supplier<Listener> {
		private final Listener listener;

		private ListenerSupplier(Listener listener) {
			Validate.notNull(listener, "listener may not be null");
			this.listener = listener;
		}

		@Override
		public Listener get() {
			return listener;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(listener);
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(!(obj instanceof ListenerSupplier))
				return false;
			return listener.equals(((ListenerSupplier) obj).listener);
		}
	}

	private class ListenerLoader implements Supplier<Listener> {
		private final Class<? extends Listener> listenerClass;

		private ListenerLoader(Class<? extends Listener> listenerClass) {
			Validate.notNull(listenerClass, "type may not be null");
			Validate.isTrue(Listener.class.isAssignableFrom(listenerClass),
					"type does not implement org.bukkit.event.Listener");
			this.listenerClass = listenerClass;
		}

		@Override
		public Listener get() {
			return loadFromClassWithThis(listenerClass);
		}

		@Override
		public int hashCode() {
			return listenerClass.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(!(obj instanceof ListenerLoader))
				return false;
			ListenerLoader l = (ListenerLoader) obj;
			return listenerClass == l.listenerClass
					&& getOwner() == l.getOwner();
		}

		private RaptorPlugin getOwner() {
			return RaptorPlugin.this;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T loadFromClassWithThis(Class<T> type) {
		if(type == getClass()) {
			return (T) RaptorPlugin.this;
		} else {
			Constructor<T> csttr;
			try {
				csttr = type.getConstructor(getClass());
				try {
					return csttr.newInstance(RaptorPlugin.this);
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					getLogger().log(SEVERE, "Could not construct new instance of " + type, e);
				}
			} catch(NoSuchMethodException e) {
				try {
					csttr = type.getConstructor(new Class[0]);
					try {
						return csttr.newInstance();
					} catch(InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e1) {
						getLogger().log(SEVERE, "Could not construct new instance of " + type, e1);
					}
				} catch(NoSuchMethodException e1) {
					getLogger().log(SEVERE, "Could not find valid constructor for " + type, e);
				}
			}
		}
		return null;
	}

	private HashMap<String, Supplier<CommandExecutor>> commands = new HashMap<>();

	public final void registerCommand(String name, CommandExecutor executor) {
		Validate.notNull(name, "command name may not be null");
		if(commands.containsKey(name)) {
			getLogger().warning("A command by the name '" + name
					+ "' has already been registered, original value will be overwritten");
		}
		commands.put(name, new CommandSupplier(executor));
		
		String specialUsageKey = "commands." + name + ".special-usage";
		if(plugin_yml.isString(specialUsageKey)) {
			final String specialUsage = plugin_yml.getString(specialUsageKey);
			String specialPermissionKey = "commands." + name + ".special-permission";
			final String specialPermission = plugin_yml.getString(specialPermissionKey);
			Bukkit.getHelpMap().registerHelpTopicFactory(executor.getClass(), (command) -> {
				return new PermissionBasedCommandHelpTopic(command, specialUsage, specialPermission);
			});
		}
	}

	public final void registerCommand(String name, Class<? extends CommandExecutor> commandClass) {
		Validate.notNull(name, "command name may not be null");
		if(commands.containsKey(name)) {
			getLogger().warning("A command by the name '" + name
					+ "' has already been registered, original value will be overwritten");
		}
		commands.put(name, new CommandLoader(commandClass));
		
		String specialUsageKey = "commands." + name + ".special-usage";
		if(plugin_yml.isString(specialUsageKey)) {
			final String specialUsage = plugin_yml.getString(specialUsageKey);
			String specialPermissionKey = "commands." + name + ".special-permission";
			final String specialPermission = plugin_yml.getString(specialPermissionKey);
			Bukkit.getHelpMap().registerHelpTopicFactory(commandClass, (command) -> {
				return new PermissionBasedCommandHelpTopic(command, specialUsage, specialPermission);
			});
		}
	}

	/*@SuppressWarnings("unchecked")
	public final void registerCommand(String name, Supplier<? extends CommandExecutor> commandSupplier) {
		Validate.notNull(name, "command name may not be null");
		Validate.notNull(commandSupplier, "command supplier may not be null");
		if(commands.containsKey(name)) {
			getLogger().warning("A command by the name '" + name
					+ "' has already been registered, original value will be overwritten");
		}
		commands.put(name, (Supplier<CommandExecutor>) commandSupplier);
	}*/

	private static class CommandSupplier implements Supplier<CommandExecutor> {
		private final CommandExecutor command;

		private CommandSupplier(CommandExecutor command) {
			Validate.notNull(command, "command executor may not be null");
			this.command = command;
		}

		@Override
		public CommandExecutor get() {
			return command;
		}
	}

	private class CommandLoader implements Supplier<CommandExecutor> {
		private final Class<? extends CommandExecutor> commandClass;

		private CommandLoader(Class<? extends CommandExecutor> commandClass) {
			Validate.notNull(commandClass, "type may not be null");
			Validate.isTrue(CommandExecutor.class.isAssignableFrom(commandClass),
					"type does not implement org.bukkit.command.CommandExecutor");
			this.commandClass = commandClass;
		}

		@Override
		public CommandExecutor get() {
			return loadFromClassWithThis(commandClass);
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	@Override
	public FileConfiguration getConfig() {
		if(config == null) {
			reloadConfig();
		}
		return config;
	}

	public Object getConfigObject() {
		if(configType != ConfigType.CLASS)
			throw new NoSuchElementException("config type is not CLASS");
		if(configObject == null) {
			reloadConfig();
		}
		return configObject;
	}

	@Override
	public void saveDefaultConfig() {
		if(!configFile.exists()) {
			try {
				saveResource(getConfigFile().getName(), false);
			} catch(IllegalArgumentException e) {
			}
		}
	}

	@Override
	public void reloadConfig() {
		switch(configType) {
		case YAML: {
			config = YamlConfiguration.loadConfiguration(getConfigFile());

			try(InputStream defConfigStream = getResource("config.yml")) {
				if(defConfigStream != null)
					config.setDefaults(YamlConfiguration
							.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
			} catch(IOException e) {
				getLogger().log(WARNING, "IOException upon closing stream", e);
			} catch(IllegalArgumentException e) {
			}

			break;
		}
		case PROPERTIES: {
			config = PropertiesConfiguration.loadConfiguration(getConfigFile());

			try(InputStream defConfigStream = getResource("config.properties")) {
				if(defConfigStream != null)
					config.setDefaults(PropertiesConfiguration
							.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
			} catch(IOException e) {
				getLogger().log(WARNING, "IOException upon closing stream", e);
			} catch(IllegalArgumentException e) {
			}

			break;
		}
		case CLASS:
		case JSON: {
			config = JsonConfiguration.loadConfiguration(getConfigFile(), gson);

			try(InputStream defConfigStream = getResource("config.json")) {
				if(defConfigStream != null)
					config.setDefaults(JsonConfiguration
							.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8), gson));
			} catch(IOException e) {
				getLogger().log(WARNING, "IOException upon closing stream", e);
			} catch(IllegalArgumentException e) {
			}

			break;
		}
		default:
			throw new AssertionError();
		}

		if(configType == ConfigType.CLASS) {
			if(configClass instanceof Class) {
				configObject = gson.fromJson(((JsonConfiguration) config).toJsonObject(), (Class<?>) configClass);
			} else {
				configObject = gson.fromJson(((JsonConfiguration) config).toJsonObject(), configClass);
			}
		}
	}

	public void onReloadStart() {
	}

	public void onReloadFinish() {
	}

	public final void reload() {
		onReloadStart();
		onDisable();
		onEnable();
		onReloadFinish();
	}

	public final File getFile(String name) {
		return new File(getDataFolder(), name);
	}

	/**
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded Map or an empty HashMap if there was an IOException
	 *         reading the file.
	 * 
	 * @throws YAMLException
	 * @throws ClassCastException if the top level wasn't a Map
	 * 
	 * @see Properties#load(InputStream)
	 */
	public Map<?, ?> loadYaml(final String name) {
		return loadYaml(getFile(name));
	}
	
	/**
	 * @param file the file to load
	 * 
	 * @return the loaded Map or an empty HashMap if there was an IOException
	 *         reading the file.
	 * 
	 * @throws YAMLException
	 * @throws ClassCastException if the top level wasn't a Map
	 * 
	 * @see Properties#load(InputStream)
	 */
	public Map<?, ?> loadYaml(final File file) {
		try(InputStream input = new FileInputStream(file)) {
			return (Map<?, ?>) yaml.load(input);
		} catch(FileNotFoundException e) {
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon closing stream", e);
		}

		return new HashMap<>();
	}

	/**
	 * @param name the name of the file in this plugin's folder to write to
	 * @param data the data to write
	 */
	public void saveYaml(final String name, final Map<?, ?> data) {
		saveYaml(getFile(name), data);
	}
	
	/**
	 * @param file the file to write to
	 * @param data the data to write
	 */
	public void saveYaml(final File file, final Map<?, ?> data) {
		try(Writer output = new FileWriter(file)) {
			yaml.dump(data, output);
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon opening stream", e);
		}
	}

	/**
	 * Attempts to load the given file in this plugins folder. If the file is not
	 * present, attemts to save a default one from the plugin's jar file. If there
	 * IS a default one, sets the returned configuration's defaults.
	 * 
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded {@linkplain YamlConfiguration}
	 */
	public YamlConfiguration loadYamlConfig(final String name) {
		if(name.equals(configFile.getName()))
			return (YamlConfiguration) getConfig();
		saveResource(name, false);

		YamlConfiguration config = YamlConfiguration.loadConfiguration(getFile(name));

		try(InputStream defConfigStream = getResource(name)) {
			if(defConfigStream != null) {
				config.setDefaults(
						YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
			}
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon closing stream", e);
		} catch(NullPointerException e) {
		}

		return config;
	}

	/**
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded Properties object or {@code null} if there was an
	 *         IOException reading the file.
	 * 
	 * @throws IllegalArgumentException if the file is not a valid Properties file.
	 * 
	 * @see Properties#load(InputStream)
	 */
	public Properties loadProperties(final String name) {
		return loadProperties(getFile(name));
	}
	
	/**
	 * @param file the file to load
	 * 
	 * @return the loaded Properties object or {@code null} if there was an
	 *         IOException reading the file.
	 * 
	 * @throws IllegalArgumentException if the file is not a valid Properties file.
	 * 
	 * @see Properties#load(InputStream)
	 */
	public Properties loadProperties(final File file) {
		try(InputStream input = new FileInputStream(file)) {
			Properties props = new Properties();
			props.load(input);
			return props;
		} catch(FileNotFoundException e) {
		} catch(IOException e) {
			getLogger().log(SEVERE, "IOException on reading properties file " + file.getName(), e);
		}

		return null;
	}

	/**
	 * @param name the name of the file in this plugin's folder to write to
	 * @param data the data to write
	 */
	public void saveProperties(final String name, final Properties data) {
		saveProperties(getFile(name), data);
	}
	
	/**
	 * @param file the file to write to
	 * @param data the data to write
	 */
	public void saveProperties(final File file, final Properties data) {
		try(Writer output = new FileWriter(file)) {
			data.store(output, null);
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon opening stream", e);
		}
	}

	/**
	 * Attempts to load the given file in this plugins folder. If the file is not
	 * present, attemts to save a default one from the plugin's jar file. If there
	 * IS a default one, sets the returned configuration's defaults.
	 * 
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded {@linkplain PropertiesConfiguration}
	 */
	public PropertiesConfiguration loadPropertiesConfig(final String name) {
		if(name.equals(configFile.getName()))
			return (PropertiesConfiguration) getConfig();
		saveResource(name, false);

		PropertiesConfiguration config = PropertiesConfiguration.loadConfiguration(getFile(name));

		try(InputStream defConfigStream = getResource(name)) {
			if(defConfigStream != null) {
				config.setDefaults(PropertiesConfiguration
						.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
			}
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon closing stream", e);
		}

		return config;
	}

	private static JsonParser jsonParser = new JsonParser();

	/**
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded JsonElement or an empty JsonObject if there was an
	 *         IOException reading the file.
	 * 
	 * @throws JsonParseException if the file is not a valid JSON object
	 * 
	 * @see JsonParser#parse(JsonReader)
	 */
	public JsonObject loadJsonObject(final String name) {
		return loadJsonObject(getFile(name));
	}

	/**
	 * @param file the file to read
	 * 
	 * @return the loaded JsonElement or an empty JsonObject if there was an
	 *         IOException reading the file.
	 * 
	 * @throws JsonParseException if the file is not a valid JSON object
	 * 
	 * @see JsonParser#parse(JsonReader)
	 */
	public JsonObject loadJsonObject(final File file) {
		try(JsonReader reader = gson.newJsonReader(new FileReader(file))) {
			JsonElement result = jsonParser.parse(reader);
			if(result == JsonNull.INSTANCE)
				return new JsonObject();
			return (JsonObject) result;
		} catch(FileNotFoundException e) {
			return new JsonObject();
		} catch(IOException e) {
			getLogger().log(SEVERE, e.getMessage(), e);
			return new JsonObject();
		} catch(ClassCastException e) {
			getLogger().log(SEVERE, "The file '" + file.getName() + "' did not contain a JSON object", e);
			return new JsonObject();
		}
	}
	
	/**
	 * If you want to require the result to be a JsonObject, use
	 * {@link #loadJsonObject(String)}.
	 * 
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded JsonElement or {@link JsonNull#INSTANCE} if there was an
	 *         IOException reading the file.
	 * 
	 * @throws JsonParseException if the file is not valid JSON
	 * @throws FileNotFoundException 
	 * 
	 * @see JsonParser#parse(JsonReader)
	 */
	public JsonElement loadJson(final String name) throws JsonParseException, JsonIOException, JsonSyntaxException, FileNotFoundException {
		return loadJson(getFile(name));
	}

	/**
	 * If you want to require the result to be a JsonObject, use
	 * {@link #loadJsonObject(String)}.
	 * 
	 * @param file the file to read
	 * 
	 * @return the loaded JsonElement or {@link JsonNull#INSTANCE} if there was an
	 *         IOException reading the file.
	 * 
	 * @throws JsonParseException if the file is not valid JSON
	 * 
	 * @see JsonParser#parse(JsonReader)
	 */
	public JsonElement loadJson(final File file) throws JsonParseException, JsonIOException, JsonSyntaxException, FileNotFoundException {
		try(JsonReader reader = gson.newJsonReader(new FileReader(file))) {
			return jsonParser.parse(reader);
		} catch(FileNotFoundException e) {
			throw e;
		} catch(IOException e) {
			getLogger().log(SEVERE, e.getMessage(), e);
			return JsonNull.INSTANCE;
		}
	}
	
	/**
	 * @param name the name of the file in this plugin's folder to write to
	 * @param data the data to write
	 */
	public void saveJson(final String name, final JsonElement data) {
		saveJson(getFile(name), data);
	}
	
	/**
	 * @param name the name of the file in this plugin's folder to write to
	 * @param data the data to write
	 */
	public void saveJson(final File file, final JsonElement data) {
		try(Writer output = new FileWriter(file);
			JsonWriter writer = gson.newJsonWriter(output)) {
			Streams.write(data, writer);
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon opening stream", e);
		}
	}

	/**
	 * Attempts to load the given file in this plugins folder. If the file is not
	 * present, attemts to save a default one from the plugin's jar file. If there
	 * IS a default one, sets the returned configuration's defaults.
	 * 
	 * @param name the name of the file in this plugin's folder to load
	 * 
	 * @return the loaded {@linkplain JsonConfiguration}
	 */
	public JsonConfiguration loadJsonConfig(final String name) {
		if(name.equals(configFile.getName()))
			return (JsonConfiguration) getConfig();
		saveResource(name, false);

		JsonConfiguration config = JsonConfiguration.loadConfiguration(getFile(name), gson);

		try(InputStream defConfigStream = getResource(name)) {
			if(defConfigStream != null)
				config.setDefaults(JsonConfiguration
						.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8), gson));
		} catch(IOException e) {
			getLogger().log(WARNING, "IOException upon closing stream", e);
		}

		return config;
	}

	@Override
	public void saveResource(String resourcePath, boolean replace) {
		if(resourcePath == null || resourcePath.equals("")) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if(in == null) {
			return;
			// throw new IllegalArgumentException(
			// "The embedded resource '" + resourcePath + "' cannot be found in " +
			// getFile());
		}

		File outFile = getFile(resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = getFile(resourcePath.substring(0, lastIndex >= 0? lastIndex : 0));

		if(!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if(!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}
		} catch(IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public static String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		seconds -= 60 * minutes;
		long hours = minutes / 60;
		minutes -= 60 * hours;
		long days = hours / 24;
		hours -= 24 * days;
		long weeks = days / 7;
		days -= 7 * weeks;

		StringBuilder b = new StringBuilder();

		if(weeks != 0) {
			b.append(weeks).append(" week");
			if(weeks != 1)
				b.append('s');
		}

		if(days != 0) {
			if(b.length() > 0)
				b.append(", ");
			b.append(days).append(" day");
			if(days != 1)
				b.append('s');
		}

		if(hours != 0) {
			if(b.length() > 0)
				b.append(", ");
			b.append(hours).append(" hour");
			if(hours != 1)
				b.append('s');
		}

		if(minutes != 0) {
			if(b.length() > 0)
				b.append(", ");
			b.append(minutes).append(" minute");
			if(minutes != 1)
				b.append('s');
		}

		if(seconds != 0) {
			if(b.length() > 0)
				b.append(", ");
			b.append(seconds).append(" second");
			if(seconds != 1)
				b.append('s');
		}

		return b.toString();
	}

	public static int secondsToTicks(int seconds) {
		return seconds * 20;
	}

	public static long secondsToTicks(long seconds) {
		return seconds * 20;
	}

	public static int ticksToSeconds(int ticks) {
		return ticks / 20;
	}

	public static long ticksToSeconds(long ticks) {
		return ticks / 20;
	}
}
