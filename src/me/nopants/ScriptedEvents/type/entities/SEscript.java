package me.nopants.ScriptedEvents.type.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SEscript extends SEentity {
	private static final long serialVersionUID = 1L;
	
	private Map<Integer, String> actionList = new HashMap<Integer, String>();
	private File scriptFile;
	
	public SEscript(File newFile, String newName, String newOwner, Map<Integer, String> newActionList, String newPack) {
		super(newName, newOwner, newPack);
		this.actionList = newActionList;
		this.scriptFile = newFile;
	}
		
	public void setActionList(Map<Integer, String> newActionList) {
		this.actionList = newActionList;
	}

	public void setScriptFile(File newScriptFile) {
		this.scriptFile = newScriptFile;
	}
	
	public File getScriptFile() {
		return this.scriptFile;
	} 
	
	public Map<Integer, String> getActionList() {
		return this.actionList;
	}
}
