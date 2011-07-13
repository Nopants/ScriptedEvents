package me.nopants.ScriptedEvents.type.entities;

import java.io.Serializable;

public class SEentity implements Serializable {

	private static final long serialVersionUID = 1L;
	String name;
	String owner;
		
	public SEentity(String newName, String newOwner) {
		this.setName(newName);
		this.setOwner(newOwner);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
