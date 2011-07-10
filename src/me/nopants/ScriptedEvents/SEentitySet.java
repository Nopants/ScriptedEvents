package me.nopants.ScriptedEvents;

import me.nopants.ScriptedEvents.SEtrigger.triggerEvent;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SEentitySet {
	public SEtrigger.triggerEvent triggerEvent = SEtrigger.triggerEvent.none;
	public SEtrigger trigger = null;
	public SEcondition condition = null;
	public SEscript script = null;
	
	public int cycles = -1;
	public int randomMax = 0;
	public int randomMin = 0;
	
	int typeID = -1;
	int data = -1;
	
	public Player player = null;
	public SEcuboid cuboid = null;
	public Location location = null;
	
	public String command = null;
	public String[] args = null;
	
	public String setItem = null;
	public String name = null;
	
	public PlayerInteractEvent interactEvent = null;
	public PlayerRespawnEvent respawnEvent = null;
	public BlockBreakEvent blockBreakEvent = null;
	public BlockPlaceEvent blockPlaceEvent = null;
		
 	public boolean isEmpty() {
		return ((triggerEvent == SEtrigger.triggerEvent.none)
				&&(player == null)
				&&(trigger == null)
				&&(cuboid == null)
				&&(location == null)
				&&(cycles == -1)
				&&(randomMax == 0)
				&&(randomMin == 0)
				&&(command == null)
				&&(script == null)
				&&(condition == null)
				&&(name == null)
				&&(args == null)
				&&(setItem == null));
	}
	 	
	public SEentitySet(String newName, SEtrigger.triggerEvent newEvent, SEcuboid newCuboid, SEcondition newCondition, SEscript newScript, String newCommand) {
		this.triggerEvent = newEvent;
		this.cuboid = newCuboid;
		this.condition = newCondition;
		this.script = newScript;
		this.command = newCommand;
		this.name = newName;
	}
	
	public SEentitySet() {
	}

	public SEentitySet(SEtrigger.triggerEvent newEvent) {
		this.triggerEvent = newEvent;
	}
	
	public SEentitySet(SEtrigger newTrigger) {
		this.trigger = newTrigger;
	}
	
	//onInteract
	public SEentitySet(PlayerInteractEvent newInteractEvent) {
		this.interactEvent = newInteractEvent;
		this.player = interactEvent.getPlayer();
	}
	
	//onRespawn
	public SEentitySet(PlayerRespawnEvent newRespawnEvent) {
		this.respawnEvent = newRespawnEvent;
		this.player = respawnEvent.getPlayer();
	}
	
	//onBlockBreak
	public SEentitySet(BlockBreakEvent newblockBreakEvent, int newTypeID, int newData) {
		this.blockBreakEvent = newblockBreakEvent;
		this.player = blockBreakEvent.getPlayer();
		this.typeID = newTypeID;
		this.data = newData;
		this.location = blockBreakEvent.getBlock().getLocation();
	}
	
	//onBlockPlace
	public SEentitySet(BlockPlaceEvent newblockPlaceEvent) {
		this.blockPlaceEvent = newblockPlaceEvent;
		this.player = blockPlaceEvent.getPlayer();
		this.location = blockPlaceEvent.getBlock().getLocation();
	}
	
	//onEnter && onLeave
	public SEentitySet(SEtrigger.triggerEvent newEvent, SEcuboid newCuboid) {
		this.triggerEvent = newEvent;
		this.cuboid = newCuboid;
	}
	public SEentitySet(Player newPlayer, SEcuboid newCuboid) {
		this.player = newPlayer;
		this.cuboid = newCuboid;
	}
	
	//onCommand
	public SEentitySet(triggerEvent newEvent, String newCommand) {
		this.triggerEvent = newEvent;
		this.command = newCommand;
	}
	public SEentitySet(Player newPlayer, String newCommand, String[] newArgs) {
		this.player = newPlayer;
		this.command = newCommand;
		this.args = newArgs;
	}
	
	//edit entities	
	public SEentitySet(SEcuboid newCuboid) {
		this.cuboid = newCuboid;
	}
	public SEentitySet(SEscript newScript) {
		this.script = newScript;
	}
	public SEentitySet(SEcondition newCondition) {
		this.condition = newCondition;
	}


	
}
