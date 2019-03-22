package com.raptor.plugins.api;

import static com.raptor.plugins.util.ItemUtils.isSpawnEgg;
import static java.util.logging.Level.SEVERE;
import static org.bukkit.event.EventPriority.HIGHEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

import com.raptor.plugins.RCommand;
import com.raptor.plugins.RaptorPlugin;

public final class RaptorAPI extends RaptorPlugin implements Listener {
	private static RaptorAPI instance;
	private static final String ITEMS_FOLDER = "items";
	private File itemsFolder;
	private final HashMap<String, ItemStack> customItems = new HashMap<>();
	
	@Override
	public void onLoad() {
		super.onLoad();
		instance = this;
		
		registerCommand("customitem", new CommandCustomItem());
		registerCommand("customitems", new CommandCustomItems());
		registerListener(this);
	}
	
	public static RaptorAPI getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		
		getLogger().info("itemsFolder = " + getItemsFolder());
		
		for(File file : getItemsFolder().listFiles((File file) -> file.getName().endsWith(".json") && file.isFile())) {
			try {
				ItemStack item = getGson().fromJson(loadJson(file), ItemStack.class);
				customItems.put(file.getName().substring(0, file.getName().length()-5), item);
			} catch(RuntimeException e) {
				getLogger().log(Level.WARNING, "Error loading custom item " + file, e);
			} catch(FileNotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		
		customItems.clear();
	}
	
	public File getItemsFolder() {
		if(itemsFolder == null) {
			itemsFolder = getFile(ITEMS_FOLDER);
			if(!itemsFolder.exists() || !itemsFolder.isDirectory()) {
				itemsFolder.mkdirs();
			}
		}
		return itemsFolder;
	}
	
	public ItemStack getCustomItem(String name) {
		ItemStack result = customItems.get(name);
		if(result != null)
			result = result.clone();
		return result;
	}
	
	private class CommandCustomItems extends RCommand {

		@Override
		public void onCommand(CommandSender sender, String label, String[] args) {
			if(args.length == 1 && args[0].equals("reload")) {
				try {
					reload();
				} catch(RuntimeException e) {
					sender.sendMessage("\u00a7c" + e.getMessage());
					getLogger().log(SEVERE, e.getClass().getSimpleName() + " while reloading custom items from command", e);
				}
				sender.sendMessage("\u00a7aReloaded RaptorAPI and custom items");
				return;
			}
			if(args.length != 0) {
				sender.sendMessage(unknownCommand(label, args, 0));
				return;
			}
			
			sender.sendMessage(ChatPaginator.wordWrap(String.join(", ", customItems.keySet()), ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH));
			
			return;
		}
		
	}
	
	private class CommandCustomItem extends RCommand {

		@Override
		public void onCommand(CommandSender sender, String label, String[] args) {
			if(args.length == 0 || args.length > 3) {
				sender.sendMessage(unknownCommand(label, args, Math.min(args.length, 4)));
				return;
			}
			
			ItemStack item = getCustomItem(args[0]);
			
			if(item == null) {
				sender.sendMessage(unknown("item", args[0], label, args, 1));
				return;
			}
			
			if(args.length > 1) {
				try {
					int amount = Integer.parseUnsignedInt(args[1]);
					item.setAmount(amount);
				} catch(NumberFormatException e) {
					sender.sendMessage(invalidArgument(args[1], "a positive integer", label, args, 2));
					return;
				}
			}
			
			if(args.length == 3) {
				Player receiver = player(args[2]);
				if(receiver == null) {
					sender.sendMessage(playerNotFound(args[2], label, args, 3));
					return;
				}
				
				for(ItemStack unaddedItem : receiver.getInventory().addItem(item).values()) {
					receiver.getWorld().dropItemNaturally(receiver.getLocation(), unaddedItem);
				}				
			} else {
				if(!(sender instanceof Player)) {
					sender.sendMessage(mustBePlayerMessage());
				} else {
					for(ItemStack unaddedItem : ((Player)sender).getInventory().addItem(item).values()) {
						((Player)sender).getWorld().dropItemNaturally(((Player)sender).getLocation(), unaddedItem);
					}
				}
			}
			
			return;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
			if(args.length <= 1)
				return stringsMatchingLastArg(args, customItems.keySet());
			else if(args.length == 3)
				return onlinePlayerNamesMatchingLastArg(args);
			else return Collections.emptyList();
		}
		
	}
	
	@EventHandler(ignoreCancelled = true, priority = HIGHEST)
	public void preventSpawnerChange(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(event.useItemInHand() != Result.DENY
					&& event.hasBlock() && event.hasItem() && isSpawnEgg(event.getItem().getType())) {
				if(event.getClickedBlock().getType() == Material.SPAWNER
						&& (event.getPlayer().getGameMode() != GameMode.CREATIVE
								|| !event.getPlayer().hasPermission("minecraft.spawner.change"))) {
					event.setUseItemInHand(Result.DENY);
				}
			}
		}
	}
}
