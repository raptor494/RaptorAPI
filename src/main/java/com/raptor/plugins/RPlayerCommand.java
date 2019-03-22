package com.raptor.plugins;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class RPlayerCommand extends RCommand {

	@Override
	public abstract void onCommand(Player player, String label, String[] args);
	
	@Override
	public final void onCommand(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player) {
			onCommand((Player)sender, label, args);
		} else {
			sender.sendMessage(mustBePlayerMessage());
		}
	}
	
	@Override
	public List<String> onTabComplete(Player player, String label, String[] args) {
		return Collections.emptyList();
	}
	
	@Override
	public final List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player)
			return onTabComplete((Player)sender, label, args);
		else return Collections.emptyList();
	}
	
}
