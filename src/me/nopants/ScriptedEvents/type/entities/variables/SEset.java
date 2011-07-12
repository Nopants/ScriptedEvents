package me.nopants.ScriptedEvents.type.entities.variables;

import java.util.Set;

public class SEset extends SEvariable{
	private static final long serialVersionUID = 1L;

	Set<String> values;

	public SEset(String newName, String newOwner, Set<String> newValues) {
		super(newName, newOwner);
		setValues(newValues);
	}
	
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}
}
