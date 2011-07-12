package me.nopants.ScriptedEvents;

import java.util.Map;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SEblockListener extends BlockListener{

	private ScriptedEvents plugin;
	//private SEdataManager SEdata;
	//private SEutils utils;
	
	public SEblockListener(ScriptedEvents scriptedEvents) {
		this.plugin = scriptedEvents;
		//SEdata = plugin.SEdata;
		//utils = SEdata.utils;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {		
		//---[ onBlockBreak ]-----------------------------------------------------------//
		// get the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onBlockBreak));
		// release the triggers
		plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(event, event.getBlock().getTypeId(), Integer.valueOf(event.getBlock().getData())));
		//----------------------------------------------------------------------------//
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		//---[ onBlockPlace ]-----------------------------------------------------------//
		// get the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onBlockPlace));
		// release the triggers
		plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(event));
		//----------------------------------------------------------------------------//
	}
	
}
