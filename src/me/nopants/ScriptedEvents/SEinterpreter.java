package me.nopants.ScriptedEvents;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEcondition;
import me.nopants.ScriptedEvents.type.entities.SEcuboid;
import me.nopants.ScriptedEvents.type.entities.SEscript;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;
import me.nopants.ScriptedEvents.type.entities.SEcondition.logicalOperator;
import me.nopants.ScriptedEvents.type.entities.variables.SEinteger;
import me.nopants.ScriptedEvents.type.entities.variables.SEset;
import me.nopants.ScriptedEvents.type.entities.variables.SEstring;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.TrapDoor;

public class SEinterpreter extends Thread {
	
	private ScriptedEvents plugin;
	private SEdataManager SEdata;
	private SEutils utils;
	private SEplayerListener playerListener;
	
	public enum kindType {script,condition};
	
	String packageName = null;
	kindType kind = null;
	SEtrigger trigger = null;
	SEscript script = null;
	SEcondition condition = null;
	SEentitySet entitySet = null;
	boolean isWorking = true;
	
	String workingPlace = null;
	int scriptLine = 0;
	int conditionLine = 0;
	int workingLine = 0;
	int cycles = -1;
	boolean check = true;
	boolean If = false;
	
	String unexpectedExpression = "Unexpected Expression";
	String unknownExpression = "Unknown Expression";
	String tooManyExpressions = "Too many Expressions";
	String wrongArguments = "Wrong number of Arguments";
	String missingArgument = "More Arguments expected";
	String missingCloseBracket = "Closing bracket expected";
	String missingOpenBracket = "Opening bracket expected";
	String missingCloseAngleBracket = "Closing angle bracket expected";
	String missingOpenAngleBracket = "Opening angle bracket expected";
	String unexpectedBracket = "Unexpected bracket";
	String calcfailed = "Calculation failed";
	String sleep = "Interpreter thread doesn't want to go to sleep yet!";
	
	String invalidInteger = "Invalid Integer given";
	String invalidEffect = "Invalid Effect given";
	String invalidLocation = "Invalid Location given";
	String calcFailed = "Calculation failed";
	String playerNotFound = "Player not found/online given";
	String worldNotFound = "World not found given";
	String cuboidNotFound = "Cuboid not found given";
	String triggerNotFound = "trigger not found given";
	String setNotFound = "Set variable not found given";
	String scriptNotFound = "Script not found given";
	String conditionNotFound = "Condition not found given";
	
	Set<String> tempIntegers;
	Set<String> tempStrings;
	
	
	// ACTIONS
	// =======
	// 		WORLD ACTIONS
	// 		-------------
	
	// All WorldActions that don't have input
	Set<String> bracketlessWorldActions = new HashSet<String> (Arrays.asList(
			"do",
			"/"
			));
	
	// All WorldActions that use brackets for their input
	Set<String> bracketWorldActions = new HashSet<String> (Arrays.asList(
			"removeItemInHand",
			"removeItem",
			"removeItemAt",
			"giveItem",
			"giveItemAt",
			"setHealth",
			"messageTo",
			"broadcast",
			"changeBlockType",
			"changeBlockData",
			"playerCommand",
			"playEffect",
			"teleport",
			"setTime",
			"toggleDoor",
			"toggleLever"
			));

	// all worldActions:
	@SuppressWarnings("serial")
	Set<String> worldActions = new HashSet<String>() {{
		addAll(bracketlessWorldActions);
		addAll(bracketWorldActions);
	}};

	// 		SCRIPT ACTIONS
	// 		--------------
	
	// All ScriptActions that don't have input
	Set<String> bracketlessScriptActions = new HashSet<String>(Arrays.asList(
			"cancelEvent"		
			));
	
	// All ScriptActions that use brackets for their input
	Set<String> bracketScriptActions = new HashSet<String>(Arrays.asList(
			"if",
			"then",
			"else",
			"delay",
			"trigger",
			"doForCuboidBlocks",
			"doForSetItems",
			"script",
			"loop",
			"while"
			));
	
	// all scriptActions:
	@SuppressWarnings("serial")
	Set<String> scriptActions = new HashSet<String>() {{
		addAll(bracketlessScriptActions);
		addAll(bracketScriptActions);
	}};

	//		ALL ACTIONS
	// 		-------------
	
	// All actions that don't have input
	@SuppressWarnings("serial")
	Set<String> bracketlessActions = new HashSet<String>() {{
		addAll(bracketlessWorldActions);
		addAll(bracketlessScriptActions);
	}};
	
	// All actions that use brackets for their input
	@SuppressWarnings("serial")
	Set<String> bracketActions = new HashSet<String>() {{
		addAll(bracketWorldActions);
		addAll(bracketScriptActions);
	}};
	
	// All actions in one set
	@SuppressWarnings("serial")
	Set<String> actions = new HashSet<String>() {{
		addAll(worldActions);
		addAll(scriptActions);
	}};

	// FUNCTIONS
	// =========
	
	// Get Functions:
	Set<String> functions = new HashSet<String>(Arrays.asList(
			"health",
			"itemInHand",
			"itemAtSlot",
			"searchItem",
			"slotInHand",
			"time",
			"isInBed",
			"isSneaking",
			"playerLocation",
			"playerLocationX",
			"playerLocationY",
			"playerLocationZ",
			"blockID",
			"blockData",
			"calc",
			"size",
			"arg",
			"playerWorld",
			"random"
			));

	// CONDITIONS
	// ==========
	
	// Conditions:
	Set<String> conditions = new HashSet<String>(Arrays.asList(
			"equals",
			"bigger",
			"contains",
			"and",
			"or",
			"check",
			"hasItem",
			"online",
			"isEmpty",
			"isOpen",
			"isSwitchedOn",
			"isInside",
			"hasPermission",
			"inGroup"
			));

	// VARIABLES
	// =========

	// Premade event-related variables:
	Set<String> preVariables = new HashSet<String>(Arrays.asList(
			"<triggeringPlayer>",
			"<triggeringCuboid>",
			"<cuboidBlockLocation>",
			"<cuboidBlockLocationX>",
			"<cuboidBlockLocationY>",
			"<cuboidBlockLocationZ>",
			"<clickedLocation>",
			"<clickedLocationX>",
			"<clickedLocationY>",
			"<clickedLocationZ>",
			"<clickedLocationID>",
			"<rightClick>",
			"<randomInt>",
			"<setItem>",
			"<placedBlockID>",
			"<placedBlockLocation>",
			"<placedBlockLocationX>",
			"<placedBlockLocationY>",
			"<placedBlockLocationZ>",
			"<placedBlockData>",
			"<brokenBlockID>",
			"<brokenBlockLocation>",
			"<brokenBlockLocationX>",
			"<brokenBlockLocationY>",
			"<brokenBlockLocationZ>",
			"<brokenBlockData>",
			"<deathCause>"
			));

	@SuppressWarnings("serial")
	Set<String> variables = new HashSet<String>() {{
		addAll(preVariables);
	}};
	
	// ALL EXPRESSIONS
	// ===============
	
	@SuppressWarnings("serial")
	Set<String> bracketExpressions = new HashSet<String>() {{
		addAll(bracketActions);
		addAll(functions);
		addAll(conditions);
	}};
	
	// All known expressions in one set
	@SuppressWarnings("serial")
	Set<String> expressions = new HashSet<String>() {{
		addAll(actions);
		addAll(functions);
		addAll(conditions);
		addAll(preVariables);
	}};
	
	
	// ----------- //

	
	//-------------//
	// CLASS STUFF //
	//-------------//
	
	// constructor
	public SEinterpreter(ScriptedEvents newPlugin, SEtrigger newTrigger, SEentitySet newEntitySet, kindType newKind, String newPackageName) {
		this.packageName = newPackageName;
		this.kind = newKind;
		this.plugin = newPlugin;
		this.SEdata = newPlugin.SEdata;
		this.utils = newPlugin.utils;
		this.playerListener = newPlugin.playerListener;
		this.trigger = newTrigger;
		this.script = newTrigger.getScript();
		this.condition = newTrigger.getCondition();
		this.entitySet = newEntitySet;
		this.scriptLine = 1;
		
		if (kind == kindType.script) {
			if (this.script.getScriptFile() != null)
				this.workingPlace = this.script.getScriptFile().getName();
			else
				this.workingPlace = "SubScript";
		}
		if (kind == kindType.condition) {
			if (this.condition.getConditionFile() != null)
				this.workingPlace = this.condition.getConditionFile().getName();
			else
				this.workingPlace = "SubCondition?! There are no SubConditions! Better report this!";
		}
	}

