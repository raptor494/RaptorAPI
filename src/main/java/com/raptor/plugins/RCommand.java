package com.raptor.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.raptor.plugins.util.StringUtils;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class RCommand implements CommandExecutor, TabCompleter {
		
	public abstract void onCommand(CommandSender sender, String label, String[] args);
	
	public void onCommand(Player player, String label, String[] args) {
		onCommand((CommandSender)player, label, args);
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			onCommand((Player)sender, label, args);
		} else {
			onCommand(sender, label, args);
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return Collections.emptyList();
	}
	
	public List<String> onTabComplete(Player player, String label, String[] args) {
		return onTabComplete((CommandSender)player, label, args);
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			return onTabComplete((Player)sender, label, args);
		} else {
			return onTabComplete(sender, label, args);
		}
	}
	
	private String name;
	
	public String getName() {
		if(name == null) {
			name = getClass().getSimpleName();
			if(name.endsWith("Command"))
				name = name.substring(0, name.length()-7).toLowerCase();
			else if(name.startsWith("Command"))
				name = name.substring(7).toLowerCase();
			else
				name = name.toLowerCase();
		}
		return name;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	/*                                                             *\
	----------------< BEGIN UTILITY METHODS SECTION >----------------
	\*                                                             */
	
	public static ArrayList<String> newStringList() {
		return new ArrayList<>();
	}
	
	public static ArrayList<String> newStringList(String... strings) {
		ArrayList<String> result = new ArrayList<>(strings.length);
		for(String str : strings) {
			result.add(str);
		}
		return result;
	}
	
	public static ArrayList<String> newStringList(Collection<String> strings) {
		if(strings instanceof ArrayList)
			return (ArrayList<String>)strings;
		else return new ArrayList<>(strings);
	}
	
	public static List<String> stringsMatching(String arg, String... options) {
		return arg.isEmpty()
				? Arrays.asList(options)
				: Arrays.stream(options).filter(str -> str.startsWith(arg)).collect(Collectors.toList());
	}
	
	public static List<String> stringsMatching(String arg, boolean ignoreCase, String... options) {
		if(ignoreCase)
			return arg.isEmpty()
				? Arrays.asList(options)
				: Arrays.stream(options).filter(str -> StringUtils.startsWithIgnoreCase(str, arg)).collect(Collectors.toList());
		else
			return stringsMatching(arg, options);
	}
	
	public static List<String> stringsMatching(String arg, Collection<String> options) {
		return arg.isEmpty()
				? options instanceof List? (List<String>) options : options.stream().collect(Collectors.toList())
				: options.stream().filter(str -> str.startsWith(arg)).collect(Collectors.toList());
	}
	
	public static List<String> stringsMatching(String arg, boolean ignoreCase, Collection<String> options) {
		if(ignoreCase)
			return arg.isEmpty()
				? options instanceof List? (List<String>) options : options.stream().collect(Collectors.toList())
				: options.stream().filter(str -> StringUtils.startsWithIgnoreCase(str, arg)).collect(Collectors.toList());
		else return stringsMatching(arg, options);
	}
	
	public static List<String> stringsMatchingLastArg(String[] args, String... options) {
		if(args.length == 0)
			return Arrays.asList(options);
		else return stringsMatching(args[args.length-1], options);
	}
	
	public static List<String> stringsMatchingLastArg(String[] args, boolean ignoreCase, String... options) {
		if(args.length == 0)
			return Arrays.asList(options);
		else return stringsMatching(args[args.length-1], ignoreCase, options);
	}
	
	public static List<String> stringsMatchingLastArg(String[] args, Collection<String> options) {
		if(args.length == 0)
			return options instanceof List? (List<String>) options : options.stream().collect(Collectors.toList());
		else return stringsMatching(args[args.length-1], options);
	}
	
	public static List<String> stringsMatchingLastArg(String[] args, boolean ignoreCase, Collection<String> options) {
		if(args.length == 0)
			return options instanceof List? (List<String>) options : options.stream().collect(Collectors.toList());
		else return stringsMatching(args[args.length-1], ignoreCase, options);
	}
	
	public static Player player(String name) {
		{	Player player = Bukkit.getServer().getPlayer(name);
			if(player != null) 
				return player;
		}
		int longestName = 0;
		Player mostLikely = null;
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			String playerName = ChatColor.stripColor(player.getDisplayName());
			if(playerName.regionMatches(true, 0, name, 0, name.length())) {
				if(playerName.length() > longestName) {
					longestName = playerName.length();
					mostLikely = player;
				}
			}
		}
		return mostLikely;
	}
	
	
	/*private static final Class[] NO_PARAMS = new Class[0];
	
	public static <T, C> Collector<T, C, C> collectorFor(Class<C> type) {
		Constructor<C> csttr;
		try {
			csttr = type.getConstructor(NO_PARAMS);
		} catch(NoSuchMethodException | SecurityException e) {
			String msg = type + " does not have a public no-args constructor";
			if(!Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type)) {
				msg += " and does not implement either java.util.Collection or java.util.Map";
			}
			throw new IllegalArgumentException(msg);
		}
		if(Collection.class.isAssignableFrom(type)) {
			Supplier<Collection> supplier = () -> {
				try {
					return (Collection) csttr.newInstance();
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					throw new AssertionError(e);
				}
			};
			return (Collector<T, C, C>) Collector.of(supplier,
					Collection::add,
					(c1, c2) -> { c1.addAll(c2); return c1; });
		} else if(Map.class.isAssignableFrom(type)) {
			Supplier<Map> supplier = () -> {
				try {
					return (Map) csttr.newInstance();
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					throw new AssertionError(e);
				}
			};
			return (Collector<T, C, C>) Collector.of(supplier,
					(Map map, Map.Entry entry) -> map.put(entry.getKey(), entry.getValue()),
					(map1, map2) -> { map1.putAll(map2); return map1; });
		} else {
			throw new IllegalArgumentException(type + " does not implement either java.util.Collection or java.util.Map");
		}
	}
	
	public static <T, C, R> Collector<T, C, R> collectorFor(Class<C> type, Function<C, R> finisher) {
		Constructor<C> csttr;
		try {
			csttr = type.getConstructor(NO_PARAMS);
		} catch(NoSuchMethodException | SecurityException e) {
			String msg = type + " does not have a public no-args constructor";
			if(!Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type)) {
				msg += " and does not implement either java.util.Collection or java.util.Map";
			}
			throw new IllegalArgumentException(msg);
		}
		if(Collection.class.isAssignableFrom(type)) {
			Supplier<Collection> supplier = () -> {
				try {
					return (Collection) csttr.newInstance();
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					throw new AssertionError(e);
				}
			};
			return (Collector<T, C, R>) Collector.of(supplier,
					Collection::add,
					(c1, c2) -> { c1.addAll(c2); return c1; },
					(Function<Collection, R>) finisher);
		} else if(Map.class.isAssignableFrom(type)) {
			Supplier<Map> supplier = () -> {
				try {
					return (Map) csttr.newInstance();
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					throw new AssertionError(e);
				}
			};
			return (Collector<T, C, R>) Collector.of(supplier,
					(Map map, Map.Entry entry) -> map.put(entry.getKey(), entry.getValue()),
					(map1, map2) -> { map1.putAll(map2); return map1; },
					(Function<Map, R>) finisher);
		} else {
			throw new IllegalArgumentException(type + " does not implement either java.util.Collection or java.util.Map");
		}
	}
	
	public static <T, C extends Collection<T>> Collector<T, C, C> collectionCollectorOf(Supplier<C> supplier) {
		return Collector.of(supplier,
				Collection::add,
				(c1, c2) -> { c1.addAll(c2); return c1; });
	}
	
	public static <T, C extends Collection<T>, R> Collector<T, C, R> collectionCollectorOf(Supplier<C> supplier, Function<? super C, R> finisher) {
		return Collector.<T, C, R>of(supplier,
				Collection::add,
				(C c1, C c2) -> { c1.addAll(c2); return c1; },
				(Function<C, R>) finisher);
	}*/
	
	public static <K, V, M extends Map<K, V>> Collector<Map.Entry<K, V>, M, M> mapCollectorOf(Supplier<M> supplier) {
		return Collector.of(supplier,
				(map, entry) -> map.put(entry.getKey(), entry.getValue()),
				(map1, map2) -> { map1.putAll(map2); return map1; });
	}
	
	public static <K, V, M extends Map<K, V>, R> Collector<Map.Entry<K, V>, M, R> mapCollectorOf(Supplier<M> supplier, Function<? super M, R> finisher) {
		return Collector.of(supplier,
				(map, entry) -> map.put(entry.getKey(), entry.getValue()),
				(map1, map2) -> { map1.putAll(map2); return map1; },
				(Function<M, R>) finisher);
	}
	
	private static final Collector 
		ARRAYLIST_COLLECTOR = Collectors.toCollection(ArrayList::new),
		HASHSET_COLLECTOR = Collectors.toCollection(HashSet::new),
		TREESET_COLLECTOR = Collectors.toCollection(TreeSet::new),
		HASHMAP_COLLECTOR = mapCollectorOf(HashMap::new),
		IMMUTABLE_ARRAYLIST_COLLECTOR = Collectors.toList(), 
		IMMUTABLE_HASHSET_COLLECTOR = Collectors.toSet(),
		IMMUTABLE_TREESET_COLLECTOR = Collector.of(TreeSet::new, Set::add, (s1, s2) -> { s1.addAll(s2); return s1; }, Collections::unmodifiableSet),
		IMMUTABLE_HASHMAP_COLLECTOR = mapCollectorOf(HashMap::new, Collections::unmodifiableMap);
	
	public static <T> Collector<T, ArrayList<T>, ArrayList<T>> arrayListCollector() {
		return ARRAYLIST_COLLECTOR;
	}
	
	public static <T> Collector<T, HashSet<T>, HashSet<T>> hashSetCollector() {
		return HASHSET_COLLECTOR;
	}
	
	public static <T> Collector<T, TreeSet<T>, TreeSet<T>> treeSetCollector() {
		return TREESET_COLLECTOR;
	}
	
	public static <K, V> Collector<Map.Entry<K, V>, HashMap<K, V>, HashMap<K, V>> hashMapCollector() {
		return HASHMAP_COLLECTOR;
	}
	
	public static <T> Collector<T, ArrayList<T>, ArrayList<T>> immutableArrayListCollector() {
		return IMMUTABLE_ARRAYLIST_COLLECTOR;
	}
	
	public static <T> Collector<T, HashSet<T>, HashSet<T>> immutableHashSetCollector() {
		return IMMUTABLE_HASHSET_COLLECTOR;
	}
	
	public static <T> Collector<T, TreeSet<T>, TreeSet<T>> immutableTreeSetCollector() {
		return IMMUTABLE_TREESET_COLLECTOR;
	}
	
	public static <K, V> Collector<Map.Entry<K, V>, HashMap<K, V>, HashMap<K, V>> immutableHashMapCollector() {
		return IMMUTABLE_HASHMAP_COLLECTOR;
	}
	
	public static List<String> onlinePlayerNames() {
		return Bukkit.getServer().getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNicknames() {
		return Bukkit.getServer().getOnlinePlayers().stream()
				.map(player -> ChatColor.stripColor(player.getDisplayName()))
				.collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNamesAndNicknames() {
		HashSet<String> names = new HashSet<>();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			names.add(player.getName());
			names.add(ChatColor.stripColor(player.getDisplayName()));
		}
		return names.stream().collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNamesMatching(String arg) {
		Stream<String> stream = Bukkit.getServer().getOnlinePlayers().stream()
				.map(Player::getName);
		if(!arg.isEmpty()) {
			stream = stream.filter(name -> StringUtils.startsWithIgnoreCase(name, arg));
		}
		return stream.collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNicknamesMatching(String arg) {
		Stream<String> stream = Bukkit.getServer().getOnlinePlayers().stream()
				.map(player -> ChatColor.stripColor(player.getDisplayName()));
		if(!arg.isEmpty()) {
			stream = stream.filter(name -> StringUtils.startsWithIgnoreCase(name, arg));
		}
		return stream.collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNamesAndNicknamesMatching(String arg) {
		if(arg.isEmpty())
			return onlinePlayerNamesAndNicknames();
		HashSet<String> names = new HashSet<>();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			String name = player.getName();
			if(StringUtils.startsWithIgnoreCase(name, arg))
				names.add(name);
			name = ChatColor.stripColor(player.getDisplayName());
			if(StringUtils.startsWithIgnoreCase(name, arg))
				names.add(name);
		}
		return names.stream().collect(Collectors.toList());
	}
	
	public static List<String> onlinePlayerNamesMatchingLastArg(String[] args) {
		if(args.length == 0)
			return onlinePlayerNames();
		else return onlinePlayerNamesMatching(args[args.length-1]);
	}
	
	public static List<String> onlinePlayerNicknamesMatchingLastArg(String[] args) {
		if(args.length == 0)
			return onlinePlayerNicknames();
		else return onlinePlayerNicknamesMatching(args[args.length-1]); 
	}
	
	public static List<String> onlinePlayerNamesAndNicknamesMatchingLastArg(String[] args) {
		if(args.length == 0)
			return onlinePlayerNamesAndNicknames();
		else return onlinePlayerNamesAndNicknamesMatching(args[args.length-1]); 
	}

	@SuppressWarnings("deprecation")
	public static OfflinePlayer offlinePlayer(String name) {
		return Bukkit.getServer().getOfflinePlayer(name);
	}
	
	public static OfflinePlayer offlinePlayer(UUID uuid) {
		return Bukkit.getServer().getOfflinePlayer(uuid);
	}
	
	public static String join(String[] args) {
		return join(args, 0);
	}
	
	public static String join(String[] args, int start) {
		return join(args, start, args.length);
	}
	
	public static String join(String[] args, int start, int end) {
		return join(args, start, end, " ");
	}
	
	public static String join(String[] args, int start, int end, String separator) {
		StringBuilder b = new StringBuilder();
		if(end > args.length)
			end = args.length;
		for(int i = start; i < end; i++) {
			if(b.length() != 0)
				b.append(separator);
			b.append(args[i]);
		}
		return b.toString();
	}
	
	public static String mustBePlayerMessage() {
		return "\u00a7cYou must be a player in order to perform this command.";
	}
	
	public static String playerNotFound(String name, String command, String[] args, int position) {
		return error("Player not found: '" + name + "'", command, args, position);
	}
	
	public static String unknownCommand() {
		return Bukkit.getServer().spigot().getConfig().getString("messages.unknown-command");
	}
	
	public static String unknownCommand(String command, String[] args) {
		return error("Unknown command", command, args, args.length);
	}
	
	public static String unknownCommand(String command, String[] args, int position) {
		return error("Unknown command", command, args, position);
	}
	
	public static String missingArgument(String argumentName, String command, String[] args, int position) {
		return error("Missing argument '" + argumentName + "'", command, args, position);
	}
	
	public static String invalidArgument(String argumentName, String command, String[] args, int position) {
		return error("Invalid argument '" + argumentName + "'", command, args, position);
	}
	
	public static String invalidArgument(String argumentName, String expected, String command, String[] args, int position) {
		return error("Invalid argument '" + argumentName + "', expected " + expected, command, args, position);
	}
	
	public static String unknown(String thingType, String thing, String command, String[] args, int position) {
		return error("Unknown " + thingType + " '" + thing + "'", command, args, position);
	}
	
	public static String error(String message, String command, String[] args) {
		return error(message, command, args, args.length);
	}
	
	public static String error(String message, String command, String[] args, int position) {
		StringBuilder b = new StringBuilder("\u00a7c");
		b.append(message).append("\n\u00a77");
		if(position > args.length)
			position = args.length;
		int start = position - 3;
		if(start > 0) {
			b.append("..");
		} else {
			b.append("/").append(command);
			start = 0;
		}
		for(int i = start; i < position; i++) {
			b.append(" \u00a77")
			 .append(args[i]);
		}
		b.append("\u00a7c\u00a7o<--[HERE]");
		return b.toString();
	}
}
