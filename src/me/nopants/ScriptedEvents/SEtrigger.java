package me.nopants.ScriptedEvents;

public class SEtrigger {
	public enum triggerEvent {onEnter, onLeave, onInteract, onCommand, none};
	triggerEvent event;
	private String name;
	private String triggerCommand;
	private SEcuboid triggerCuboid;
	private SEscript triggerScript;
	private SEcondition triggerCondition;
	
	public SEtrigger(SEentitySet entitySet){
		this.name = entitySet.name;
		this.event = entitySet.triggerEvent;
		this.triggerCuboid = entitySet.cuboid;
		this.triggerCondition = entitySet.condition;
		this.triggerScript = entitySet.script;
		this.triggerCommand = entitySet.command;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public String getName() {
		return  this.name;
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
		String tempEntity = "";
		String tempScript = "none";
		String tempCondition = "none";
		
		// event:onEnter(1),script:staticScript		
		if ((event == triggerEvent.onEnter) || (event == triggerEvent.onLeave))
			if (triggerCuboid!=null) tempEntity = triggerCuboid.getName();

		/*
		// event:onInteract(),script:staticScript
		if (event == triggerEvent.onInteract)
			if (triggerLocation!=null) tempEntity = (triggerLocation.getBlockX()+";"+triggerLocation.getBlockY()+";"+triggerLocation.getBlockZ());
		*/
		
		// event:onCommand(),script:staticScript
		if (event == triggerEvent.onCommand)
			if (triggerCommand!=null) tempEntity = triggerCommand;

		// condition-ID
		if (triggerCondition!=null) tempCondition = triggerCondition.getName();
		
		// script-ID
		if (triggerScript!=null) tempScript = triggerScript.getName();
		
		// name:TestTrigger,event:none(),script:none;
		result = "name:"+this.name+",event:"+this.event.toString()+"("+tempEntity+"),condition:"+tempCondition+",script:"+tempScript;
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
