package me.nopants.ScriptedEvents.type.entities.variables;

import me.nopants.ScriptedEvents.type.entities.SEentity;

public class SEinteger extends SEentity{
	private static final long serialVersionUID = 1L;
	
	int value;

	public SEinteger(String newName, String newOwner, int newValue) {
		super(newName, newOwner);
		this.setValue(newValue);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
