package me.nopants.ScriptedEvents.type.entities.variables;

public class SEinteger extends SEvariable{
	private static final long serialVersionUID = 1L;
	
	int value;

	public SEinteger(String newName, String newOwner, int newValue, String newPack) {
		super(newName, newOwner, newPack);
		this.setValue(newValue);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
