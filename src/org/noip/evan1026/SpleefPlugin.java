package org.noip.evan1026;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SpleefPlugin extends JavaPlugin {
	Logger log = getLogger();
	WorldEditPlugin worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
	
	public void onEnable(){
		log.info(ChatColor.BLUE + "Spleef has been enabled and such.");
	}
	
	public void onDisable(){
		log.info(ChatColor.BLUE + "Y U NO LOVE SPLEEF?");
	}
}
