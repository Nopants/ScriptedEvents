package me.nopants.ScriptedEvents;

import java.util.HashMap;
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
	public void releaseTriggerList(Map<Integer, SEtrigger> triggerList, SEentitySet entitySet) {
		for (int i=1; i <= triggerList.size(); i++) {
			SEinterpreter interpreter = new SEinterpreter(plugin, triggerList.get(i), entitySet, SEinterpreter.kindType.script);
			interpreter.start();
		}
		
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
				
				//-------------------------------------//
				// triggerEvent has no input
				//-------------------------------------//
				if (triggerEvent != SEtrigger.triggerEvent.onCommand &&
					triggerEvent != SEtrigger.triggerEvent.onEnter &&
					triggerEvent != SEtrigger.triggerEvent.onLeave) {
					
					if (triggerList.get(i).getEvent().equals(triggerEvent)){
						relevantTrigger.put(x, triggerList.get(i));
						x++;
					}
				} // no input
				
			} // if: this trigger is in the List
		} // loop: checked every trigger in the List
		
		return relevantTrigger;
	}	
}
