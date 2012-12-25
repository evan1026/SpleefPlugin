package org.noip.evan1026;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandHandler {
	public static boolean HandleAddBlocks(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		if (!(sender instanceof Player)){
			sender.sendMessage("This command can only be used in-game.");
			return true;
		}
		Selection selection = plugin.we.getSelection((Player)sender);
		if (selection == null){
			sender.sendMessage(ChatColor.RED + "Make a WorldEdit selection first.");
			return true;
		}
		if (args.length != 1){
			return false;
		}
		if (!plugin.arenaBlockLocations.containsKey(args[0])){
			sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
			return true;
		}
		if (plugin.HasMax && !sender.hasPermission("evan1026.spleef.overrideblocklimit") && plugin.arenaBlockLocations.get(args[0]).size() + selection.getArea() > plugin.MaxSize){
			sender.sendMessage(ChatColor.RED + "Your selection will put the arena over the maximum block size. Please remove " + (plugin.arenaBlockLocations.get(args[0]).size() + selection.getArea() - plugin.MaxSize) + "blocks to add them to the arena.");
			return true;
		}
		for(int i = selection.getMinimumPoint().getBlockX(); i <= selection.getMaximumPoint().getBlockX(); i++){
			for(int j = selection.getMinimumPoint().getBlockY(); j <= selection.getMaximumPoint().getBlockY(); j++){
				for(int k = selection.getMinimumPoint().getBlockZ(); k <= selection.getMaximumPoint().getBlockZ(); k++){
					Location here = new Location(selection.getWorld(), i, j, k);
					if (!plugin.arenaBlockLocations.get(args[0]).contains(here)) plugin.arenaBlockLocations.get(args[0]).add(here);
					plugin.blocks.put(here, new MyState(selection.getWorld().getBlockAt(here).getState(), args[0]));
				}
			}
		}
		sender.sendMessage(ChatColor.AQUA + "Blocks successfully added.");
		return true;
	}
	public static boolean HandleRemoveBlocks(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		if (!(sender instanceof Player)){
			sender.sendMessage("This command can only be used in-game.");
			return true;
		}
		Selection selection = plugin.we.getSelection((Player)sender);
		if (selection == null){
			sender.sendMessage(ChatColor.RED + "Make a WorldEdit selection first.");
			return true;
		}
		if (args.length != 1){
			return false;
		}
		if (!plugin.arenaBlockLocations.containsKey(args[0])){
			sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
			return true;
		}
		for(int i = selection.getMinimumPoint().getBlockX(); i <= selection.getMaximumPoint().getBlockX(); i++){
			for(int j = selection.getMinimumPoint().getBlockY(); j <= selection.getMaximumPoint().getBlockY(); j++){
				for(int k = selection.getMinimumPoint().getBlockZ(); k <= selection.getMaximumPoint().getBlockZ(); k++){
					Location here = new Location(selection.getWorld(), i, j, k);
					if (plugin.arenaBlockLocations.get(args[0]).contains(here)) plugin.arenaBlockLocations.get(args[0]).remove(here);
					if (plugin.blocks.containsKey(here)) plugin.blocks.remove(here);
				}
			}
		}
		sender.sendMessage(ChatColor.AQUA + "Blocks successfully removed.");
		return true;
	}
	public static boolean HandleReset(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		if (args.length != 1){
			return false;
		}
		if (!plugin.arenaBlockLocations.containsKey(args[0])){
			sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
			return true;
		}
		for (Location loc : plugin.arenaBlockLocations.get(args[0])){
			loc.getBlock().setType(plugin.blocks.get(loc).getState().getType());
			loc.getBlock().setData(plugin.blocks.get(loc).getState().getRawData());
		}
		sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully reset.");
		return true;
	}
	public static boolean HandleAddArena(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		if (args.length != 1){
			return false;
		}
		if (plugin.arenaBlockLocations.containsKey(args[0])){
			sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " already exists. You can add blocks to it by typing /spleefAddBlocks " + args[0] + ".");
			return true;
		}
		plugin.arenaBlockLocations.put(args[0], new ArrayList<Location>());
		sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully added.");
		return true;
	}
	public static boolean HandleRemoveArena(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		if (args.length != 1){
			return false;
		}
		if (!plugin.arenaBlockLocations.containsKey(args[0])){
			sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " doesn't exist. You don't need to remove it.");
			return true;
		}
		plugin.arenaBlockLocations.remove(args[0]);
		sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully removed.");
		return true;
	}
	public static boolean HandleList(CommandSender sender, Command cmd, String label, String[] args, SpleefPlugin plugin){
		String output = ChatColor.AQUA + "";
		for(String x: plugin.arenaBlockLocations.keySet()){
			output += x + ", ";
		}
		sender.sendMessage(output.substring(0,output.length() - 2));
		return true;
	}
}
