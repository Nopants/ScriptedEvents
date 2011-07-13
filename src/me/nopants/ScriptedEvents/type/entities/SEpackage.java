package me.nopants.ScriptedEvents.type.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.nopants.ScriptedEvents.SEdataManager;
import me.nopants.ScriptedEvents.SEutils;
import me.nopants.ScriptedEvents.ScriptedEvents;
import me.nopants.ScriptedEvents.type.entities.variables.SEinteger;
import me.nopants.ScriptedEvents.type.entities.variables.SEset;
import me.nopants.ScriptedEvents.type.entities.variables.SEstring;

public class SEpackage extends SEentity{
	private static final long serialVersionUID = 1L;

	// Managers
	private ScriptedEvents plugin;
	private SEdataManager data;
	private SEutils utils;
	
	// Directorys and Files
	private String packageDirectory;
	private String conditionDirectory;
	private String scriptDirectory;
	private String variableDirectory;
	private String setDirectory;
	private File stringVarFile;
	private File intVarFile;
	private File cuboidFile;
	private File triggerFile;
	
	// Lists
	private Map<String, SEcuboid> cuboidList = new HashMap<String, SEcuboid>();
	private Map<String, SEtrigger> triggerList = new HashMap<String, SEtrigger>();
	private Map<String, SEcondition> conditionList = new HashMap<String, SEcondition>();
	private Map<String, SEscript> scriptList = new HashMap<String, SEscript>();
	private Map<String, SEstring> stringVarList = new HashMap<String, SEstring>();
	private Map<String, SEinteger> intVarList = new HashMap<String, SEinteger>();
	private Map<String,SEset> setVarList =  new HashMap<String, SEset>();
	
	public SEpackage(String newName, ScriptedEvents newPlugin) {
		super(newName, "Console");
		
		plugin = newPlugin;
		data = plugin.SEdata;
		utils = data.utils;
		
		packageDirectory = SEdataManager.packageDirectory + File.separator + this.getName();
		conditionDirectory = packageDirectory + File.separator + "conditions";
		scriptDirectory = packageDirectory + File.separator + "scripts";
		variableDirectory = packageDirectory + File.separator + "variables";
		setDirectory = variableDirectory + File.separator + "sets";
		
		new File(conditionDirectory).mkdir();
		new File(scriptDirectory).mkdir();
		new File(variableDirectory).mkdir();
		new File(setDirectory).mkdir();
		
		stringVarFile = new File(variableDirectory + File.separator + "string.var");
		intVarFile = new File(variableDirectory + File.separator + "integer.var");
		cuboidFile = new File(packageDirectory + File.separator + "cuboid.dat");
		triggerFile = new File(packageDirectory + File.separator + "trigger.dat");
	}

	// does a refresh on all lists
	public void refreshPackage() {
		refreshCuboidList();
		refreshConditionList();
		refreshScriptList();
		refreshTriggerList();
		refreshStringVarList();
		refreshIntVarList();
		refreshSetVarList();
		
		int variableCount = stringVarList.size() + intVarList.size() + setVarList.size();
		
		String refreshMessage = "ScriptedEvents: '"+this.getName()+"' loaded (";
		refreshMessage = refreshMessage + cuboidList.size() + " Cuboids, ";
		refreshMessage = refreshMessage + conditionList.size() + " Conditions, ";
		refreshMessage = refreshMessage + scriptList.size() + " Scripts, ";
		refreshMessage = refreshMessage + triggerList.size() + " Triggers, ";
		refreshMessage = refreshMessage + variableCount + " Variables)";
		utils.writeinlog(1, refreshMessage);
	}
	
