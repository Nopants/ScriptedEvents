package me.nopants.ScriptedEvents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import me.nopants.ScriptedEvents.SEcondition.logicalOperator;
import me.nopants.ScriptedEvents.SEtrigger.triggerEvent;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SEutils {
	
	private Logger log = Logger.getLogger("Minecraft");
	private SEdataManager SEdata;

	public SEutils(SEdataManager newSEdata) {
		this.SEdata = newSEdata;
	}
	
	/*
	public void MessagePages(CommandSender sender, Map<Integer, String> messages){
		for (int i=0;i<=messages.size();i++) {
			SEmessage(sender, messages.get(i));
		}
		
	}
	*/

	// returns an ItemStack if the player has a the item with the itemID
	public ItemStack searchItem(Player player, int itemID) {
		ItemStack tempItem = null;
		if (player != null) {
			ItemStack[] content = player.getInventory().getContents();
			
			if ((player.getItemInHand()!=null) && (player.getItemInHand().getTypeId() == itemID)) {
				tempItem = player.getItemInHand();
			} else {
				for (int i=0;i<content.length;i++) {
					if ((content[i]!=null) && (content[i].getTypeId() == itemID)) tempItem = content[i];
				}	
			}
		}
		return tempItem;
	}
	
	public int calc(String math) throws Exception {
		int result=0;
		double temp=0;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		temp = Double.valueOf(String.valueOf(engine.eval(math)));
		
		result = (int) temp;
		return result;
	} 
	
	// sends a message, beginning with "SE: " 
	public void SEmessage(CommandSender sender, String message) {
		if (sender != null)
			sender.sendMessage("SE: "+message);
	}
	
	// writes in log
	public void writeinlog(int defcon, String message){
		switch (defcon) {
		case 1:
			log.info(message);
			break;
		case 2:
			log.warning(message);
			break;
		case 3:
			log.severe(message);
			break;
		}
	}
	
	// writes in log, beginning with "SE: "
	public void SElog(int defcon, String message) {
		String prefix = "SE: ";
		switch (defcon) {
		case 1:
			if (message == "Line") log.info("-------------------------------------------------------"); // debug
			else log.info(prefix+message);
			break;
		case 2:
			if (message == "Line") log.warning("-------------------------------------------------------"); // debug
			else log.warning(prefix+message);
			break;
		case 3:
			if (message == "Line") log.severe("-------------------------------------------------------"); // debug
			else log.severe(prefix+message);
			break;
		}
	}
	
	// returns the bigger integer
	public int biggerInt(int var1, int var2) {
		if (var1 >= var2) {
			return var1;
		} else {
			return var2;
		}
	}

	// returns the smaller integer
	public int smallerInt(int var1, int var2) {
		if (var1 <= var2) {
			return var1;
		} else {
			return var2;
		}
	}

	// returns the operator which is represented by the input-string
	public logicalOperator stringToOperator(String input){
		logicalOperator result = null;
		
		if (input.equalsIgnoreCase("and")) result = logicalOperator.and;
		if (input.equalsIgnoreCase("or")) result = logicalOperator.or;
		
		return result;
	}
	
	// returns the event which is represented by the input-string
	public triggerEvent stringToEvent(String input){
		triggerEvent result = null;
		
		if (input.equalsIgnoreCase("onEnter")) result = triggerEvent.onEnter;
		if (input.equalsIgnoreCase("onLeave")) result = triggerEvent.onLeave;
		if (input.equalsIgnoreCase("onInteract")) result = triggerEvent.onInteract;
		if (input.equalsIgnoreCase("onCommand")) result = triggerEvent.onCommand;
		if (input.equalsIgnoreCase("none")) result = triggerEvent.none;
		
		return result;
	}

	// returns the trigger which is represented by the input-string
	public SEtrigger stringToTrigger(String input){
		SEtrigger result = null;
		//trigger1: name:Test1,event:none(),script:none
		//trigger2: name:Test2,event:onInteractAt(),script:myInteractScript
		if (input != null) {
		
			String temp;
			String[] triggerStrings = input.split(",");
			
			// Trigger-Entities
			triggerEvent event;
			SEcuboid triggerCuboid;
			//Location triggerLocation;
			String triggerCommand;
			SEcondition triggerCondition;
			SEscript triggerScript;
			String triggerName;
			
			if (       (triggerStrings.length == 4)
					&& (triggerStrings[0].startsWith("name:"))
					&& (triggerStrings[1].startsWith("event:"))
					&& (triggerStrings[2].startsWith("condition:"))
					&& (triggerStrings[3].startsWith("script:"))) {

				// Name
				triggerName = triggerStrings[0].substring(5);
				//SElog(1, "TriggerName: "+triggerName); // debug
				
				// Event
				temp = triggerStrings[1].substring(6);
				temp = temp.substring(0, temp.indexOf('('));
				event = stringToEvent(temp);
				//SElog(1, "TriggerEvent: "+event); // debug
				
				// Command (temp=command)
				if (event == SEtrigger.triggerEvent.onCommand){
				temp = triggerStrings[1];
					if (!temp.contains("()")) {
						temp = triggerStrings[1].substring(temp.indexOf('('));
						temp = temp.substring(1, temp.length()-1);
						triggerCommand = temp;
						//SElog(1, "TriggerCommand: "+temp); // debug
					} else triggerCommand = null;
				} else triggerCommand = null;
				
				// Cuboid (temp=cuboid-ID)
				if ((event == SEtrigger.triggerEvent.onEnter) || (event == SEtrigger.triggerEvent.onLeave)) {
				temp = triggerStrings[1];
					if (!temp.contains("()")) {
						temp = triggerStrings[1].substring(temp.indexOf('('));
						temp = temp.substring(1, temp.length()-1);
						try {
							triggerCuboid = SEdata.getCuboidList().get(SEdata.searchCuboidList(temp));
							// SElog(1, "TriggerCuboid: "+triggerCuboid.getName()); // debug
						} catch (Exception e) {
							triggerCuboid = null;
						}
					} else triggerCuboid = null;
				} else triggerCuboid = null;

				// Condition (temp=condition-ID)
				if (triggerStrings[2].length()>10) {
					temp = triggerStrings[2].substring(10);
					try {
						if (SEdata.searchConditionList(temp) == -1)
							triggerCondition = null;
						else
							triggerCondition = SEdata.getConditionList().get(SEdata.searchConditionList(temp));
						// SElog(1, "TriggerCondition: "+triggerCondition.getName()); // debug
					} catch (Exception e) {
						triggerCondition = null;
					}	
				} else triggerCondition = null;
				
				// Script (temp=script-ID)
				if (triggerStrings[3].length()>7) {
					temp = triggerStrings[3].substring(7);
					try {
						if (SEdata.searchScriptList(temp) == -1)
							triggerScript = null;
						else
							triggerScript = SEdata.getScriptList().get(SEdata.searchScriptList(temp));
						//SElog(1, "TriggerScript: "+triggerScript.getName()); // debug
					} catch (Exception e) {
						triggerScript = null;
					}	
				} else triggerScript = null;
				
				result = new SEtrigger(new SEentitySet(triggerName, event, triggerCuboid, triggerCondition, triggerScript, triggerCommand));
			} else result = null;
			
		} else result = null;
		
		return result;
	}
	
	// returns the cuboid which is represented by the input-string
	public SEcuboid stringToCuboid(String input){
		SEcuboid result = null;
		// input = world:world,name:Static Cuboid,vertex1:347;92;470,vertex2:350;92;468;
		if (input != null) {
			
			String world;
			String name;
			Location corner1;
			Location corner2;
			String[] cuboidStrings = input.split(",");
			
			if ((cuboidStrings.length == 4)
					&&(cuboidStrings[0].startsWith("world:"))
					&&(cuboidStrings[1].startsWith("name:"))
					&&(cuboidStrings[2].startsWith("vertex1:"))
					&&(cuboidStrings[3].startsWith("vertex2:"))) {
			
				world = cuboidStrings[0].substring(6);
				name = cuboidStrings[1].substring(5);
				corner1 = stringToLocation(cuboidStrings[2].substring(8));
				corner2 = stringToLocation(cuboidStrings[3].substring(8));
				result = new SEcuboid(world, name, corner1, corner2);		
			} else result = null;
			
		} else result = null;
		
		return result;
	}
	
	// returns the location which is represented by the input-string
	public Location stringToLocation(String input) {
		// input = 347;92;470
		if (input != null) {
			Location location = new Location(null, 0, 0, 0);
			String[] locationStrings = input.split(";");
			try {
				location.setX(Double.valueOf(locationStrings[0]));
				location.setY(Double.valueOf(locationStrings[1]));
				location.setZ(Double.valueOf(locationStrings[2]));
			} catch (Exception e) {
				return null;
			}
			return location;
		} else return null;
	}
	
	// returns the player named 'playerName', if he is online 
	public Player stringToPlayer(Player[] onlinePlayers, String playerName) {
		Player result = null;
		//ScriptedEvents.writeInLog(1, "searched Player: "+playerName); // debug
		for (int i = 0; i < onlinePlayers.length ; i++) {
			//ScriptedEvents.writeInLog(1, "checked Player: "+onlinePlayers[i].getName()); // debug
			if (onlinePlayers[i].getName().equalsIgnoreCase(playerName)) {
				result = onlinePlayers[i];
			}
		}
		return result;
	}
	
	// returns a text-version of a location
	public String locationToString(Location location){
		return (location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ());
	}

	
	public Set<String> mapToSet(Map<Integer, String> map) {
		Set<String> result = new HashSet<String>();
		result.addAll(map.values());
		return result;
	}
}
