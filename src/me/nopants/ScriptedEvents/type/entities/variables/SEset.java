package me.nopants.ScriptedEvents.type.entities.variables;

import java.util.Set;

public class SEset extends SEvariable{
	private static final long serialVersionUID = 1L;

	Set<String> values;

	public SEset(String newName, String newOwner, Set<String> newValues, String newPack) {
		super(newName, newOwner, newPack);
		setValues(newValues);
	}
	
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}
}