	// overrides run()
	@SuppressWarnings("unchecked")
	public void run() {
		HashSet<String> intVars = new HashSet<String>();
		intVars.addAll(SEdata.getAllIntVars().keySet());
		intVars = (HashSet<String>) intVars.clone();
		tempIntegers = (HashSet<String>) intVars.clone();
		Iterator<String> intLauf = intVars.iterator();
		
		HashSet<String> stringVars = new HashSet<String>();
		stringVars.addAll(SEdata.getAllStringVars().keySet());
		stringVars = (HashSet<String>) stringVars.clone();
		tempStrings = (HashSet<String>) stringVars.clone();
		Iterator<String> stringLauf = stringVars.iterator();
		
		while (intLauf.hasNext()) {
			String tempVar = intLauf.next();
			tempIntegers.remove(tempVar);
			tempIntegers.add("<"+tempVar+">");
		}
		
		while (stringLauf.hasNext()) {
			String tempVar = stringLauf.next();
			tempStrings.remove(tempVar);
			tempStrings.add("<"+tempVar+">");
		}
		
		variables.addAll(tempIntegers);
		variables.addAll(tempStrings);
		expressions.addAll(tempIntegers);
		expressions.addAll(tempStrings);
		
		// SEutils.SElog(1, variables.toString()); // debug
		
		if(this.kind.equals(kindType.script))
			executeScript();
		
		if(this.kind.equals(kindType.condition))
			this.check = executeCondition(condition.getOperator(), condition.getConditionList());
		
		this.isWorking = false;
	}
		
	
	//-------------------//
	// INTERPRETER STUFF //
	//-------------------//
	
	public void sendError(String expression, String error) {
		String timeStamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
		timeStamp = timeStamp.substring(0, timeStamp.length()-4);
		String message;
		
		// SEutils.SElog(1, plugin.SEdata.ErrorDestination); // debug
		
		// create Error-Message
		if (expression != null && workingPlace != null) {
			message = "\""+expression+"\": "+error+" in line: "+workingLine+" ("+workingPlace+")";
		}
		else if (workingPlace != null) {
			message = error+" in line: "+workingLine+" ("+workingPlace+")";
		}
		else {
			message = error+" in line: "+workingLine;
		}
		
		// send Error to LOG
		if (plugin.SEdata.ErrorDestination.equals("LOG")) {
			SEutils.SElog(2, message);	
		}

		// send Error to FILE
		if (plugin.SEdata.ErrorDestination.equals("FILE")) {
			File errorLogFile = SEdataManager.errorLogFile;
			if (!errorLogFile.exists()) {
				try {
					errorLogFile.createNewFile();
				} catch (Exception ex) {
					SEutils.SElog(3, "Couldn't create 'errorLog.txt'!");
				}
			}
			plugin.SEdata.write(errorLogFile, timeStamp+": "+message);
		}
		
		// send Error to Player
		if (plugin.SEdata.ErrorDestination.startsWith("PLAYER")) {
			Player errorPlayer = utils.stringToPlayer(plugin.getServer().getOnlinePlayers(), plugin.SEdata.ErrorDestination.substring(6));
			//SEutils.SElog(1, plugin.SEdata.ErrorDestination.substring(6));
			if (errorPlayer != null) {
				utils.SEmessage(errorPlayer, "�4"+message);
			}
		}
	}
	
	// executes the interpreters script
	public void executeScript(){
		if (script != null && script.getActionList() != null) {
			Map<Integer, String> actionList = script.getActionList();
			boolean check = true;
			
			if (condition != null && condition.getConditionList() != null) {
				SEinterpreter interpreter = new SEinterpreter(plugin, this.trigger, this.entitySet, SEinterpreter.kindType.condition, this.packageName);
				interpreter.start();
				while(interpreter.isWorking)
					try {
						sleep(10);
					} catch (InterruptedException e) {
					}
				check = interpreter.check;
			}
			
			if (check) {
				for (this.scriptLine = 1; this.scriptLine<=actionList.size(); this.scriptLine++) {
					String temp = actionList.get(this.scriptLine);
					if (temp != "")
						executeLine(temp, actions);
				}
			}
		}	
	}	
	
	// check a condition-List
	public boolean executeCondition(logicalOperator operator, Map<Integer, String> conditionList) {
		boolean result = false;
		boolean tempResult = false;
		String temp = "null";
		
		if (operator==null) result = true;
		if (operator==logicalOperator.and) result = true;
		if (operator==logicalOperator.or) result = false;
		
		// checks every condition in the list
		for (this.conditionLine = 1; this.conditionLine <= conditionList.size(); this.conditionLine++) {
			temp = executeLine(conditionList.get(this.conditionLine), conditions);
			
			if (temp!="null") {
				tempResult = Boolean.valueOf(temp); //checkCondition(conditionList.get(i), entitySet);
			} else {
				tempResult = false;
			}
			
			
			if (operator==null) result = (result && tempResult);
			if (operator==logicalOperator.and) result = (result && tempResult);
			if (operator==logicalOperator.or) result = (result || tempResult);
		}
		return result;
	}
	
