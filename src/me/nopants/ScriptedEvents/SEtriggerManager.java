package me.nopants.ScriptedEvents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import me.nopants.ScriptedEvents.SEcondition.logicalOperator;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class SEtriggerManager {
	private SEdataManager SEdata;
	private SEutils utils;
	private ScriptedEvents plugin;
	
	public SEtriggerManager(ScriptedEvents scriptedEvents) {
		plugin = scriptedEvents;
		SEdata = plugin.SEdata;
		utils = plugin.SEdata.utils;
	}

	// releases a list of triggers
	public void releaseTriggerList(Map<Integer, SEtrigger> triggerList, SEentitySet entitySet) {
		
		for (int i=1; i <= triggerList.size(); i++) {
			entitySet.trigger = triggerList.get(i);
			releaseTrigger(entitySet);	
		}
		
	}
	
	// releases a Trigger
	public void releaseTrigger(SEentitySet entitySet) {
		SEtrigger trigger = entitySet.trigger;
		Player triggeringPlayer = entitySet.player;
		//SEcuboid triggeringCuboid = entitySet.cuboid;
		//int itemID = entitySet.itemID;
		boolean check = true;
		
		// if there is a matching trigger
		if (trigger!=null) {
			if ((trigger.getTriggerCommand() == null) || (trigger.getTriggerCommand() != null && plugin.commander.checkPermission(triggeringPlayer, "se.customCMD."+trigger.getTriggerCommand()))) {
				// get the condition that trigger is pointing at and check it
				if (trigger.getCondition() != null) {
					Map<Integer, String> conditionList = trigger.getCondition().getConditionList();
					check = checkConditionList(trigger.getCondition().getOperator(), conditionList, entitySet); //new SEentitySet(triggeringPlayer, itemID, triggeringCuboid));
				}
				
				// get the script that trigger is pointing at
				if ((trigger.getScript()!=null) && check) {
					executeScript(trigger.getScript(), entitySet, 0);
					
					if (trigger.getScript() == null)
						if (SEdata.getDebugees(triggeringPlayer)) utils.SEmessage(triggeringPlayer, "Script not found!"); // debug
				}
			}
		}
	}
	
	// resolves all variables in a String
	public String resolveVariables(String input, SEentitySet entitySet) {
		String result = input;
		Map<String,String> stringList = SEdata.getStringVarList();
		Map<String,Integer> intList = SEdata.getIntVarList();
		Random generator = new Random();
		
		String[] args = entitySet.args;
		SEcuboid triggeringCuboid = entitySet.cuboid;
		Player triggeringPlayer   = entitySet.player;
		Location blockLocation = entitySet.location;
		Server server = null;
		String worldName = "none";
		
		if (triggeringPlayer!=null) {
			//health = triggeringPlayer.getHealth();
			//itemInHand = triggeringPlayer.getItemInHand().getTypeId();
			server = triggeringPlayer.getServer();
			worldName = triggeringPlayer.getWorld().getName();
		}
		
		// resolve Command arguments
		
		if (args!=null) {
			for (int i=0;i<args.length;i++) {
				if (i==0) {
					while (result.contains("<command>")&&(args[i]!=null)) {
						result = result.replaceAll("<command>", args[i]);
					}	
				} else {
					while (result.contains("<arg"+i+">")&&(args[i]!=null)) {
						result = result.replaceAll("<arg"+i+">", args[i]);
						//utils.SElog(1, "debug"); // debug
					}
				}
			}
		}
		
		// resolve blockID(<location>)
		if (result.contains("blockID(")) {
			String temp = result.substring(result.indexOf("blockID("), result.indexOf(')', result.indexOf("blockID("))+1);
			String[] tempInput = result.substring(result.indexOf("blockID(")+8, result.indexOf(')',result.indexOf("blockID(")+1)).split(",");
			World tempWorld = server.getWorld(tempInput[0]);
			Location tempLocation = utils.stringToLocation(tempInput[1]);
			while (result.contains("blockID(") && (tempWorld != null) && (tempLocation != null)) {
				result = result.replace(temp, String.valueOf(tempWorld.getBlockTypeIdAt(tempLocation)));				
			}	
		}
		
		// resolve blockData(<location>)
		if (result.contains("blockData(")) {
			String temp = result.substring(result.indexOf("blockData("), result.indexOf(')', result.indexOf("blockData("))+1);
			String[] tempInput = result.substring(result.indexOf("blockData(")+10, result.indexOf(')',result.indexOf("blockData(")+1)).split(",");
			World tempWorld = server.getWorld(tempInput[0]);
			Location tempLocation = utils.stringToLocation(tempInput[1]);
			while (result.contains("blockData(") && (tempWorld != null) && (tempLocation != null)) {
				result = result.replace(temp, String.valueOf(tempWorld.getBlockAt(tempLocation).getData()));				
			}	
		}
		
		// Interact-related Variables
		if (entitySet.interactEvent != null) {
			boolean rightClick       = (entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_AIR)||entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK));
			
			String clickedLocation;
			if (entitySet.interactEvent.hasBlock())
				clickedLocation = utils.locationToString(entitySet.interactEvent.getClickedBlock().getLocation());
			else
				clickedLocation = "none";
			
			// resolve clickedLocation		
			while (result.contains("<clickedLocation>")) {
				result = result.replaceAll("<clickedLocation>", clickedLocation);
			}
			// resolve rightClick		
			while (result.contains("<rightClick>")) {
				result = result.replaceAll("<rightClick>", String.valueOf(rightClick));
			}
		}
		
		// resolve randomInt
		while (result.contains("<randomInt>")) {
			int randomInt;
			
			if (entitySet.randomMax == 0)
				randomInt = generator.nextInt()+entitySet.randomMin;
			else
				randomInt = generator.nextInt(entitySet.randomMax)+entitySet.randomMin;
			
			result = result.replaceFirst("<randomInt>", String.valueOf(randomInt));
			
			//utils.SElog(1, "randomInt: "+String.valueOf(randomInt)); // debug
		}
		
		// resolve blockLocation
		while (result.contains("<blockLocation>")&&(blockLocation!=null)) {
			result = result.replaceAll("<blockLocation>", utils.locationToString(blockLocation));
		}
		
		// resolve triggeringPlayer
		while (result.contains("<triggeringPlayer>")&&(triggeringPlayer!=null)) {
			result = result.replaceAll("<triggeringPlayer>", triggeringPlayer.getName());
		}
		// resolve world
		while (result.contains("<world>")&&(triggeringPlayer!=null)) {
			result = result.replaceAll("<world>", worldName);
		}
		
		// resolve triggeringCuboid
		while ((result.contains("<triggeringCuboid>")) && (triggeringCuboid!=null)) {
			result = result.replaceAll("<triggeringCuboid>", triggeringCuboid.getName());
		}
		
		// resolve user-defined String variables
		if (stringList!=null) {
			for ( Iterator<String> i = stringList.keySet().iterator(); i.hasNext(); )
			{
				String tempVar = (String) i.next();
				
				while (result.contains("<"+ tempVar +">"))
					result = result.replaceAll("<"+ tempVar +">", stringList.get(tempVar));
			}
		}
		
		// resolve user-defined Integer variables
		if (intList!=null) {
			for ( Iterator<String> i = intList.keySet().iterator(); i.hasNext(); )
			{
				String tempVar = (String) i.next();
				
				while (result.contains("<"+ tempVar +">"))
					result = result.replaceAll("<"+ tempVar +">", String.valueOf(intList.get(tempVar)));
			}
		}
		
		// resolve itemInHand(<player>)
		while (result.contains("itemInHand(")) {
			String temp = result.substring(result.indexOf("itemInHand("), result.indexOf(')', result.indexOf("itemInHand("))+1);
			String playerName = temp.substring(temp.indexOf('(')+1, temp.indexOf(')'));
			Player targetPlayer = utils.stringToPlayer(server.getOnlinePlayers(), playerName);
			
			if ((targetPlayer != null)&&(targetPlayer.getItemInHand()!=null)) {
				result = result.replace(temp, String.valueOf(targetPlayer.getItemInHand().getTypeId()));
			}
			
		}
		
		// resolve health(<player>)
		if (result.contains("health(")) {
			String temp = result.substring(result.indexOf("health("), result.indexOf(')', result.indexOf("health("))+1);
			String playerName = temp.substring(temp.indexOf("(")+1, temp.indexOf(')'));
			Player targetPlayer = utils.stringToPlayer(server.getOnlinePlayers(), playerName);
			while (result.contains("health(") && targetPlayer != null) {
				result = result.replace(temp, String.valueOf(targetPlayer.getHealth()));				
			}	
		}
		
		// resolve isInBed(<player>)
		if (result.contains("isInBed(")) {
			String temp = result.substring(result.indexOf("isInBed("), result.indexOf(')', result.indexOf("isInBed("))+1);
			String playerName = temp.substring(temp.indexOf('(')+1, temp.indexOf(')'));
			Player targetPlayer = utils.stringToPlayer(server.getOnlinePlayers(), playerName);
			while (result.contains("isInBed(") && targetPlayer != null) {
				result = result.replace(temp, String.valueOf(targetPlayer.isSleeping()));				
			}	
		}

		// resolve time(<world>)
		if (result.contains("time(")) {
			String temp = result.substring(result.indexOf("time("), result.indexOf(')', result.indexOf("time("))+1);
			String tempWorldName = temp.substring(temp.indexOf('(')+1, temp.indexOf(')'));
			World targetWorld = server.getWorld(tempWorldName);
			while (result.contains("time(") && targetWorld != null) {
				result = result.replace(temp, String.valueOf(targetWorld.getTime()));				
			}	
		}
		
		// resolve playerLocation(<player>)
		if (result.contains("playerLocation(")) {
			String temp = result.substring(result.indexOf("playerLocation("), result.indexOf(')', result.indexOf("playerLocation("))+1);
			String playerName = temp.substring(temp.indexOf('(')+1, temp.indexOf(')'));
			Player targetPlayer = utils.stringToPlayer(server.getOnlinePlayers(), playerName);
			while (result.contains("playerLocation(") && targetPlayer != null) {
				result = result.replace(temp, utils.locationToString(targetPlayer.getLocation()));				
			}	
		}
		
		return result;
	}
	
	// check a condition-String
	public boolean checkCondition(String condition, SEentitySet entitySet){
		boolean result = false; // turn 'true', if you want to get 'true' for not recognized conditions
		boolean checked = false;
		
		// resolve Variables first
		condition = resolveVariables(condition, entitySet);
		
		// check check(<condition-ID>)
		if (condition.contains("check(") && !checked) {	
			condition = condition.substring(condition.indexOf('(')+1,condition.lastIndexOf(')'));
			//condition = condition.substring(1, condition.length()-1);
			try {
				SEcondition tempCondition = plugin.SEdata.getConditionByID(Integer.valueOf(condition));
				result = checkConditionList(tempCondition.getOperator(), tempCondition.getConditionList(), entitySet);	
			} catch (Exception e) {
				result = false;
			}
			checked = true;
		}
		
		// check and(<condition1>,<condition2>)
		if ((condition.contains("and(") || condition.contains("or(")) && !checked) {
			String oldCondition = condition;
			String tempCondition = "";
			String tempBracket = "";
			String bracketreplacement = "";
			int startIndex = 0;
			

			condition = condition.substring(condition.indexOf('(')); // Schritt 1: equals(Hans,Hans),bigger(2,1))
			condition = condition.substring(1, condition.length()-1); // Schritt 2: equals(Hans,Hans),bigger(2,1)
			
			// Schritt 3: equalsXXXXXXXXXXX,biggerXXXXX			
			tempCondition = condition;
			while ((tempCondition.contains("("))&&(tempCondition.contains(")"))) {
				tempBracket = tempCondition.substring(tempCondition.indexOf("("), tempCondition.indexOf(")")+1);
				bracketreplacement = "";
				for (int i=0; i < tempBracket.length(); i++) {
					bracketreplacement = bracketreplacement+"X"; 
				}
				tempCondition = tempCondition.replace(tempBracket, bracketreplacement);
			}			
			
			// Schritt 4: 'equalsXXXXXXXXXXX' und 'biggerXXXXX'
			String[] conditionStrings = tempCondition.split(",");
			
			// Schritt 5: 'equals(Hans,Hans)' und 'bigger(2,1)'
			for (int i=0; i<conditionStrings.length;i++){
				conditionStrings[i] = condition.substring(startIndex, startIndex + conditionStrings[i].length());
				startIndex = startIndex + conditionStrings[i].length() + 1;
			}
			
			// do check
			if (conditionStrings.length==2) {
				if (oldCondition.contains("and(")) {
					result = (checkCondition(conditionStrings[0], entitySet) && checkCondition(conditionStrings[1], entitySet));
				}
				if (oldCondition.contains("or(")) {
					result = (checkCondition(conditionStrings[0], entitySet) || checkCondition(conditionStrings[1], entitySet));
				}
			}
			checked = true;
		}
		
		// check hasItem()
		if (condition.contains("hasItem(") && !checked) {
			try {
				String[] input = condition.substring(condition.indexOf('(')+1, condition.indexOf(')')).split(",");
				if (input.length==2) {
					Player targetPlayer = utils.stringToPlayer(entitySet.player.getServer().getOnlinePlayers(), input[0]);
					ItemStack searchedItem = utils.searchItem(targetPlayer, Integer.valueOf(input[1]));
					
					result = ( (targetPlayer != null)
							&& (searchedItem != null) );	
				}
				
				if (input.length==3) {
					Player targetPlayer = utils.stringToPlayer(entitySet.player.getServer().getOnlinePlayers(), input[0]);
					ItemStack searchedItem = utils.searchItem(targetPlayer, Integer.valueOf(input[1]));
					
					result = ( (targetPlayer != null)
							&& (searchedItem != null)
							&& (searchedItem.getAmount() >= Integer.valueOf(input[2])) );	
				}
				
				
			} catch (Exception e) {}
			
			checked = true;
		}
		
		// check equals(<x>,<y>)
		if (condition.contains("equals(") && !checked) {	
			condition = condition.substring(condition.indexOf('('));
			condition = condition.substring(1, condition.length()-1);
			String[] conditionStrings = condition.split(",");
			
			if (conditionStrings.length==2) {
				result = conditionStrings[0].equals(conditionStrings[1]);
			}
			checked = true;
		}
		
		// check bigger(<x>,<y>)
		if (condition.contains("bigger(") && !checked) {	
			condition = condition.substring(condition.indexOf('('));
			condition = condition.substring(1, condition.length()-1);
			String[] conditionStrings = condition.split(",");
			
			if (conditionStrings.length==2) {
				try {
					result = (Integer.valueOf(conditionStrings[0]) > Integer.valueOf(conditionStrings[1]));
				} catch (Exception e){
					result = false;
				}
				
			}
			checked = true;
		}
		
		return result;
	}
	
	// check a condition-List
	public boolean checkConditionList(logicalOperator operator, Map<Integer, String> conditionList, SEentitySet entitySet) {
		boolean result = false;
		boolean tempResult = false;
		
		if (operator==null) result = true;
		if (operator==logicalOperator.and) result = true;
		if (operator==logicalOperator.or) result = false;
		
		// checks every condition in the list
		for (int i = 1; i <= conditionList.size(); i++) {
			tempResult = checkCondition(conditionList.get(i), entitySet);
			
			if (operator==null) result = (result && tempResult);
			if (operator==logicalOperator.and) result = (result && tempResult);
			if (operator==logicalOperator.or) result = (result || tempResult);
		}

		if (!result)
			if ((entitySet.player!=null) && (SEdata.getDebugees(entitySet.player)))
				utils.SEmessage(entitySet.player, "Conditions = false!"); // debug
		
		return result;
	}

	// execute an action-String
	public void executeAction(String executeAction, SEentitySet entitySet) {
		if (executeAction=="") return;
		// resolve Variables first
		String action = resolveVariables(executeAction, entitySet);
		
		String command = action;
		Player triggeringPlayer = entitySet.player;
		Server server = triggeringPlayer.getServer();
		Player[] onlinePlayers = server.getOnlinePlayers();
		Player targetPlayer = null;
		int itemID = entitySet.itemID;
		SEcuboid triggeringCuboid = entitySet.cuboid;
		
		// resolve setRandomRange()
		if (action.contains("setRandomRange")) {
			try {
				String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");
				if (input.length==1) {
					entitySet.randomMax = Integer.valueOf(input[0]);	
				}
				if (input.length==2) {
					entitySet.randomMin = Integer.valueOf(input[0]);
					entitySet.randomMax = Integer.valueOf(input[1])-entitySet.randomMin+1;
				}
				
				command = null;
			} catch (Exception e) {
				entitySet.randomMax = 0;
				entitySet.randomMin = 0;
			}
		}
		
		// resolve cancelEvent()
		if (action.contains("cancelEvent()")&&entitySet.interactEvent != null) {
			plugin.playerListener.cancel = true;
			command = null;
		}
		
		// resolve messageTo()
		if (action.contains("messageTo(")) {
			targetPlayer = utils.stringToPlayer(onlinePlayers, action.substring(action.indexOf('(')+1, action.indexOf(')')));
			action = action.substring(action.indexOf(")")+2);
			if (targetPlayer != null) {
				targetPlayer.sendMessage(action);
			}
			command = null;
		}
		
		// resolve removeItem()
		if (action.contains("removeItem(")) {
			try {
				String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");
				targetPlayer = utils.stringToPlayer(onlinePlayers, input[0]);
				ItemStack tempItem = utils.searchItem(targetPlayer, Integer.valueOf(input[1]));
				
				int removeAmount = 1;
				if (input.length==3)
					removeAmount = Integer.valueOf(input[2]);
				
				if (tempItem!=null) {
					if (tempItem.getAmount()-removeAmount==0)
						targetPlayer.getInventory().removeItem(tempItem);
					else
						tempItem.setAmount(tempItem.getAmount()-removeAmount);
					command = null;	
				}	
			} catch (Exception e) {}
		}
		
		// resolve removeItemInHand()
		if (action.contains("removeItemInHand(")) {
			targetPlayer = utils.stringToPlayer(onlinePlayers, action.substring(action.indexOf('(')+1, action.indexOf(')')));
			if ((targetPlayer != null)&&(targetPlayer.getItemInHand()!=null)) {
				if (targetPlayer.getItemInHand().getAmount()>1) {
					targetPlayer.getItemInHand().setAmount(targetPlayer.getItemInHand().getAmount()-1);	
				} else {
					targetPlayer.getInventory().removeItem(targetPlayer.getItemInHand());
				}
			}
			command = null;
		}
		
		// resolve changeBlockType()
		if (action.contains("changeBlockType(")) {
			String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");
			server.getWorld(input[0]).getBlockAt(utils.stringToLocation(input[1])).setTypeId(Integer.valueOf(input[2]));
			command = null;
		}
		
		// resolve changeBlockData()
		if (action.contains("changeBlockData(")) {
			String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");			
			server.getWorld(input[0]).getBlockAt(utils.stringToLocation(input[1])).setData(Integer.valueOf(input[2]).byteValue());
			command = null;
		}
		
		// resolve playEffect()
		if (action.contains("playEffect(")) {
			String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");
			targetPlayer = utils.stringToPlayer(onlinePlayers, input[0]);
			Effect effect = Effect.valueOf(input[1]);
			
			/*
			Effect.BOW_FIRE
			Effect.CLICK1
			Effect.CLICK2
			Effect.DOOR_TOGGLE
			Effect.EXTINGUISH
			Effect.RECORD_PLAY
			Effect.SMOKE
			Effect.STEP_SOUND
			*/
			
			targetPlayer.playEffect(targetPlayer.getLocation(), effect, 0);
			
			command = null;
		}
		
		
		// resolve setHealth(<player>,<new value>)
		if (action.contains("setHealth(")) {
			String[] input = action.substring(action.indexOf('(')+1, action.indexOf(')')).split(",");
			targetPlayer = utils.stringToPlayer(onlinePlayers, input[0]);
			try {
				// calculates a String into an Integer
				int newHealth = utils.calc(input[1]);
				if (targetPlayer!=null) {
					targetPlayer.setHealth(newHealth);
				}	
			} catch (Exception e) {
			}
			command = null;
		}
		
		// resolve playerCommand()
		if (action.contains("playerCommand(")) {
			String temp = action.substring(action.indexOf("playerCommand("), action.indexOf(')', action.indexOf("playerCommand("))+1);
			targetPlayer = utils.stringToPlayer(onlinePlayers, temp.substring(temp.indexOf("(")+1, temp.lastIndexOf(')'))); 
			temp = action.substring(action.indexOf(')')+1).trim();
			//utils.SElog(1, targetPlayer.getName()+" "+temp); // debug
			
			if ((targetPlayer != null) && (server != null)) {
				server.dispatchCommand(targetPlayer, temp);
			}
			command = null;
		}
		
		// resolve executeScript()
		if (action.contains("executeScript(")) {
			String temp = action.substring(action.indexOf('(')+1, action.indexOf(')'));
			executeScript(SEdata.getScriptByID(SEdata.searchScriptList(temp)), entitySet, 0);
			command = null;
		}
		
		// resolve trigger()
		if (action.contains("trigger(")) {
			String tempTrigger = action.substring(action.indexOf('(')+1, action.indexOf(')'));
			releaseTrigger(new SEentitySet(SEdata.getTriggerByID(SEdata.searchTriggerList(tempTrigger)), triggeringPlayer, itemID, triggeringCuboid));
			command = null;
		}
		
		// some actions like messageTo() don't have to get dispatched by the console
		if (command != null) {
			server.dispatchCommand(new ConsoleCommandSender(server),command);	
		}
	}
	
	// executes every action in the List
	public Callable<Object> executeScript(SEscript script, SEentitySet entitySet, int offset){
		Map<Integer, String> actionList = script.getAcionList();
		Player triggeringPlayer = entitySet.player;
		
		int actionCount = actionList.size();
		String action;
		
		boolean If=false;
		boolean Then=false;
		boolean check=false;
		boolean loop=false;
		boolean While=false;
		
		int newOffset = 0;
		int delay = 0;
		
		// checks every action in the list
		for (int i = offset+1; i <= actionCount; i++) {
			action = actionList.get(i);
			action = action.trim();
			
			// ======================== //
			// before resolve variables //
			// ======================== //
			
			// doForCuboidBlocks() // <blockLocation> must not be resolved before the iteration!
			if (action.contains("doForCuboidBlocks(")) {
				try {
					String cuboidName = action.substring(action.indexOf('(')+1, action.indexOf(')'));
					SEcuboid tempCuboid = plugin.SEdata.getCuboidByID(SEdata.searchCuboidList(cuboidName));
					action=action.substring(action.indexOf(')')+2);
					
					int smallerX = tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX();
					int smallerY = tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY();
					int smallerZ = tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ();
					int biggerX = tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX();
					int biggerY = tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY();
					int biggerZ = tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ();
					
					for (int x=smallerX; x<=biggerX; x++) {
						for (int y=smallerY; y<=biggerY; y++) {
							for (int z=smallerZ; z<=biggerZ; z++) {
								entitySet.location = new Location(entitySet.player.getWorld(), x, y, z);
								executeAction(action, entitySet);
							}
						}
					}	
				} catch (Exception e) {}
				
				action = "";
			}
			
			action = resolveVariables(action, entitySet);
			
			// ======================= //
			// after resolve variables //
			// ======================= //
			
			// resolve while()
			if (action.contains("while(")) {
				String tempCondition = action.substring(action.indexOf('(')+1, action.lastIndexOf(')'));
				While = checkCondition(tempCondition,entitySet);
				action = "";
			}
			
			//  resolve loop()
			if (action.contains("loop(")) {
				try {
					if (entitySet.cycles==-1)
						entitySet.cycles = Integer.valueOf(action.substring(action.indexOf('(')+1, action.lastIndexOf(')'))); 
					loop = true;
					
					action = "";	
				} catch (Exception e) {}
			}
			
			// since the delay() is critical for the loop it gets resolved here
			if (action.contains("delay(")) {
				try {
					delay = Integer.valueOf(action.substring(action.indexOf('(')+1, action.lastIndexOf(')')));
					//utils.SElog(1, String.valueOf(delay)); // debug
					newOffset = i;
					SEdynamicThread dynamicThread = new SEdynamicThread(newOffset, script, entitySet, delay, plugin);
					dynamicThread.start();
					if (SEdata.getDebugees(triggeringPlayer))
						utils.SEmessage(triggeringPlayer, "Delayed by "+delay/1000+" Seconds!"); // debug
					i = actionCount; // finish loop	
				} catch (Exception e) {}
				
				action = "";
			}
		
			// if - then - else
			if (action.contains("if(")) {
				String temp = action.substring(action.indexOf('(')+1, action.lastIndexOf(')'));
				check = checkCondition(temp, entitySet);
				If = true;
				action = "";
			}
			if (action.contains("then(") && If) {
				
				//utils.SElog(1, String.valueOf(check));
				
				if (check) {
					
					// resolve then(do)
					if (action.contains("then(do)")) {
						if (actionList.get(i-1)!=null && actionList.get(i-1).contains("if(")) {
							int j=i+1;
							int y=1;
							Map<Integer,String> dummyActionList = new HashMap<Integer,String>();
							// utils.SElog(1, "then(do) found"); // debug
							
							while (actionList.get(j)!=null && actionList.get(j).startsWith("| ")) {
								// utils.SElog(1, "action"+y+": "+actionList.get(j).substring(2)); // debug
								dummyActionList.put(y, actionList.get(j).substring(2));
								// utils.SElog(1, dummyActionList.get(y)); // debug
								j++;
								y++;
							}
							SEscript dummyScript = new SEscript(null, null, dummyActionList);
							executeScript(dummyScript,entitySet,0);
						}
					} else executeAction(action.substring(action.indexOf('(')+1, action.lastIndexOf(')')), entitySet);
				
				}
				if (actionList.get(i+1)!=null) {
					Then = If;
				}
				action = "";
			}
			if ((action.contains("else(") && If) && Then) {
				if (!check) {
					
					// utils.SElog(1, "else " + String.valueOf(check)); // debug
					
					// resolve then(do)
					if (action.contains("else(do)")) {
						int j=i+1;
						int y=1;
						Map<Integer,String> dummyActionList = new HashMap<Integer,String>();
						//utils.SElog(1, "else(do) found"); // debug
						
						while (actionList.get(j)!=null && actionList.get(j).startsWith("|")) {
							//utils.SElog(1, "action"+y+": "+actionList.get(j).substring(1)); // debug
							dummyActionList.put(y, actionList.get(j).substring(1).trim());
							j++;
							y++;
						}
						SEscript dummyScript = new SEscript(null, null, dummyActionList);
						executeScript(dummyScript,entitySet,0);
					} else executeAction(action.substring(action.indexOf('(')+1, action.lastIndexOf(')')), entitySet);
				}
				If = false;
				Then = false;
				action = "";
			}
			
			// resolve . <action>
			if (action.startsWith("|")) {
				action="";
			}
			
			executeAction(action, entitySet);
			
			// WHILE
			if (While && (i == actionCount)) {
				executeScript(script,entitySet,0);
			} 
			
			// LOOP
			if ((loop && (entitySet.cycles>0)) && (i == actionCount)) {
				entitySet.cycles = entitySet.cycles -1;
				executeScript(script,entitySet,0);
				//utils.SElog(1, "cycles: "+entitySet.cycles); // debug
			}
			
		}
		return null;
	}
	
	// returns the triggers matching to the event and entities
	public Map<Integer, SEtrigger> getRelevantTriggers(SEentitySet entitySet) {		
		Map<Integer, SEtrigger> relevantTrigger = new HashMap<Integer, SEtrigger>();
		Map<Integer, SEtrigger> triggerList = SEdata.getTriggerList();
		
		SEcuboid triggerCuboid = entitySet.cuboid;
		SEtrigger.triggerEvent triggerEvent = entitySet.triggerEvent;
		//Location triggerLocation = entitySet.location;
		String triggerCommand = entitySet.command;
		
		//SEcuboid tempCuboid;
		int x = 1;
		
		// loop: check every trigger in the List
		for (int i = 1; i <= triggerList.size(); i++) {
			// if there is a trigger in the List 
			if (triggerList.get(i) != null) {
				SEtrigger checkTrigger = triggerList.get(i);
				
				//--------------------------------//
				// triggerEvent is cuboid-related
				//--------------------------------//
				if ((triggerEvent == SEtrigger.triggerEvent.onEnter) || (triggerEvent == SEtrigger.triggerEvent.onLeave)) {
					// if there is a triggerCuboid
					// this check should be needless :-S
					if (triggerCuboid!=null){
						//  if the checkedTrigger contains no cuboid
						if (checkTrigger.getTriggerCuboid() == null) {
							// if the triggerEvent matches the checkTriggers event
							if (triggerEvent == checkTrigger.getEvent()){
								relevantTrigger.put(x, checkTrigger);
								x++;
							}
						//  the checkedTrigger contains a cuboid	
						} else {
							// if the triggerEvent matches the checkTriggers event
							// triggerCuboid matchs the checkTriggers cuboid
							SEcuboid tempCuboid = triggerList.get(i).getTriggerCuboid();
							
							if (tempCuboid!=null) {
								if (   (triggerCuboid.getName().equalsIgnoreCase(tempCuboid.getName()))
										&& (triggerList.get(i).getEvent().equals(triggerEvent))) {
									relevantTrigger.put(x, triggerList.get(i));
									x++;
								}
							}
						}
					}
				} // cuboid-related

				//-------------------------------------//
				// triggerEvent is interaction-related
				//-------------------------------------//
				if (triggerEvent == SEtrigger.triggerEvent.onInteract) {
					
					//utils.SElog(1, "Trigger: "+i); // debug
					
					if (triggerList.get(i).getEvent().equals(triggerEvent)){
						relevantTrigger.put(x, triggerList.get(i));
						x++;
					}
					
					/*
					// if there is NO Location defined in the trigger
					if (triggerList.get(i).getTriggerLocation() == null) {
						
					} else {
						// there is a Location defined in the Trigger
						// check if it matches the triggerLocation
						if ((triggerList.get(i).getEvent().equals(triggerEvent))
								&&(triggerLocation.getBlockX() == triggerList.get(i).getTriggerLocation().getBlockX())
								&&(triggerLocation.getBlockY() == triggerList.get(i).getTriggerLocation().getBlockY())
								&&(triggerLocation.getBlockZ() == triggerList.get(i).getTriggerLocation().getBlockZ())){
							relevantTrigger.put(x, triggerList.get(i));
							x++;
						}
					}
					*/
				} // interaction-related
				
				//-------------------------------------//
				// triggerEvent is command-related
				//-------------------------------------//
				if (triggerEvent == SEtrigger.triggerEvent.onCommand) {
					// if there is NO command defined in the trigger
					if (triggerList.get(i).getTriggerCommand() == null) {
						/* This would override ALL commands!!!
						if (triggerList.get(i).getEvent().equals(triggerEvent)){
							relevantTrigger.put(x, triggerList.get(i));
							x++;
						}*/
					} else {
						// there is a Command defined in the Trigger
						// check if it matches the triggerCommand
						if ((triggerList.get(i).getEvent().equals(triggerEvent))
								&& (triggerCommand.equalsIgnoreCase(checkTrigger.getTriggerCommand()))){
							relevantTrigger.put(x, triggerList.get(i));
							x++;
						}
					}
				} // Command-related
				
			} // if: this trigger is in the List
		} // loop: checked every trigger in the List
		
		return relevantTrigger;
	}	
}
