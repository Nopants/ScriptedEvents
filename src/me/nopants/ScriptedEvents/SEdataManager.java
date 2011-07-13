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
import me.nopants.ScriptedEvents.type.entities.SEscript;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;
import me.nopants.ScriptedEvents.type.entities.variables.SEinteger;
import me.nopants.ScriptedEvents.type.entities.variables.SEset;
import me.nopants.ScriptedEvents.type.entities.variables.SEstring;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class SEdataManager {

	static String mainDirectory = "plugins" + File.separator + "ScriptedEvents";
	static String packageDirectory = mainDirectory + File.separator + "packages";
	static String conditionDirectory = mainDirectory + File.separator + "conditions";
	static String scriptDirectory = mainDirectory + File.separator + "scripts";
	static String variableDirectory = mainDirectory + File.separator + "variables";
	static String setDirectory = variableDirectory + File.separator + "sets";
	static File configFile = new File(mainDirectory + File.separator + "config.yml");
	static File cuboidFile = new File(mainDirectory + File.separator + "cuboid.dat");
	static File triggerFile = new File(mainDirectory + File.separator + "trigger.dat");
	static String stringVarPath = variableDirectory + File.separator + "string.var";
	static String intVarPath = variableDirectory + File.separator + "integer.var";
	static File stringVarFile = new File(stringVarPath);
	static File intVarFile = new File(intVarPath);
	private Map<CommandSender, SEentitySet> editEntityList = new HashMap<CommandSender, SEentitySet>();
	private Map<String,String> packages = new HashMap<String,String>();
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
	public SEutils utils;
	
	public SEdataManager() {
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
		
		refreshConfig(); //ready
		refreshPackages(); //ready
		refreshMainCuboidList(); //ready
		refreshMainConditionList();
		refreshMainScriptList();
		refreshMainTriggerList(); //ready
		refreshStringVarList();
		refreshIntVarList();
		refreshSetVarList();
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
		
	// returns a SEcondition with the information of a conditionFile
	public SEcondition loadConditionFile(String packageName, File conditionFile) {
		
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
		return new SEcondition(conditionFile, temp, null, operator, conList, packageName);
	}
	
	// returns a SEscript with the information of a scriptFile
	public SEscript loadScriptFile(String packageName, File scriptFile) {
		
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
		return new SEscript(scriptFile, temp, null, actionList, packageName);
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
	
	public void refreshPackages() {
		// get all dirs in main apart from base
		File dir = new File(packageDirectory);
		File[] children = dir.listFiles(new FileFilter() { public boolean accept(File file) { return (file.isDirectory()); } } );
		if (children != null) {
			for (int i=0; i<children.length; i++) {
				String tempPath = children[i].getPath();
				String tempName = tempPath.substring(tempPath.lastIndexOf(File.separator)+1);
				packages.put(tempName, tempPath);
			}
		}
	}
	
	// does a refresh on the main list of triggers
 	public void refreshMainConditionList() {
 	// if there is a list, delete it
		if (!(conditionList.isEmpty()))
			conditionList.clear();
		
		addConditionList(null);
		
		Iterator<String> lauf = packages.keySet().iterator();
		while (lauf.hasNext()) {
			addConditionList(lauf.next());
		}
		
	}
	
	// does a refresh on the list of scripts
	public void addConditionList(String packageName) {
		String path;
		
		if (packageName == null) {
 			path = conditionDirectory;
 		} else {
 			String packagePath = packages.get(packageName);
 			path = packagePath + File.separator + "conditions";
 		}
		
		try {
			// get all files in conditionDirectory which end on .condition			
			File dir = new File(path);
			File[] conditionFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".condition" );} } );
			if (conditionFileList != null) {
				// loop over all .condition-files
				for(int i = 0; i < conditionFileList.length; i++) {
					// save the condition into conditionList
					SEcondition tempCondition = loadConditionFile(packageName, conditionFileList[i]);
					if (packageName != null)
						tempCondition.setName(packageName+"."+tempCondition.getName());
					conditionList.put(tempCondition.getName(), tempCondition) ;
				}	
			}
		} catch (Exception ex) {
			if (packageName == null)
				SEutils.SElog(3, "Failed to load conditions!");
			else
				SEutils.SElog(3, "Failed to load conditions, in package '"+packageName+"'!");
		}
	}
	
	// does a refresh on the main list of triggers
 	public void refreshMainScriptList() {
 	// if there is a list, delete it
		if (!(scriptList.isEmpty()))
			scriptList.clear();
		
		addScriptList(null);
		
		Iterator<String> lauf = packages.keySet().iterator();
		while (lauf.hasNext()) {
			addScriptList(lauf.next());
		}
		
	}
	
	// does a refresh on the list of scripts
	public void addScriptList(String packageName) {
		String path;
		
		if (packageName == null) {
 			path = scriptDirectory;
 		} else {
 			String packagePath = packages.get(packageName);
 			path = packagePath + File.separator + "scripts";
 		}
		
		try {
			// get all files in scriptDirectory which end on .script			
			File dir = new File(path);
			File[] scriptFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".script" );} } );
			if (scriptFileList != null) {
				// loop over all .script-files
				for(int i = 0; i < scriptFileList.length; i++) {
					// save the script into scriptList
					SEscript tempScript = loadScriptFile(packageName, scriptFileList[i]);
					if (packageName != null)
						tempScript.setName(packageName+"."+tempScript.getName());
					scriptList.put(tempScript.getName(), tempScript) ;
				}	
			}
		} catch (Exception ex) {
			if (packageName == null)
				SEutils.SElog(3, "Failed to load scripts!");
			else
				SEutils.SElog(3, "Failed to load scripts, in package '"+packageName+"'!");
		}
	}
	
	// does a refresh on the main list of triggers
 	public void refreshMainTriggerList() {
 	// if there is a list, delete it
		if (!(triggerList.isEmpty()))
			triggerList.clear();
		
		addTriggerList(null);
		
		Iterator<String> lauf = packages.keySet().iterator();
		while (lauf.hasNext()) {
			addTriggerList(lauf.next());
		}
		
	}
 	
 	// does a refresh on the list of triggers in a package
 	public void addTriggerList(String packageName) {
 		Map<String,SEtrigger> list = triggerList;
		File file;
		
 		if (packageName == null) {
 			file = triggerFile;
 		} else {
 			String packagePath = packages.get(packageName);
 			file = new File(packagePath + File.separator + "trigger.dat");
 		}
 		
 		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create trigger.dat, in package '"+packageName+"'!");
			}
		}
 		
		try {	
			Map<Integer,String> stringTriggers = read(file);
			for (int i=1;i<=stringTriggers.size();i++) {
				SEtrigger tempTrigger = utils.stringToTrigger(packageName, stringTriggers.get(i));
				if (packageName != null)
					tempTrigger.setName(packageName+"."+tempTrigger.getName());
				list.put(tempTrigger.getName(), tempTrigger);
			}
		} catch (Exception ex) {
			if (packageName == null)
				SEutils.SElog(3, "Failed to load triggers from file!");
			else
				SEutils.SElog(3, "Failed to load triggers from file, in package '"+packageName+"'!");
		}
	}
 	
	// does a refresh on the list of cuboids
 	public void refreshMainCuboidList() {
 	 	// if there is a list, delete it
		if (!(cuboidList.isEmpty()))
			cuboidList.clear();
		
		addCuboidList(null);
		
		Iterator<String> lauf = packages.keySet().iterator();
		while (lauf.hasNext()) {
			addCuboidList(lauf.next());
		}
		
	}
 	
 	// does a refresh on the list of cuboids in a package
 	public void addCuboidList(String packageName) {
 		Map<String,SEcuboid> list = cuboidList;;
		File file;
		
 		if (packageName == null) {
 			file = cuboidFile;
 		} else {
 			String packagePath = packages.get(packageName);
 			file = new File(packagePath + File.separator + "cuboid.dat");
 		}
 		
 		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				SEutils.SElog(3, "Couldn't create cuboid.dat, in package '"+packageName+"'!");
			}
		}
 		
		try {
			Map<Integer,String> stringCuboids = read(file);
			for (int i=1;i<=stringCuboids.size();i++) {
				SEcuboid tempCuboid = utils.stringToCuboid(packageName, stringCuboids.get(i));
				if (packageName != null)
					tempCuboid.setName(packageName+"."+tempCuboid.getName());
				list.put(tempCuboid.getName(), tempCuboid);
			}
		} catch (Exception ex) {
			if (packageName == null)
				SEutils.SElog(3, "Failed to load cuboids from file!");
			else
				SEutils.SElog(3, "Failed to load cuboids from file, in package '"+packageName+"'!");
		}
	}
 	
 	// does a refresh on the list of cuboids
	public void refreshSetVarList() {
     	// get all files in scriptDirectory which end on .script
		File dir = new File(setDirectory);
		File[] setFileList = dir.listFiles(new FilenameFilter() { public boolean accept( File f, String s ) {return s.toLowerCase().endsWith( ".set" );} } );
		
		// loop over all .dat-files
		for(int i = 0; i < setFileList.length; i++) {
			// save the script into scriptList
			String tempName = setFileList[i].getName();
			tempName = tempName.substring(0, tempName.indexOf(".set"));
			
			try {
				if (load(SEdataManager.setDirectory + File.separator + tempName + ".set") instanceof SEset) {
					SEset tempSet = (SEset) load(SEdataManager.setDirectory + File.separator + tempName + ".set");
					this.setVarList.put(tempName, tempSet);
				}
			} catch (Exception e) {
				SEutils.SElog(3, "Failed to load "+tempName+".set!");
			}
		}
	}
 	
	// does a refresh on the list of String variables
 	@SuppressWarnings("unchecked")
	public void refreshStringVarList() {
		try {
			
			if (!stringVarFile.exists()) {
				try {
					save(stringVarList, stringVarPath);
					SEutils.SElog(2, "string.var created!");
				} catch (Exception ex) {
					SEutils.SElog(3, "Couldn't create string.var!");
				}
			} else {
				if (load(stringVarPath) instanceof Map<?,?>) {			
					this.stringVarList = (Map<String,SEstring>)load(stringVarPath);
				}
			}
		} catch (Exception ex) {
			SEutils.SElog(3, "Failed to load string-variables from file!");
		}
	}
 	
	// does a refresh on the list of Integer variables
 	@SuppressWarnings("unchecked")
	public void refreshIntVarList() {
		try {
			
			if (!intVarFile.exists()) {
				try {
					save(intVarList, intVarPath);
					SEutils.SElog(2, "integer.var created!");
				} catch (Exception ex) {
					SEutils.SElog(3, "Couldn't create integer.var!");
				}
			} else {
				if (load(intVarPath) instanceof Map<?,?>) {			
					this.intVarList = (Map<String,SEinteger>)load(intVarPath);
				}	
			}
		} catch (Exception ex) {
			SEutils.SElog(3, "Failed to load integer-variables from file!");
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
		
		for (int i = 1; i <= triggerList.size()+1; i++) {
			if (triggerList.get(i)!=null) {
				writeTrigger(triggerList.get(i).toString());
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
			save(tempSetVar, SEdataManager.setDirectory + File.separator + setName + ".set");
		} catch (Exception e) {
			SEutils.SElog(3, "Couldn't rewrite "+setName+".set!");
		}
	}
	
	// writes the stringVarList into the strings-file
	public void rewriteStringVarFile() {
		try {
			stringVarFile.delete();
			save(stringVarList, stringVarPath);
		} catch (Exception ex) {
			SEutils.SElog(3, "Couldn't rewrite string.var!");
		}
	}
	
	// writes the intVarList into the integer-file
	public void rewriteIntVarFile() {
		try {
			intVarFile.delete();
			save(intVarList, intVarPath);
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
		    out.write(x+System.getProperty("line.separator"));
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
