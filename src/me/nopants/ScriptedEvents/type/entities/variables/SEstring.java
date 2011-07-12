package me.nopants.ScriptedEvents.type.entities.variables;

public class SEstring extends SEvariable{
	private static final long serialVersionUID = 1L;
	
	String value;

	public SEstring(String newName, String newOwner, String newValue) {
		super(newName, newOwner);
		this.setValue(newValue);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
