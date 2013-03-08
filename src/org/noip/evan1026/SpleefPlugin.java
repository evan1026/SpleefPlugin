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
	
	protected Logger _log;
	protected WorldEditPlugin _we;
	protected HashMap<String, ArrayList<Location>> _arenaBlockLocations = new HashMap<String, ArrayList<Location>>();
	protected HashMap<Location, MyState> _blocks = new HashMap<Location, MyState>();
	protected FileConfiguration _config;
	protected int _MaxSize;
	protected boolean _HasMax;

	public void onEnable(){
		
		_log = getLogger();
		_log.info("Spleef is enabled WOO!!");
		
		_we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		if (_we == null){
			_log.severe("This plugin CANNOT work without WorldEdit. GET IT!!!");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		_log = getLogger();
		_log.info("Spleef is enabled WOO!!");
		
		getServer().getPluginManager().registerEvents(this, this);
		loadFiles();
		
		try {
			
			Metrics metrics = new Metrics(this);
			metrics.start();
			_log.info("PluginMetrics enabled. They can be disabled in the plugins/PluginMetrics/config.yml file.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		saveDefaultConfig();
		_config = getConfig();
		_MaxSize = _config.getInt("maxarenasize");
		_HasMax = _config.getBoolean("hasmax");
	}

	public void onDisable(){
		_log.info("Y U NO LOVE SPLEEF?!");
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
		
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && _blocks.containsKey(event.getClickedBlock().getLocation()) && (event.getPlayer().hasPermission("evan1026.spleef.instabreak." + _blocks.get(event.getClickedBlock().getLocation()).getArena()) || event.getPlayer().hasPermission("evan1026.spleef.instabreak.*"))){
			
			event.getClickedBlock().setTypeId(0);
		}
		
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		if (_blocks.containsKey(event.getBlock().getLocation()) && !(event.getPlayer().hasPermission("evan1026.spleef.break." + _blocks.get(event.getBlock().getLocation()).getArena()) || event.getPlayer().hasPermission("evan1026.spleef.break.*"))){
			
			event.setCancelled(true);
		}
	}

	private void loadFiles(){
		
		List<String> fileContents;
		
		try {
			
			fileContents = Files.readAllLines(Paths.get("plugins/SpleefPlugin/arenaBlocks.txt"), Charset.defaultCharset());
			
		} catch (IOException e) {
			
			_log.info("Save file not found. It will be created next time this plugin is disabled.");
			return;
			
		}
		
		for(String x : fileContents){
			
			String arena = x.substring(0,x.indexOf(":"));
			
			x = x.substring(x.indexOf(":") + 1);
			
			_arenaBlockLocations.put(arena, new ArrayList<Location>());
			
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
					
					_arenaBlockLocations.get(arena).add(loc);
					
					Block block = loc.getBlock();
					BlockState tempState = block.getState();
					
					block.setTypeId(blockID);
					block.setData(data);
					_blocks.put(loc, new MyState(block.getState(), arena));
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
		
		for(String x : _arenaBlockLocations.keySet()){
			
			String tempString = x + ":";
			
			for(Location loc : _arenaBlockLocations.get(x)){
				tempString += loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + _blocks.get(loc).getState().getTypeId() + "," + _blocks.get(loc).getState().getRawData() + ";";
			}
			
			if(_arenaBlockLocations.get(x).isEmpty()){
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
