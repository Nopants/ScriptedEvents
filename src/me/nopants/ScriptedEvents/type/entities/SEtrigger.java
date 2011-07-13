package me.nopants.ScriptedEvents.type.entities;

import me.nopants.ScriptedEvents.type.SEentitySet;


public class SEtrigger extends SEentity {
	private static final long serialVersionUID = 1L;
	
	public enum triggerEvent {onEnter, onLeave, onInteract, onCommand, onBlockBreak, onBlockPlace, onRespawn, none};
	triggerEvent event;
	private String triggerCommand;
	private SEcuboid triggerCuboid;
	private SEscript triggerScript;
	private SEcondition triggerCondition;
	
	public SEtrigger(SEentitySet entitySet, String newOwner){
		super(entitySet.name, newOwner);
		this.event = entitySet.triggerEvent;
		this.triggerCuboid = entitySet.cuboid;
		this.triggerCondition = entitySet.condition;
		this.triggerScript = entitySet.script;
		this.triggerCommand = entitySet.command;
	}
	
	/*
	public void setTriggerLocation(Location newLocation) {
		this.triggerLocation = newLocation;
	}
	
	public Location getTriggerLocation() {
		return this.triggerLocation;
	}
	*/
	
	public void setTriggerCommand(String newCommand) {
		this.triggerCommand = newCommand;
	}
	
	public String getTriggerCommand() {
		return this.triggerCommand;
	}
	
	// returns a text-version of this trigger
	public String toString() {
		String result = null;
		String tempEntity = null;
		String tempScript = "none";
		String tempCondition = "none";
		
		// event:onEnter(1),script:staticScript		
		if ((event == triggerEvent.onEnter) || (event == triggerEvent.onLeave))
			if (triggerCuboid!=null) tempEntity = triggerCuboid.getName();

		// event:onCommand(),script:staticScript
		if (event == triggerEvent.onCommand)
			if (triggerCommand!=null) tempEntity = triggerCommand;

		// condition-ID
		if (triggerCondition!=null) tempCondition = triggerCondition.getName();
		
		// script-ID
		if (triggerScript!=null) tempScript = triggerScript.getName();
		
		// name:TestTrigger,event:none(),script:none;
		if (tempEntity == null)
			result = "name:"+this.name+",owner:"+this.owner+",event:"+this.event.toString()+",condition:"+tempCondition+",script:"+tempScript;
		else
			result = "name:"+this.name+",owner:"+this.owner+"event:"+this.event.toString()+"("+tempEntity+"),condition:"+tempCondition+",script:"+tempScript;
		return result;
	}

	// sets the condition which is executed onTriggerRelease
	public void setCondition(SEcondition newCondition) {
		this.triggerCondition = newCondition;
	}
	
	// returns the script which is executed onTriggerRelease 
	public SEcondition getCondition() {
		return triggerCondition;
	}
	
	// sets the script which is executed onTriggerRelease
	public void setScript(SEscript newScript) {
		this.triggerScript = newScript;
	}
	
	// returns the script-name which is executed onTriggerRelease 
	public SEscript getScript() {
		return triggerScript;
	}

	// sets the triggerEntities ID
	public void setTriggerCuboid(SEcuboid newCuboid) {
		this.triggerCuboid = newCuboid;
	}
	
	// returns the triggerEntities ID
	public SEcuboid getTriggerCuboid() {
		return triggerCuboid;
	}

	// sets the event on which the trigger is released
	public void setEvent(triggerEvent newEvent){
		this.event = newEvent;
	}
	
	// returns the event on which the trigger is released
	public triggerEvent getEvent(){
		return event;
	}
	
}
