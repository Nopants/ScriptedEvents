package me.nopants.ScriptedEvents.type.entities;

import java.io.Serializable;

public class SEentity implements Serializable {

	private static final long serialVersionUID = 1L;
	String name;
	String owner;
	String pack;
	
	public SEentity(String newName, String newOwner) {
		this.setName(newName);
		this.setOwner(newOwner);
	}
	
	public SEentity(String newName, String newOwner, String newPack) {
		this.setName(newName);
		this.setOwner(newOwner);
		this.setPack(newPack);
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
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
}
