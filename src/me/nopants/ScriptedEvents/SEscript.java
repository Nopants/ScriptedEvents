package me.nopants.ScriptedEvents;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SEscript {
	private Map<Integer, String> actionList = new HashMap<Integer, String>();
	private String name;
	private File scriptFile;
	
	public SEscript(File newFile, String newName, Map<Integer, String> newActionList) {
		this.actionList = newActionList;
		this.name = newName;
		this.scriptFile = newFile;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public String getName() {
		return this.name;
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
	
	public Map<Integer, String> getAcionList() {
		return this.actionList;
	}
}