	// interprets and executes a Script-Line
	private String executeLine(String line, Set<String> expected) {
		String result = "null";
		
		if (expected.equals(conditions)) workingLine = conditionLine;
		else workingLine = scriptLine;
		
		// remove encoding-bytes
		if (line != null && line.length() > 2 && line.charAt(2)==(char) 191) {
			line = line.substring(3);
		}
		
		line = line.trim();
		
		// INTERPRET line
		if (line != null) {
			//utils.SElog(1, "#"+this.line+": "+line); // debug
			
			//--------------------//
			// insert SYNTAX here //
			//--------------------//			
			
			// remove comments
			if (line.contains("#"))
				line = line.substring(0, line.indexOf("#"));
			
			// BRACKETS are correct OR hole line is a comment
			if (line.startsWith("#") || correctBrackets(line)) {
				
				// ignore subscripts or comment-lines
				if (! (line.startsWith("|") || line.startsWith("#") || line.isEmpty())) {
					// dispatch and ignore server-commands
					if (line.startsWith("/")) {
						line = this.resolveVariables(line);
						line = this.resolveFunctions(line);
						plugin.getServer().dispatchCommand(new ConsoleCommandSender(plugin.getServer()),line.substring(1));
					} else {		
						// remove BRACKETS in dummy
						int words;
						if (line.contains("(") && line.contains(")")) {
							String bracket = line.substring(line.indexOf("("), line.lastIndexOf(")")+1);
							String dummy = line.replace(bracket, bracket.replaceAll(".", "X"));
							words = dummy.split(" ").length;
						} else
							words = line.split(" ").length;
						
						// ONE WORD per line (brackets are ignored)
						if (words > 1) {
							sendError(null, tooManyExpressions);
						} else {
							String expression;
							
							// get EXPRESSION
							if (line.contains("("))
								expression = line.substring(0, line.indexOf("("));
							else
								expression = line;
							
							// 1. UNKNOWN expression
							if (!expressions.contains(expression)) {
								sendError(expression, unknownExpression);
								expression = null;
							} else {
								
								// 2.    KNOWN expression
																
								// 2.1   EXPECTED expressions
								if (expected.contains(expression)) {
								// 2.1.1 BRACKET expression
									if (line.contains("(") || line.contains(")")) {
										if (!bracketExpressions.contains(expression)) {
											sendError(expression, unexpectedBracket);
										} else {
											
											// line:        doForCuboidBlocks(<myCuboid>,changeBlockType(world,<blockLocation>,0))
											// mainBracket: <myCuboid>,changeBlockType(world,<blockLocation>,0)
											// dummy:       <myCuboid>,changeBlockTypeXXXXXXXXXXXXXXXXXXXXXXXXX
											// dummyInput:  [<myCuboid>],[changeBlockTypeXXXXXXXXXXXXXXXXXXXXXXXXX]
											// input:       [<myCuboid>],[changeBlockType(world,<blockLocation>,0)]
											
											String mainBracket = line.substring(line.indexOf("(")+1, utils.findBracket(line, line.indexOf("(")));
											
											String dummy = mainBracket;
											
											while(dummy.contains("(") && dummy.contains(")")) {
												String tempBracket = dummy.substring(dummy.indexOf("("), utils.findBracket(dummy, dummy.indexOf("("))+1);
												dummy = dummy.replace(tempBracket, tempBracket.replaceAll(".", "X"));
											}
											
											while(utils.countChar(dummy, '"')>1) {
												String tempString = dummy.substring(dummy.indexOf("\""), dummy.indexOf("\"",dummy.indexOf("\"")+1)+1);
												dummy = dummy.replace(tempString, tempString.replaceAll(".", "X"));
											}
											
											String[] dummyInput = dummy.split(",");
											int startIndex = 0;
											for (int i=0; i<dummyInput.length;i++){
												dummyInput[i] = mainBracket.substring(startIndex, startIndex + dummyInput[i].length());
												startIndex = startIndex + dummyInput[i].length() + 1;
												//utils.SElog(1, dummyInput[i]); // debug	
											}
											
											String[] input = dummyInput;
											
											for (int i=0; i<input.length;i++){
												if (input[i].startsWith("\"") && input[i].endsWith("\""))
													input[i] = input[i].substring(1, input[i].length()-1);
											}
											
											// ONLY RESOLVE RELEVANT PARTS, NOT IRRELEVANT BRACKETS
											input = resolveInput(input);
											
											// WORLD ACTIONS
											//==============
											if (expression.equals("removeItemInHand")) {
												removeItemInHand(input);
											}
											if (expression.equals("removeItem")) {
												removeItem(input);
											}
											if (expression.equals("removeItemAt")) {
												removeItemAt(input);
											}
										    if (expression.equals("giveItem")) {
												giveItem(input);
											}
										    if (expression.equals("giveItemAt")) {
												giveItemAt(input);
											}
											if (expression.equals("setHealth")) {
												setHealth(input);
											}
											if (expression.equals("messageTo")) {
												messageTo(input);
											}
											if (expression.equals("broadcast")) {
												broadcast(input);
											}
											if (expression.equals("changeBlockType")) {
												changeBlockType(input);
											}
											if (expression.equals("changeBlockData")) {
												changeBlockType(input);
											}
											if (expression.equals("playerCommand")) {
												playerCommand(input);
											}
											if (expression.equals("playEffect")) {
												playEffect(input);
											}
											if (expression.equals("teleport")) {
												teleport(input);
											}
											if (expression.equals("setTime")) {
												setTime(input);
											}
											if (expression.equals("toggleDoor")) {
												toggleDoor(input);
											}
											if (expression.equals("toggleLever")) {
												toggleLever(input);
											}
											
											// SCRIPT ACTIONS
											//===============
											if (expression.equals("if")) {
												If(input);
											}
											if (expression.equals("then")) {
												Then(input);
											}
											if (expression.equals("else")) {
												Else(input);
											}
											if (expression.equals("delay")) {
												delay(input);
											}
											if (expression.equals("trigger")) {
												trigger(input);
											}
											if (expression.equals("script")) {
												script(input);
											}
											if (expression.equals("doForCuboidBlocks")) {
												doForCuboidBlocks(input);
											}
											if (expression.equals("doForSetItems")) {
												doForSetItems(input);
											}
											if (expression.equals("loop")) {
												loop(input);
											}
											if (expression.equals("while")) {
												While(input);
											}
											/*if (expression.equals("setRandomRange")) {
												setRandomRange(input);
											}*/
											
											// FUNCTIONS
											//==========
											if (expression.equals("calc")) {
												result = calc(input);
											}
											if (expression.equals("health")) {
												result = health(input);
											}
											if (expression.equals("itemInHand")) {
												result = itemInHand(input);
											}
											if (expression.equals("itemAtSlot")) {
												result = itemAtSlot(input);
											}
											if (expression.equals("searchItem")) {
												result = searchItem(input);
											}
											if (expression.equals("slotInHand")) {
												result = slotInHand(input);
											}
											if (expression.equals("time")) {
												result = time(input);
											}
											if (expression.equals("blockID")) {
												result = blockID(input);
											}
											if (expression.equals("blockData")) {
												result = blockData(input);
											}
											if (expression.equals("isInBed")) {
												result = isInBed(input);
											}
											if (expression.equals("isSneaking")) {
												result = isSneaking(input);
											}
											if (expression.equals("playerLocation")) {
												result = playerLocation(input);
											}
											if (expression.equals("playerLocationX")) {
												result = playerLocationX(input);
											}
											if (expression.equals("playerLocationY")) {
												result = playerLocationY(input);
											}
											if (expression.equals("playerLocationZ")) {
												result = playerLocationZ(input);
											}
											if (expression.equals("size")) {
												result = size(input);
											}
											if (expression.equals("arg")) {
												result = arg(input);
											}
											if (expression.equals("playerWorld")) {
												result = playerWorld(input);
											}
											if (expression.equals("random")) {
												result = random(input);
											}
											
											// CONDITIONS
											//===========
											if (expression.equals("equals")) {
												result = Equals(input);
											}
											if (expression.equals("bigger")) {
												result = bigger(input);
											}
											if (expression.equals("hasItem")) {
												result = hasItem(input);
											}
											if (expression.equals("contains")) {
												result = contains(input);
											}
											if (expression.equals("and")) {
												result = and(input);
											}
											if (expression.equals("or")) {
												result = or(input);
											}
											if (expression.equals("check")) {
												result = check(input);
											}
											if (expression.equals("online")) {
												result = online(input);
											}
											if (expression.equals("isEmpty")) {
												result = isEmpty(input);
											}
											if (expression.equals("isOpen")) {
												result = isOpen(input);
											}
											if (expression.equals("isSwitchedOn")) {
												result = isSwitchedOn(input);
											}
											if (expression.equals("isInside")) {
												result = isInside(input);
											}
											if (expression.equals("hasPermission")) {
												result = hasPermission(input);
											}
											if (expression.equals("inGroup")) {
												result = inGroup(input);
											}
										}
									} else {
								//	2.1.2 BRACKETLESS expression
										if (bracketExpressions.contains(expression)) {
											sendError(expression, missingArgument);
										} else {
											// EXECUTION of bracketless expressions
											if (expression.equals("do")) {
												Do();
											}
											if (expression.equals("cancelEvent")) {
												cancelEvent();
											}
											
											// PREMADE VARIABLES
											//==================
											if (expression.equals("<triggeringPlayer>")) {
												if (entitySet.player != null)
													result = entitySet.player.getName();
											}
											if (expression.equals("<triggeringCuboid>")) {
												if (entitySet.cuboid != null)
													result = entitySet.cuboid.getName();
											}
											if (expression.equals("<cuboidBlockLocation>")) {
												if (entitySet.location != null)
													result = utils.locationToString(entitySet.location);
											}
											if (expression.equals("<cuboidBlockLocationX>")) {
												if (entitySet.location != null)
													result = String.valueOf(entitySet.location.getBlockX());
											}
											if (expression.equals("<cuboidBlockLocationY>")) {
												if (entitySet.location != null)
													result = String.valueOf(entitySet.location.getBlockY());
											}
											if (expression.equals("<cuboidBlockLocationZ>")) {
												if (entitySet.location != null)
													result = String.valueOf(entitySet.location.getBlockZ());
											}
											if (expression.equals("<setItem>")) {
												if (entitySet.setItem != null)
													result = entitySet.setItem;
											}
											// interact-related variables
											if (entitySet.interactEvent != null) {
												boolean rightClick = (entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_AIR)||entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK));
												
												String clickedLocation;
												String clickedLocationX;
												String clickedLocationY;
												String clickedLocationZ;
												String clickedLocationID;
												if (entitySet.interactEvent.hasBlock()) {
													clickedLocation = utils.locationToString(entitySet.interactEvent.getClickedBlock().getLocation());
													clickedLocationX = String.valueOf(entitySet.interactEvent.getClickedBlock().getLocation().getBlockX());
													clickedLocationY = String.valueOf(entitySet.interactEvent.getClickedBlock().getLocation().getBlockY());
													clickedLocationZ = String.valueOf(entitySet.interactEvent.getClickedBlock().getLocation().getBlockZ());
													clickedLocationID = String.valueOf(entitySet.interactEvent.getClickedBlock().getTypeId());
												}
												else {
													clickedLocation = "none";
													clickedLocationX = "none";
													clickedLocationY = "none";
													clickedLocationZ = "none";
													clickedLocationID = "none";
												}
													
												if (expression.equals("<clickedLocationID>")) {
													result = clickedLocationID;
												}
												if (expression.equals("<clickedLocation>")) {
														result = clickedLocation;
												}
												if (expression.equals("<clickedLocationX>")) {
													result = clickedLocationX;
												}
												if (expression.equals("<clickedLocationY>")) {
													result = clickedLocationY;
												}
												if (expression.equals("<clickedLocationZ>")) {
													result = clickedLocationZ;
												}
												if (expression.equals("<rightClick>")) {
													result = String.valueOf(rightClick);
												}
											}
											// BlockPlace-related variables
											if (entitySet.blockPlaceEvent != null) {
												Block placedBlock = entitySet.blockPlaceEvent.getBlockPlaced();
												String placedBlockLocation = utils.locationToString(placedBlock.getLocation());
												String placedBlockLocationX = String.valueOf(placedBlock.getLocation().getBlockX());
												String placedBlockLocationY = String.valueOf(placedBlock.getLocation().getBlockY());
												String placedBlockLocationZ = String.valueOf(placedBlock.getLocation().getBlockZ());
												String placedBlockID = String.valueOf(placedBlock.getTypeId());
												String placedBlockData = String.valueOf(placedBlock.getData());
												
												if (expression.equals("<placedBlockID>")) {
														result = placedBlockID;
												}
												if (expression.equals("<placedBlockData>")) {
													result = placedBlockData;
												}
												if (expression.equals("<placedBlockLocation>")) {
													result = placedBlockLocation;
												}
												if (expression.equals("<placedBlockLocationX>")) {
													result = placedBlockLocationX;
												}
												if (expression.equals("<placedBlockLocationY>")) {
													result = placedBlockLocationY;
												}
												if (expression.equals("<placedBlockLocationZ>")) {
													result = placedBlockLocationZ;
												}
											}
											// BlockBreak-related variables
											if (entitySet.blockBreakEvent != null) {
												Block breakBlock = entitySet.blockBreakEvent.getBlock();
												String breakBlockLocation = utils.locationToString(breakBlock.getLocation());
												String breakBlockLocationX = String.valueOf(breakBlock.getLocation().getBlockX());
												String breakBlockLocationY = String.valueOf(breakBlock.getLocation().getBlockY());
												String breakBlockLocationZ = String.valueOf(breakBlock.getLocation().getBlockZ());
												String breakBlockID = String.valueOf(entitySet.typeID);
												String breakBlockData = String.valueOf(entitySet.data);
												
												if (expression.equals("<brokenBlockID>")) {
														result = breakBlockID;
												}
												if (expression.equals("<brokenBlockData>")) {
													result = breakBlockData;
												}
												if (expression.equals("<brokenBlockLocation>")) {
													result = breakBlockLocation;
												}
												if (expression.equals("<brokenBlockLocationX>")) {
													result = breakBlockLocationX;
												}
												if (expression.equals("<brokenBlockLocationY>")) {
													result = breakBlockLocationY;
												}
												if (expression.equals("<brokenBlockLocationZ>")) {
													result = breakBlockLocationZ;
												}
											}
											
											// Death-related variables
											if (entitySet.deathEvent != null) {
												if (expression.equals("<deathCause>")) {
														result = entitySet.deathCause;
												}
											}
											
											/*
											"<randomInt>"
											*/
											
											// resolve user-defined String variables
											Map<String,SEstring> tempStrings = SEdata.getAllStringVars();
											if (tempStrings != null) {
												if (this.packageName != null)
													expression = "<"+this.packageName+"."+expression.substring(1, expression.length()-1)+">";
												for ( Iterator<String> i = tempStrings.keySet().iterator(); i.hasNext(); ) {
													String tempVar = (String) i.next();
													// utils.SElog(1, expression+" = "+ "<"+tempVar+">"); // debug
													if (expression.equals("<"+tempVar+">")) {
														result = tempStrings.get(tempVar).getValue();
													}
												}
											}
												
											
											// resolve user-defined Integer variables
											Map<String,SEinteger> tempIntegers = SEdata.getAllIntVars();
											if (tempIntegers != null) {
												if (this.packageName != null)
													expression = "<"+this.packageName+"."+expression.substring(1, expression.length()-1)+">";
												for ( Iterator<String> i = tempIntegers.keySet().iterator(); i.hasNext(); ) {
													String tempVar = (String) i.next();
													// utils.SElog(1, expression+" = "+ "<"+tempVar+">"); // debug
													
													//SEutils.SElog(1, expression+" = <"+tempVar+">");
													
													if (expression.equals("<"+tempVar+">")) {
														//SEutils.SElog(1, "value: "+tempIntegers.get(tempVar).getValue());
														result = String.valueOf(tempIntegers.get(tempVar).getValue());
													}
												}
											}
										}
									}	
								} else {
									sendError(expression, unexpectedExpression);
								}
							}
						}
					}
				}		
			}
		}
		
		return result;
	}

	
	//-------------//
	// INPUT STUFF //
	//-------------//

	public boolean checkInput(String expression, boolean check) {
		if (!check) {
			sendError(expression, wrongArguments);
			//utils.SElog(2, wrongArguments+" in line: "+this.workingLine);
		}
		return check;
	}
	
	public String inputToInteger(String expression, String input) {
		String result = "null";
		
		if (input.contains("(") && input.contains(")")) {
			String temp = executeLine(input, functions);
			if (temp != "null" && temp != null)
				result = String.valueOf(Integer.valueOf(temp));
		} else {
			try {
				result = String.valueOf(Integer.valueOf(input));
			} catch (Exception e) {
				sendError(expression, invalidInteger);
			}
		}
		return result;
	}
	
	// trys to turn an input String into a player and can send an error
	public Player inputToPlayer(String expression, String input) {
		Player result = null;
		result = utils.stringToPlayer(plugin.getServer().getOnlinePlayers(), input);
		if (result == null)
			sendError(expression, playerNotFound);
		return result;
	}
	
	// trys to turn an input String into a world and can send an error
	public World inputToWorld(String expression, String input) {
		World result = null;
		result = plugin.getServer().getWorld(input);
		if (result == null)
			sendError(expression, worldNotFound);
		return result;
	}
	
	// trys to turn an input String into a cuboid and can send an error
	public SEcuboid inputToCuboid(String expression, String input) {
		SEcuboid result = null;
		if (this.packageName != null)
			input = packageName+"."+input;
		result = SEdata.getAllCuboids().get(input);
		if (result == null)
			sendError(expression, cuboidNotFound);
		return result;
	}
		
	// trys to turn an input String into a trigger and can send an error
	public SEtrigger inputToTrigger(String expression, String input) {
		SEtrigger result = null;
		if (this.packageName != null)
			input = packageName+"."+input;
		result = SEdata.getAllTriggers().get(input);
		if (result == null)
			sendError(expression, triggerNotFound);
		return result;
	}

	// trys to turn an input String into a set-variable and can send an error
	public SEset inputToSet(String expression, String input) {
		SEset result = null;
		if (this.packageName != null)
			input = packageName+"."+input;
		result = SEdata.getSetVarList().get(input); 
		if (result == null)
			sendError(expression, setNotFound);
		return result;
	}
	
	// trys to turn an input String into a script and can send an error
	public SEscript inputToScript(String expression, String input) {
		SEscript result = null;
		if (this.packageName != null)
			input = packageName+"."+input;
		result = SEdata.getAllScripts().get(input);
		if (result == null)
			sendError(expression, scriptNotFound);
		return result;
	}

	// trys to turn an input String into a condition and can send an error
	public SEcondition inputToCondition(String expression, String input) {
		SEcondition result = null;
		if (this.packageName != null)
			input = packageName+"."+input;
		result = SEdata.getAllConditions().get(input);
		if (result == null)
			sendError(expression, conditionNotFound);
		return result;
	}
	
	// trys to turn an input String into location and can send an error
	public Location inputToLocation(String expression, String input) {
		Location result = null;
		result = utils.stringToLocation(input);
		if (result == null)
			sendError(expression, invalidLocation);
		return result;
	}
	
	public boolean correctBrackets(String input) {
		boolean result = true; 
		int x=0;
		for (int i=0; i<input.length();i++) {
			if (input.charAt(i)=='(')
				x++;
			if (input.charAt(i)==')')
				x--;
			if (x<0) {
				i = input.length();
				sendError(null, missingOpenBracket);
				result = false;
			}
		}
		if (x>0) {
			sendError(null, missingCloseBracket);
			result = false;
		}
		return result;
	}
	
	public boolean correctAngleBrackets(String input) {
		boolean result = true; 
		int x=0;
		for (int i=0; i<input.length();i++) {
			if (input.charAt(i)=='<')
				x++;
			if (input.charAt(i)=='>')
				x--;
			if (x<0) {
				i = input.length();
				sendError(null, missingOpenAngleBracket);
				result = false;
			}
		}
		if (x>0) {
			sendError(null, missingCloseAngleBracket);
			result = false;
		}
		return result;
	}
	
	public String resolveFunctions(String input) {
		String result = input;
		
		Iterator<String> lauf = functions.iterator();
		while (lauf.hasNext()) {
			String function = lauf.next();
			while (result.contains(function+"(")) {
				String temp = function+result.substring(result.indexOf("(", result.indexOf(function)), utils.findBracket(result,result.indexOf("(", result.indexOf(function)))+1);
				result = result.replace(temp, executeLine(temp, functions));
			}
		}
		return result;
	}
	
	public String resolveVariables(String input) {
		// input: changeBlockType(world,<blockLocation>,0)
		// dummy: changeBlockTypeXXXXXXXXXXXXXXXXXXXXXXXXX
		
		String result = input;
		String angleBracket = null;
		String dummy = result;
		
		while(dummy.contains("(") && dummy.contains(")")) {
			String tempBracket = dummy.substring(dummy.indexOf("("), utils.findBracket(dummy, dummy.indexOf("("))+1);
			dummy = dummy.replace(tempBracket, tempBracket.replaceAll(".", "X"));
		}
		
		while (dummy.contains("<")) {
			angleBracket = result.substring(result.indexOf("<"), utils.findAngleBracket(result, result.indexOf("<"))+1);
			String replacement = executeLine(angleBracket, variables);
			// utils.SElog(1, "variables: "+variables.toString()); // debug
			
			dummy = dummy.replace(angleBracket, replacement);
			result = result.replace(angleBracket, replacement);
			//utils.SElog(1, "msg: " +msg); // debug
		}
		return result;
	}
	
	public String[] resolveInput(String[] input) {
		// input: [<myCuboid>],[changeBlockType(world,<blockLocation>,0)]
				
		String[] result = input;
		for (int i=0; i<result.length;i++) {
			
			if (correctAngleBrackets(result[i]))
				result[i] = resolveVariables(result[i]);
			
			result[i] = resolveFunctions(result[i]);
		}
		
		return result;
	}

	
	
	//---------------//
	// WORLD ACTIONS //
	//---------------//
	
	// do() creates and executes subscripts
	public void Do() {
		Map<Integer,String> tempActions = this.script.getActionList();
		Map<Integer,String> subScriptActions = new HashMap<Integer,String>();
		
		int i=this.scriptLine+1;
		int j=1;
		while (tempActions.get(i)!=null && tempActions.get(i).trim().startsWith("|")) {
			subScriptActions.put(j, tempActions.get(i).trim().substring(1).trim());
			//utils.SElog(1, j+": "+subScriptActions.get(j)); // debug
			j++;
			i++;
		}
		
		if (!subScriptActions.isEmpty()) {
			SEscript subScript = new SEscript(null, "subScript", script.getOwner(), subScriptActions, this.packageName);
			SEentitySet subEntitySet = new SEentitySet();
			subEntitySet.script = subScript;
			SEinterpreter interpreter = new SEinterpreter(this.plugin, new SEtrigger(subEntitySet, script.getOwner(), this.packageName), entitySet, kindType.script, this.packageName);
			interpreter.start();
			
			while(interpreter.isAlive()) {
				try {
					sleep(10);
					//utils.SElog(1, "waiting"); // debug
				} catch (InterruptedException e) {
					SEutils.SElog(3, sleep);
				}
			}	
		}
	}
	
	// cancelEvent() cancels the fired bukkit-event 
	public void cancelEvent() {
		SEutils.SElog(1, "cancelEvent");
		playerListener.setCancel(true);
	}
	
	public void removeItemInHand(String[] input) {
		String name = "removeItemInHand";
		if (checkInput(name, input.length > 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if ((targetPlayer != null)&&(targetPlayer.getItemInHand()!=null)) {
				if (targetPlayer.getItemInHand().getAmount()>1) {
					targetPlayer.getItemInHand().setAmount(targetPlayer.getItemInHand().getAmount()-1);	
				} else {
					targetPlayer.getInventory().removeItem(targetPlayer.getItemInHand());
				}
			}	
		}
		
	}

	// removeItem(<world>,<location> | <player>,<type>[,<amount>])
	public void removeItem(String[] input) {
	    String name = "removeItem";
	    if (checkInput(name, (input.length == 2) || (input.length == 3))) {
	    	Player targetPlayer = inputToPlayer(name, input[0]);
	    	if ((targetPlayer != null) && (inputToInteger(name, input[1]) != "null")) {
	    		int removeAmount = 1;
	    		if ((input.length == 3) && (inputToInteger(name, input[2]) != "null")) {
	    			removeAmount = Integer.valueOf(inputToInteger(name, input[2])).intValue();
	    		}

	    		ItemStack tempItem = this.utils.searchItem(targetPlayer, Integer.valueOf(inputToInteger(name, input[1])).intValue(), removeAmount);

	    		if (tempItem != null)
	    			if (tempItem.getAmount() - removeAmount <= 0)
	    				targetPlayer.getInventory().removeItem(new ItemStack[] { tempItem });
	    			else
	    				tempItem.setAmount(tempItem.getAmount() - removeAmount);
	    	}
	    }
	}
	
	// removeItemAt(<player>,<slot>[,<amount>])
	public void removeItemAt(String[] input) {
		String name = "removeItemAt";
		if (checkInput(name, input.length == 2 || input.length == 3)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null && inputToInteger(name, input[1])!="null") {
				int slot = Integer.valueOf(inputToInteger(name, input[1]));
				ItemStack tempItem = targetPlayer.getInventory().getItem(slot);
				
				int removeAmount = 1;
				if (input.length==3 && inputToInteger(name, input[2]) != "null")
					removeAmount = Integer.valueOf(inputToInteger(name, input[2]));
				
				if (tempItem!=null) {
					if ((tempItem.getAmount()-removeAmount)<=0)
						targetPlayer.getInventory().removeItem(tempItem);
					else
						tempItem.setAmount(tempItem.getAmount()-removeAmount);	
				}		
			}
		}
	}
	
	// giveItem(<player>,<type>[,<amount>])
	public void giveItem(String[] input) {
		String name = "giveItem";
		if (checkInput(name, input.length == 2 || input.length == 3)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null && inputToInteger(name, input[1])!="null") {
				int type = Integer.valueOf(inputToInteger(name, input[1]));
				int amount = 1;
				if (input.length==3 && inputToInteger(name, input[2]) != "null")
					amount = Integer.valueOf(inputToInteger(name, input[2]));
				ItemStack tempItem = new ItemStack(type);
				tempItem.setAmount(amount);
				targetPlayer.getInventory().addItem(tempItem);
			}	
		}
	}
	
	public void giveItemAt(String[] input) {
		String name = "giveItemAt";
		if (checkInput(name, input.length == 3 || input.length == 4)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null && inputToInteger(name, input[1])!="null" && inputToInteger(name, input[2])!="null") {
				int slot = Integer.valueOf(inputToInteger(name, input[1]));
				int type = Integer.valueOf(inputToInteger(name, input[2]));
				int amount = 1;
				if (input.length==4 && inputToInteger(name, input[3]) != "null")
					amount = Integer.valueOf(inputToInteger(name, input[3]));
				
				ItemStack tempItem = new ItemStack(type);
				tempItem.setAmount(amount);
				
				if (targetPlayer.getInventory().getItem(slot).getTypeId()==type)
					targetPlayer.getInventory().getItem(slot).setAmount(targetPlayer.getInventory().getItem(slot).getAmount()+amount);
				else
					targetPlayer.getInventory().setItem(slot, tempItem);
			}	
		}
	}
		
	public void setHealth(String[] input) {
		String name = "setHealth";
		if (checkInput(name, input.length == 2)) {
			if (inputToInteger(name, input[1])!="null") {
				Player targetPlayer = inputToPlayer(name, input[0]);
				int newHealth = Integer.valueOf(inputToInteger(name, input[1]));
				if (targetPlayer!=null) {
					targetPlayer.setHealth(newHealth);
				}	
			}
		}
	}
	
	public void messageTo(String[] input) {
		String name = "messageTo";
		if (checkInput(name, input.length == 2)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!=null) {
				// targetPlayer.sendMessage(resolveFunctions(input[1])); hopefully not needed anymore
				targetPlayer.sendMessage(input[1]);
			}
		}
	}
	
	public void broadcast(String[] input) {
		String name = "broadcast";
		if (checkInput(name, input.length == 1)) {
			plugin.getServer().broadcastMessage(input[0]);
		}		
	}
	
	public void changeBlockType(String[] input) {
		String name = "changeBlockType";
		if (checkInput(name, input.length == 3)) {
			if (inputToInteger(name, input[2])!="null") {
				World targetWorld = inputToWorld(name, input[0]);
				Location targetLocation = inputToLocation(name, input[1]);
				if (targetWorld!=null && targetLocation!=null) {
						targetWorld.getBlockAt(targetLocation).setTypeId(Integer.valueOf(inputToInteger(name, input[2])));	
				}	
			}
			
		}
	}
	
	public void changeBlockData(String[] input) {
		String name = "changeBlockData";
		if (checkInput(name, input.length == 3)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if (targetWorld!=null) {
					targetWorld.getBlockAt(targetLocation).setData(Integer.valueOf(inputToInteger(name, input[2])).byteValue());
			}
		}
	}

	public void playerCommand(String[] input) {
		String name = "playerCommand";
		if (checkInput(name, input.length == 2)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!=null) {
				targetPlayer.performCommand(input[1]);
			}
		}
	}
	
	public void playEffect(String[] input) {
		String name = "playEffect";
		if (checkInput(name, input.length == 2)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			try {
				/*
				Avaiable Effects:
				Effect.BOW_FIRE
				Effect.CLICK1
				Effect.CLICK2
				Effect.DOOR_TOGGLE
				Effect.EXTINGUISH
				Effect.RECORD_PLAY
				Effect.SMOKE
				Effect.STEP_SOUND
				*/
				Effect effect = Effect.valueOf(input[1]);
				if (targetPlayer!=null) {
					targetPlayer.playEffect(targetPlayer.getLocation(), effect, 0);	
				}
			} catch (Exception e) {
				sendError(name, calcFailed);
				//SEutils.SElog(2, "\""+input[1]+"\": Unvalid effect given in line :" +this.scriptLine);
			}
		}
	}

	public String teleport(String[] input) {
		String name = "teleport";
		String result = "null";
		if (checkInput(name, input.length == 3)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			World targetWorld = inputToWorld(name, input[1]);
			Location targetLocation = inputToLocation(name, input[2]);
			targetLocation.setWorld(targetWorld);
			if (targetPlayer!= null && targetLocation != null) {
				targetPlayer.teleport(targetLocation);
			}
		}
		return result;
	}
	
	public String setTime(String[] input) {
		String name = "setTime";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			String time = inputToInteger(name, input[1]);
			if (targetWorld!= null && time != "null") {
				targetWorld.setTime(Integer.valueOf(time));
			}
		}
		return result;
	}
	
	public String toggleDoor(String[] input) {
		String name = "toggleDoor";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if (targetWorld!= null && targetLocation != null) {
				targetLocation.setWorld(targetWorld);
				Block block = targetLocation.getBlock();
			
				if (block.getType().equals(Material.WOOD_DOOR) || block.getType().equals(Material.WOODEN_DOOR) || block.getType().equals(Material.IRON_DOOR) || block.getType().equals(Material.IRON_DOOR_BLOCK)) {
					Door door = (Door)block.getState().getData();
					//Swing the door open/shut
		            block.setData((byte)(block.getState().getData().getData()^4));
		            Block neighbor;
		            if (door.isTopHalf())
		                neighbor = block.getRelative(BlockFace.DOWN);
		            else
		                neighbor = block.getRelative(BlockFace.UP);
		            neighbor.setData((byte)(neighbor.getState().getData().getData()^4));
				}
				
				if (block.getType().equals(Material.TRAP_DOOR)) {
					//Swing the door open/shut
		            block.setData((byte)(block.getState().getData().getData()^4));
				}
			}
		}
		return result;
	}
	
	public String toggleLever(String[] input) {
		String name = "toggleLever";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if (targetWorld!= null && targetLocation != null) {
				targetLocation.setWorld(targetWorld);
				Block block = targetLocation.getBlock();
				
				if (block.getType().equals(Material.LEVER)) {
					//toggle the Lever
			        byte data = block.getData();
			        int on = data | 0x8;
			        int off = data & 0x7;
			        if(data == on){
			        	block.setData((byte) off, true);
			        } else {
			        	block.setData((byte) on, true);
			        }
				}
			}
		}
		return result;
	}
	
	//----------------//
	// SCRIPT ACTIONS //
	//----------------//
	
	public void If(String[] input) {
		String name = "if";
		if (checkInput(name, input.length == 1)) {
			String temp = executeLine(input[0], conditions);
			if (temp != "null") {
				check = Boolean.valueOf(temp);	
			}
			//utils.SElog(1, input+": "+String.valueOf(check)); // debug
			// executeLine(input[0], conditions)
			If = true;	
		}
	}
	
	public void Then(String[] input) {
		String name = "then";
		if (checkInput(name, input.length == 1) && If && check) {
			executeLine(input[0], actions);
			If = false;
		}
	}
	
	public void Else(String[] input) {
		String name = "else";
		if (checkInput(name, input.length == 1) && If && !check) {
			executeLine(input[0], actions);
			If = false;
		}
	}
	
	public void delay(String[] input) {
		String name = "delay";
		if (checkInput(name, input.length == 1)) {
			if (inputToInteger(name, input[0])!="null") {
				try {
					sleep(Integer.valueOf(inputToInteger(name, input[0])));
				} catch (InterruptedException e) {
					SEutils.SElog(3, sleep);
				}	
			}
		}
	}
	
	public void trigger(String[] input) {
		String name = "trigger";
		if (checkInput(name, input.length == 1)) {
			SEtrigger tempTrigger = inputToTrigger(name, input[0]);
			
			if (tempTrigger != null) {
				SEinterpreter interpreter = new SEinterpreter(this.plugin, tempTrigger, entitySet, kindType.script, this.packageName);
				interpreter.start();	
			}
		}
	}
	
	public void script(String[] input) {
		String name = "script";
		if (checkInput(name, input.length == 1)) {
			SEscript tempScript;
			tempScript = inputToScript(name, input[0]);
			
			if (tempScript != null) {
				SEentitySet subEntitySet = new SEentitySet();
				subEntitySet.script = tempScript;
				SEinterpreter interpreter = new SEinterpreter(this.plugin, new SEtrigger(subEntitySet, script.getOwner(), this.packageName), entitySet, kindType.script, this.packageName);
				interpreter.start();	
			}
		}
	}
	
	public void doForCuboidBlocks(String[] input) {
		String name = "doForCuboidBlocks";
		if (checkInput(name, input.length == 2)) {
			SEcuboid tempCuboid = inputToCuboid(name, input[0]);
			World tempWorld = null;
			if (tempCuboid != null)
				tempWorld = inputToWorld(name, tempCuboid.getWorld());
			String action = input[1]; 
			
			if (tempCuboid != null && tempWorld != null) {
				int smallerX = tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX();
				int smallerY = tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY();
				int smallerZ = tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ();
				int biggerX = tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX();
				int biggerY = tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY();
				int biggerZ = tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ();
				
				for (int x=smallerX; x<=biggerX; x++) {
					for (int y=smallerY; y<=biggerY; y++) {
						for (int z=smallerZ; z<=biggerZ; z++) {
							entitySet.location = new Location(tempWorld, x, y, z);
							executeLine(action, actions);
						}
					}
				}	
			}
		}
	}
	
	public void doForSetItems(String[] input) {
		String name = "doForSetItems";
		if (checkInput(name, input.length == 2)) {
			
			SEset tempSet = inputToSet(name, input[0]);
			String action = input[1];
			
			Iterator<String> lauf = tempSet.getValues().iterator();
			
			while (lauf.hasNext()) {
				entitySet.setItem = lauf.next();
				executeLine(action, actions);	
			}
		}
	}
	
	public void loop(String[] input) {
		String name = "loop";
		if (checkInput(name, input.length == 1)) {
			if (inputToInteger(name, input[0])!="null") {
				int cycles = Integer.valueOf(inputToInteger(name, input[0]));
				
				if (this.cycles == -1 && cycles > 0) {
					this.cycles = cycles;
				}
				
				if (this.cycles>0) {
					this.cycles--;
					this.scriptLine = 0;
				}	
			}
		}
	}
	
	public void While(String[] input) {
		String name = "while";
		if (checkInput(name, input.length == 1)) {
			String temp = executeLine(input[0], conditions);
			if (temp != "null") {
				check = Boolean.valueOf(temp);	
			}
			
			if (check) {
				this.scriptLine = 0;
			}
		}
	}
	
	/*
	public void setRandomRange(String[] input) {
		String name = "setRandomRange";
		if (checkInput(name, input.length == 2)) {
			if (inputToInteger(name, input[0])!="null") {
				int cycles = Integer.valueOf(inputToInteger(name, input[0]));
				
				if (this.cycles == -1 && cycles > 0) {
					this.cycles = cycles;
				}
				
				if (this.cycles>0) {
					this.cycles--;
					this.scriptLine = 0;
				}	
			}
		}
	}
	*/
	
	//-----------//
	// FUNCTIONS //
	//-----------//
	
	public String calc(String[] input) {
		String name = "calc";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			try {
				result = String.valueOf(utils.calc(input[0]));
			} catch (Exception e) {
				sendError(name, calcFailed);
				//SEutils.SElog(2, "Calculation failed in line: "+this.workingLine);
			}
		}
		return result;
	}
	
	public String health(String[] input) {
		String name = "health";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null)
				result = String.valueOf(targetPlayer.getHealth());
		}
		return result;
	}
	
	public String itemInHand(String[] input) {
		String name = "itemInHand";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null)
				result = String.valueOf(targetPlayer.getItemInHand().getTypeId());
		}
		return result;
	}

	public String itemAtSlot(String[] input) {
		String name = "itemAtSlot";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null && inputToInteger(name, input[1]) != "null") {
				int slot = Integer.valueOf(inputToInteger(name, input[1]));
				result = String.valueOf(targetPlayer.getInventory().getItem(slot).getTypeId());
			}
		}
		return result;
	}
	
	public String searchItem(String[] input) {
		String name = "searchItem";
		String result = "null";
		if (checkInput(name, input.length == 2 || input.length == 3)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null && inputToInteger(name, input[1]) != "null") {
				int type = Integer.valueOf(inputToInteger(name, input[1]));
				
				int amount = 1;
				if (input.length==3 && inputToInteger(name, input[2]) != "null")
					amount = Integer.valueOf(inputToInteger(name, input[2]));
				
				if (targetPlayer.getInventory().getItemInHand().getTypeId() == type &&
					targetPlayer.getInventory().getItemInHand().getAmount() >= amount) {
					ItemStack itemInHand = targetPlayer.getItemInHand();
					for (int i=0;i<=8;i++) {
						if (itemInHand.equals(targetPlayer.getInventory().getContents()[i]))
							result = String.valueOf(i);
					}
				} else {
					for (int i=39;i>=0;i--) {
						if (targetPlayer.getInventory().getItem(i).getTypeId() == type &&
							targetPlayer.getInventory().getItem(i).getAmount() >= amount)
							result = String.valueOf(i);
					}	
				}
			}
		}
		return result;
	}
	
	public String slotInHand(String[] input) {
		String name = "slotInHand";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer != null) {
				/*
				ItemStack itemInHand = targetPlayer.getItemInHand();
				ItemStack[] contents = targetPlayer.getInventory().getContents();
				for (int i=0;i<=8;i++) {
					if (itemInHand.equals(contents[i]))
						result = String.valueOf(i);
				}
				*/
				result = String.valueOf(targetPlayer.getInventory().getHeldItemSlot());
			}
		}
		return result;
	}
	
	public String time(String[] input) {
		String name = "time";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			World targetWorld= inputToWorld(name, input[0]);
			if (targetWorld!= null)
				result = String.valueOf(targetWorld.getTime());
		}
		return result;
	}
	
	public String blockID(String[] input) {
		String name = "blockID";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if ((targetWorld != null) && (targetLocation != null))
				result = String.valueOf(targetWorld.getBlockAt(targetLocation).getTypeId());
		}
		return result;
	}
	
	public String blockData(String[] input) {
		String name = "blockData";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if ((targetWorld != null) && (targetLocation != null))
				result = String.valueOf(targetWorld.getBlockAt(targetLocation).getData());
		}
		return result;
	}
	
	public String isInBed(String[] input) {
		String name = "isInBed";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.isSleeping());
		}
		return result;
	}

	public String isSneaking(String[] input) {
		String name = "isSneaking";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.isSneaking());
		}
		return result;
	}
	
	public String playerLocation(String[] input) {
		String name = "playerLocation";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = utils.locationToString(targetPlayer.getLocation());
		}
		return result;
	}
	
	public String playerLocationX(String[] input) {
		String name = "playerLocationX";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.getLocation().getBlockX());
		}
		return result;
	}
	
	public String playerLocationY(String[] input) {
		String name = "playerLocationY";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.getLocation().getBlockY());
		}
		return result;
	}
	
	public String playerLocationZ(String[] input) {
		String name = "playerLocationZ";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.getLocation().getBlockZ());
		}
		return result;
	}
	
	public String playerWorld(String[] input) {
		String name = "playerWorld";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			if (targetPlayer!= null)
				result = targetPlayer.getWorld().getName();
		}
		return result;
	}
	
	public String size(String[] input) {
		String name = "size";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			SEset targetSet = inputToSet(name, input[0]);
			if (targetSet!= null)
				result = String.valueOf(targetSet.getValues().size());
		}
		return result;
	}
	
	public String arg(String[] input) {
		String name = "arg";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			String i = inputToInteger(name, input[0]);
			if ((i!= "null") && (entitySet.args.length > Integer.valueOf(i)) && (Integer.valueOf(i)>=0))
				result = entitySet.args[Integer.valueOf(i)];
		}
		return result;
	}
	
	public String random(String[] input) {
		String name = "random";
		String result = "null";
		Random generator = new Random();
		int randomInt = 0;
		
		if (checkInput(name, input.length == 2 || input.length == 1)) {
			
			if (input.length==1 && inputToInteger(name, input[0]) != "null") {
				int max = Integer.valueOf(inputToInteger(name, input[0]));
				randomInt = generator.nextInt(max);
				result = String.valueOf(randomInt);
			}
			if (input.length==2 && inputToInteger(name, input[0]) != "null" && inputToInteger(name, input[1]) != "null") {
				int min = Integer.valueOf(inputToInteger(name, input[0]));
				int max = Integer.valueOf(inputToInteger(name, input[1]))-min+1;
				randomInt = generator.nextInt(max)+min;
				result = String.valueOf(randomInt);
			}
		}
		return result;
	}

	
	//------------//
	// CONDITIONS //
	//------------//
	
	public String Equals(String[] input) {
		String name = "equals";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
					result = String.valueOf(input[0].equals(input[1]));
		}
		return result;
	}
	
	public String hasItem(String[] input) {
		String name = "hasItem";
		String result = "null";
		if (checkInput(name, input.length == 2 || input.length == 3)) {
			if (inputToPlayer(name, input[0]) != null && inputToInteger(name, input[1]) != "null") {
				//SEutils.SElog(1, "test");
				Player targetPlayer = inputToPlayer(name, input[0]);
				int type = Integer.valueOf(inputToInteger(name, input[1]));
				int amount = 1;
				if (input.length==3 && inputToInteger(name, input[2]) != "null")
					amount = Integer.valueOf(inputToInteger(name, input[2]));
				
				ItemStack searchedItem = utils.searchItem(targetPlayer, type, amount);
				result = String.valueOf(searchedItem != null);
			}
		}
		return result;
	}
	
	public String bigger(String[] input) {
		String name = "bigger";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			if (inputToInteger(name, input[0]) != "null" && inputToInteger(name, input[1]) != "null") {
				int one = Integer.valueOf(inputToInteger(name, input[0]));
				int two = Integer.valueOf(inputToInteger(name, input[1]));
				result = String.valueOf(one>two);
			}
		}
		return result;
	}
	
	public String contains(String[] input) {
		String name = "contains";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			if (inputToSet(name, input[0]) != null) {
				result = String.valueOf(inputToSet(name, input[0]).getValues().contains(input[1]));
			}
		}
		return result;
	}
	
	public String and(String[] input) {
		String name = "and";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			String check1 = executeLine(input[0], conditions);
			String check2 = executeLine(input[1], conditions);
			
			if (check1 != "null" && check2 != "null") {
				result = String.valueOf((Boolean.valueOf(check1) && Boolean.valueOf(check2))); 
			}
		}
		return result;
	}
	
	public String or(String[] input) {
		String name = "or";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			String check1 = executeLine(input[0], conditions);
			String check2 = executeLine(input[1], conditions);
			
			if (check1 != "null" && check2 != "null") {
				result = String.valueOf((Boolean.valueOf(check1) || Boolean.valueOf(check2))); 
			}
		}
		return result;
	}
	
	public String check(String[] input) {
		String name = "check";
		String result = "null";
		boolean tempCheck = false;
		
		SEcondition tempCondition;
		tempCondition = inputToCondition(name, input[0]);	
		
		if (checkInput(name, input.length == 1)) {
			if (tempCondition != null && tempCondition.getConditionList() != null) {
				SEentitySet subEntitySet = new SEentitySet();
				subEntitySet.condition = tempCondition;
				SEinterpreter interpreter = new SEinterpreter(this.plugin, new SEtrigger(subEntitySet, script.getOwner(), this.packageName), entitySet, kindType.condition, this.packageName);
				interpreter.start();
				while(interpreter.isWorking)
					try {
						sleep(10);
					} catch (InterruptedException e) {
					}
				tempCheck = interpreter.check;
				result = String.valueOf(tempCheck);
				}
		}
		return result;
	}
	
	public String online(String[] input) {
		String name = "online";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			result = "false";
			Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
			String playerName = input[0];
			for (int i = 0; ((i < onlinePlayers.length) && (result == "false")) ; i++) {
				if (onlinePlayers[i].getName().equals(playerName)) {
					result = "true";
				}
			}
		}
		return result;
	}
	
	public String isEmpty(String[] input) {
		String name = "isEmpty";
		String result = "null";
		if (checkInput(name, input.length == 1)) {
			SEset tempSet = inputToSet(name, input[0]);
			if (tempSet!=null) {
				result = String.valueOf(tempSet.getValues().isEmpty());
			}
		}
		return result;
	}
	
	public String isOpen(String[] input) {
		String name = "isOpen";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if (targetWorld != null && targetLocation != null) {
				targetLocation.setWorld(targetWorld);
				Block block = targetLocation.getBlock();
				
				if (block.getType().equals(Material.WOOD_DOOR) || block.getType().equals(Material.WOODEN_DOOR) || block.getType().equals(Material.IRON_DOOR) || block.getType().equals(Material.IRON_DOOR_BLOCK)) {
					Door door = (Door)block.getState().getData();
					result = String.valueOf(!door.isOpen());
				}
				
				if (block.getType().equals(Material.TRAP_DOOR)) {
					TrapDoor door = (TrapDoor)block.getState().getData();
					result = String.valueOf(door.isOpen());
				}
			}
		}
		return result;
	}
	
	public String isSwitchedOn(String[] input) {
		String name = "isOn";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			World targetWorld = inputToWorld(name, input[0]);
			Location targetLocation = inputToLocation(name, input[1]);
			if (targetWorld != null && targetLocation != null) {
				targetLocation.setWorld(targetWorld);
				Block block = targetLocation.getBlock();
				
				if (block.getType().equals(Material.LEVER)) {
			        byte data = block.getData();
			        int on = data | 0x8;
			        return String.valueOf(data == on);
				}
			}
		}
		return result;
	}

	public String isInside(String[] input) {
		String name = "isInside";
		String result = "null";
		if (checkInput(name, input.length == 2)) {
			Player targetPlayer = inputToPlayer(name, input[0]);
			SEcuboid targetCuboid = inputToCuboid(name, input[1]);
			if (targetPlayer != null && targetCuboid != null) {
				return String.valueOf(plugin.playerListener.playerInsideCuboid(targetPlayer.getLocation(), targetCuboid) || plugin.playerListener.playerInsideCuboid(targetPlayer.getEyeLocation(), targetCuboid));
			}
		}
		return result;
	}
	
	public String hasPermission(String[] input) {
		String name = "hasPermission";
		String result = "null";
		if (plugin.hasPermissions && checkInput(name, input.length == 3)) {
			World targetWorld = inputToWorld(name, input[0]);
			Player targetPlayer = inputToPlayer(name, input[1]);
			if (targetPlayer != null && targetWorld != null) {
				return String.valueOf(plugin.permissionHandler.has(targetWorld.getName(), targetPlayer.getName(), input[2]));
			}
		}
		return result;
	}
	
	public String inGroup(String[] input) {
		String name = "inGroup";
		String result = "null";
		if (plugin.hasPermissions && checkInput(name, input.length == 3)) {
			World targetWorld = inputToWorld(name, input[0]);
			Player targetPlayer = inputToPlayer(name, input[1]);
			if (targetPlayer != null && targetWorld != null) {
				return String.valueOf(plugin.permissionHandler.inGroup(targetWorld.getName(), targetPlayer.getName(), input[2]));
			}
		}
		return result;
	}
	
	//-----------//
	// OLD STUFF //
	//-----------//
	
	/*
	// check inGroup(<world>,<player>,<group>)
	if (condition.contains("inGroup(") && !checked) {	
		condition = condition.substring(condition.indexOf('('));
		condition = condition.substring(1, condition.length()-1);
		String[] conditionStrings = condition.split(","); 
		
		//utils.SElog(1, conditionStrings[0] + conditionStrings[1] + conditionStrings[2]); // debug
		
		if (conditionStrings.length==3) {
			result = commander.permissionHandler.inGroup(conditionStrings[0], conditionStrings[1], conditionStrings[2]);
		}
		checked = true;
	}
	*/
}
