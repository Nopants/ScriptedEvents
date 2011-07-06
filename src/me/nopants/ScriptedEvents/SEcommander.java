package me.nopants.ScriptedEvents;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.nopants.ScriptedEvents.SEcondition.logicalOperator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class SEcommander {
	private ScriptedEvents plugin;
	private SEutils utils;
	public PermissionHandler permissionHandler;
	public boolean hasPermissions = false;
	
	private static String seNode = "se";
	private static String debugNode = ".debug";
	private static String helpNode = ".help";
	private static String refreshNode = ".debug";
	private static String editNode = ".edit";
	private static String cuboidNode = ".cuboid";
	private static String triggerNode = ".trigger";
	private static String scriptNode = ".script";
	private static String conditionNode = ".condition";
	private static String variableNode = ".variable";
	private static String listNode = ".list";
	private static String createNode = ".variable";
	private static String deleteNode = ".variable";
	private static String getIDNode = ".getid";
	private static String addNode = ".add";
	private static String nameNode = ".name";
	private static String eventNode = ".event";
	private static String entityNode = ".entity";
	private static String closeNode = ".close";
	private static String saveNode = ".save";
	public static String noPermission = "�cYou don't have Permission.";
	
	// constructor
	public SEcommander(ScriptedEvents scriptedEvents) {
		this.plugin = scriptedEvents;
		this.utils = plugin.utils;
		this.permissionHandler = plugin.permissionHandler;
		this.hasPermissions = plugin.hasPermissions; 
	}

	// converts the sender to a player or sends the console a message
	public Player senderToPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			//utils.SElog(1, "This command is for players only!");
			return null;
		}
	}

	public boolean checkPermission(CommandSender sender, String permission){
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if( (player == null || (hasPermissions && permissionHandler.has(player, permission))) || player.isOp()){
			result=true;
		} else {
			result=false;
			sender.sendMessage(noPermission);
		}
		return result;
	}
	
	// se.debug
	public boolean debug(CommandSender sender, String[] args) {
		boolean result = false;
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+debugNode)){
			plugin.SEdata.toggleDebugees(player);
			result = true;
		} else result = true;
		return result;
	}
	
	// se.help
	public boolean help(CommandSender sender, String[] args){
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+helpNode)){
			if (args.length == 0) {
				sender.sendMessage("--------------(1/2)--------------");
				sender.sendMessage("�6Scripted Events:");
				sender.sendMessage("- /SE.help <page>");
				sender.sendMessage("�8      Shows this help text.");
				sender.sendMessage("- /SE.debug");
				sender.sendMessage("�8      Turns on the debuging info.");
				sender.sendMessage("- /SE.save.cuboid <cuboid name>");
				sender.sendMessage("�8      Use a feather to select cuboids.");
				sender.sendMessage("�8      Do this by right-click and left-click.");
				sender.sendMessage("- /SE.delete.cuboid <cuboid ID>");
				sender.sendMessage("�8      Deletes the cuboid with the given ID.");
				sender.sendMessage("- /SE.cuboid.getID <cuboid name>");
				sender.sendMessage("�8      Shows a Cuboids ID.");
				sender.sendMessage("---------------------------------");
			}
			else {
				int page = -1;
				try {
					page = Integer.valueOf(args[0]);
					if (!((page==1)||(page==2))) throw new Exception(); else result = true;
				} catch (Exception e) {
					utils.SEmessage(sender, "<Pages>: 1 or 2");
					result = false;
				}
				switch (page) {
					case 1: {
						sender.sendMessage("--------------(1/2)--------------");
						sender.sendMessage("�6Scripted Events:");
						sender.sendMessage("- /SE.help <page>");
						sender.sendMessage("�8      Shows this help text.");
						sender.sendMessage("- /SE.debug");
						sender.sendMessage("�8      Turns on the debuging info.");
						sender.sendMessage("- /SE.cuboid.save <cuboid name>");
						sender.sendMessage("�8      Use a feather to select cuboids.");
						sender.sendMessage("�8      Do this by right-click and left-click.");
						sender.sendMessage("- /SE.cuboid.delete <cuboid ID>");
						sender.sendMessage("�8      Deletes the cuboid with the given ID.");
						sender.sendMessage("- /SE.cuboid.getID <cuboid name>");
						sender.sendMessage("�8      Shows a Cuboids ID.");
						sender.sendMessage("---------------------------------");
						break;
					}
					case 2: {
						sender.sendMessage("--------------(2/2)--------------");
						sender.sendMessage("�6Scripted Events:");
						sender.sendMessage("- /SE.trigger.create");
						sender.sendMessage("�8      Starts the Trigger-Creationprozess.");
						sender.sendMessage("�8      1 - You have to enter the Trigger-Event.");
						sender.sendMessage("�8      2 - Depending on what event you entered,");
						sender.sendMessage("�8              you have to choose a Trigger-Entity.");
						sender.sendMessage("�8      3 - You have to enter a Script-Name.");
						sender.sendMessage("---------------------------------");
						break;
					}
				}
			}
		} else result = true;
		return result;
	}
	
	// se.refresh
	public boolean refresh(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+refreshNode)){
			plugin.SEdata.initializeData();
			utils.SEmessage(sender, "Refresh finished!");
			result = true;
		} else {
			result = true;
		}
		return result;
	}
	
	//---------------------//
	// EDIT
	//---------------------//
	
	// se.edit.add
	public boolean add(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
	
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+addNode)){
			
			if (!(editEntity==null||editEntity.isEmpty())) {	
				if ((editEntity.script != null)||(editEntity.condition != null)) {
					
					// add script
					if (editEntity.script != null) {
						Map<Integer, String> actionList = editEntity.script.getActionList();
						
						// change script
						String newAction = "";
						for (int i=0; i < args.length; i++) {
							if (i==0)
								newAction = args[i];
							else
								newAction = newAction+" "+args[i];
						}
						actionList.put(actionList.size()+1, newAction);
						editEntity.script.setActionList(actionList);
						
						utils.SEmessage(sender, "Action added!");
						result = true;
					}
					
					// add condition
					if (editEntity.condition != null) {
						Map<Integer, String> conList = editEntity.condition.getConditionList();
						
						// change condition
						String newCondition = "";
						for (int i=0; i < args.length; i++) {
							if (i==0)
								newCondition = args[i];
							else
								newCondition = newCondition+" "+args[i];
						}
						conList.put(conList.size()+1, newCondition);
						editEntity.condition.setConditionList(conList);
						
						utils.SEmessage(sender, "Condition added!");
						result = true;	
					}	
				}else {
					utils.SEmessage(sender, "Only Script/Condition-Files can have actions/conditions!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}
		} else result = true;
		return result;
	}
	
	// se.edit.delete
	public boolean delete(CommandSender sender, String[] args) {
		boolean result=false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		boolean intArgs = false;
		int arg = 0;
		try {
			arg = Integer.valueOf(args[0]);
			intArgs = true;
		} catch (Exception e) {
			intArgs = false;
		}
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+deleteNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if ((editEntity.script != null)||(editEntity.condition != null)) {
					
					// delete script
					if (editEntity.script != null) {
						Map<Integer, String> actionList = editEntity.script.getActionList();
						Map<Integer, String> newActionList = new HashMap<Integer, String>();
							
						if (intArgs||(args.length==0)) {
							if ( ((arg <= actionList.size())&&(arg > 0)) || (args.length==0) ) {
								
								if (args.length==0) {
									actionList.remove(actionList.size());
								} else {
									actionList.remove(Integer.valueOf(args[0]));	
								}
								int newID = 1;
								for (int i = 1; i <= actionList.size()+1; i++) {
									if (actionList.get(i)!=null) {
										newActionList.put(newID, actionList.get(i));
										newID++;
									}
								}
								editEntity.script.setActionList(newActionList);
								utils.SEmessage(sender, "Action deleted!");
								result = true;
								
							} else {
								utils.SEmessage(sender, "Action ID not found!");
								result = false;
							}	
							
						} else {
							utils.SEmessage(sender, "Action has to be an Integer!");
							result = false;
						}
					}
					
					// delete condition
					if (editEntity.condition != null) {
						Map<Integer, String> conList = editEntity.condition.getConditionList();
						Map<Integer, String> newConList = new HashMap<Integer, String>();
						
						if (intArgs||(args.length==0)) {
							if ( ((arg <= conList.size())&&(arg > 0)) || (args.length==0) ) {
								
								if (args.length==0) {
									conList.remove(conList.size());
								} else {
									conList.remove(Integer.valueOf(args[0]));	
								}
								
								int newID = 1;
								for (int i = 1; i <= conList.size()+1; i++) {
									if (conList.get(i)!=null) {
										newConList.put(newID, conList.get(i));
										newID++;
									}
								}
								editEntity.condition.setConditionList(newConList);
								utils.SEmessage(sender, "Condition deleted!");
								result = true;
								
							} else {
								utils.SEmessage(sender, "Condition ID not found!");
								result = false;
							}	
						} else {
							utils.SEmessage(sender, "Condition ID has to be an Integer!");
							result = false;
						}
					}
					
				} else {
					utils.SEmessage(sender, "Only Script/Condition-Files have actions/conditions!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.Event
	public boolean editEvent(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+eventNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if (args.length == 1) {
					
					// change trigger-script
					if (editEntity.trigger != null) {
						editEntity.trigger.setEvent(utils.stringToEvent(args[0]));
						utils.SEmessage(sender, "Event changed");
						result = true;
					} else {
						utils.SEmessage(sender, "Only Triggers can have events!");
						result = false;
					}
				
				} else {
					utils.SEmessage(sender, "Wrong number of arguments! Try again.");	
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.entity
	public boolean editEntity(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+entityNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if (args.length == 1) {
					
					// change trigger-entity
					if (editEntity.trigger != null) {
						SEtrigger.triggerEvent triggerEvent = editEntity.trigger.getEvent();
						
						// onCommand
						if (triggerEvent==SEtrigger.triggerEvent.onCommand) {
							editEntity.trigger.setTriggerCommand(args[0]);
							utils.SEmessage(sender, "Command changed");
							result = true;
						}
						
						// onEnter and onLeave
						if ((triggerEvent==SEtrigger.triggerEvent.onEnter)||(triggerEvent==SEtrigger.triggerEvent.onLeave)) {
							try {
								SEcuboid cuboid = plugin.SEdata.getCuboidByID(plugin.SEdata.searchCuboidList(args[0]));
								if (cuboid!=null) {
									editEntity.trigger.setTriggerCuboid(cuboid);
									utils.SEmessage(sender, "Cuboid changed");
									result = true;
								} else {
									utils.SEmessage(sender, "Cuboid not found!");
									result = false;
								}
							} catch (Exception e) {
								utils.SEmessage(sender, "Something went really wrong!");
								result = false;
							}
						}
						
						// none
						if (triggerEvent==SEtrigger.triggerEvent.none) {
							utils.SEmessage(sender, "Trigger is missing an Event!");
							result = false;
						}
						
					} else {
						utils.SEmessage(sender, "Only Triggers can have trigger-entities!");
						result = false;
					}
				
				} else {
					utils.SEmessage(sender, "Wrong number of arguments! Try again.");	
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.script
	public boolean editScript(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+scriptNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if (args.length == 1) {
					try {
						
						SEscript tempScript = plugin.SEdata.getScriptByID(plugin.SEdata.searchScriptList(args[0]));
						if (tempScript != null) {
							// change trigger-script
							if (editEntity.trigger != null) {
								editEntity.trigger.setScript(tempScript);
								utils.SEmessage(sender, "Script changed");
								result = true;
							} else {
								utils.SEmessage(sender, "Only Triggers can have scripts!");
								result = false;
							}
						} else {
							utils.SEmessage(sender, "Script "+ args[0] +" not found.");	
							result = false;
						}
					} catch (Exception e) {
						utils.SEmessage(sender, "Something went really wrong!");
						result = false;	
					}
				} else {
					utils.SEmessage(sender, "Wrong number of arguments! Try again.");	
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.condition
	public boolean editCondition(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+conditionNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if (args.length == 1) {
					try {
						SEcondition tempCondition = plugin.SEdata.getConditionByID(plugin.SEdata.searchConditionList(args[0]));
						if (tempCondition != null) {
							// change trigger-condition
							if (editEntity.trigger != null) {
								editEntity.trigger.setCondition(tempCondition);
								utils.SEmessage(sender, "Condition changed");
								result = true;
							} else {
								utils.SEmessage(sender, "Only Triggers can have conditions!");
								result = false;
							}
						} else {
							utils.SEmessage(sender, "Condition "+ args[0] +" not found.");	
							result = false;
						}
					} catch (Exception e) {
						utils.SEmessage(sender, "Something went really wrong!");
						result = false;	
					}
				} else {
					utils.SEmessage(sender, "Wrong number of arguments! Try again.");	
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.name
	public boolean editName(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+nameNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				if (args.length == 1) {
					
					// change script-name
					if (editEntity.script != null) {
						editEntity.script.setName(args[0]);
						utils.SEmessage(sender, "Name changed");
						result = true;
					}
					
					// change script-name
					if (editEntity.condition != null) {
						editEntity.condition.setName(args[0]);
						utils.SEmessage(sender, "Name changed");
						result = true;
					}
					
					// change trigger-name
					if (editEntity.trigger != null) {
						editEntity.trigger.setName(args[0]);
						utils.SEmessage(sender, "Name changed");
						result = true;
					}
					
					// change cuboid-name
					if (editEntity.cuboid != null) {
						editEntity.cuboid.setName(args[0]);
						utils.SEmessage(sender, "Name changed");
						result = true;
					}
					
				} else {
					utils.SEmessage(sender, "Wrong number of arguments! Try again.");	
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.edit.close
	public boolean editClose(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+closeNode)){
			Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
			tempList.remove(sender);
			plugin.SEdata.setEditEntityList(tempList);
			utils.SEmessage(sender, "Edit-Mode closed");
			result = true;	
		} else result = true;
		
		
		return result;
	}
	
	// se.edit.save
	public boolean editSave(CommandSender sender, String[] args) {
		boolean result = false;
		SEentitySet editEntity = plugin.SEdata.getEditEntityList().get(sender);
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+editNode+saveNode)){
			if (!(editEntity==null||editEntity.isEmpty())) {
				
				// save condition
				SEcondition editCondition = editEntity.condition;
				if (editCondition != null) {
					Map<Integer, SEcondition> conList = plugin.SEdata.getConditionList();
					conList.put(conList.size()+1, editCondition);
					plugin.SEdata.setConditionList(conList);
					plugin.SEdata.rewriteCondition(plugin.SEdata.getConditionList().get(conList.size()));
					utils.SEmessage(sender, "Edited Condition saved!");
					result = true;
				}
				
				// save script
				SEscript editScript = editEntity.script;
				if (editScript != null) {
					Map<Integer, SEscript> scriptList = plugin.SEdata.getScriptList();
					scriptList.put(scriptList.size()+1, editScript);
					plugin.SEdata.setScriptList(scriptList);
					plugin.SEdata.rewriteScript(plugin.SEdata.getScriptList().get(scriptList.size()));
					utils.SEmessage(sender, "Edited Script saved!");
					result = true;
				}
				
				// save trigger
				SEtrigger editTrigger = editEntity.trigger;
				if (editTrigger != null) {
					Map<Integer, SEtrigger> triggerList = plugin.SEdata.getTriggerList();
					triggerList.put(triggerList.size()+1, editTrigger);
					plugin.SEdata.setTriggerList(triggerList);
					plugin.SEdata.rewriteTriggerFile();
					utils.SEmessage(sender, "Edited Trigger saved!");
					result = true;
				}
				
				// save cuboid
				SEcuboid editCuboid = editEntity.cuboid;
				if (editCuboid != null) {
					Map<Integer, SEcuboid> cuboidList = plugin.SEdata.getCuboidList();
					cuboidList.put(cuboidList.size()+1, editCuboid);
					plugin.SEdata.setCuboidList(cuboidList);
					plugin.SEdata.rewriteCuboidFile();
					utils.SEmessage(sender, "Edited Cuboid saved!");
					result = true;
				}
				
				Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
				tempList.remove(sender);
				plugin.SEdata.setEditEntityList(tempList);
				utils.SEmessage(sender, "Edit-Mode closed");
				
			} else {
				utils.SEmessage(sender, "Not in Edit-Mode!");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	//---------------------//
	// CUBOID
	//---------------------//
	
	// se.cuboid.create
	public boolean cuboidCreate(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+cuboidNode+createNode)){
			if (args.length == 1) {
				if (player!=null) {
					if (plugin.SEdata.searchCuboidList(args[0])<0) {
						if ((plugin.playerListener.getVertex1(player)!=null)&&(plugin.playerListener.getVertex2(player)!=null)) {
							SEcuboid saveCuboid = new SEcuboid(player.getWorld().getName(), args[0], plugin.playerListener.getVertex1(player), plugin.playerListener.getVertex2(player));
							plugin.SEdata.writeCuboid(saveCuboid.toString());
							utils.SEmessage(player, "Cuboid '"+args[0]+"' created!");
							plugin.SEdata.refreshCuboidList();
							//check new distance
							plugin.playerListener.resetDist(player);
							result = true;	
						} else {
							utils.SEmessage(player, "Select two vertexes with the Selection-Tool first!");
							result = false;
						}
						
					} else {
						utils.SEmessage(player, "Cuboidname '"+args[0]+"' already used!");
						utils.SEmessage(player, "Change name or delete saved cuboid.");
						result = false;
					}	
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.cuboid.delete
	public boolean cuboidDelete(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+cuboidNode+deleteNode)){
			Map<Integer, SEcuboid> cuboidList = new HashMap<Integer, SEcuboid>();
			cuboidList = plugin.SEdata.getCuboidList();
			boolean intArgs = false;
			int arg = 0;
			try {
				arg = Integer.valueOf(args[0]);
				intArgs = true;
			} catch (Exception e) {
				intArgs = false;
			}
			
			if (intArgs) {
				if ((arg <= cuboidList.size())&&(arg > 0)) {
					cuboidList.remove(Integer.valueOf(args[0]));
					plugin.SEdata.setCuboidList(cuboidList);
					try {
						utils.SEmessage(sender, "Cuboid deleted!");
						plugin.SEdata.rewriteCuboidFile();
						plugin.SEdata.refreshCuboidList();
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete Cuboid!");
						result = false;				
					}
				} else {
					plugin.SEdata.utils.SEmessage(sender, "Cuboid ID not found!");
					result = false;
				}	
			} else {
				plugin.SEdata.utils.SEmessage(sender, "Cuboid ID has to be an Integer!");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.cuboid.edit
	public boolean cuboidEdit(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+cuboidNode+editNode)){
			if (args.length == 1) {
				
				Map<Integer, SEcuboid> cuboidList = new HashMap<Integer, SEcuboid>();
				cuboidList = plugin.SEdata.getCuboidList();
				boolean intArgs = false;
				int arg = 0;
				
				try {
					arg = Integer.valueOf(args[0]);
					intArgs = true;
				} catch (Exception e) {
					intArgs = false;
				}
				
				if (intArgs) {
					if ((arg <= cuboidList.size())&&(arg > 0)) {
						
						// Turn on Edit-Mode via entering an edit-entity
						Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
						tempList.put(sender, new SEentitySet(plugin.SEdata.getCuboidByID(arg)));
						plugin.SEdata.setEditEntityList(tempList);
						utils.SEmessage(sender, "Edit-Mode enabled for Cuboid '"+plugin.SEdata.getCuboidByID(arg).getName()+"'");
						result = true;
						
					} else {
						utils.SEmessage(sender, "Cuboid '"+args[0]+"' not found!");
						result = false;
					}
				} else {
					utils.SEmessage(sender, "Cuboid ID has to be an Integer!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}
		} else result = true;
		
		return result;
	}

	// se.cuboid.getID
	public boolean cuboidGetID(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+cuboidNode+getIDNode)){
			if (args.length == 1) {
				int searchResult = plugin.SEdata.searchCuboidList(args[0]);
				if (searchResult > 0) {
					utils.SEmessage(sender, "CuboidID of cuboid '"+args[0]+"' is '"+searchResult+"'!");
					result = true;
				} else {
					utils.SEmessage(sender, "Cuboid '"+args[0]+"' not found!");
					result = false;
				}
			}	
		} else result = true;
		
		return result;
	}
	
	//---------------------//
	// TRIGGER
	//---------------------//
	
	// se.trigger.create
	public boolean triggerCreate(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+triggerNode+createNode)){
			if (args.length == 1) {
				if (plugin.SEdata.searchTriggerList(args[0])<0) {
					//int triggerCount = plugin.SEdata.getTriggerList().size()+1;
					// create a blank trigger with 'name' and 'ID'
					SEentitySet entitySet = new SEentitySet();
					entitySet.name = args[0];
					SEtrigger newTrigger = new SEtrigger(entitySet);
				
					// and update it to the dataManager and trigger.yml
					plugin.SEdata.writeTrigger(newTrigger.toString());
					plugin.SEdata.refreshTriggerList();
					plugin.SEdata.utils.SEmessage(sender, "Trigger '"+args[0]+"' created!");
					result = true;
				} else {
					utils.SEmessage(sender, "Trigger-Name '"+args[0]+"' already used!");
					utils.SEmessage(sender, "Change name or delete saved trigger.");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.trigger.delete
	public boolean triggerDelete(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+triggerNode+deleteNode)){
			Map<Integer, SEtrigger> triggerList = new HashMap<Integer, SEtrigger>();
			triggerList = plugin.SEdata.getTriggerList();
			boolean intArgs = false;
			int arg = 0;
			try {
				arg = Integer.valueOf(args[0]);
				intArgs = true;
			} catch (Exception e) {
				intArgs = false;
			}
			
			if (intArgs) {
				if ((arg <= triggerList.size())&&(arg > 0)) {
					triggerList.remove(Integer.valueOf(args[0]));
					plugin.SEdata.setTriggerList(triggerList);
					try {
						utils.SEmessage(sender, "Trigger deleted!");
						plugin.SEdata.rewriteTriggerFile();
						plugin.SEdata.refreshTriggerList();
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete Trigger!");
						result = false;				
					}
				} else {
					utils.SEmessage(sender, "Trigger ID not found!");
					result = false;
				}	
			} else {
				utils.SEmessage(sender, "Trigger ID has to be an Integer!");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.trigger.edit
	public boolean triggerEdit(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+triggerNode+editNode)){
			if (args.length == 1) {
				
				Map<Integer, SEtrigger> triggerList = new HashMap<Integer, SEtrigger>();
				triggerList = plugin.SEdata.getTriggerList();
				boolean intArgs = false;
				int arg = 0;
				
				try {
					arg = Integer.valueOf(args[0]);
					intArgs = true;
				} catch (Exception e) {
					intArgs = false;
				}
				
				if (intArgs) {
					if ((arg <= triggerList.size())&&(arg > 0)) {
						
						// Turn on Edit-Mode via entering an edit-entity
						Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
						tempList.put(sender, new SEentitySet(plugin.SEdata.getTriggerByID(arg)));
						plugin.SEdata.setEditEntityList(tempList);
						utils.SEmessage(sender, "Edit-Mode enabled for Trigger '"+plugin.SEdata.getTriggerByID(arg).getName()+"'");
						result = true;
						
					} else {
						utils.SEmessage(sender, "Trigger '"+args[0]+"' not found!");
						result = false;
					}
				} else {
					utils.SEmessage(sender, "Trigger ID has to be an Integer!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}			
		} else result = true;
		
		return result;
	}
	
	// se.trigger.getID
	public boolean triggerGetID(CommandSender sender, String[] args) {
		boolean result = false;
	
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+triggerNode+getIDNode)){
			if (args.length == 1) {
				int searchResult = plugin.SEdata.searchTriggerList(args[0]);
				if (searchResult > 0) {
					utils.SEmessage(sender, "TriggerID of trigger '"+args[0]+"' is '"+searchResult+"'!");
					result = true;
				} else {
					utils.SEmessage(sender, "Trigger '"+args[0]+"' not found!");
					result = false;
				}
			}	
		} else result = true;
		
		return result;
	}
	
	//---------------------//
	// SCRIPT
	//---------------------//
	
	// se.script.create
	public boolean scriptCreate(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+scriptNode+createNode)){
			if (args.length == 1) {
				if (plugin.SEdata.searchScriptList(args[0])<0) {
															
					File newScriptFile = new File(SEdataManager.scriptDirectory + File.separator + args[0] + ".script");
					SEscript newScript = new SEscript(newScriptFile, args[0], new HashMap<Integer, String>());
					if (!newScriptFile.exists()) {
						plugin.SEdata.rewriteScript(newScript);
						plugin.SEdata.refreshScriptList();
						plugin.SEdata.utils.SEmessage(sender, "Script '"+args[0]+"' created!");
						result = true;
					} else {
						utils.SEmessage(sender, "Script-Name '"+args[0]+"' already used!");
						utils.SEmessage(sender, "Change name or delete saved script.");
						result = false;
					}										
				} else {
					utils.SEmessage(sender, "Script-Name '"+args[0]+"' already used!");
					utils.SEmessage(sender, "Change name or delete saved script.");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.script.delete
	public boolean scriptDelete(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+scriptNode+deleteNode)){
			boolean intArgs = false;
			int arg = 0;
			try {
				arg = Integer.valueOf(args[0]);
				intArgs = true;
			} catch (Exception e) {
				intArgs = false;
			}
			
			if (intArgs) {
				if ((arg <= plugin.SEdata.getScriptList().size())&&(arg > 0)) {
					try {
					
						plugin.SEdata.getScriptByID(arg).getScriptFile().delete();
						plugin.SEdata.getScriptList().remove(arg);
						plugin.SEdata.rewriteAllScriptFiles();
						plugin.SEdata.refreshScriptList();
						
						utils.SEmessage(sender, "Script deleted!");
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete script!");
						result = false;				
					}
				} else {
					utils.SEmessage(sender, "Script ID not found!");
					result = false;
				}	
			} else {
				utils.SEmessage(sender, "Script ID has to be an Integer!");
				result = false;
			}			
		} else result = true;
		
		return result;
	}
	
	// se.script.edit
	public boolean scriptEdit(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+scriptNode+editNode)){
			if (args.length == 1) {
				
				Map<Integer, SEscript> scriptList = new HashMap<Integer, SEscript>();
				scriptList = plugin.SEdata.getScriptList();
				boolean intArgs = false;
				int arg = 0;
				
				try {
					arg = Integer.valueOf(args[0]);
					intArgs = true;
				} catch (Exception e) {
					intArgs = false;
				}
				
				if (intArgs) {
					if ((arg <= scriptList.size())&&(arg > 0)) {
						
						// Turn on Edit-Mode via entering an edit-entity
						Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
						tempList.put(sender, new SEentitySet(plugin.SEdata.getScriptByID(arg)));
						plugin.SEdata.setEditEntityList(tempList);
						utils.SEmessage(sender, "Edit-Mode enabled for Script '"+plugin.SEdata.getScriptByID(arg).getName()+"'");
						result = true;
						
					} else {
						utils.SEmessage(sender, "Script '"+args[0]+"' not found!");
						result = false;
					}
				} else {
					utils.SEmessage(sender, "Script ID has to be an Integer!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		return result;
	}
	
	// se.script.getID
	public boolean scriptGetID(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+scriptNode+getIDNode)){
			if (args.length == 1) {
				int searchResult = plugin.SEdata.searchScriptList(args[0]);
				if (searchResult > 0) {
					utils.SEmessage(sender, "ScriptID of script '"+args[0]+"' is '"+searchResult+"'!");
					result = true;
				} else {
					utils.SEmessage(sender, "Script '"+args[0]+"' not found!");
					result = false;
				}
			}	
		} else result = true;
		
		return result;
	}
	
	//---------------------//
	// CONDITION
	//---------------------//
	
	// se.condition.create
	public boolean conditionCreate(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+conditionNode+createNode)){
			if (args.length == 1) {
				if (plugin.SEdata.searchConditionList(args[0])<0) {
															
					File newConditionFile = new File(SEdataManager.conditionDirectory + File.separator + args[0] + ".condition");
					SEcondition newCondition = new SEcondition(newConditionFile, args[0], logicalOperator.and, new HashMap<Integer, String>());
					if (!newConditionFile.exists()) {
						plugin.SEdata.rewriteCondition(newCondition);
						plugin.SEdata.refreshConditionList();
						plugin.SEdata.utils.SEmessage(sender, "Condition '"+args[0]+"' created!");
						result = true;
					} else {
						utils.SEmessage(sender, "Condition-Name '"+args[0]+"' already used!");
						utils.SEmessage(sender, "Change name or delete saved condition.");
						result = false;
					}										
				} else {
					utils.SEmessage(sender, "Condition-Name '"+args[0]+"' already used!");
					utils.SEmessage(sender, "Change name or delete saved condition.");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.condition.delete
	public boolean conditionDelete(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+conditionNode+deleteNode)){
			boolean intArgs = false;
			int arg = 0;
			try {
				arg = Integer.valueOf(args[0]);
				intArgs = true;
			} catch (Exception e) {
				intArgs = false;
			}
			
			if (intArgs) {
				if ((arg <= plugin.SEdata.getConditionList().size())&&(arg > 0)) {
					try {
					
						plugin.SEdata.getConditionByID(arg).getConditionFile().delete();
						plugin.SEdata.getConditionList().remove(arg);
						plugin.SEdata.rewriteAllConditionFiles();
						plugin.SEdata.refreshConditionList();
						
						utils.SEmessage(sender, "Condition deleted!");
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete condition!");
						result = false;				
					}
				} else {
					utils.SEmessage(sender, "Condition ID not found!");
					result = false;
				}	
			} else {
				utils.SEmessage(sender, "Condition ID has to be an Integer!");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.condition.edit
	public boolean conditionEdit(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+conditionNode+editNode)){
			if (args.length == 1) {
				
				Map<Integer, SEcondition> conList = new HashMap<Integer, SEcondition>();
				conList = plugin.SEdata.getConditionList();
				boolean intArgs = false;
				int arg = 0;
				
				try {
					arg = Integer.valueOf(args[0]);
					intArgs = true;
				} catch (Exception e) {
					intArgs = false;
				}
				
				if (intArgs) {
					if ((arg <= conList.size())&&(arg > 0)) {
						
						// Turn on Edit-Mode via entering an edit-entity
						Map<CommandSender, SEentitySet> tempList = plugin.SEdata.getEditEntityList();
						tempList.put(sender, new SEentitySet(plugin.SEdata.getConditionByID(arg)));
						plugin.SEdata.setEditEntityList(tempList);
						
						utils.SEmessage(sender, "Edit-Mode enabled for Condition '"+plugin.SEdata.getConditionByID(arg).getName()+"'");
						result = true;
						
					} else {
						utils.SEmessage(sender, "Condition '"+args[0]+"' not found!");
						result = false;
					}
				} else {
					utils.SEmessage(sender, "Condition ID has to be an Integer!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
	
	// se.condition.getID
	public boolean conditionGetID(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+conditionNode+getIDNode)){
			if (args.length == 1) {
				int searchResult = plugin.SEdata.searchConditionList(args[0]);
				if (searchResult > 0) {
					utils.SEmessage(sender, "ConditionID of condition '"+args[0]+"' is '"+searchResult+"'!");
					result = true;
				} else {
					utils.SEmessage(sender, "Condition '"+args[0]+"' not found!");
					result = false;
				}
			}	
		} else result = true;
		
		
		
		return result;
	}

	//---------------------//
	// VARIABLE
	//---------------------//
	
	// se.variable.create
	public boolean variableCreate(CommandSender sender, String[] args) {
		boolean result = false;
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+variableNode+createNode)){
			if (args.length == 2 || args.length == 3) {
				
				if ((args[0].equalsIgnoreCase("int")||args[0].equalsIgnoreCase("string")||args[0].equalsIgnoreCase("set"))) {

					if (args.length == 3) {
						// String Variable
						if (args[0].equalsIgnoreCase("string")) {
							if (!plugin.SEdata.variableExists(args[1])) {
								
								Map<String,String> tempList = plugin.SEdata.getStringVarList();
								String value = args[2]; // plugin.triggerManager.resolveVariables(args[2], new SEentitySet());
								
								tempList.put(args[1], value);
								plugin.SEdata.setStringVarList(tempList);
								plugin.SEdata.rewriteStringVarFile();
								plugin.SEdata.refreshStringVarList();
								plugin.SEdata.utils.SEmessage(sender, "Variable '"+args[1]+"' created!");
								result = true;
																		
							} else {
								utils.SEmessage(sender, "Variable-Name '"+args[1]+"' already used!");
								utils.SEmessage(sender, "Change name or delete saved variable!");
								result = false;
							}		
						}

						// Integer Variable
						if (args[0].equalsIgnoreCase("int")) {	
							String stringValue = args[2]; // plugin.triggerManager.resolveVariables(args[2], new SEentitySet());
							int value = 0;
							boolean intArgs = false;
							
							try {
								// calculates a String into an Integer
								value = utils.calc(stringValue);
								intArgs = true;
							} catch (Exception e) {
								intArgs = false;
							}
									
							if (intArgs) {
								if (!plugin.SEdata.variableExists(args[1])) {
									
									Map<String,Integer> tempList = plugin.SEdata.getIntVarList();
									tempList.put(args[1], value);
									plugin.SEdata.setIntVarList(tempList);
									plugin.SEdata.rewriteIntVarFile();
									plugin.SEdata.refreshIntVarList();
									plugin.SEdata.utils.SEmessage(sender, "Variable '"+args[1]+"' created!");
									result = true;
																			
								} else {
									utils.SEmessage(sender, "Variable-Name '"+args[1]+"' already used!");
									utils.SEmessage(sender, "Change name or delete saved variable!");
									result = false;
								}
							} else {
								utils.SEmessage(sender, "Integer-variables can only have an integer as a value!");
								result = false;
							}
						} 
					}
					
					if (args.length == 2) {
						// set Variable
						if (args[0].equalsIgnoreCase("set")) {
							if (!plugin.SEdata.variableExists(args[1])) {
								
								Map<String,Set<String>> tempSetVarList = plugin.SEdata.getSetVarList();
								
								tempSetVarList.put(args[1], new HashSet<String>());
								
								plugin.SEdata.setSetVarList(tempSetVarList);
								
								//utils.SElog(1, plugin.SEdata.getSetVarList().get(args[1]).toString()); // debug
								
								plugin.SEdata.rewriteSetVarFile(args[1]);
								//plugin.SEdata.refreshSetVarList();
								plugin.SEdata.utils.SEmessage(sender, "Variable '"+args[1]+"' created!");
								result = true;
																		
							} else {
								utils.SEmessage(sender, "Variable-Name '"+args[1]+"' already used!");
								utils.SEmessage(sender, "Change name or delete saved variable!");
								result = false;
							}
						}	
					}
				} else {
					utils.SEmessage(sender, "Variable-type has to be 'int', 'string' or 'set'!");
					result = false;
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
			
		return result;
	}

	// se.variable.delete
	public boolean variableDelete(CommandSender sender, String[] args) {
		boolean result = false;
		Map<String,String> tempStringVarList = plugin.SEdata.getStringVarList();
		Map<String,Integer> tempIntVarList = plugin.SEdata.getIntVarList();
		Map<String,Set<String>> tempSetVarList = plugin.SEdata.getSetVarList();
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+variableNode+deleteNode)){
			if (tempIntVarList.containsKey(args[0]) || tempStringVarList.containsKey(args[0]) || tempSetVarList.containsKey(args[0])) {
				if (tempStringVarList.containsKey(args[0])) {
					try {
						tempStringVarList.remove(args[0]);
						plugin.SEdata.setStringVarList(tempStringVarList);
						plugin.SEdata.rewriteStringVarFile();
						plugin.SEdata.refreshStringVarList();
						
						utils.SEmessage(sender, "Variable deleted!");
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete Variable!");
						result = false;				
					}
				}
				if (tempIntVarList.containsKey(args[0])) {
					try {
						tempIntVarList.remove(args[0]);
						plugin.SEdata.setIntVarList(tempIntVarList);
						plugin.SEdata.rewriteIntVarFile();
						plugin.SEdata.refreshIntVarList();
						
						utils.SEmessage(sender, "Variable deleted!");
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete Variable!");
						result = false;				
					}	
				}	
				if (tempSetVarList.containsKey(args[0])) {
					try {
						tempSetVarList.remove(args[0]);
						plugin.SEdata.setSetVarList(tempSetVarList);
						File tempSetFile = new File(SEdataManager.setDirectory + File.separator + args[0] + ".dat");
						tempSetFile.delete();
						plugin.SEdata.refreshSetVarList();
						
						utils.SEmessage(sender, "Variable deleted!");
						result = true;
					}
					catch (Exception e){
						utils.SElog(3, "Couldn't delete Variable!");
						result = false;				
					}	
				}
			} else {
				utils.SEmessage(sender, "No such Variable!");
				result = false;
			}
		}
			
		
		return result;
	}
	
	// se.variable.edit
	public boolean variableEdit(CommandSender sender, String[] args) {
		boolean result = false;
		Map<String,String> tempStringVarList = plugin.SEdata.getStringVarList();
		Map<String,Integer> tempIntVarList = plugin.SEdata.getIntVarList();
		Map<String,Set<String>> tempSetVarList = plugin.SEdata.getSetVarList();
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+variableNode+editNode)){
			if (args.length == 3 || args.length == 4) {
			
				if (args.length == 3) {
					// edit string
					if (args[0].equalsIgnoreCase("string")) {
						if (tempStringVarList.containsKey(args[1])) {
							try {
								tempStringVarList.put(args[1], args[2]);
								plugin.SEdata.setStringVarList(tempStringVarList);
								plugin.SEdata.rewriteStringVarFile();
								plugin.SEdata.refreshStringVarList();
								
								if (sender instanceof Player)
									utils.SEmessage(sender, "Value changed!");
								result = true;
							} catch (Exception e) {
								utils.SElog(3, "Couldn't change value!");
								result = false;
							}
						}
					}
						
					// edit int
					if (args[0].equalsIgnoreCase("int")) {
						if (tempIntVarList.containsKey(args[1])) {
							
							String stringValue = args[2]; // plugin.triggerManager.resolveVariables(args[2], new SEentitySet());
							int value = 0;
							boolean intArgs = false;
							
							try {
								// calculates a String into an Integer
								value = utils.calc(stringValue);
								intArgs = true;
							} catch (Exception e) {
								intArgs = false;
							}
							
							if (intArgs) {
								try {
									
									tempIntVarList.put(args[1], value);
									plugin.SEdata.setIntVarList(tempIntVarList);
									plugin.SEdata.rewriteIntVarFile();
									plugin.SEdata.refreshIntVarList();
									
									utils.SEmessage(sender, "Value changed!");
									result = true;
									
								} catch (Exception e) {
									utils.SElog(3, "Couldn't change value!");
									result = false;				
								}	
							} else {
								utils.SEmessage(sender, "Integer-variables can only have an integer as a value!");
								result = false;	
							}
						
						} else {
							utils.SEmessage(sender, "No such Variable!");
							result = false;
						}
					}
				}
				
				if (args.length == 3 || args.length == 4) {
					// edit set
					if (args[0].equalsIgnoreCase("set")) {
						if (tempSetVarList.containsKey(args[1])) {
							try {
								Set<String> tempSetVar = tempSetVarList.get(args[1]);
								if ((args[2].equalsIgnoreCase("remove")) || (args[2].equalsIgnoreCase("add")) || (args[2].equalsIgnoreCase("onlinePlayers"))) {
									if (args.length == 4) {
										if ((args[2].equalsIgnoreCase("remove")) && tempSetVar.contains(args[3])) {
											tempSetVar.remove(args[3]);
										}
										if (args[2].equalsIgnoreCase("add")) {
											tempSetVar.add(args[3]);
										}
									}
									if (args.length == 3) {
										if (args[2].equalsIgnoreCase("onlinePlayers")) {
											tempSetVar.clear();
											Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
											for (int i=0; i < plugin.getServer().getOnlinePlayers().length; i++) {
												tempSetVar.add(onlinePlayers[i].getName());
											}
										}
									}
									tempSetVarList.put(args[1], tempSetVar);
									plugin.SEdata.setSetVarList(tempSetVarList);
									plugin.SEdata.rewriteAllSetVarFiles();
									//plugin.SEdata.refreshSetVarList();
									
									if (player!=null) {
										utils.SEmessage(player, "Value changed!");
										
										if (plugin.SEdata.getDebugees(player))
											utils.SElog(1, args[1]+": "+plugin.SEdata.getSetVarList().get(args[1]).toString()); // debug
									}
										
									
									result = true;
									//utils.SElog(1, args[0]+": "+plugin.SEdata.getSetVarList().get(args[0]).toString()); // debug
								}
							} catch (Exception e) {
								utils.SElog(3, "Couldn't change value!");
								result = false;				
							}
						} else {
							utils.SEmessage(sender, "List not found.");
							result = false;
						}
					}
				}
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}

	// se.variable.edit
	public boolean variableList(CommandSender sender, String[] args) {
		boolean result = false;
		Map<String,String> tempStringVarList = plugin.SEdata.getStringVarList();
		Map<String,Integer> tempIntVarList = plugin.SEdata.getIntVarList();
		Map<String,Set<String>> tempSetVarList = plugin.SEdata.getSetVarList();
		
		Player player = senderToPlayer(sender);
		if(checkPermission(player, seNode+variableNode+listNode)){
			if (args.length == 1) {
			
				if (args[0].equalsIgnoreCase("int")) {
					player.sendMessage(tempIntVarList.keySet().toString());
				}
				if (args[0].equalsIgnoreCase("string")) {
					player.sendMessage(tempStringVarList.keySet().toString());
				}
				if (args[0].equalsIgnoreCase("set")) {
					player.sendMessage(tempSetVarList.keySet().toString());
				}
				
			} else {
				utils.SEmessage(sender, "Wrong number of arguments! Try again.");
				result = false;
			}	
		} else result = true;
		
		return result;
	}
}
