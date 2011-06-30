package me.nopants.ScriptedEvents;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SEcondition {
	public static enum logicalOperator {and, or, none};
	private logicalOperator operator;
	private Map<Integer, String> conditionList = new HashMap<Integer, String>();
	private String name;
	private File conditionFile;
	
	public SEcondition(File newFile, String newName, logicalOperator newOperator, Map<Integer, String> newConditionList) {
		this.conditionList = newConditionList;
		this.name = newName;
		this.conditionFile = newFile;
		this.operator = newOperator;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public logicalOperator getOperator(){
		return this.operator;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setConditionList(Map<Integer, String> newConditionList) {
		this.conditionList = newConditionList;
	}

	public void setConditionFile(File newConditionFile) {
		this.conditionFile = newConditionFile;
	}
	
	public File getConditionFile() {
		return this.conditionFile;
	} 
	
	public Map<Integer, String> getConditionList() {
		return this.conditionList;
	}
}
