package me.nopants.ScriptedEvents.type.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SEcondition extends SEentity {
	private static final long serialVersionUID = 1L;
	
	public static enum logicalOperator {and, or, none};
	private logicalOperator operator;
	private Map<Integer, String> conditionList = new HashMap<Integer, String>();
	private File conditionFile;
	
	public SEcondition(File newFile, String newName, String newOwner, logicalOperator newOperator, Map<Integer, String> newConditionList) {
		super(newName, newOwner);
		this.conditionList = newConditionList;
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
