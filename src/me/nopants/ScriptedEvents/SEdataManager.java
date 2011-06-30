package me.nopants.ScriptedEvents;

import java.io.File;
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
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class SEdataManager {

	static String mainDirectory = "plugins/ScriptedEvents";
	static String conditionDirectory = mainDirectory+"/conditions";
	static String scriptDirectory = mainDirectory+"/scripts";
	static String variableDirectory = mainDirectory+"/variables";
	static String setDirectory = variableDirectory + "/sets";
	static File configFile = new File(mainDirectory + File.separator + "config.yml");
	static File cuboidFile = new File(mainDirectory + File.separator + "cuboid.dat");
	static File triggerFile = new File(mainDirectory + File.separator + "trigger.dat");
	static String stringVarPath = variableDirectory + File.separator + "string.var";
	static String intVarPath = variableDirectory + File.separator + "integer.var";
	static File stringVarFile = new File(stringVarPath);
	static File intVarFile = new File(intVarPath);
	private Map<CommandSender, SEentitySet> editEntityList = new HashMap<CommandSender, SEentitySet>();
	private Map<Integer, SEcuboid> cuboidList = new HashMap<Integer, SEcuboid>();
	private Map<Integer, SEtrigger> triggerList = new HashMap<Integer, SEtrigger>();
	private Map<Integer, SEcondition> conditionList = new HashMap<Integer, SEcondition>();
	private Map<Integer, SEscript> scriptList = new HashMap<Integer, SEscript>();
	private Map<String, String> stringVarList = new HashMap<String, String>();
	private Map<String, Integer> intVarList = new HashMap<String, Integer>();
	private Map<String,Set<String>> setVarList =  new HashMap<String, Set<String>>();
	private Map<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private int TriggerItem = 0;
	private static int operatorLine = 1;
	public SEutils utils;
	
	public SEdataManager() {
		utils = new SEutils(this);
	}

	// initializes the Data
	public void initializeData() {
		// load file or create it
		new File(mainDirectory).mkdir();
		new File(conditionDirectory).mkdir();
		new File(scriptDirectory).mkdir();
		new File(variableDirectory).mkdir();
		new File(setDirectory).mkdir();
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				writeConfig("TriggerItem", "288");
				utils.SElog(2, "config.yml created!");
			} catch (Exception ex) {
				utils.SElog(3, "Couldn't create config.yml!");
			}
		}
		
		if (!cuboidFile.exists()) {
			try {
				cuboidFile.createNewFile();
				utils.SElog(2, "cuboid.yml created!");
			} catch (Exception ex) {
				utils.SElog(3, "Couldn't create cuboid.yml!");
			}
		}
		
		if (!triggerFile.exists()) {
			try {
				triggerFile.createNewFile();
				utils.SElog(2, "trigger.yml created!");
			} catch (Exception ex) {
				utils.SElog(3, "Couldn't create trigger.yml!");
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
		int variableCount = getStringVarList().size() + getIntVarList().size() + getSetVarList().size();
		
		String refreshMessage = "ScriptedEvents: ";
		refreshMessage = refreshMessage + getCuboidList().size() + " Cuboids, ";
		refreshMessage = refreshMessage + getConditionList().size() + " Conditions, ";
		refreshMessage = refreshMessage + getScriptList().size() + " Scripts, ";
		refreshMessage = refreshMessage + getTriggerList().size() + " Triggers, ";
		refreshMessage = refreshMessage + variableCount + " Variables loaded";
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
	// GET BY ID
	//---------------------//
	
	// returns the Condition with 'ID'
	public SEcondition getConditionByID(int ID){
		if (conditionList.containsKey(ID)) {
			return conditionList.get(ID);
		}else return null;
	}
	
	// returns the Script with 'ID'
	public SEscript getScriptByID(int ID){
		if (scriptList.containsKey(ID)) {
			return scriptList.get(ID);
		}else return null;
	}
	
	// returns the Trigger with 'ID'
	public SEtrigger getTriggerByID(int ID){
		if (triggerList.containsKey(ID)) {
			return triggerList.get(ID);
		}else return null;
	}
	
	// returns the cuboid with 'ID'
	public SEcuboid getCuboidByID(int ID){
		if (cuboidList.containsKey(ID)) {
			return cuboidList.get(ID);
		}else return null;
		
		//return utils.stringToCuboid(readCuboid("cuboid"+ID));
	}
	
	//---------------------//
	// GET Lists
	//---------------------//

	// returns the setVarList
	public Map<String,Set<String>> getSetVarList() {
		return this.setVarList;
	}
	
	// returns the intVarList
	public Map<String, Integer> getIntVarList() {
		return this.intVarList;
	}
	
	// returns the stringVarList
	public Map<String, String> getStringVarList() {
		return this.stringVarList;
	}
	
	// returns the editEntityList
	public Map<CommandSender, SEentitySet> getEditEntityList() {
		return this.editEntityList;
	}
	
	// returns the loaded conditionList
	public Map<Integer, SEcondition> getConditionList() {
		return this.conditionList;
	}
	
	// returns the loaded scriptList
	public Map<Integer, SEscript> getScriptList() {
		return this.scriptList;
	}
	
	// returns the loaded triggerList
	public Map<Integer, SEtrigger> getTriggerList() {
		return triggerList;
	}
	
	// returns the cuboidList loaded in this playerListener
	public Map<Integer, SEcuboid> getCuboidList() {
		return cuboidList;
	}

	//---------------------//
	// SET Lists
	//---------------------//
	
	// sets the editEntityList
	public void setEditEntityList(Map<CommandSender, SEentitySet> newEditEntityList){
		this.editEntityList=newEditEntityList;
	}
	
	// sets the conditionList
	public void setConditionList(Map<Integer, SEcondition> newConditionList) {
		this.conditionList = newConditionList;
	}
	
	// sets the scriptList
	public void setScriptList(Map<Integer, SEscript> newScriptList) {
		this.scriptList = newScriptList;
	}
	
	// sets the triggerList
	public void setTriggerList(Map<Integer, SEtrigger> newTriggerList) {
		triggerList = newTriggerList;
	}
	
	// sets the cuboidList
	public void setCuboidList(Map<Integer, SEcuboid> newCuboidList) {
		cuboidList = newCuboidList;
		//ScriptedEvents.writeInLog(1, "Size of CuboidList: "+String.valueOf(cuboidList.size()));
	}

	// sets the listVarList
	public void setSetVarList(Map<String,Set<String>> newSetVarList) {
		this.setVarList = newSetVarList;
	}
	
	// sets the stringVarList
	public void setStringVarList(Map<String, String> newStringVarList) {
		this.stringVarList = newStringVarList;
	}
	
	// sets the intVarList
	public void setIntVarList(Map<String, Integer> newIntVarList) {
		this.intVarList = newIntVarList;
	}
	
	//---------------------//
	// LOAD
	//---------------------//
	
	// returns a SEcondition with the information of a conditionFile
	public SEcondition loadConditionFile(File conditionFile) {
		
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
		return new SEcondition(conditionFile, temp, operator, conList);
	}
	
	// returns a SEscript with the information of a scriptFile
	public SEscript loadScriptFile(File scriptFile) {
		
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
		return new SEscript(scriptFile, temp, actionList);
	}
	
	//---------------------//
	// LIST-TOOLS
	//---------------------//
	
	// rewrites the IDs of scriptList
	public void incrementScriptIDs(int offset) {
		Map<Integer, SEscript> oldScriptList = this.scriptList;
		Map<Integer, SEscript> newScriptList = new HashMap<Integer, SEscript>();
		
		// rewrite HERE!
		for (int i=offset; i <= oldScriptList.size()+1; i++) {
			newScriptList.put(i+1, oldScriptList.get(i));
		}		
		this.scriptList = newScriptList;
	}

	// rewrites the IDs of scriptList
	public void removeNullConditions() {
		Map<Integer, SEcondition> newConditionList = new HashMap<Integer, SEcondition>();
		int y = 1;
		
		newConditionList.clear();
		
		// rewrite HERE!
		for (int i=1; i <= conditionList.size()+1; i++) {
			if (conditionList.get(i)!=null) {
				newConditionList.put(y, conditionList.get(i));
				y++;
			}
			
		}	
		
		this.conditionList = newConditionList;
	}
	
	// rewrites the IDs of scriptList
	public void removeNullScripts() {
		Map<Integer, SEscript> newScriptList = new HashMap<Integer, SEscript>();
		int y = 1;
		
		newScriptList.clear();
		
		// rewrite HERE!
		for (int i=1; i <= scriptList.size()+1; i++) {
			if (scriptList.get(i)!=null) {
				newScriptList.put(y, scriptList.get(i));
				y++;
			}
			
		}	
		
		this.scriptList = newScriptList;
	}
	
	// increments the IDs of triggerList
	public void incrementTriggerIDs(int offset) {
		Map<Integer, SEtrigger> oldTriggerList = this.triggerList;
		Map<Integer, SEtrigger> newTriggerList = new HashMap<Integer, SEtrigger>();
		
		// rewrite HERE!
		for (int i=offset; i <= oldTriggerList.size()+1; i++) {
			newTriggerList.put(i+1, oldTriggerList.get(i));
		}		
		this.triggerList = newTriggerList;
	}
		
	// increments the IDs of cuboidList
	public void incrementCuboidIDs(int offset) {
		Map<Integer, SEcuboid> oldCuboidList = this.cuboidList;
		Map<Integer, SEcuboid> newCuboidList = new HashMap<Integer, SEcuboid>();
		
		// rewrite HERE!
		for (int i=offset; i <= oldCuboidList.size()+1; i++) {
			newCuboidList.put(i+1, oldCuboidList.get(i));
		}		
		this.cuboidList = newCuboidList;
	}
	
	//---------------------//
	// REFRESH
	//---------------------//
	
	// does a refresh on the config values
	public void refreshConfig() {
		try {
			TriggerItem = Integer.valueOf(readConfig("TriggerItem"));
		} catch (Exception e) {
			utils.SElog(3, "Failed to load config from file!");
		}
		
	}
	
	// does a refresh on the list of condition
	public void refreshConditionList() {
		try {
			// if there is a list, delete it
			if (!(conditionList.isEmpty()))
				conditionList = new HashMap<Integer, SEcondition>();

			// get all files in conditionDirectory which end on .condition
			File dir = new File(conditionDirectory);
			File[] conditionFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".condition" );} } );
			
			// loop over all .condition-files
			for(int i = 0; i < conditionFileList.length; i++) {
				// save the script into scriptList
				conditionList.put(i+1, loadConditionFile(conditionFileList[i])) ;
			}
			
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load condition!");
		}
	}
	
	// does a refresh on the list of scripts
	public void refreshScriptList() {
		try {
			// if there is a list, delete it
			if (!(scriptList.isEmpty()))
				scriptList = new HashMap<Integer, SEscript>();

			// get all files in scriptDirectory which end on .script
			File dir = new File(scriptDirectory);
			File[] scriptFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".script" );} } );
			
			// loop over all .script-files
			for(int i = 0; i < scriptFileList.length; i++) {
				// save the script into scriptList
				scriptList.put(i+1, loadScriptFile(scriptFileList[i])) ;
			}
			
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load scripts!");
		}
	}
	
	// does a refresh on the list of triggers
 	public void refreshTriggerList() {
		try {
			// if there is a list, delete it
			if (!(triggerList.isEmpty()))
				triggerList.clear();
			
			Map<Integer,String> stringTriggers = readTriggerFile();
			for (int i=1;i<=stringTriggers.size();i++) {
				triggerList.put(i, utils.stringToTrigger(stringTriggers.get(i)));
			}
			
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load triggers from file!");
		}
	}

	// does a refresh on the list of cuboids
 	public void refreshCuboidList() {
		try {

			if (!(cuboidList.isEmpty()))
				cuboidList.clear();
			
			Map<Integer,String> stringCuboids = readCuboidFile();
			for (int i=1;i<=stringCuboids.size();i++) {
				cuboidList.put(i, utils.stringToCuboid(stringCuboids.get(i)));
			}
			
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load cuboids from file!");
		}
	}
 	
 	// does a refresh on the list of cuboids
 	public void refreshSetVarList() {
     	// get all files in scriptDirectory which end on .script
		File dir = new File(setDirectory);
		File[] setFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".dat" );} } );
		
		// loop over all .dat-files
		for(int i = 0; i < setFileList.length; i++) {
			// save the script into scriptList
			String tempName = setFileList[i].getName();
			tempName = tempName.substring(0, tempName.indexOf(".dat"));
			
			setVarList.put(tempName, utils.mapToSet(read(setFileList[i])));
		}
	}
 	
	// does a refresh on the list of String variables
 	@SuppressWarnings("unchecked")
	public void refreshStringVarList() {
		try {
			
			if (!stringVarFile.exists()) {
				try {
					save(stringVarList, stringVarPath);
					utils.SElog(2, "string.var created!");
				} catch (Exception ex) {
					utils.SElog(3, "Couldn't create string.var!");
				}
			} else {
				if (load(stringVarPath) instanceof Map<?,?>) {			
					this.stringVarList = (Map<String,String>)load(stringVarPath);
				}	
			}
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load string-variables from file!");
		}
	}
 	
	// does a refresh on the list of Integer variables
 	@SuppressWarnings("unchecked")
	public void refreshIntVarList() {
		try {
			
			if (!intVarFile.exists()) {
				try {
					save(intVarList, intVarPath);
					utils.SElog(2, "integer.var created!");
				} catch (Exception ex) {
					utils.SElog(3, "Couldn't create integer.var!");
				}
			} else {
				if (load(intVarPath) instanceof Map<?,?>) {			
					this.intVarList = (Map<String,Integer>)load(intVarPath);
				}	
			}
		} catch (Exception ex) {
			utils.SElog(3, "Failed to load integer-variables from file!");
		}
	}
 	
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
 	 	
	// returns the ID of the searched condition or returns -1 if the condition is not found
	public int searchConditionList(String search) {
		int result=-1; 
		for (int i=1; i <= conditionList.size(); i++) {
			if (conditionList.get(i).getName().equalsIgnoreCase(search)) {
				return i;
			}
		}		
		return result;
	}
 	
	// returns the ID of the searched script or returns -1 if the script is not found
	public int searchScriptList(String search) {
		int result=-1; 
		for (int i=1; i <= scriptList.size(); i++) {
			if (scriptList.get(i).getName().equalsIgnoreCase(search)) {
				return i;
			}
		}		
		return result;
	}
 	
	// returns the ID of the searched trigger or returns -1 if the trigger is not found
	public int searchTriggerList(String search) {
		int result=-1; 
		for (int i=1; i <= triggerList.size(); i++) {
			if (triggerList.get(i).getName().equalsIgnoreCase(search)) {
				return i;
			}
		}		
		return result;
	}
 	
	// returns the ID of the searched cuboid or returns -1 if the cuboid is not found
	public int searchCuboidList(String search) {
		int result=-1; 
		for (int i=1; i <= cuboidList.size(); i++) {
			if (cuboidList.get(i).getName().equalsIgnoreCase(search)) {
				return i;
			}
		}		
		return result;
	}
	
	//---------------------//
	// REWRITE
	//---------------------//

	// rewrites the files of all loaded condition
	public void rewriteAllConditionFiles() {
		removeNullConditions();
		for (int i=1; i <= conditionList.size(); i++) {
			rewriteCondition(conditionList.get(i));
		}
	}
	
	// rewrites the files of all loaded scripts
	public void rewriteAllScriptFiles() {
		removeNullScripts();
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
			utils.SElog(3, "Couldn't rewrite "+condition.getName()+".condition!");
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
			for (int i = 1; i <= script.getAcionList().size(); i++) {
				write(newScriptFile, script.getAcionList().get(i));
			}
		} catch (IOException e) {
			utils.SElog(3, "Couldn't rewrite "+script.getName()+".script!");
		}
	}
	
	// writes the triggerList into the trigger-file
	public void rewriteTriggerFile() {
		try {
			triggerFile.delete();
			triggerFile.createNewFile();
		} catch (IOException e) {
			utils.SElog(3, "Couldn't rewrite trigger.yml!");
		}
				
		int newID = 1;
		
		for (int i = 1; i <= triggerList.size()+1; i++) {
			if (triggerList.get(i)!=null) {
				writeTrigger(triggerList.get(i).toString());
				newID++;
			}
		}
	}

	// writes the cuboidList into the cuboid-file
	public void rewriteCuboidFile() {
		try {
			cuboidFile.delete();
			cuboidFile.createNewFile();
		} catch (IOException e) {
			utils.SElog(3, "Couldn't rewrite cuboid.yml!");
		}
		
		// Cuboid-File gets written here		
		int newID = 1;
		for (int i = 1; i <= cuboidList.size()+1; i++) {
			if (cuboidList.get(i)!=null) {
				writeCuboid(cuboidList.get(i).toString());
				newID++;
			}
		}
	}
	
	// writes a SetVarFile into a .dat-file
	public void rewriteSetVarFile(String setName) {
		Set<String> tempSetVar = this.setVarList.get(setName);
		File tempSetFile = new File(SEdataManager.setDirectory + File.separator + setName + ".dat");
		
		try {
			tempSetFile.delete();
			tempSetFile.createNewFile();
		} catch (IOException e) {
			utils.SElog(3, "Couldn't rewrite "+setName+".dat!");
		}				
		
		Iterator<String> lauf = tempSetVar.iterator();
		
		while (lauf.hasNext()) {
			write(tempSetFile, lauf.next());
		}
	}
	
	// writes the stringVarList into the strings-file
	public void rewriteStringVarFile() {
		try {
			stringVarFile.delete();
			save(stringVarList, stringVarPath);
		} catch (Exception ex) {
			utils.SElog(3, "Couldn't rewrite string.var!");
		}
	}
	
	// writes the intVarList into the integer-file
	public void rewriteIntVarFile() {
		try {
			intVarFile.delete();
			save(intVarList, intVarPath);
		} catch (Exception ex) {
			utils.SElog(3, "Couldn't rewrite integer.var!");
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
		    out.write(x+System.getProperty("line.separator"));
		    out.close();	
		} catch (Exception e) {
			utils.SElog(3, "Could not write to '"+file.getName()+"'!");
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
			utils.SElog(3, "Could not load "+root+" in "+configFile.getName());
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
			utils.SElog(3, "Could not load '"+file.getName()+"'!");
		}
		return null;
	}

	// saves an Object to file-path
	public static void save(Object obj,String path) throws Exception
	{
	  ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
	  oos.writeObject(obj);
	  oos.flush();
	  oos.close();
	}

	// loads an Object from file-path
	public static Object load(String path) throws Exception
	{
	 ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
	 Object result = ois.readObject();
	 ois.close();
	 return result;
	}

	

}
