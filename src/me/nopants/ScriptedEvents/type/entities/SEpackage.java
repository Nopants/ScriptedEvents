package me.nopants.ScriptedEvents.type.entities;

import java.io.File;
import java.util.HashMap;
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
		super(newName, null);
		
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
		
		refreshPackage();
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
	}
	
	// does a refresh on the main list of Scripts
 	public void refreshScriptList() {
 		data.reloadScriptList(scriptList, scriptDirectory);
	}
 	
	// does a refresh on the main list of Triggers
 	public void refreshTriggerList() {
 		data.reloadTriggerList(triggerList, triggerFile);
	}
 	 	
	// does a refresh on the list of Cuboids
 	public void refreshCuboidList() {
 		data.reloadCuboidList(cuboidList, cuboidFile);
	}
	
 	// does a refresh on the list of IntVars
 	public void refreshIntVarList() {
 		data.reloadIntVarList(intVarList, intVarFile);
	}
 	
 	// does a refresh on the list of StringVars
 	public void refreshStringVarList() {
 		data.reloadStringVarList(stringVarList, stringVarFile);
	}
 	
 	// does a refresh on the list of SetVars
 	public void refreshSetVarList() {
 		data.reloadSetVarList(setVarList, setDirectory);
	}
	
	
}
