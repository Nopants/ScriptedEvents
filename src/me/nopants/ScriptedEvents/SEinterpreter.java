package me.nopants.ScriptedEvents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.nopants.ScriptedEvents.SEcondition.logicalOperator;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class SEinterpreter extends Thread {
	
	private ScriptedEvents plugin;
	private SEdataManager SEdata;
	private SEutils utils;
	private SEcommander commander;
	private SEplayerListener playerListener;
	
	SEtrigger trigger = null;
	SEscript script = null;
	SEcondition condition = null;
	SEentitySet entitySet = null;
	
	int input = 0;
	int cycles = -1;
	boolean check = true;
	boolean If = false;
	
	String unexpectedExpression = "Unexpected Expression in line: ";
	String unknownExpression = "Unknown Expression in line: ";
	String tooManyExpressions = "Too many Expressions in line: ";
	String wrongArguments = "Wrong number of Arguments in line: ";
	String missingArgument = "More Arguments expected in line: ";
	String missingCloseBracket = "Closing bracket expected in line: ";
	String missingOpenBracket = "Opening bracket expected in line: ";
	String missingCloseAngleBracket = "Closing angle bracket expected in line: ";
	String missingOpenAngleBracket = "Opening angle bracket expected in line: ";
	String unexpectedBracket = "Unexpected bracket in line: ";
	String sleep = "Interpreter thread doesn't want to go to sleep yet!";
	
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
			"setHealth",
			"messageTo",
			"changeBlockType",
			"changeBlockData",
			"playerCommand",
			"playEffect",
			"playerInv"
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
			"setRandomRange",
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
			"time",
			"isInBed",
			"isSneaking",
			"playerLocation",
			"blockID",
			"blockData",
			"calc",
			"size",
			"arg",
			"random"
			));

	// CONDITIONS
	// ==========
	
	// Conditions:
	Set<String> conditions = new HashSet<String>(Arrays.asList(
			"equals",
			"bigger",
			"and",
			"or",
			"check",
			"hasItem"
			));

	// VARIABLES
	// =========

	// Premade event-related variables:
	Set<String> preVariables = new HashSet<String>(Arrays.asList(
			"<triggeringPlayer>",
			"<triggeringCuboid>",
			"<blockLocation>",
			"<clickedLocation>",
			"<rightClick>",
			"<randomInt>",
			"<item>"
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
	public SEinterpreter(ScriptedEvents newPlugin, SEtrigger newTrigger, SEentitySet newEntitySet) {
		this.plugin = newPlugin;
		this.SEdata = newPlugin.SEdata;
		this.utils = newPlugin.utils;
		this.commander = newPlugin.commander;
		this.playerListener = newPlugin.playerListener;
		this.trigger = newTrigger;
		this.script = newTrigger.getScript();
		this.condition = newTrigger.getCondition();
		this.entitySet = newEntitySet;
		this.input = 1;
	}

	// overrides run()
	@SuppressWarnings("unchecked")
	public void run() {
		HashSet<String> intVars = new HashSet<String>();
		intVars.addAll(SEdata.getIntVarList().keySet());
		intVars = (HashSet<String>) intVars.clone();
		tempIntegers = (HashSet<String>) intVars.clone();
		Iterator<String> intLauf = intVars.iterator();
		
		HashSet<String> stringVars = new HashSet<String>();
		stringVars.addAll(SEdata.getStringVarList().keySet());
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
		
		//utils.SElog(1, tempStrings.toString()); // debug
		//utils.SElog(1, tempIntegers.toString()); // debug
		
		variables.addAll(tempIntegers);
		variables.addAll(tempStrings);
		expressions.addAll(tempIntegers);
		expressions.addAll(tempStrings);
		executeScript();
	}
		
	
	//-------------------//
	// INTERPRETER STUFF //
	//-------------------//
	
	// executes the interpreters script
	public void executeScript(){
		if (script != null && script.getActionList() != null) {
			Map<Integer, String> actionList = script.getActionList();
			boolean check = true;
			
			if (condition != null && condition.getConditionList() != null) {
				check = checkConditionList(condition.getOperator(), condition.getConditionList(), entitySet);
			}
			
			if (check)
				for (this.input = 1; this.input<=actionList.size(); this.input++) {
					String temp = actionList.get(this.input);
					if (temp != "")
						executeLine(temp, actions);
				}
		}	
	}	
	
	// interprets and executes a Script-Line
	private String executeLine(String line, Set<String> expected) {
		String result = "null";
		
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
			
			// BRACKETS are correct
			if (correctBrackets(line)) {
				
				// ignore subscripts or comment-lines
				if (! (line.startsWith("|") || line.startsWith("#") || line.isEmpty())) {
					// dispatch and ignore server-commands
					if (line.startsWith("/")) {
						plugin.getServer().dispatchCommand(new ConsoleCommandSender(plugin.getServer()),line.substring(1));
					} else {
						// remove comments
						if (line.contains("#"))
							line = line.substring(1, line.indexOf("#"));
						
						// remove BRACKETS in dummy
						String[] words;
						if (line.contains("(") && line.contains(")")) {
							String bracket = line.substring(line.indexOf("("), line.lastIndexOf(")")+1);
							String dummy = line.replace(bracket, bracket.replaceAll(".", "X"));
							words = dummy.split(" ");
						} else
							words = line.split(" ");
						
						// ONE WORD per line (brackets are ignored)
						if (words.length > 1) {
							utils.SElog(2, tooManyExpressions+this.input);
						} else {
							String expression;
							
							// WORD to EXPRESSION
							if (line.contains("("))
								expression = line.substring(0, line.indexOf("("));
							else
								expression = line;
							
							// 1. UNKNOWN expression
							if (!expressions.contains(expression)) {
								utils.SElog(2, "\""+expression+"\": "+unknownExpression+this.input);
								expression = null;
							} else {
								
								// 2.    KNOWN expression
																
								// 2.1   EXPECTED expressions
								if (expected.contains(expression)) {
								// 2.1.1 BRACKET expression
									if (line.contains("(") || line.contains(")")) {
										if (!bracketExpressions.contains(expression)) {
											utils.SElog(2, "\""+expression+"\": "+unexpectedBracket+this.input);
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
											
											String[] dummyInput = dummy.split(",");
											int startIndex = 0;
											for (int i=0; i<dummyInput.length;i++){
												dummyInput[i] = mainBracket.substring(startIndex, startIndex + dummyInput[i].length());
												startIndex = startIndex + dummyInput[i].length() + 1;
												//utils.SElog(1, dummyInput[i]); // debug	
											}
											
											String[] input = dummyInput;
											
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
											if (expression.equals("setHealth")) {
												setHealth(input);
											}
											if (expression.equals("messageTo")) {
												messageTo(input);
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
											if (expression.equals("playerInv")) {
												playerInv(input);
											}
											
											/*
											"setRandomRange",
											*/
											
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
											if (expression.equals("size")) {
												result = size(input);
											}
											if (expression.equals("arg")) {
												result = arg(input);
											}
											if (expression.equals("random")) {
												result = random(input);
											}
										}
									} else {
								//	2.1.2 BRACKETLESS expression
										if (bracketExpressions.contains(expression)) {
											utils.SElog(2, "\""+expression+"\": "+missingArgument+this.input);
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
											if (expression.equals("<blockLocation>")) {
												if (entitySet.location != null)
													result = utils.locationToString(entitySet.location);
											}
											if (expression.equals("<item>")) {
												if (entitySet.item != null)
													result = entitySet.item;
											}
											// interact-related variables
											if (entitySet.interactEvent != null) {
												boolean rightClick = (entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_AIR)||entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK));
												
												String clickedLocation;
												if (entitySet.interactEvent.hasBlock())
													clickedLocation = utils.locationToString(entitySet.interactEvent.getClickedBlock().getLocation());
												else
													clickedLocation = "none";
												
												if (expression.equals("<clickedLocation>")) {
														result = clickedLocation;
												}
												if (expression.equals("<rightClick>")) {
													result = String.valueOf(rightClick);
												}
											}
											
											/*
											"<randomInt>"
											*/
											
											// resolve user-defined String variables
											Map<String,String> tempStrings = SEdata.getStringVarList();
											if (tempStrings != null) {
												for ( Iterator<String> i = tempStrings.keySet().iterator(); i.hasNext(); ) {
													String tempVar = (String) i.next();
													// utils.SElog(1, expression+" = "+ "<"+tempVar+">"); // debug
													if (expression.equals("<"+tempVar+">")) {
														result = tempStrings.get(tempVar);
													}
												}
											}
												
											
											// resolve user-defined Integer variables
											Map<String,Integer> tempIntegers = SEdata.getIntVarList();
											if (tempIntegers != null) {
												for ( Iterator<String> i = tempIntegers.keySet().iterator(); i.hasNext(); ) {
													String tempVar = (String) i.next();
													// utils.SElog(1, expression+" = "+ "<"+tempVar+">"); // debug
													if (expression.equals("<"+tempVar+">")) {
														result = String.valueOf(tempIntegers.get(tempVar));
													}
												}
											}
										}
									}	
								} else {
									utils.SElog(2, "\""+expression+"\": "+unexpectedExpression+this.input);
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

	public boolean checkInput(boolean check) {
		if (!check)
			utils.SElog(2, wrongArguments+this.input);
		return check;
	}
	
	// REWORK
	public String inputToInteger(String input) {
		String result = "null";
		
		// REWORK HERE
		if (input.contains("(") && input.contains(")")) {
			String temp = executeLine(input, functions);
			if (temp != "null" && temp != null)
				result = String.valueOf(Integer.valueOf(temp));
		} else {
			try {
				result = String.valueOf(Integer.valueOf(input));
			} catch (Exception e) {
				utils.SElog(2, "No Integer given in line: "+this.input);
			}
		}
		return result;
	}
	
	// trys to turn an input String into a player and can send an error
	public Player inputToPlayer(String input) {
		Player result = null;
		result = utils.stringToPlayer(plugin.getServer().getOnlinePlayers(), input);
		if (result == null)
			utils.SElog(2, "Player not found/online given in line: "+this.input);
		return result;
	}
	
	// trys to turn an input String into a world and can send an error
	public World inputToWorld(String input) {
		World result = null;
		result = plugin.getServer().getWorld(input);
		if (result == null)
			utils.SElog(2, "World not found given in line: "+this.input);
		return result;
	}
	
	// trys to turn an input String into a cuboid and can send an error
	public SEcuboid inputToCuboid(String input) {
		SEcuboid result = null;
		result = SEdata.getCuboidByID(SEdata.searchCuboidList(input));
		if (result == null)
			utils.SElog(2, "Cuboid not found given in line: "+this.input);
		return result;
	}
	
	// trys to turn an input String into a trigger and can send an error
	public SEtrigger inputToTrigger(String input) {
		SEtrigger result = null;
		result = SEdata.getTriggerByID(SEdata.searchTriggerList(input));
		if (result == null)
			utils.SElog(2, "Trigger not found given in line: "+this.input);
		return result;
	}

	// trys to turn an input String into a set-variable and can send an error
	public Set<String> inputToSet(String input) {
		Set<String> result = null;
		result = SEdata.getSetVarList().get(input); 
		if (result == null)
			utils.SElog(2, "Set variable not found given in line: "+this.input);
		return result;
	}
	
	// trys to turn an input String into a script and can send an error
	public SEscript inputToScript(String input) {
		SEscript result = null;
		result = SEdata.getScriptByID(SEdata.searchScriptList(input));
		if (result == null)
			utils.SElog(2, "Script not found given in line: "+this.input);
		return result;
	}

	// trys to turn an input String into location and can send an error
	public Location inputToLocation(String input) {
		Location result = null;
		result = utils.stringToLocation(input);
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
				utils.SElog(2, missingOpenBracket+this.input);
				result = false;
			}
		}
		if (x>0) {
			utils.SElog(2, missingCloseBracket+this.input);
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
				utils.SElog(2, missingOpenAngleBracket+this.input);
				result = false;
			}
		}
		if (x>0) {
			utils.SElog(2, missingCloseAngleBracket+this.input);
			result = false;
		}
		return result;
	}
	
	public String resolveFunctions(String input) {
		String result = input;
		
		Iterator<String> lauf = functions.iterator();
		while (lauf.hasNext()) {
			String temp = lauf.next();
			if (result.contains(temp+"(")) {
				temp = temp+result.substring(result.indexOf("(", result.indexOf("temp")), utils.findBracket(result,result.indexOf("(", result.indexOf("temp")))+1);
				//utils.SElog(1, "function: " +temp); // debug
				//utils.SElog(1, "result: " +result); // debug
				result = result.replace(temp, executeLine(temp, functions));
				//utils.SElog(1, "msg: " +msg); // debug
			}
		}
		
		// result = this.resolveVariables(result, entitySet); // not the best way
		
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
		
		int i=this.input+1;
		int j=1;
		while (tempActions.get(i)!=null && tempActions.get(i).trim().startsWith("|")) {
			subScriptActions.put(j, tempActions.get(i).trim().substring(1).trim());
			//utils.SElog(1, j+": "+subScriptActions.get(j)); // debug
			j++;
			i++;
		}
		
		if (!subScriptActions.isEmpty()) {
			SEscript subScript = new SEscript(null, "subScript", subScriptActions);
			SEentitySet subEntitySet = new SEentitySet();
			subEntitySet.script = subScript;
			SEinterpreter interpreter = new SEinterpreter(this.plugin, new SEtrigger(subEntitySet), entitySet);
			interpreter.start();
			
			while(interpreter.isAlive()) {
				try {
					sleep(10);
					//utils.SElog(1, "waiting"); // debug
				} catch (InterruptedException e) {
					utils.SElog(3, sleep);
				}
			}	
		}
	}
	
	// cancelEvent() cancels the fired bukkit-event 
	public void cancelEvent() {
		playerListener.cancel = true;
	}
	
	public void removeItemInHand(String[] input) {
		if (checkInput(input.length > 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if ((targetPlayer != null)&&(targetPlayer.getItemInHand()!=null)) {
				if (targetPlayer.getItemInHand().getAmount()>1) {
					targetPlayer.getItemInHand().setAmount(targetPlayer.getItemInHand().getAmount()-1);	
				} else {
					targetPlayer.getInventory().removeItem(targetPlayer.getItemInHand());
				}
			}	
		}
		
	}
	
	public void removeItem(String[] input) {
		if (checkInput(input.length == 2 || input.length == 3)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (inputToInteger(input[1])!="null" && inputToInteger(input[2])!="null") {
				ItemStack tempItem = utils.searchItem(targetPlayer, Integer.valueOf(inputToInteger(input[1])));
				
				int removeAmount = 1;
				if (input.length==3)
					removeAmount = Integer.valueOf(inputToInteger(input[2]));
				
				if (tempItem!=null) {
					if (tempItem.getAmount()-removeAmount==0)
						targetPlayer.getInventory().removeItem(tempItem);
					else
						tempItem.setAmount(tempItem.getAmount()-removeAmount);	
				}		
			}
		}
	}
	
	public void setItemType(String[] input) {
		if (checkInput(input.length == 3)) {
			
			if (input.length == 2 && inputToPlayer(input[0]) != null && inputToInteger(input[1]) != "null" && inputToInteger(input[2]) != "null") {
				Player targetPlayer = inputToPlayer(input[0]);
				int slot = Integer.valueOf(inputToInteger(input[1]));
				int type = Integer.valueOf(inputToInteger(input[2]));
				targetPlayer.getInventory().getItem(slot).setTypeId(type);
			}	
		}
	}
	
	public void setItemData(String[] input) {
		if (checkInput(input.length == 3)) {
			
			if (input.length == 2 && inputToPlayer(input[0]) != null && inputToInteger(input[1]) != "null" && inputToInteger(input[2]) != "null") {
				Player targetPlayer = inputToPlayer(input[0]);
				int slot = Integer.valueOf(inputToInteger(input[1]));
				int data = Integer.valueOf(inputToInteger(input[2]));
				targetPlayer.getInventory().getItem(slot).getData().toString();
			}	
		}
	}
	
	public void setHealth(String[] input) {
		if (checkInput(input.length == 2)) {
			if (inputToInteger(input[1])!="null") {
				Player targetPlayer = inputToPlayer(input[0]);
				int newHealth = Integer.valueOf(inputToInteger(input[1]));
				if (targetPlayer!=null) {
					targetPlayer.setHealth(newHealth);
				}	
			}
		}
	}
	
	public void messageTo(String[] input) {
		if (checkInput(input.length == 2)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!=null) {
				// targetPlayer.sendMessage(resolveFunctions(input[1])); hopefully not needed anymore
				targetPlayer.sendMessage(input[1]);
			}
		}
	}
	
	public void changeBlockType(String[] input) {
		if (checkInput(input.length == 3)) {
			if (inputToInteger(input[2])!="null") {
				World targetWorld = inputToWorld(input[0]);
				Location targetLocation = inputToLocation(input[1]);
				if (targetWorld!=null) {
						targetWorld.getBlockAt(targetLocation).setTypeId(Integer.valueOf(inputToInteger(input[2])));	
				}	
			}
			
		}
	}
	
	public void changeBlockData(String[] input) {
		if (checkInput(input.length == 3)) {
			World targetWorld = inputToWorld(input[0]);
			Location targetLocation = inputToLocation(input[1]);
			if (targetWorld!=null) {
					targetWorld.getBlockAt(targetLocation).setData(Integer.valueOf(inputToInteger(input[2])).byteValue());
			}
		}
	}

	public void playerCommand(String[] input) {
		if (checkInput(input.length == 2)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!=null) {
				targetPlayer.performCommand(input[1]);
			}
		}
	}
	
	public void playEffect(String[] input) {
		if (checkInput(input.length == 2)) {
			Player targetPlayer = inputToPlayer(input[0]);
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
				utils.SElog(2, "\""+input[1]+"\": Unvalid effect given in line :" +this.input);
			}
		}
	}

	public String playerInv(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!= null)
				for (int i = 0; i<=39; i++) {
					if (targetPlayer.getInventory().getItem(i) != null)
						utils.SElog(1, i+": "+targetPlayer.getInventory().getItem(i).getDurability());		
					
				}
				
		}
		return result;
	}
	
	
	//----------------//
	// SCRIPT ACTIONS //
	//----------------//
	
	// REWORK!
	public void If(String[] input) {
		if (checkInput(input.length == 1)) {
			check = checkCondition(input[0], entitySet);
			//utils.SElog(1, input+": "+String.valueOf(check)); // debug
			// executeLine(input[0], conditions)
			If = true;	
		}
	}
	
	public void Then(String[] input) {
		if (checkInput(input.length == 1) && If && check) {
			executeLine(input[0], actions);
			If = false;
		}
	}
	
	public void Else(String[] input) {
		if (checkInput(input.length == 1) && If && !check) {
			executeLine(input[0], actions);
			If = false;
		}
	}
	
	public void delay(String[] input) {
		if (checkInput(input.length == 1)) {
			if (inputToInteger(input[0])!="null") {
				try {
					sleep(Integer.valueOf(inputToInteger(input[0])));
				} catch (InterruptedException e) {
					utils.SElog(3, sleep);
				}	
			}
		}
	}
	
	public void trigger(String[] input) {
		if (checkInput(input.length == 1)) {
			SEtrigger tempTrigger = inputToTrigger(input[0]);
			
			if (tempTrigger != null) {
				SEinterpreter interpreter = new SEinterpreter(this.plugin, tempTrigger, entitySet);
				interpreter.start();	
			}
		}
	}
	
	public void script(String[] input) {
		if (checkInput(input.length == 1)) {
			SEscript tempScript = inputToScript(input[0]);
			if (tempScript != null) {
				SEentitySet subEntitySet = new SEentitySet();
				subEntitySet.script = tempScript;
				SEinterpreter interpreter = new SEinterpreter(this.plugin, new SEtrigger(subEntitySet), entitySet);
				interpreter.start();	
			}
		}
	}
	
	public void doForCuboidBlocks(String[] input) {
		if (checkInput(input.length == 2)) {
			SEcuboid tempCuboid = inputToCuboid(input[0]);
			World tempWorld = inputToWorld(tempCuboid.getWorld());
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
		if (checkInput(input.length == 2)) {
			
			Set<String> tempSet = inputToSet(input[0]);
			String action = input[1];
			
			Iterator<String> lauf = tempSet.iterator();
			
			while (lauf.hasNext()) {
				entitySet.item = lauf.next();
				executeLine(action, actions);	
			}
		}
	}
	
	public void loop(String[] input) {
		if (checkInput(input.length == 1)) {
			if (inputToInteger(input[0])!="null") {
				int cycles = Integer.valueOf(inputToInteger(input[0]));
				
				if (this.cycles == -1 && cycles > 0) {
					this.cycles = cycles;
				}
				
				if (this.cycles>0) {
					this.cycles--;
					this.input = 0;
				}	
			}
		}
	}
	
	// REWORK
	public void While(String[] input) {
		if (checkInput(input.length == 1)) {
			check = checkCondition(input[0], entitySet);
			
			if (check) {
				this.input = 0;
			}
		}
	}
	
	
	//-----------//
	// FUNCTIONS //
	//-----------//
	
	public String calc(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			try {
				result = String.valueOf(utils.calc(input[0]));
			} catch (Exception e) {
				utils.SElog(2, "Calculation failed in line: "+this.input);
			}
		}
		return result;
	}
	
	public String health(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer != null)
				result = String.valueOf(targetPlayer.getHealth());
		}
		return result;
	}
	
	public String itemInHand(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer != null)
				result = String.valueOf(targetPlayer.getItemInHand().getTypeId());
		}
		return result;
	}
	
	public String time(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			World targetWorld= inputToWorld(input[0]);
			if (targetWorld!= null)
				result = String.valueOf(targetWorld.getTime());
		}
		return result;
	}
	
	public String blockID(String[] input) {
		String result = "null";
		if (checkInput(input.length == 2)) {
			World targetWorld = inputToWorld(input[0]);
			Location targetLocation = inputToLocation(input[1]);
			if ((targetWorld != null) && (targetLocation != null))
				result = String.valueOf(targetWorld.getBlockAt(targetLocation).getTypeId());
		}
		return result;
	}
	
	public String blockData(String[] input) {
		String result = "null";
		if (checkInput(input.length == 2)) {
			World targetWorld = inputToWorld(input[0]);
			Location targetLocation = inputToLocation(input[1]);
			if ((targetWorld != null) && (targetLocation != null))
				result = String.valueOf(targetWorld.getBlockAt(targetLocation).getData());
		}
		return result;
	}
	
	public String isInBed(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.isSleeping());
		}
		return result;
	}

	public String isSneaking(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!= null)
				result = String.valueOf(targetPlayer.isSneaking());
		}
		return result;
	}
	
	public String playerLocation(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Player targetPlayer = inputToPlayer(input[0]);
			if (targetPlayer!= null)
				result = utils.locationToString(targetPlayer.getLocation());
		}
		return result;
	}
	
	public String size(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			Set<String> targetSet = inputToSet(input[0]);
			if (targetSet!= null)
				result = String.valueOf(targetSet.size());
		}
		return result;
	}
	
	public String arg(String[] input) {
		String result = "null";
		if (checkInput(input.length == 1)) {
			String i = inputToInteger(input[0]);
			if ((i!= "null") && (entitySet.args.length > Integer.valueOf(i)) && (Integer.valueOf(i)>=0))
				result = entitySet.args[Integer.valueOf(i)];
		}
		return result;
	}
	
	public String random(String[] input) {
		String result = "null";
		Random generator = new Random();
		int randomInt = 0;
		
		if (checkInput(input.length == 2 || input.length == 1)) {
			
			if (input.length==1 && inputToInteger(input[0]) != "null") {
				int max = Integer.valueOf(inputToInteger(input[0]));
				randomInt = generator.nextInt(max);
				result = String.valueOf(randomInt);
			}
			if (input.length==2 && inputToInteger(input[0]) != "null" && inputToInteger(input[1]) != "null") {
				int min = Integer.valueOf(inputToInteger(input[0]));
				int max = Integer.valueOf(inputToInteger(input[1]))-min+1;
				randomInt = generator.nextInt(max)+min;
				result = String.valueOf(randomInt);
			}
		}
		return result;
	}

	
	//-----------//
	// OLD STUFF //
	//-----------//
	
	// check a condition-String
	public boolean checkCondition(String condition, SEentitySet entitySet){
		boolean result = false; // turn 'true', if you want to get 'true' for not recognized conditions
		boolean checked = false;
		
		// resolve Variables first
		condition = oldResolveVariables(condition, entitySet);
		
		// check check(<condition-ID>)
		if (condition.contains("check(") && !checked) {	
			condition = condition.substring(condition.indexOf('(')+1,condition.lastIndexOf(')'));
			//condition = condition.substring(1, condition.length()-1);
			try {
				SEcondition tempCondition = SEdata.getConditionByID(SEdata.searchConditionList(condition));
				result = checkConditionList(tempCondition.getOperator(), tempCondition.getConditionList(), entitySet);	
			} catch (Exception e) {
				result = false;
			}
			checked = true;
		}
		
		// check and(<condition1>,<condition2>)
		if ((condition.contains("and(") || condition.contains("or(")) && !checked) {
			String oldCondition = condition;
			String tempCondition = "";
			String tempBracket = "";
			String bracketreplacement = "";
			int startIndex = 0;
			

			condition = condition.substring(condition.indexOf('(')); // Schritt 1: equals(Hans,Hans),bigger(2,1))
			condition = condition.substring(1, condition.length()-1); // Schritt 2: equals(Hans,Hans),bigger(2,1)
			
			// Schritt 3: equalsXXXXXXXXXXX,biggerXXXXX			
			tempCondition = condition;
			while ((tempCondition.contains("("))&&(tempCondition.contains(")"))) {
				tempBracket = tempCondition.substring(tempCondition.indexOf("("), tempCondition.indexOf(")")+1);
				bracketreplacement = "";
				for (int i=0; i < tempBracket.length(); i++) {
					bracketreplacement = bracketreplacement+"X"; 
				}
				tempCondition = tempCondition.replace(tempBracket, bracketreplacement);
			}			
			
			// Schritt 4: 'equalsXXXXXXXXXXX' und 'biggerXXXXX'
			String[] conditionStrings = tempCondition.split(",");
			
			// Schritt 5: 'equals(Hans,Hans)' und 'bigger(2,1)'
			for (int i=0; i<conditionStrings.length;i++){
				conditionStrings[i] = condition.substring(startIndex, startIndex + conditionStrings[i].length());
				startIndex = startIndex + conditionStrings[i].length() + 1;
			}
			
			// do check
			if (conditionStrings.length==2) {
				if (oldCondition.contains("and(")) {
					result = (checkCondition(conditionStrings[0], entitySet) && checkCondition(conditionStrings[1], entitySet));
				}
				if (oldCondition.contains("or(")) {
					result = (checkCondition(conditionStrings[0], entitySet) || checkCondition(conditionStrings[1], entitySet));
				}
			}
			checked = true;
		}
		
		// check hasItem()
		if (condition.contains("hasItem(") && !checked) {
			try {
				String[] input = condition.substring(condition.indexOf('(')+1, condition.indexOf(')')).split(",");
				if (input.length==2) {
					Player targetPlayer = utils.stringToPlayer(entitySet.player.getServer().getOnlinePlayers(), input[0]);
					ItemStack searchedItem = utils.searchItem(targetPlayer, Integer.valueOf(input[1]));
					
					result = ( (targetPlayer != null)
							&& (searchedItem != null) );	
				}
				
				if (input.length==3) {
					Player targetPlayer = utils.stringToPlayer(entitySet.player.getServer().getOnlinePlayers(), input[0]);
					ItemStack searchedItem = utils.searchItem(targetPlayer, Integer.valueOf(input[1]));
					
					result = ( (targetPlayer != null)
							&& (searchedItem != null)
							&& (searchedItem.getAmount() >= Integer.valueOf(input[2])) );	
				}
				
				
			} catch (Exception e) {}
			
			checked = true;
		}
		
		// check equals(<x>,<y>)
		if (condition.contains("equals(") && !checked) {	
			condition = condition.substring(condition.indexOf('('));
			condition = condition.substring(1, condition.length()-1);
			String[] conditionStrings = condition.split(",");
			
			if (conditionStrings.length==2) {
				result = conditionStrings[0].equals(conditionStrings[1]);
			}
			checked = true;
		}
		
		// check bigger(<x>,<y>)
		if (condition.contains("bigger(") && !checked) {	
			condition = condition.substring(condition.indexOf('('));
			condition = condition.substring(1, condition.length()-1);
			String[] conditionStrings = condition.split(",");
			
			if (conditionStrings.length==2) {
				try {
					result = (Integer.valueOf(conditionStrings[0]) > Integer.valueOf(conditionStrings[1]));
				} catch (Exception e){
					result = false;
				}
				
			}
			checked = true;
		}
		
		
		
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
		
		// check contains(<set>,<item>)
		if (condition.contains("contains(") && !checked) {	
			condition = condition.substring(condition.indexOf('('));
			condition = condition.substring(1, condition.length()-1);
			String[] conditionStrings = condition.split(",");
			
			if (conditionStrings.length==2) {
				if (SEdata.getSetVarList().containsKey(conditionStrings[0])) {
					result = SEdata.getSetVarList().get(conditionStrings[0]).contains(conditionStrings[1]);
				}
			}
			checked = true;
		}
		
		return result;
	}
	
	// check a condition-List
	public boolean checkConditionList(logicalOperator operator, Map<Integer, String> conditionList, SEentitySet entitySet) {
		boolean result = false;
		boolean tempResult = false;
		
		if (operator==null) result = true;
		if (operator==logicalOperator.and) result = true;
		if (operator==logicalOperator.or) result = false;
		
		// checks every condition in the list
		for (int i = 1; i <= conditionList.size(); i++) {
			tempResult = checkCondition(conditionList.get(i), entitySet);
			
			if (operator==null) result = (result && tempResult);
			if (operator==logicalOperator.and) result = (result && tempResult);
			if (operator==logicalOperator.or) result = (result || tempResult);
		}

		if (!result)
			if ((entitySet.player!=null) && (SEdata.getDebugees(entitySet.player)))
				utils.SEmessage(entitySet.player, "Conditions = false!"); // debug
		
		return result;
	}
	
	// resolves all variables in a String
	public String oldResolveVariables(String input, SEentitySet entitySet) {
		String result = input;
		Map<String,String> stringList = SEdata.getStringVarList();
		Map<String,Integer> intList = SEdata.getIntVarList();
		Random generator = new Random();
		
		String[] args = entitySet.args;
		SEcuboid triggeringCuboid = entitySet.cuboid;
		Player triggeringPlayer   = entitySet.player;
		Location blockLocation = entitySet.location;
		String item = entitySet.item;
		String worldName = "none";
		
		if (triggeringPlayer!=null) {
			//health = triggeringPlayer.getHealth();
			//itemInHand = triggeringPlayer.getItemInHand().getTypeId();
			worldName = triggeringPlayer.getWorld().getName();
		}
		
		// resolve Command arguments
		
		if (args!=null) {
			for (int i=0;i<args.length;i++) {
				if (i==0) {
					while (result.contains("<command>")&&(args[i]!=null)) {
						result = result.replaceAll("<command>", args[i]);
					}	
				} else {
					while (result.contains("<arg"+i+">")&&(args[i]!=null)) {
						result = result.replaceAll("<arg"+i+">", args[i]);
						//utils.SElog(1, "debug"); // debug
					}
				}
			}
		}
		
		// Interact-related Variables
		if (entitySet.interactEvent != null) {
			boolean rightClick       = (entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_AIR)||entitySet.interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK));
			
			String clickedLocation;
			if (entitySet.interactEvent.hasBlock())
				clickedLocation = utils.locationToString(entitySet.interactEvent.getClickedBlock().getLocation());
			else
				clickedLocation = "none";
			
			// resolve clickedLocation		
			while (result.contains("<clickedLocation>")) {
				result = result.replaceAll("<clickedLocation>", clickedLocation);
			}
			// resolve rightClick		
			while (result.contains("<rightClick>")) {
				result = result.replaceAll("<rightClick>", String.valueOf(rightClick));
			}
		}
		
		// resolve randomInt
		while (result.contains("<randomInt>")) {
			int randomInt;
			
			if (entitySet.randomMax == 0)
				randomInt = generator.nextInt()+entitySet.randomMin;
			else
				randomInt = generator.nextInt(entitySet.randomMax)+entitySet.randomMin;
			
			result = result.replaceFirst("<randomInt>", String.valueOf(randomInt));
			
			//utils.SElog(1, "randomInt: "+String.valueOf(randomInt)); // debug
		}
		
		// resolve blockLocation
		while (result.contains("<blockLocation>")&&(blockLocation!=null)) {
			result = result.replaceAll("<blockLocation>", utils.locationToString(blockLocation));
		}
		
		// resolve item
		while (result.contains("<item>")&&(item!=null)) {
			result = result.replaceAll("<item>", item);
		}
		
		// resolve triggeringPlayer
		while (result.contains("<triggeringPlayer>")&&(triggeringPlayer!=null)) {
			result = result.replaceAll("<triggeringPlayer>", triggeringPlayer.getName());
		}
		// resolve world
		while (result.contains("<world>")&&(triggeringPlayer!=null)) {
			result = result.replaceAll("<world>", worldName);
		}
		
		// resolve triggeringCuboid
		while ((result.contains("<triggeringCuboid>")) && (triggeringCuboid!=null)) {
			result = result.replaceAll("<triggeringCuboid>", triggeringCuboid.getName());
		}
		
		// resolve user-defined String variables
		if (stringList!=null) {
			for ( Iterator<String> i = stringList.keySet().iterator(); i.hasNext(); )
			{
				String tempVar = (String) i.next();
				
				while (result.contains("<"+ tempVar +">"))
					result = result.replaceAll("<"+ tempVar +">", stringList.get(tempVar));
			}
		}
		
		// resolve user-defined Integer variables
		if (intList!=null) {
			for ( Iterator<String> i = intList.keySet().iterator(); i.hasNext(); )
			{
				String tempVar = (String) i.next();
				
				while (result.contains("<"+ tempVar +">"))
					result = result.replaceAll("<"+ tempVar +">", String.valueOf(intList.get(tempVar)));
			}
		}
		
		return result;
	}
}
