package me.nopants.ScriptedEvents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/*
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
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
*/

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEcuboid;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;

public class SEtriggerManager {
	private SEdataManager SEdata;
	//private SEutils utils;
	private ScriptedEvents plugin;
	
	
	public SEtriggerManager(ScriptedEvents scriptedEvents) {
		plugin = scriptedEvents;
		SEdata = plugin.SEdata;
		//utils = plugin.SEdata.utils;
		
	}
	
	// releases a list of triggers
	public void releaseTriggerList(Map<String, SEtrigger> triggerList, SEentitySet entitySet) {
		Iterator<String> lauf = triggerList.keySet().iterator();
		while (lauf.hasNext()) {
			SEtrigger tempTrigger = triggerList.get(lauf.next());
			if (tempTrigger.getScript() != null) {
				SEinterpreter interpreter = new SEinterpreter(plugin, tempTrigger, entitySet, SEinterpreter.kindType.script, tempTrigger.getPack());
				interpreter.start();
			}
		}
	}
	
	// returns the triggers matching to the event and entities
	public Map<String, SEtrigger> getRelevantTriggers(SEentitySet entitySet) {		
		Map<String, SEtrigger> relevantTrigger = new HashMap<String, SEtrigger>();
		Map<String, SEtrigger> allTriggerLists = SEdata.getAllTriggers();
		
		SEcuboid triggerCuboid = entitySet.cuboid;
		SEtrigger.triggerEvent triggerEvent = entitySet.triggerEvent;
		//Location triggerLocation = entitySet.location;
		String triggerCommand = entitySet.command;
		
		//SEcuboid tempCuboid;
		int x = 1;
		
		// loop: check every trigger in the List
		
		Iterator<String> lauf = allTriggerLists.keySet().iterator();
		while(lauf.hasNext()) {
			SEtrigger checkTrigger = allTriggerLists.get(lauf.next());
			
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
							relevantTrigger.put(checkTrigger.getName(), checkTrigger);
							x++;
						}
					//  the checkedTrigger contains a cuboid	
					} else {
						// if the triggerEvent matches the checkTriggers event
						// triggerCuboid matchs the checkTriggers cuboid
						SEcuboid tempCuboid = checkTrigger.getTriggerCuboid();
						
						if (tempCuboid!=null) {
							if (   (triggerCuboid.getName().equalsIgnoreCase(tempCuboid.getName()))
									&& (checkTrigger.getEvent().equals(triggerEvent))) {
								relevantTrigger.put(checkTrigger.getName(), checkTrigger);
								x++;
							}
						}
					}
				}
			} // cuboid-related

			//-------------------------------------//
			// triggerEvent is command-related
			//-------------------------------------//
			if (triggerEvent == SEtrigger.triggerEvent.onCommand) {
				// if there is NO command defined in the trigger
				if (checkTrigger.getTriggerCommand() == null) {
					/* This would override ALL commands!!!
					if (triggerList.get(i).getEvent().equals(triggerEvent)){
						relevantTrigger.put(x, triggerList.get(i));
						x++;
					}*/
				} else {
					// there is a Command defined in the Trigger
					// check if it matches the triggerCommand
					if ((checkTrigger.getEvent().equals(triggerEvent))
							&& (triggerCommand.equalsIgnoreCase(checkTrigger.getTriggerCommand()))){
						relevantTrigger.put(checkTrigger.getName(), checkTrigger);
						x++;
					}
				}
			} // Command-related
			
			//-------------------------------------//
			// triggerEvent has no input
			//-------------------------------------//
			if (triggerEvent != SEtrigger.triggerEvent.onCommand &&
				triggerEvent != SEtrigger.triggerEvent.onEnter &&
				triggerEvent != SEtrigger.triggerEvent.onLeave) {
				
				if (checkTrigger.getEvent().equals(triggerEvent)){
					relevantTrigger.put(checkTrigger.getName(), checkTrigger);
					x++;
				}
			} // no input
		}
		
		return relevantTrigger;
	}	
}
