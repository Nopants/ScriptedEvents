package me.nopants.ScriptedEvents.type.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SEcondition extends SEentity {
	public static enum logicalOperator {and, or, none};
	private logicalOperator operator;
	private Map<Integer, String> conditionList = new HashMap<Integer, String>();
	private File conditionFile;
	
	public SEcondition(File newFile, String newName, logicalOperator newOperator, Map<Integer, String> newConditionList) {
		this.conditionList = newConditionList;
		this.name = newName;
		this.conditionFile = newFile;
		this.operator = newOperator;
	}
		
	public logicalOperator getOperator(){
		return this.operator;
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
