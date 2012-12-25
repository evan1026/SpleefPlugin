package org.noip.evan1026;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SpleefPlugin extends JavaPlugin implements Listener {
	Logger log;
	WorldEditPlugin we;
	HashMap<String, ArrayList<Location>> arenaBlockLocations = new HashMap<String, ArrayList<Location>>();
	HashMap<Location, MyState> blocks = new HashMap<Location, MyState>();
	FileConfiguration config;
	int MaxSize;
	boolean HasMax;

	public void onEnable(){
		we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		log = getLogger();
		log.info("Spleef is enabled WOO!!");
		getServer().getPluginManager().registerEvents(this, this);
		loadFiles();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			log.info("PluginMetrics enabled. They can be disabled in the plugins/PluginMetrics/config.yml file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.saveDefaultConfig();
		config = getConfig();
		MaxSize = config.getInt("maxarenasize");
		HasMax = config.getBoolean("hasmax");
	}

	public void onDisable(){
		log.info("Y U NO LOVE SPLEEF?!");
		saveFiles();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		     if (cmd.getName().equalsIgnoreCase("spleefAddBlocks"))    return CommandHandler.HandleAddBlocks   (sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefRemoveBlocks")) return CommandHandler.HandleRemoveBlocks(sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefReset"))        return CommandHandler.HandleReset       (sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefAddArena"))     return CommandHandler.HandleAddArena    (sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefRemoveArena"))  return CommandHandler.HandleRemoveArena (sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefList"))         return CommandHandler.HandleList        (sender, cmd, label, args, this);
		else if (cmd.getName().equalsIgnoreCase("spleefReload"))       return CommandHandler.HandleReload      (sender, cmd, label, args, this);
		return false;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && blocks.containsKey(event.getClickedBlock().getLocation()) && (event.getPlayer().hasPermission("evan1026.spleef.instabreak." + blocks.get(event.getClickedBlock().getLocation()).getArena()) || event.getPlayer().hasPermission("evan1026.spleef.instabreak.*"))){
			event.getClickedBlock().setTypeId(0);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if (blocks.containsKey(event.getBlock().getLocation()) && !(event.getPlayer().hasPermission("evan1026.spleef.break." + blocks.get(event.getBlock().getLocation()).getArena()) || event.getPlayer().hasPermission("evan1026.spleef.break.*"))){
			event.setCancelled(true);
		}
	}

	private void loadFiles(){
		List<String> file;
		try {
			file = Files.readAllLines(Paths.get("plugins/SpleefPlugin/arenaBlocks.txt"), Charset.defaultCharset());
		} catch (IOException e) {
			log.info("Save file not found. It will be created next time this plugin is disabled.");
			return;
		}
		for(String x : file){
			String arena = x.substring(0,x.indexOf(":"));
			x = x.substring(x.indexOf(":") + 1);
			arenaBlockLocations.put(arena, new ArrayList<Location>());
			if(!x.equals("empty")){
				for(String y : x.split(";")){
					String world = y.substring(0, y.indexOf(","));
					y = y.substring(y.indexOf(",") + 1);
					double locX = Double.parseDouble(y.substring(0, y.indexOf(",")));
					y = y.substring(y.indexOf(",") + 1);
					double locY = Double.parseDouble(y.substring(0, y.indexOf(",")));
					y = y.substring(y.indexOf(",") + 1);
					double locZ = Double.parseDouble(y.substring(0, y.indexOf(",")));
					y = y.substring(y.indexOf(",") + 1);
					int blockID = Integer.parseInt(y.substring(0, y.indexOf(",")));
					y = y.substring(y.indexOf(",") + 1);
					byte data = Byte.parseByte(y);
					Location loc = new Location(getServer().getWorld(world), locX, locY, locZ);
					arenaBlockLocations.get(arena).add(loc);
					Block block = loc.getBlock();
					BlockState tempState = block.getState();
					block.setTypeId(blockID);
					block.setData(data);
					blocks.put(loc, new MyState(block.getState(), arena));
					block.setTypeId(tempState.getTypeId());
					block.setData(tempState.getRawData());
				}
			}
		}
	}

	private void saveFiles(){
		List<String> file = new ArrayList<String>();
		File path = new File("plugins/SpleefPlugin");
		path.mkdirs();
		path = new File("plugins/SpleefPlugin/arenaBlocks.txt");
		try {
			path.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(String x : arenaBlockLocations.keySet()){
			String tempString = x + ":";
			for(Location loc : arenaBlockLocations.get(x)){
				tempString += loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + blocks.get(loc).getState().getTypeId() + "," + blocks.get(loc).getState().getRawData() + ";";
			}
			if(arenaBlockLocations.get(x).isEmpty()){
				tempString += "empty;";
			}
			file.add(tempString.substring(0,tempString.length() - 1));

		}
		try {
			Files.write(Paths.get("plugins/SpleefPlugin/arenaBlocks.txt"), file, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
