package me.nopants.ScriptedEvents;

import me.nopants.ScriptedEvents.SEtrigger.triggerEvent;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SEentitySet {
	public SEtrigger.triggerEvent triggerEvent = SEtrigger.triggerEvent.none;
	public Player player = null;
	public SEtrigger trigger = null;
	public SEcuboid cuboid = null;
	public Location location = null;
	public int itemID = 0;
	public int cycles = -1;
	public int randomMax = 0;
	public int randomMin = 0;
	public String command = null;
	public SEcondition condition = null;
	public SEscript script = null;
	public String name = null;
	public PlayerInteractEvent interactEvent = null;
	public String[] args = null;
		
 	public boolean isEmpty() {
		return ((triggerEvent == SEtrigger.triggerEvent.none)
				&&(player == null)
				&&(trigger == null)
				&&(cuboid == null)
				&&(location == null)
				&&(itemID == 0)
				&&(cycles == -1)
				&&(randomMax == 0)
				&&(randomMin == 0)
				&&(command == null)
				&&(script == null)
				&&(condition == null)
				&&(name == null)
				&&(args == null));
	}
	 	
	public SEentitySet(String newName, SEtrigger.triggerEvent newEvent, SEcuboid newCuboid, SEcondition newCondition, SEscript newScript, String newCommand) {
		this.triggerEvent = newEvent;
		this.cuboid = newCuboid;
		this.condition = newCondition;
		this.script = newScript;
		this.command = newCommand;
		this.name = newName;
	}
	
	public SEentitySet(SEtrigger newTrigger, Player newPlayer, int newItemID, SEcuboid newCuboid) {
		this.player = newPlayer;
		this.trigger = newTrigger;
		this.cuboid = newCuboid;
		this.itemID = newItemID;
	}
	
	public SEentitySet(SEtrigger.triggerEvent newEvent, SEcuboid newCuboid) {
		this.triggerEvent = newEvent;
		this.cuboid = newCuboid;
	}
	
	public SEentitySet(Player newPlayer, SEcuboid newCuboid) {
		this.player = newPlayer;
		this.cuboid = newCuboid;
	}
	
	public SEentitySet(Player newPlayer, int newItemID,  SEcuboid newCuboid) {
		this.player = newPlayer;
		this.cuboid = newCuboid;
		this.itemID = newItemID;
	}
	
	public SEentitySet(SEtrigger.triggerEvent newEvent) {
		this.triggerEvent = newEvent;
	}
	
	public SEentitySet(Player newPlayer, int newItemID) {
		this.player = newPlayer;
		this.itemID = newItemID;
	}
	
	public SEentitySet(triggerEvent newEvent, String newCommand) {
		this.triggerEvent = newEvent;
		this.command = newCommand;
	}
	
	public SEentitySet(Player newPlayer, String newCommand, String[] newArgs) {
		this.player = newPlayer;
		this.command = newCommand;
		this.args = newArgs;
	}

	public SEentitySet() {
	}

	public SEentitySet(SEtrigger newTrigger) {
		this.trigger = newTrigger;
	}

	public SEentitySet(SEcuboid newCuboid) {
		this.cuboid = newCuboid;
	}
	
	public SEentitySet(SEscript newScript) {
		this.script = newScript;
	}
	
	public SEentitySet(SEcondition newCondition) {
		this.condition = newCondition;
	}

	public SEentitySet(PlayerInteractEvent newInteractEvent) {
		this.interactEvent = newInteractEvent;
		this.player = interactEvent.getPlayer();
	}
}
