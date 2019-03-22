package com.raptor.plugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.GenericCommandHelpTopic;

import net.md_5.bungee.api.ChatColor;

public class PermissionBasedCommandHelpTopic extends GenericCommandHelpTopic {
	protected String normalUsage, specialUsage, specialPermission;
	private String fullText_pre, fullText_post;
	
	public PermissionBasedCommandHelpTopic(Command command, String specialUsage, String specialPermission) {
		super(command);
		String usageStr = ChatColor.GOLD + "Usage: " + ChatColor.WHITE;
		int i = fullText.indexOf(usageStr);
		
		fullText_pre = fullText.substring(0, i + usageStr.length());
		
		if(command.getAliases().isEmpty()) {
			normalUsage = fullText.substring(i + usageStr.length());
			fullText_post = "";
		} else {
			int j = fullText.indexOf("\n" + ChatColor.GOLD + "Aliases: " + ChatColor.WHITE, i + usageStr.length());
			normalUsage = fullText.substring(i + usageStr.length(), j);
			fullText_post = fullText.substring(j);
		}
		this.specialUsage = specialUsage.replace("<command>", name.substring(1));
		this.specialPermission = specialPermission;
	}
	
	@Override
	public String getFullText(CommandSender forWho) {
		String usage;
		
		if(forWho instanceof ConsoleCommandSender || (specialPermission == null? forWho.isOp() : forWho.hasPermission(specialPermission))) {
			usage = specialUsage;
		} else {
			usage = normalUsage;
		}
		
		return fullText_pre + usage + fullText_post;
	}
	
}