	// does a refresh on the main list of Conditions
 	public void refreshConditionList() {
 		data.reloadConditionList(conditionList, conditionDirectory);
 		
 		HashMap<String,SEcondition> tempList = new HashMap<String,SEcondition>(); 		
 		Iterator<String> lauf = conditionList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEcondition tempCondition = conditionList.get(lauf.next());
 			tempCondition.setName(this.getName()+"."+tempCondition.getName());
 			tempList.put(tempCondition.getName(), tempCondition);
 		}
 		conditionList = tempList;
	}
	
	// does a refresh on the main list of Scripts
 	public void refreshScriptList() {
 		data.reloadScriptList(scriptList, scriptDirectory);
 		
 		HashMap<String,SEscript> tempList = new HashMap<String,SEscript>(); 		
 		Iterator<String> lauf = scriptList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEscript tempScript = scriptList.get(lauf.next());
 			tempScript.setName(this.getName()+"."+tempScript.getName());
 			tempList.put(tempScript.getName(), tempScript);
 		}
 		scriptList = tempList;
	}
 	
	// does a refresh on the main list of Triggers
 	public void refreshTriggerList() {
 		data.reloadTriggerList(triggerList, triggerFile, this.getName());
 		
 		HashMap<String,SEtrigger> tempList = new HashMap<String,SEtrigger>(); 		
 		Iterator<String> lauf = triggerList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEtrigger tempTrigger = triggerList.get(lauf.next());
 			tempTrigger.setName(this.getName()+"."+tempTrigger.getName());
 			tempList.put(tempTrigger.getName(), tempTrigger);
 		}
 		triggerList = tempList;
	}
 	 	
	// does a refresh on the list of Cuboids
 	public void refreshCuboidList() {
 		data.reloadCuboidList(cuboidList, cuboidFile);
 		
 		HashMap<String,SEcuboid> tempList = new HashMap<String,SEcuboid>(); 		
 		Iterator<String> lauf = cuboidList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEcuboid tempCuboid = cuboidList.get(lauf.next());
 			tempCuboid.setName(this.getName()+"."+tempCuboid.getName());
 			tempList.put(tempCuboid.getName(), tempCuboid);
 		}
 		cuboidList = tempList;
	}
	
 	// does a refresh on the list of IntVars
 	public void refreshIntVarList() {
 		data.reloadIntVarList(intVarList, intVarFile);
 		
 		HashMap<String,SEinteger> tempList = new HashMap<String,SEinteger>(); 		
 		Iterator<String> lauf = intVarList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEinteger tempInt = intVarList.get(lauf.next());
 			tempInt.setName(this.getName()+"."+tempInt.getName());
 			tempList.put(tempInt.getName(), tempInt);
 		}
 		intVarList = tempList;
	}
 	
 	// does a refresh on the list of StringVars
 	public void refreshStringVarList() {
 		data.reloadStringVarList(stringVarList, stringVarFile);
 		
 		HashMap<String,SEstring> tempList = new HashMap<String,SEstring>(); 		
 		Iterator<String> lauf = stringVarList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEstring tempString = stringVarList.get(lauf.next());
 			tempString.setName(this.getName()+"."+tempString.getName());
 			tempList.put(tempString.getName(), tempString);
 		}
 		stringVarList = tempList;
	}
 	
 	// does a refresh on the list of SetVars
 	public void refreshSetVarList() {
 		data.reloadSetVarList(setVarList, setDirectory);
 		
 		HashMap<String,SEset> tempList = new HashMap<String,SEset>(); 		
 		Iterator<String> lauf = setVarList.keySet().iterator();
 		while(lauf.hasNext()) {
 			SEset tempSet = setVarList.get(lauf.next());
 			tempSet.setName(this.getName()+"."+tempSet.getName());
 			tempList.put(tempSet.getName(), tempSet);
 		}
 		setVarList = tempList;
	}

	public Map<String, SEcuboid> getCuboidList() {
		return cuboidList;
	}

	public Map<String, SEtrigger> getTriggerList() {
		return triggerList;
	}

	public Map<String, SEcondition> getConditionList() {
		return conditionList;
	}

	public Map<String, SEscript> getScriptList() {
		return scriptList;
	}

	public Map<String, SEstring> getStringVarList() {
		return stringVarList;
	}

	public Map<String, SEinteger> getIntVarList() {
		return intVarList;
	}

	public Map<String, SEset> getSetVarList() {
		return setVarList;
	}
	
 	
	
}
