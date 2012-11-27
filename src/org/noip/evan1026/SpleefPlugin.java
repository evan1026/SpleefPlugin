package org.noip.evan1026;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class SpleefPlugin extends JavaPlugin {
	Logger log;
	WorldEditPlugin we;
	HashMap<String, ArrayList<Location>> arenaBlockLocations = new HashMap<String, ArrayList<Location>>();
	HashMap<Location, Block> blocks = new HashMap<Location, Block>();

	public void onEnable(){
		we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		log = getLogger();
		log.info(ChatColor.GREEN + "Spleef is enabled WOO!!");
	}

	public void onDisable(){
		log.info(ChatColor.RED + "Y U NO LOVE SPLEEF?!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("spleefAddBlocks")){
			if (!(sender instanceof Player)){
				sender.sendMessage("This command can only be used in-game.");
				return true;
			}
			Selection selection = we.getSelection((Player)sender);
			if (selection == null){
				sender.sendMessage(ChatColor.RED + "Make a WorldEdit selection first.");
				return true;
			}
			if (args.length != 1){
				return false;
			}
			if (arenaBlockLocations.containsKey(args[0])){
				sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
				return true;
			}
			for(int i = selection.getMinimumPoint().getBlockX(); i < selection.getMaximumPoint().getBlockX(); i++){
				for(int j = selection.getMinimumPoint().getBlockY(); j < selection.getMaximumPoint().getBlockY(); j++){
					for(int k = selection.getMinimumPoint().getBlockZ(); k < selection.getMaximumPoint().getBlockZ(); k++){
						Location here = new Location(selection.getWorld(), i, j, k);
						if (!arenaBlockLocations.containsValue(here)) arenaBlockLocations.get(args[0]).add(here);
						if (!blocks.containsKey(here)){
							blocks.put(here, selection.getWorld().getBlockAt(here));
						}
						else if(!blocks.get(here).equals(selection.getWorld().getBlockAt(here))){
							blocks.remove(here);
							blocks.put(here, selection.getWorld().getBlockAt(here));
						}
					}
				}
			}
			sender.sendMessage(ChatColor.AQUA + "Blocks successfully added.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("spleefRemoveBlocks")){
			if (!(sender instanceof Player)){
				sender.sendMessage("This command can only be used in-game.");
				return true;
			}
			Selection selection = we.getSelection((Player)sender);
			if (selection == null){
				sender.sendMessage(ChatColor.RED + "Make a WorldEdit selection first.");
				return true;
			}
			if (args.length != 1){
				return false;
			}
			if (!arenaBlockLocations.containsKey(args[0])){
				sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
				return true;
			}
			for(int i = selection.getMinimumPoint().getBlockX(); i < selection.getMaximumPoint().getBlockX(); i++){
				for(int j = selection.getMinimumPoint().getBlockY(); j < selection.getMaximumPoint().getBlockY(); j++){
					for(int k = selection.getMinimumPoint().getBlockZ(); k < selection.getMaximumPoint().getBlockZ(); k++){
						Location here = new Location(selection.getWorld(), i, j, k);
						if (arenaBlockLocations.containsValue(here)) arenaBlockLocations.get(args[0]).remove(here);
						if (blocks.containsKey(here)) blocks.remove(here);
					}
				}
			}
			sender.sendMessage(ChatColor.AQUA + "Blocks successfully removed.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("spleefReset")){
			if (args.length != 1){
				return false;
			}
			if (!arenaBlockLocations.containsKey(args[0])){
				sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist. You can add it by typing /spleefAddArena " + args[0] + ".");
				return true;
			}
			for (Location loc : arenaBlockLocations.get(args[0])){
				loc.getBlock().setType(blocks.get(loc).getType());
				loc.getBlock().setData(blocks.get(loc).getData());
				loc.getBlock().setBiome(blocks.get(loc).getBiome());
			}
			sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully reset.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("spleefAddArena")){
			if (args.length != 1){
				return false;
			}
			if (arenaBlockLocations.containsKey(args[0])){
				sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " already exists. You can add blocks to it by typing /spleefAddBlocks " + args[0] + ".");
				return true;
			}
			arenaBlockLocations.put(args[0], new ArrayList<Location>());
			sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully added.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("spleefRemoveArena")){
			if (args.length != 1){
				return false;
			}
			if (!arenaBlockLocations.containsKey(args[0])){
				sender.sendMessage(ChatColor.RED + "Arena " + args[0] + " doesn't exist. You don't need to remove it.");
				return true;
			}
			arenaBlockLocations.remove(args[0]);
			sender.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " successfully removed.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("spleefList")){
			String output = ChatColor.AQUA + "";
			for(String x: arenaBlockLocations.keySet()){
				output += x + ", ";
			}
			sender.sendMessage(output.substring(0,output.length() - 1));
			return true;
		}
		return false;
	}
}
