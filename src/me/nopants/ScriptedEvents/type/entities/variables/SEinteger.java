package me.nopants.ScriptedEvents.type.entities.variables;

import me.nopants.ScriptedEvents.SEutils;
import me.nopants.ScriptedEvents.type.entities.SEentity;

public class SEinteger extends SEentity{
	private static final long serialVersionUID = 1L;
	
	int value;

	public SEinteger(String newName, String newOwner, int newValue) {
		this.setName(newName);
		this.setOwner(newOwner);
		this.setValue(newValue);
		SEutils.SElog(1, this.getName() +", "+ this.getOwner() +", "+ this.getValue());
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
