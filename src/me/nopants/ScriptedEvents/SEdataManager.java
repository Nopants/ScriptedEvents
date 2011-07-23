package me.nopants.ScriptedEvents;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEcondition;
import me.nopants.ScriptedEvents.type.entities.SEcuboid;
import me.nopants.ScriptedEvents.type.entities.SEpackage;
import me.nopants.ScriptedEvents.type.entities.SEscript;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;
import me.nopants.ScriptedEvents.type.entities.variables.SEinteger;
import me.nopants.ScriptedEvents.type.entities.variables.SEset;
import me.nopants.ScriptedEvents.type.entities.variables.SEstring;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class SEdataManager {

	static public String mainDirectory = "plugins" + File.separator + "ScriptedEvents";
	static public String packageDirectory = mainDirectory + File.separator + "packages";
	static public String conditionDirectory = mainDirectory + File.separator + "conditions";
	static public String scriptDirectory = mainDirectory + File.separator + "scripts";
	static public String variableDirectory = mainDirectory + File.separator + "variables";
	static public String setDirectory = variableDirectory + File.separator + "sets";
	static public File configFile = new File(mainDirectory + File.separator + "config.yml");
	static public File cuboidFile = new File(mainDirectory + File.separator + "cuboid.dat");
	static public File triggerFile = new File(mainDirectory + File.separator + "trigger.dat");
	static public String stringVarPath = variableDirectory + File.separator + "string.var";
	static public String intVarPath = variableDirectory + File.separator + "integer.var";
	static public File stringVarFile = new File(stringVarPath);
	static public File intVarFile = new File(intVarPath);
	private Map<CommandSender, SEentitySet> editEntityList = new HashMap<CommandSender, SEentitySet>();
	private Map<String,SEpackage> packages = new HashMap<String,SEpackage>();
	private Map<String, SEcuboid> cuboidList = new HashMap<String, SEcuboid>();
	private Map<String, SEtrigger> triggerList = new HashMap<String, SEtrigger>();
	private Map<String, SEcondition> conditionList = new HashMap<String, SEcondition>();
	private Map<String, SEscript> scriptList = new HashMap<String, SEscript>();
	private Map<String, SEstring> stringVarList = new HashMap<String, SEstring>();
	private Map<String, SEinteger> intVarList = new HashMap<String, SEinteger>();
	private Map<String,SEset> setVarList =  new HashMap<String, SEset>();
	private Map<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private int TriggerItem = 0;
	private static int operatorLine = 1;
	
	private ScriptedEvents plugin;
	public SEutils utils;
	
	public SEdataManager(ScriptedEvents newPlugin) {
		plugin = newPlugin;
		utils = new SEutils(this);
	}

	// initializes the Data
	public void initializeData() {
		// load file or create it
		new File(mainDirectory).mkdir();
		new File(packageDirectory).mkdir();
		new File(conditionDirectory).mkdir();
		new File(scriptDirectory).mkdir();
		new File(variableDirectory).mkdir();
		new File(setDirectory).mkdir();
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				writeConfig("TriggerItem", "288");
				SEutils.SElog(2, "config.yml created!");
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create config.yml!");
			}
		}
		
		if (!cuboidFile.exists()) {
			try {
				cuboidFile.createNewFile();
				SEutils.SElog(2, "cuboid.yml created!");
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create cuboid.yml!");
			}
		}
		
		if (!triggerFile.exists()) {
			try {
				triggerFile.createNewFile();
				SEutils.SElog(2, "trigger.yml created!");
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create trigger.yml!");
			}
		}
		
		refreshConfig();
		refreshCuboidList();
		refreshConditionList();
		refreshScriptList();
		refreshTriggerList();
		refreshStringVarList();
		refreshIntVarList();
		refreshSetVarList();
		refreshPackages();
		//refreshPackages();
		
		int variableCount = getStringVarList().size() + getIntVarList().size() + getSetVarList().size();
		
		String refreshMessage = "ScriptedEvents: ";
		refreshMessage = refreshMessage + getCuboidList().size() + " Cuboids, ";
		refreshMessage = refreshMessage + getConditionList().size() + " Conditions, ";
		refreshMessage = refreshMessage + getScriptList().size() + " Scripts, ";
		refreshMessage = refreshMessage + getTriggerList().size() + " Triggers, ";
		refreshMessage = refreshMessage + variableCount + " Variables, ";
		refreshMessage = refreshMessage + packages.size() + " Packages loaded";
		utils.writeinlog(1, refreshMessage);
	}
	

	
	//---------------------//
	// DEBUGEES
	//---------------------//
	
	// changes if a player gets debug info
	public void toggleDebugees(Player player) {
			if (debugees.containsKey(player)) {
				debugees.put(player, !(debugees.get(player)));
				if (debugees.get(player)) {
					utils.SEmessage(player, "debugging enabled");
				} else
					utils.SEmessage(player, "debugging disabled");
			} else {
				debugees.put(player, true);
				utils.SEmessage(player, "debugging enabled");
			}
	}

	// returns if a player gets debug info
	public boolean getDebugees(Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else
			return false;
	}
	
	// returns the TriggerItem
	public int getTriggerItem(){
		return this.TriggerItem;
	}
		
	//---------------------//
	// GET Lists
	//---------------------//

	public Map<String, SEtrigger> getAllTriggers() {
		Map<String, SEtrigger> result = new HashMap<String, SEtrigger>();
		
		result.putAll(getTriggerList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getTriggerList());
		}
		return result;
	}
	
	public Map<String, SEcuboid> getAllCuboids() {
		Map<String, SEcuboid> result = new HashMap<String, SEcuboid>();
		
		result.putAll(getCuboidList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getCuboidList());
		}
		return result;
	}
	
	public Map<String, SEscript> getAllScripts() {
		Map<String, SEscript> result = new HashMap<String, SEscript>();
		
		result.putAll(getScriptList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getScriptList());
		}
		return result;
	}
	
	public Map<String, SEcondition> getAllConditions() {
		Map<String, SEcondition> result = new HashMap<String, SEcondition>();
		
		result.putAll(getConditionList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getConditionList());
		}
		return result;
	}
	
	public Map<String, SEinteger> getAllIntVars() {
		Map<String, SEinteger> result = new HashMap<String, SEinteger>();
		
		result.putAll(getIntVarList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getIntVarList());
		}
		return result;
	}
	
	public Map<String, SEstring> getAllStringVars() {
		Map<String, SEstring> result = new HashMap<String, SEstring>();
		
		result.putAll(getStringVarList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getStringVarList());
		}
		return result;
	}
	
	public Map<String, SEset> getAllSetVars() {
		Map<String, SEset> result = new HashMap<String, SEset>();
		
		result.putAll(getSetVarList());
		Iterator<String> lauf = packages.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEpackage tempPackage = packages.get(lauf.next());
			result.putAll(tempPackage.getSetVarList());
		}
		return result;
	}
	
	public Map<String, SEpackage> getPackages() {
		return packages;
	}
	
	// returns the setVarList
	public Map<String,SEset> getSetVarList() {
		return this.setVarList;
	}
	
	// returns the intVarList
	public Map<String, SEinteger> getIntVarList() {
		return this.intVarList;
	}
	
	// returns the stringVarList
	public Map<String, SEstring> getStringVarList() {
		return this.stringVarList;
	}
	
	// returns the editEntityList
	public Map<CommandSender, SEentitySet> getEditEntityList() {
		return this.editEntityList;
	}
	
	// returns the loaded conditionList
	public Map<String, SEcondition> getConditionList() {
		return this.conditionList;
	}
	
	// returns the loaded scriptList
	public Map<String, SEscript> getScriptList() {
		return this.scriptList;
	}
	
	// returns the loaded triggerList
	public Map<String, SEtrigger> getTriggerList() {
		return triggerList;
	}
	
	// returns the cuboidList loaded in this playerListener
	public Map<String, SEcuboid> getCuboidList() {
		return cuboidList;
	}

	//---------------------//
	// SET Lists
	//---------------------//
	
	public void setPackages(Map<String, SEpackage> packages) {
		this.packages = packages;
	}
	
	// sets the editEntityList
	public void setEditEntityList(Map<CommandSender, SEentitySet> newEditEntityList){
		this.editEntityList=newEditEntityList;
	}
	
	// sets the conditionList
	public void setConditionList(Map<String, SEcondition> newConditionList) {
		this.conditionList = newConditionList;
	}
	
	// sets the scriptList
	public void setScriptList(Map<String, SEscript> newScriptList) {
		this.scriptList = newScriptList;
	}
	
	// sets the triggerList
	public void setTriggerList(Map<String, SEtrigger> newTriggerList) {
		triggerList = newTriggerList;
	}
	
	// sets the cuboidList
	public void setCuboidList(Map<String, SEcuboid> newCuboidList) {
		cuboidList = newCuboidList;
		//ScriptedEvents.writeInLog(1, "Size of CuboidList: "+String.valueOf(cuboidList.size()));
	}

	// sets the listVarList
	public void setSetVarList(Map<String,SEset> newSetVarList) {
		this.setVarList = newSetVarList;
	}
	
	// sets the stringVarList
	public void setStringVarList(Map<String, SEstring> newStringVarList) {
		this.stringVarList = newStringVarList;
	}
	
	// sets the intVarList
	public void setIntVarList(Map<String, SEinteger> newIntVarList) {
		this.intVarList = newIntVarList;
	}
	
	//---------------------//
	// LOAD
	//---------------------//
	
 	// does a refresh on the list of cuboids in a package
 	public void reloadCuboidList(Map<String, SEcuboid> list, File file, String pack) {
 		// if there is a list, delete it
		if (!(list.isEmpty()))
			list.clear();
		
 		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create '"+file.getName()+"'!");
			}
		} else {
			try {
				Map<Integer,String> stringCuboids = read(file);
				for (int i=1;i<=stringCuboids.size();i++) {
					SEcuboid tempCuboid = utils.stringToCuboid(stringCuboids.get(i), pack);
					if (tempCuboid != null) {
						list.put(tempCuboid.getName(), tempCuboid);
					}
				}
			} catch (Exception ex) {
				SEutils.SElog(3, "Failed to load cuboids from file '"+file.getName()+"'!");
			}	
		}
 	}
	
 // does a refresh on the list of cuboids in a package
 	public void reloadTriggerList(Map<String, SEtrigger> list, File file, String pack) {
 		// if there is a list, delete it
		if (!(list.isEmpty()))
			list.clear();
 		
 		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create '"+file.getName()+"'!");
			}
		} else {
			try {
				Map<Integer,String> stringTriggers = read(file);
				for (int i=1;i<=stringTriggers.size();i++) {
					SEtrigger tempTrigger = utils.stringToTrigger(stringTriggers.get(i),pack);
					if (tempTrigger != null) {
						list.put(tempTrigger.getName(), tempTrigger);
					}
				}
			} catch (Exception ex) {
				SEutils.SElog(3, "Failed to load triggers from file '"+file.getName()+"'!");
			}	
		}
 	}
 	
	// does a refresh on the list of conditions
	public void reloadConditionList(Map<String, SEcondition> list, String path, String pack) {
		// if there is a list, delete it
 		if (!(list.isEmpty()))
 			list.clear();
 		
		try {
			// get all files in conditionDirectory which end on .condition			
			File dir = new File(path);
			File[] conditionFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".condition" );} } );
			if (conditionFileList != null) {
				// loop over all .condition-files
				for(int i = 0; i < conditionFileList.length; i++) {
					// save the condition into conditionList
					SEcondition tempCondition = loadConditionFile(conditionFileList[i], pack);
					list.put(tempCondition.getName(), tempCondition) ;
				}	
			}
		} catch (Exception ex) {
			SEutils.SElog(3, "Failed to load conditions, in '"+path+"'!");
		}
	}
 	
	// does a refresh on the list of scripts
	public void reloadScriptList(Map<String, SEscript> list, String path, String pack) {
		// if there is a list, delete it
 		if (!(list.isEmpty()))
 			list.clear();
 		
		try {
			// get all files in scriptDirectory which end on .script			
			File dir = new File(path);
			File[] scriptFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".script" );} } );
			if (scriptFileList != null) {
				// loop over all .script-files
				for(int i = 0; i < scriptFileList.length; i++) {
					// save the script into scriptList
					SEscript tempScript = loadScriptFile(scriptFileList[i], pack);
					list.put(tempScript.getName(), tempScript) ;
				}	
			}
		} catch (Exception ex) {
			SEutils.SElog(3, "Failed to load scripts, in '"+path+"'!");
		}
	}
 	
	// returns a SEcondition with the information of a conditionFile
	public SEcondition loadConditionFile(File conditionFile, String pack) {
		
		// make a blank conditionList
		Map<Integer, String> conList = new HashMap<Integer, String>();
		SEcondition.logicalOperator operator = null;
		
		// get the conditionList out of the condition-File
		try {
			operator = utils.stringToOperator(read(conditionFile).get(operatorLine).substring(17)); 
			Map<Integer,String> tempCon = read(conditionFile); 
			for (int i=2;i<=tempCon.size();i++) {
				if (tempCon.get(i)!=null)
				conList.put(i-1, tempCon.get(i));
			}
		} catch (Exception e) {
			conList = null;
		}
		
		// get the name of the condition
		String temp = conditionFile.getName();
		temp = temp.substring(0, temp.lastIndexOf('.'));
		return new SEcondition(conditionFile, temp, null, operator, conList, pack);
	}
	
	// returns a SEscript with the information of a scriptFile
	public SEscript loadScriptFile(File scriptFile, String pack) {
		
		// make a blank actionList
		Map<Integer, String> actionList = new HashMap<Integer, String>();
		
		// get the conditionList out of the condition-File
		try {
			Map<Integer,String> tempActions = read(scriptFile); 
			for (int i=1;i<=tempActions.size();i++) {
				if (tempActions.get(i)!=null)
				actionList.put(i, tempActions.get(i));
				//utils.SElog(1, actionList.get(i)); // debug
			}
		} catch (Exception e) {
			actionList = null;
		}
		
		// get the name of the script
		String temp = scriptFile.getName();
		temp = temp.substring(0, temp.lastIndexOf('.'));
		return new SEscript(scriptFile, temp, null, actionList, pack);
	}
	
	// does a refresh on the list of Integer variables
	@SuppressWarnings("unchecked")
	public void reloadIntVarList(Map<String,SEinteger> list, File file) {
 		// if there is a list, delete it
		if (!(list.isEmpty()))
			list.clear();
		
		Map<String, SEinteger> temp = new HashMap<String,SEinteger>();
		
		if (!file.exists()) {
			try {
				file.createNewFile();
				saveObject(temp, file);
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create '"+file.getPath()+"'!");
			}
		} else {
			try {
				if (loadObject(file) instanceof Map<?,?>) {			
					temp = (Map<String,SEinteger>)loadObject(file);
					list.putAll(temp);
				}	
			} catch (Exception ex) {
				SEutils.SElog(3, "Failed to load integer-variables from '"+file.getPath()+"'!");
			}
		}
	}
	
	// does a refresh on the list of String variables
	@SuppressWarnings("unchecked")
	public void reloadStringVarList(Map<String,SEstring> list, File file) {
		// if there is a list, delete it
		if (!(list.isEmpty()))
			list.clear();
		
		Map<String, SEstring> temp = new HashMap<String,SEstring>();
		
		if (!file.exists()) {
			try {
				file.createNewFile();
				saveObject(temp, file);
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create '"+file.getPath()+"'!");
			}
		} else {
			try {
				if (loadObject(file) instanceof Map<?,?>) {
					temp = (Map<String,SEstring>)loadObject(file);
					list.putAll(temp);
				}
			} catch (Exception ex) {
				SEutils.SElog(3, "Failed to load string-variables from '"+file.getPath()+"'!");
			}
		}
	}
	
	// does a refresh on the list of Set variables
	public void reloadSetVarList(Map<String,SEset> list, String path) {
		// if there is a list, delete it
		if (!(list.isEmpty()))
			list.clear();
		
     	// get all files in scriptDirectory which end on .script
		File dir = new File(path);
		File[] setFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".set" );} } );
		
		if (setFileList != null) {
			// loop over all .dat-files
			for(int i = 0; i < setFileList.length; i++) {
				// save the script into scriptList
				String tempName = setFileList[i].getName();
				tempName = tempName.substring(0, tempName.indexOf(".set"));
				
				try {
					if (loadObject(setFileList[i]) instanceof SEset) {
						SEset tempSet = (SEset) loadObject(setFileList[i]);
						list.put(tempName, tempSet);
					}
				} catch (Exception e) {
					SEutils.SElog(3, "Failed to load "+tempName+".set!");
				}
			}	
		}
	}
	
	//---------------------//
	// REFRESH
	//---------------------//
	
	// does a refresh on the config values
	public void refreshConfig() {
		try {
			TriggerItem = Integer.valueOf(readConfig("TriggerItem"));
		} catch (Exception e) {
			SEutils.SElog(3, "Failed to load config from file!");
		}
		
	}

	// does a refresh on the main list of triggers
 	public void refreshConditionList() {
 		reloadConditionList(conditionList, conditionDirectory, null);
	}
	
	// does a refresh on the main list of triggers
 	public void refreshScriptList() {
 		reloadScriptList(scriptList, scriptDirectory, null);
	}
 	
	// does a refresh on the main list of triggers
 	public void refreshTriggerList() {
 		reloadTriggerList(triggerList, triggerFile, null);
	}
 	 	
	// does a refresh on the list of cuboids
 	public void refreshCuboidList() {
		reloadCuboidList(cuboidList, cuboidFile, null);
	}
	
 	public void refreshIntVarList() {
 		reloadIntVarList(intVarList, intVarFile);
	}
 	
 	public void refreshStringVarList() {
 		reloadStringVarList(stringVarList, stringVarFile);
	}
 	
 	public void refreshSetVarList() {
 		reloadSetVarList(setVarList, setDirectory);
	}
 	
	public void refreshPackages() {
		// get all dirs in main apart from base
		File dir = new File(packageDirectory);
		File[] children = dir.listFiles(new FileFilter() { public boolean accept(File file) { return (file.isDirectory()); } } );
		if (children != null) {
			for (int i=0; i<children.length; i++) {
				String tempPath = children[i].getPath();
				String tempName = tempPath.substring(tempPath.lastIndexOf(File.separator)+1);
				packages.put(tempName, new SEpackage(tempName, plugin));
				packages.get(tempName).refreshPackage();
			}
		}
	}
	 	 	// does a refresh on the list of cuboids
	// does a refresh on the list of integer variables

	//---------------------//
 	// SEARCH
	//---------------------//
 	
 	public boolean variableExists(String variable) {
 		boolean result = false;
 		
 		result = (
 				(intVarList.containsKey(variable)) ||
 				(stringVarList.containsKey(variable)) ||
 				(false)
 				);
 		
 		return result;
 		
 	}
 	
	//---------------------//
	// REWRITE
	//---------------------//

	// rewrites the files of all loaded condition
	public void rewriteAllConditionFiles() {
		Iterator<String> lauf = conditionList.keySet().iterator();
		while (lauf.hasNext()) {
			SEcondition tempCondition = conditionList.get(lauf.next());
			rewriteCondition(tempCondition);
		}
	}
	
	// rewrites the files of all loaded scripts
	public void rewriteAllScriptFiles() {
		for (int i=1; i <= scriptList.size(); i++) {
			rewriteScript(scriptList.get(i));
		}
	}
	
	// rewrites the files of all loaded List-Variables
	public void rewriteAllSetVarFiles() {
		
 		Iterator<String> lauf = setVarList.keySet().iterator();
 		while (lauf.hasNext()) {
 			String listName = lauf.next();
 			rewriteSetVarFile(listName);
 			
 			/*
 			Map<Integer, String> tempList = listVarList.get(listName);
 			
 			if (tempList!=null) {
 				File tempListFile = new File(listDirectory+ File.separator + listName + ".dat");
 	 		
 		 		try {
 					tempList = read(tempListFile);
 					this.listVarList.put(listName, tempList);
 				} catch (Exception ex) {
 					utils.SElog(3, "Failed to load "+listName+".dat!");
 				}
 			}
 			*/
 		}
	}
	
	// rewrites the file of a SEscript
	public void rewriteCondition(SEcondition condition) {
		try {
			File conditionFile = condition.getConditionFile();
			
			// name the new file alike the script
			File newConditionFile = new File(SEdataManager.conditionDirectory + File.separator + condition.getName()+".condition");
			// delete the old file
			conditionFile.delete();
			// create the new file
			newConditionFile.createNewFile();
			
			/*
			// write ID
			write(newConditionFile, "ID", String.valueOf(condition.getID()));
			*/
			// write LogicalOperator
			write(newConditionFile, "LogicalOperator: "+condition.getOperator().toString());
			// write every Condition
			for (int i = 1; i <= condition.getConditionList().size(); i++) {
				write(newConditionFile, condition.getConditionList().get(i));
			}
		} catch (IOException e) {
			SEutils.SElog(3, "Couldn't rewrite "+condition.getName()+".condition!");
		}
	}
	
	// rewrites the file of a SEscript
	public void rewriteScript(SEscript script) {
		try {
			File scriptFile = script.getScriptFile();
			
			// name the new file alike the script
			File newScriptFile = new File(SEdataManager.scriptDirectory + File.separator + script.getName()+".script");
			// delete the old file
			scriptFile.delete();
			// create the new file
			newScriptFile.createNewFile();
			
			/*
			// write ID
			write(newScriptFile, "ID", String.valueOf(script.getID()));
			*/
			// write every Action
			for (int i = 1; i <= script.getActionList().size(); i++) {
				write(newScriptFile, script.getActionList().get(i));
			}
		} catch (IOException e) {
			SEutils.SElog(3, "Couldn't rewrite "+script.getName()+".script!");
		}
	}
	
	// writes the triggerList into the trigger-file
	public void rewriteTriggerFile() {
		try {
			triggerFile.delete();
			triggerFile.createNewFile();
		} catch (IOException e) {
			SEutils.SElog(3, "Couldn't rewrite trigger.yml!");
		}
		Iterator<String> lauf = triggerList.keySet().iterator();
		
		while (lauf.hasNext()) {
			SEtrigger tempTrigger = triggerList.get(lauf.next());
			if (tempTrigger!=null) {
				writeTrigger(tempTrigger.toString());
			}
		}
	}

	// writes the cuboidList into the cuboid-file
	public void rewriteCuboidFile() {
		try {
			cuboidFile.delete();
			cuboidFile.createNewFile();
		} catch (IOException e) {
			SEutils.SElog(3, "Couldn't rewrite cuboid.yml!");
		}
		
		// Cuboid-File gets written here		
		Iterator<String> lauf = cuboidList.keySet().iterator();
		while (lauf.hasNext()) {
			String tempName = lauf.next();
			if (tempName!=null) {
				writeCuboid(cuboidList.get(tempName).toString());
			}
		}
	}
	
	// writes a SetVarFile into a .dat-file
	public void rewriteSetVarFile(String setName) {
		SEset tempSetVar = this.setVarList.get(setName);
		File tempSetFile = new File(SEdataManager.setDirectory + File.separator + setName + ".set");
		
		try {
			tempSetFile.delete();
			saveObject(tempSetVar, tempSetFile);
		} catch (Exception e) {
			SEutils.SElog(3, "Couldn't rewrite "+tempSetFile.getName()+"!");
		}
	}
	
	// writes the stringVarList into the strings-file
	public void rewriteStringVarFile() {
		try {
			stringVarFile.delete();
			//SEutils.SElog(1, "save: "+stringVarList.keySet().toString());
			saveObject(stringVarList, stringVarFile);
		} catch (Exception ex) {
			SEutils.SElog(3, "Couldn't rewrite string.var!");
		}
	}
	
	// writes the intVarList into the integer-file
	public void rewriteIntVarFile() {
		try {
			intVarFile.delete();
			saveObject(intVarList, intVarFile);
		} catch (Exception ex) {
			SEutils.SElog(3, "Couldn't rewrite integer.var!");
		}
	}
	
	//---------------------//
	// FILE-TOOLS
	//---------------------//
	
	// tries to load a config out of a file
	public static Configuration load(File file) {

		try {
			Configuration config = new Configuration(file);
			config.load();
			return config;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// writes root with the value x into a configFile	
	public void writeConfig(String root, String x) {
		
		Configuration config = load(configFile);
		config.setProperty(root, x);
		config.save();
		
		//write(configFile, root, x);
	}
	
	// writes root with the value x into a triggerFile	
	public void writeTrigger(String x) {
		write(triggerFile, x);
	}
	
	// writes root with the value x into a cuboidFile	
	public void writeCuboid(String x) {
		write(cuboidFile, x);
	}
	
	// writes root with the value x into a File
	public void write(File file, String x) {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(file,true));
		    out.write(System.getProperty("line.separator")+x);
		    out.close();	
		} catch (Exception e) {
			SEutils.SElog(3, "Could not write to '"+file.getName()+"'!");
		} 
	}

	// reads the value of root out of a configFile
	public String readConfig(String root) {
		Configuration config = load(configFile);
		try {
			String temp = config.getString(root);
			return temp;
		}
		catch (Exception ex) {
			SEutils.SElog(3, "Could not load "+root+" in "+configFile.getName());
		}
		return "";
	}
	
	// reads the value of root out of a triggerFile
	public Map<Integer,String> readTriggerFile() {
		return read(triggerFile);
	}
	
	// reads the value of root out of a cuboidFile
	public Map<Integer,String> readCuboidFile() {
		return read(cuboidFile);
	}
	
	// reads the value of root out of a File
	public Map<Integer,String> read(File file) {
		//Configuration config = load(file);
		try {
			Map<Integer,String> result = new HashMap<Integer,String>();		
		    Scanner scanner = new Scanner(new FileInputStream(file));
		    int i = 1;
		    try {
		    	while (scanner.hasNextLine()){
		    		String temp = scanner.nextLine();
		    		//utils.SElog(1, temp);
		    		if (!(temp==null || temp.equals(System.getProperty("line.separator")))) {
		    			result.put(i, temp);
		    			i++;
		    		}
		    	}
		    	return result;
		    } finally {
		    	scanner.close();
		    }
		} catch (Exception ex) {
			SEutils.SElog(3, "Could not load '"+file.getName()+"'!");
		}
		return null;
	}

	// saves an Object to file-path
	public static void saveObject(Object obj, File file) throws Exception {
		//SEutils.SElog(1, "save: "+file.getPath()); // debug
		String path = file.getPath();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	// loads an Object from file-path
	public static Object loadObject(File file) throws Exception {
		//SEutils.SElog(1, "load: "+file.getPath()); // debug
		String path = file.getPath();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}

}
