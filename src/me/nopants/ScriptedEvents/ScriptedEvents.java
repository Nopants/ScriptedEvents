package me.nopants.ScriptedEvents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ScriptedEvents extends JavaPlugin {

	public PluginManager pm;
	public SEdataManager SEdata;
	public SEutils utils;
	public SEplayerListener playerListener;
	public SEblockListener blockListener;
	public SEtriggerManager triggerManager;
	public PermissionHandler permissionHandler;
	public boolean hasPermissions = false;
	public SEcommander commander;
	PluginDescriptionFile pdfFile;
	String version;
	
	//private Map<CommandSender, SEtrigger> editTrigger = new HashMap<CommandSender, SEtrigger>();
	//private Map<CommandSender, Integer> editStep = new HashMap<CommandSender, Integer>();
	
	

	// the main function called onEnable Plugin 
	public void onEnable() {
		pdfFile = this.getDescription();
		version = pdfFile.getVersion();
		SEdata = new SEdataManager(this);
		utils = SEdata.utils;
		utils.writeinlog(1, "ScriptedEvents: "+version+" enabled");
		setupPermissions();
		
		commander = new SEcommander(this);
		playerListener = new SEplayerListener(this);
		blockListener = new SEblockListener(this);
		triggerManager = new SEtriggerManager(this);
		
		pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Event.Priority.Normal, this);
		
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
				Event.Priority.Normal, this);
		
		SEdata.initializeData();
	}

	// onDisable Plugin
	public void onDisable() {
		triggerManager = null;
		playerListener = null;
		commander = null;
		utils.writeinlog(1, "Scripted Events "+version+" disabled");
	}
	
	// contains all commands of this Plugin
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		
		/*
		boolean console = false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
		} else {
			console = true;
		}	
		*/
		
		boolean result = false;
		String [] commandParts;
		commandParts = commandLabel.split("\\.");

		if (commandParts.length > 1) {
			if (commandParts[0].equalsIgnoreCase("SE")) {
				switch (commandParts.length) {
					case 2: {

						// -------------------------------------------------------------------//
						// put SE. commands here
						// ---------------------
						
						// /SE.debug
						if ((commandParts[1].equalsIgnoreCase("debug"))||(commandParts[1].equalsIgnoreCase("d")))
							result = commander.debug(sender, args);						
						
						// /SE.help
						if ((commandParts[1].equalsIgnoreCase("help"))||(commandParts[1].equalsIgnoreCase("h")))
							result = commander.help(sender, args);	
						
						// /SE.refresh
						if ((commandParts[1].equalsIgnoreCase("refresh"))||(commandParts[1].equalsIgnoreCase("r")))
							result = commander.refresh(sender, args);
					}
					case 3: {
						if ((commandParts[1].equalsIgnoreCase("edit"))||(commandParts[1].equalsIgnoreCase("e"))) {

							// -------------------------------------------------------------------//
							// put SE.edit. commands here
							// --------------------------

							// /SE.edit.add
							if ((commandParts[2].equalsIgnoreCase("add"))||(commandParts[2].equalsIgnoreCase("a")))
								result = commander.add(sender, args);
							
							// /SE.edit.delete
							if ((commandParts[1].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.delete(sender, args);
							
							// /SE.edit.event
							if ((commandParts[2].equalsIgnoreCase("event"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.editEvent(sender, args);
							
							// /SE.edit.entity
							if ((commandParts[2].equalsIgnoreCase("entity"))||(commandParts[2].equalsIgnoreCase("ent")))
								result = commander.editEntity(sender, args);
							
							// /SE.edit.script
							if ((commandParts[2].equalsIgnoreCase("script"))||(commandParts[2].equalsIgnoreCase("scr")))
								result = commander.editScript(sender, args);
							
							// /SE.edit.condition
							if ((commandParts[2].equalsIgnoreCase("condition"))||(commandParts[2].equalsIgnoreCase("co")))
								result = commander.editCondition(sender, args);
							
							// /SE.edit.name
							if ((commandParts[2].equalsIgnoreCase("name"))||(commandParts[2].equalsIgnoreCase("n")))
								result = commander.editName(sender, args);
							
							// /SE.edit.close
							if ((commandParts[2].equalsIgnoreCase("close"))||(commandParts[2].equalsIgnoreCase("cl")))
								result = commander.editClose(sender, args);
							
							// /SE.edit.save
							if ((commandParts[2].equalsIgnoreCase("save"))||(commandParts[2].equalsIgnoreCase("s")))
								result = commander.editSave(sender, args);
							
							//
							// -------------------------------------------------------------------//
							
						}

						if ((commandParts[1].equalsIgnoreCase("cuboid"))||(commandParts[1].equalsIgnoreCase("cu"))) {
							
							// -------------------------------------------------------------------//
							// put SE.cuboid. commands here
							// --------------------------
							
							// /SE.cuboid.create
							if ((commandParts[2].equalsIgnoreCase("create"))||(commandParts[2].equalsIgnoreCase("c")))
								result = commander.cuboidCreate(sender, args);
							
							// /SE.cuboid.delete
							if ((commandParts[2].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.cuboidDelete(sender, args); 
							
							// /SE.cuboid.edit
							if ((commandParts[2].equalsIgnoreCase("edit"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.cuboidEdit(sender, args);
							
							/*
							// /SE.cuboid.getID
							if ((commandParts[2].equalsIgnoreCase("getID"))||(commandParts[2].equalsIgnoreCase("id")))
								result = commander.cuboidGetID(sender, args);
							*/
							
							//
							// -------------------------------------------------------------------//
							
						}
						
						if ((commandParts[1].equalsIgnoreCase("trigger"))||(commandParts[1].equalsIgnoreCase("t"))) {
							// -------------------------------------------------------------------//
							// put SE.trigger. commands here
							// --------------------------

							// /SE.trigger.create
							if ((commandParts[2].equalsIgnoreCase("create"))||(commandParts[2].equalsIgnoreCase("c")))
								result = commander.triggerCreate(sender, args);

							// /SE.trigger.delete
							if ((commandParts[2].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.triggerDelete(sender, args); 
							
							// /SE.trigger.edit
							if ((commandParts[2].equalsIgnoreCase("edit"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.triggerEdit(sender, args);
							
							//
							// -------------------------------------------------------------------//
							
						}
						
						if ((commandParts[1].equalsIgnoreCase("script"))||(commandParts[1].equalsIgnoreCase("s"))) {
							// -------------------------------------------------------------------//
							// put SE.script. commands here
							// --------------------------
							
							// /SE.script.create
							if ((commandParts[2].equalsIgnoreCase("create"))||(commandParts[2].equalsIgnoreCase("c")))
								result = commander.scriptCreate(sender, args);
							
							// /SE.script.delete
							if ((commandParts[2].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.scriptDelete(sender, args); 
							
							// /SE.script.edit
							if ((commandParts[2].equalsIgnoreCase("edit"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.scriptEdit(sender, args);
							
							//
							// -------------------------------------------------------------------//
						}
						
						if ((commandParts[1].equalsIgnoreCase("condition"))||(commandParts[1].equalsIgnoreCase("co"))) {
							// -------------------------------------------------------------------//
							// put SE.condition. commands here
							// --------------------------
							
							// /SE.condition.create
							if ((commandParts[2].equalsIgnoreCase("create"))||(commandParts[2].equalsIgnoreCase("c")))
								result = commander.conditionCreate(sender, args);
							
							// /SE.condition.delete
							if ((commandParts[2].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.conditionDelete(sender, args); 
							
							// /SE.condition.edit
							if ((commandParts[2].equalsIgnoreCase("edit"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.conditionEdit(sender, args);
							
							//
							// -------------------------------------------------------------------//
						}
						
						if ((commandParts[1].equalsIgnoreCase("variable"))||(commandParts[1].equalsIgnoreCase("v"))) {
							// -------------------------------------------------------------------//
							// put SE.condition. commands here
							// --------------------------
							
							// /SE.variable.create
							if ((commandParts[2].equalsIgnoreCase("create"))||(commandParts[2].equalsIgnoreCase("c")))
								result = commander.variableCreate(sender, args);
							
							
							// /SE.variable.delete
							if ((commandParts[2].equalsIgnoreCase("delete"))||(commandParts[2].equalsIgnoreCase("d")))
								result = commander.variableDelete(sender, args); 
							
							
							// /SE.variable.edit
							if ((commandParts[2].equalsIgnoreCase("edit"))||(commandParts[2].equalsIgnoreCase("e")))
								result = commander.variableEdit(sender, args);
							
							// /SE.variable.list
							if ((commandParts[2].equalsIgnoreCase("list"))||(commandParts[2].equalsIgnoreCase("l")))
								result = commander.variableList(sender, args);
							
							//
							// -------------------------------------------------------------------//
						}
					}
				}
			}
		}
		
		return result;
		
	}

	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	      
	      if (this.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	        	  PluginDescriptionFile permissionsDescription = permissionsPlugin.getDescription();
	    	      String permissionsVersion = permissionsDescription.getVersion();
	              this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	              hasPermissions = true;
	              utils.writeinlog(1, "ScriptedEvents: "+"v" + permissionsVersion +" - Permissions support enabled");
	          } else {
	              utils.writeinlog(1, "ScriptedEvents: Permission system not detected, defaulting to OP");
	          }
	      }
	}
}