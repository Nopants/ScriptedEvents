package me.nopants.ScriptedEvents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEcondition;
import me.nopants.ScriptedEvents.type.entities.SEcuboid;
import me.nopants.ScriptedEvents.type.entities.SEscript;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;
import me.nopants.ScriptedEvents.type.entities.SEcondition.logicalOperator;
import me.nopants.ScriptedEvents.type.entities.SEtrigger.triggerEvent;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SEutils {
	
	
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

	public int countChar(String input, char c) {
		int result = 0;
		for (int i=0; i<input.length();i++){
			if (input.charAt(i) == c)
				result++;
		}
		return result;
	}
	
	// returns the distance from one location to another in blocks
	public int getDist(Location location2, Location location1) {
		Location playerLocation = new Location(location1.getWorld(),location1.getBlockX(),location1.getBlockY(),location1.getBlockZ());
		Location targetLocation = new Location(location2.getWorld(),location2.getBlockX(),location2.getBlockY(),location2.getBlockZ());
		int playerX = playerLocation.getBlockX();
		int playerY = playerLocation.getBlockY();
		int playerZ = playerLocation.getBlockZ();
		int targetX = targetLocation.getBlockX();
		int targetY = targetLocation.getBlockY();
		int targetZ = targetLocation.getBlockZ();

		double step = Math.sqrt((playerX - targetX) * (playerX - targetX)
				+ (playerY - targetY) * (playerY - targetY)
				+ (playerZ - targetZ) * (playerZ - targetZ));
		
		//ScriptedEvents.writeInLog(1, "Line"); // debug
		//ScriptedEvents.writeInLog(1, "PlayerLocation: "+parser.locationToString(playerLocation)); // debug
		//ScriptedEvents.writeInLog(1, "TargetLocation: "+parser.locationToString(targetLocation)); // debug
		
		return new Double(step).intValue();
	}
	
	// returns an ItemStack if the player has a the item with the itemID
	public ItemStack searchItem(Player player, int itemID, int amount) {
		ItemStack tempItem = null;
		if (player != null) {
			
			if ((player.getItemInHand()!=null) && (player.getItemInHand().getTypeId() == itemID) && (player.getItemInHand().getAmount() >= amount)) {
				tempItem = player.getItemInHand();
			} else {
				for (int i=39;i>=0;i--) {
					//SElog(1, "i: "+i); // debug
					if ((player.getInventory().getItem(i)!=null) && (player.getInventory().getItem(i).getTypeId() == itemID) && (player.getInventory().getItem(i).getAmount() >= amount))
						tempItem = player.getInventory().getItem(i);
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
		Logger log = Logger.getLogger("Minecraft");
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
	static public void SElog(int defcon, String message) {
		Logger log = Logger.getLogger("Minecraft");
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
		if (input.equalsIgnoreCase("onBlockBreak")) result = triggerEvent.onBlockBreak;
		if (input.equalsIgnoreCase("onBlockPlace")) result = triggerEvent.onBlockPlace;
		if (input.equalsIgnoreCase("onRespawn")) result = triggerEvent.onRespawn;
		if (input.equalsIgnoreCase("none")) result = triggerEvent.none;
		
		return result;
	}

	// returns the trigger which is represented by the input-string
	public SEtrigger stringToTrigger(String input, String pack){
		SEtrigger result = null;
		String prefix;
		//trigger1: name:Test1,event:none(),script:none
		//trigger2: name:Test2,event:onInteractAt(),script:myInteractScript
		
		Map<String, SEcuboid> allCuboids = SEdata.getAllCuboids();
		Map<String, SEscript> allScripts = SEdata.getAllScripts();
		Map<String, SEcondition> allConditions= SEdata.getAllConditions();
		
		//SEutils.SElog(1, "stringToTrigger: "+input);
		
		if (pack==null) {
			prefix = "";
		} else {
			prefix = pack+".";
		}
		
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
			String triggerOwner;
			
			if (       (triggerStrings.length == 5)
					&& (triggerStrings[0].startsWith("name:"))
					&& (triggerStrings[1].startsWith("owner:"))
					&& (triggerStrings[2].startsWith("event:"))
					&& (triggerStrings[3].startsWith("condition:"))
					&& (triggerStrings[4].startsWith("script:"))) {

				// Name
				triggerName = triggerStrings[0].substring(5);
				//SElog(1, "Name: "+triggerName); // debug
				
				// Owner
				triggerOwner = triggerStrings[1].substring(6);
				//SElog(1, "TriggerOwner: "+triggerOwner); // debug
				
				// Event
				temp = triggerStrings[2].substring(6);
				if (temp.contains("(") && temp.contains(")"))
					temp = temp.substring(0, temp.indexOf('('));
				event = stringToEvent(temp);
				//SElog(1, "TriggerEvent: "+event); // debug
				
				// Command (temp=command)
				if (event == SEtrigger.triggerEvent.onCommand){
				temp = triggerStrings[2];
					if (!temp.contains("()")) {
						temp = triggerStrings[2].substring(temp.indexOf('('));
						temp = temp.substring(1, temp.length()-1);
						triggerCommand = temp;
						//SElog(1, "TriggerCommand: "+temp); // debug
					} else triggerCommand = null;
				} else triggerCommand = null;
				
				// Cuboid (temp=cuboid-name)
				if ((event == SEtrigger.triggerEvent.onEnter) || (event == SEtrigger.triggerEvent.onLeave)) {
				temp = triggerStrings[2];
					if (!temp.contains("()")) {
						temp = triggerStrings[2].substring(temp.indexOf('('));
						temp = temp.substring(1, temp.length()-1);
						try {
							triggerCuboid = allCuboids.get(prefix+temp);
							//SElog(1, "TriggerCuboid: "+triggerCuboid.getName()); // debug
						} catch (Exception e) {
							triggerCuboid = null;
						}
					} else triggerCuboid = null;
				} else triggerCuboid = null;

				// Condition (temp=condition-name)
				if (triggerStrings[3].length()>10) {
					temp = triggerStrings[3].substring(10);
					try {
						triggerCondition = allConditions.get(prefix+temp);
						//SElog(1, "TriggerCondition: "+triggerCondition.getName()); // debug
					} catch (Exception e) {
						triggerCondition = null;
					}	
				} else triggerCondition = null;
				
				// Script (temp=script-name)
				if (triggerStrings[4].length()>7) {
					temp = triggerStrings[4].substring(7);
					try {
						triggerScript = allScripts.get(prefix+temp);
						//SElog(1, "TriggerScript: "+triggerScript.getName()); // debug
					} catch (Exception e) {
						triggerScript = null;
					}	
				} else triggerScript = null;
				
				result = new SEtrigger(new SEentitySet(triggerName, event, triggerCuboid, triggerCondition, triggerScript, triggerCommand), triggerOwner, pack);
			} else result = null;
			
		} else result = null;
		
		return result;
	}
	
	// returns the cuboid which is represented by the input-string
	public SEcuboid stringToCuboid(String input, String pack){
		SEcuboid result = null;
		// input = world:world,name:Static Cuboid,vertex1:347;92;470,vertex2:350;92;468;
		if (input != null) {
			
			String world;
			String name;
			String owner;
			Location corner1;
			Location corner2;
			String[] cuboidStrings = input.split(",");
			
			if ((cuboidStrings.length == 5)
					&&(cuboidStrings[0].startsWith("world:"))
					&&(cuboidStrings[1].startsWith("name:"))
					&&(cuboidStrings[2].startsWith("owner:"))
					&&(cuboidStrings[3].startsWith("vertex1:"))
					&&(cuboidStrings[4].startsWith("vertex2:"))) {
			
				world = cuboidStrings[0].substring(6);
				name = cuboidStrings[1].substring(5);
				owner = cuboidStrings[2].substring(6);
				corner1 = stringToLocation(cuboidStrings[3].substring(8));
				corner2 = stringToLocation(cuboidStrings[4].substring(8));
				result = new SEcuboid(world, name, owner, corner1, corner2, pack);		
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
		//SElog(1,     "searched Player: "+playerName); // debug
		for (int i = 0; i < onlinePlayers.length ; i++) {
			//SElog(1, " checked Player: "+onlinePlayers[i].getName()); // debug
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

	public int findBracket(String input, int pos) {
		int result = -1; 
		// BRACKET check
		int x=0;
		for (int i=pos; i<input.length();i++) {
			if (input.charAt(i)=='(')
				x++;
			if (input.charAt(i)==')')
				x--;
			if (x==0) {
				result = i;
				//utils.SElog(1, "jeah: "+i); // debug
				i = input.length();
			}
		}
		return result;
	}
	
	public int findAngleBracket(String input, int pos) {
		int result = -1; 
		// BRACKET check
		int x=0;
		for (int i=pos; i<input.length();i++) {
			if (input.charAt(i)=='<')
				x++;
			if (input.charAt(i)=='>')
				x--;
			if (x==0) {
				result = i;
				//utils.SElog(1, "jeah: "+i); // debug
				i = input.length();
			}
		}
		return result;
	}

	// converts the sender to a player or sends the console a message
	public Player senderToPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			//utils.SElog(1, "This command is for players only!");
			return null;
		}
	}
	
	public String getSenderName(CommandSender sender) {
		String result;
		Player player = senderToPlayer(sender);
		if (player != null)
			result = player.getName();
		else
			result = "Console";
		return result;		
	}

}
