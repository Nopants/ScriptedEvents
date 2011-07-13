package me.nopants.ScriptedEvents;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEcuboid;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SEplayerListener extends PlayerListener {

	//-----------//
	// Variables
	//-----------//
	private Map<Player, Location> lastLocation = new HashMap<Player, Location>();
	private Map<Player, Integer> dist = new HashMap<Player, Integer>();
	private Map<Player, Integer> distLeft = new HashMap<Player, Integer>();
	private Map<Player, Boolean> inCuboid = new HashMap<Player, Boolean>();
	private Map<Player, Location> vertex1 = new HashMap<Player, Location>();
	private Map<Player, Location> vertex2 = new HashMap<Player, Location>();
	private SEdataManager SEdata;
	private SEutils utils;
	private ScriptedEvents plugin;
	public boolean cancel = false;

	public SEplayerListener(ScriptedEvents SEplugin) {
		plugin = SEplugin;
		SEdata = plugin.SEdata;
		utils = SEdata.utils;
	}



	//---------------------//
	// FUNCTIONS
	//---------------------//
	
	// resets the distance a player has to walk until the next check
	public void resetDist(Player player) {
		distLeft.put(player, 0);
	}
	
	// returns the nearest cuboid to a location
	public SEcuboid getNextCuboid(Location playerLocation) {
		Location tempLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
		Map<String,SEcuboid> cuboidList = SEdata.getAllCuboids();
		
		//SEutils.SElog(1, cuboidList.keySet().toString());
		
		SEcuboid result = null;
		int i = 1;
		
		if (cuboidList.size() > 0) {
			
			Iterator<String> lauf = cuboidList.keySet().iterator(); 
			while (lauf.hasNext()) {
				SEcuboid tempCuboid = cuboidList.get(lauf.next());
				if (tempCuboid != null && tempCuboid.getWorld().equalsIgnoreCase(playerLocation.getWorld().getName())) {
					if (i == 1) {
						result = tempCuboid;
						i++;
					}
					else {
						if ((utils.getDist(tempCuboid.getRelativeCenter(tempLocation), playerLocation))<(utils.getDist(result.getRelativeCenter(tempLocation), playerLocation)))
		    				result = tempCuboid;
					}
					
				}
			}
		}
		return result;
	}

	// returns if a player is inside a cuboid
	public boolean playerInsideCuboid(Location playerLocation, SEcuboid cuboid) {
		boolean result = false;
		// cuboid(Eckpunt, Koordinaten-Achse)
		int playerX = playerLocation.getBlockX();
		int playerY = playerLocation.getBlockY();
		int playerZ = playerLocation.getBlockZ();
		
		// if there are no saved cuboids
		if (SEdata.getAllCuboids().size()==0) return false;
		
		if ((cuboid != null)&&(cuboid.getWorld().equals(playerLocation.getWorld().getName()))) {
		
			result = ((playerX <= utils.biggerInt(cuboid.getCorner(1).getBlockX(), cuboid
					.getCorner(2).getBlockX()))
					&& (playerX >= utils.smallerInt(cuboid.getCorner(1).getBlockX(),
							cuboid.getCorner(2).getBlockX()))
							&& (
									(
											(playerY <= utils.biggerInt(cuboid.getCorner(1).getBlockY(), cuboid.getCorner(2).getBlockY()))
											&& (playerY >= utils.smallerInt(cuboid.getCorner(1).getBlockY(), cuboid.getCorner(2).getBlockY()))
									)
									||
									(
											(playerY+1 <= utils.biggerInt(cuboid.getCorner(1).getBlockY(), cuboid.getCorner(2).getBlockY()))
											&& (playerY+1 >= utils.smallerInt(cuboid.getCorner(1).getBlockY(), cuboid.getCorner(2).getBlockY()))
									)
							)
							&& (playerZ <= utils.biggerInt(cuboid.getCorner(1).getBlockZ(),
									cuboid.getCorner(2).getBlockZ()))
									&& (playerZ >= utils.smallerInt(cuboid.getCorner(1).getBlockZ(),
											cuboid.getCorner(2).getBlockZ())));
		} else result = false;
		return result;
	}
	
	// returns the vertex1 used by the cuboid selection
	public Location getVertex1(Player player) {
		return vertex1.get(player);
	}

	// returns the vertex2 used by the cuboid selection
	public Location getVertex2(Player player) {
		return vertex2.get(player);
	}

	
	//---------------------//
	// EVENTS
	//---------------------//
	
	// CONTAINS onLeave: is called if a player left a cuboid
	public void onPlayerLeaveCuboid(Player leavingPlayer, SEcuboid leftCuboid) {
		
		//---[ onLeave ]--------------------------------------------------------------//
		// get the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onLeave, leftCuboid));
		// release the triggers
		plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(leavingPlayer, leftCuboid));
		//----------------------------------------------------------------------------//
		
		if (SEdata.getDebugees(leavingPlayer)) utils.SEmessage(leavingPlayer, leavingPlayer.getName() + " left '"
				+ leftCuboid.getName() + "'"); // debug
	}

	// CONTAINS onEnter: is called if a player entered a cuboid
	public void onPlayerEnterCuboid(Player enteringPlayer,
			SEcuboid enteredCuboid) {
		
		//---[ onEnter ]--------------------------------------------------------------//
		// get the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onEnter, enteredCuboid));
		// release the triggers
		plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(enteringPlayer, enteredCuboid));
		//----------------------------------------------------------------------------//
		
		if (SEdata.getDebugees(enteringPlayer)) utils.SEmessage(enteringPlayer, enteringPlayer.getName() + " entered '"
				+ enteredCuboid.getName() + "'"); // debug
	}

	// CONTAINS onInteractAt: is called if a player interacts with something
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		//---[ onInteract ]-----------------------------------------------------------//
		// get the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onInteract));
		// release the triggers
		plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(event));		
		event.setCancelled(cancel);
		cancel = false;
		//----------------------------------------------------------------------------//
		
		if (event.hasBlock()) {
			// manage selection
			if (SEdata.getDebugees(player))
				utils.SEmessage(player, "Clicked at: "
						+ String.valueOf(SEdata.utils.locationToString(event.getClickedBlock().getLocation()))); // debug
			if (SEdata.getDebugees(player))
				utils.SEmessage(player, "Used Item: "
						+ String.valueOf(player.getItemInHand().getTypeId())); // debug
			if (SEdata.getDebugees(player))
				utils.SEmessage(player, "TriggerItem: "
						+ plugin.SEdata.getTriggerItem()); // debug
			if (player.getItemInHand().getTypeId() == plugin.SEdata.getTriggerItem()) {
				Location location = event.getClickedBlock().getLocation();

				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					vertex1.put(player, location);
					utils.SEmessage(player, "first vertex selected");
				}

				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					vertex2.put(player, location);
					utils.SEmessage(player, "second vertex selected");
				}			
			}
			
		}
	}

	// CONTAINS onCommand: is called if a player sends a command
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		String message = event.getMessage();
		// remove "/"
		message = message.substring(1, message.length());
		// get args and Commandlabel
		String[] args = message.split(" ");
	
		// set Commandlabel
		String commandLabel = args[0];
		
		if (SEdata.getDebugees(player))
			SEdata.utils.SEmessage(player, "Command: "+commandLabel); // debug

		//---[ onCommand ]------------------------------------------------------------//
		// return the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onCommand, commandLabel));
		
		if (!triggerList.isEmpty()) {
			// release the triggers matching to the entered Cuboid and the event onInteractAt
			plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(player, commandLabel, args));
			event.setCancelled(true);
		}
		//----------------------------------------------------------------------------//			

	}

	// is called if a player is teleported
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		//---[ onRespawn ]------------------------------------------------------------//
		// return the triggers matching to the entered Cuboid and the event onEnter
		Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onRespawn));
		
		if (!triggerList.isEmpty()) {
			// release the triggers matching to the entered Cuboid and the event onInteractAt
			plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(event));
		}
		//----------------------------------------------------------------------------//			
	}
	
	// is called if a player moves onto another Block
	public void onNewPos(Player movingPlayer) {
		SEcuboid nextCuboid;
		Location playerlocation = new Location(movingPlayer.getLocation().getWorld(),movingPlayer.getLocation().getX(),movingPlayer.getLocation().getY(),movingPlayer.getLocation().getZ());	
		
		// if (SEdata.getDebugees(movingPlayer)) utils.SEmessage(movingPlayer, "Step!"); // debug
		
		// if there are no cuboids saved, nothing has to be done
		if (SEdata.getAllCuboids().size()>0) {
			// find the nearest cuboid
			nextCuboid = getNextCuboid(playerlocation); 
			if (nextCuboid != null) {
			
				//------------check-----------------------------------------------//
				// 1. find the nearest cuboid                                     //
				// 2. check if player is inside nearest cuboid                    //
				// 3. get new distance to the nearest cuboid                      //
				// 4. decrement distance by 2 if player is moving towards an edge //
				//	
			
				// ------------------------------------------------------------------------------------------------------------//
				// initialize via doing a check on firstRun, without decrementing the distance, or else decrement the distance //
				// ------------------------------------------------------------------------------------------------------------//
				if (dist.containsKey(movingPlayer)) {
				
					// decrement distance
					distLeft.put(movingPlayer, (distLeft.get(movingPlayer) - 1));
				
				} else {

					// ---------------- Initialization via Check ----------------------//
					
					// if player is inside region call onEnterCuboid
					if (playerInsideCuboid(playerlocation, nextCuboid)) {
						inCuboid.put(movingPlayer, true);
						onPlayerEnterCuboid(movingPlayer, nextCuboid);
					} else
						inCuboid.put(movingPlayer, false);
				
					// initialize distance to the nearest Cuboid
					distLeft.put(
							movingPlayer,
							utils.getDist(nextCuboid.getRelativeCenter(playerlocation), playerlocation));

					// if the player moves towards an edge of the cuboid, the distance hat to be decreased by 2.
					// this is because of some issue with diagonal movement. i don't know exactly why.
					if (nextCuboid.edgeOrientation(playerlocation)) {
						if (distLeft.get(movingPlayer) > 2) {
							distLeft.put(movingPlayer, (distLeft.get(movingPlayer) - 2));
						}
					}
				
					dist.put(movingPlayer, distLeft.get(movingPlayer));
				
					// ----------------------------------------------------------------//
				}

				// -------------------------------------------------------//
				// if distance to check has been traveled, do a new check //
				// -------------------------------------------------------//
				if (distLeft.get(movingPlayer) <= 0) {

					// ------------------------ Do Check ------------------------------//
				
					if (SEdata.getDebugees(movingPlayer)) utils.SEmessage(movingPlayer, "Check!"); // debug
				
					// find the nearest Cuboid
					nextCuboid = getNextCuboid(playerlocation);
				
					// if a player is inside a Cuboid but his inCuboid flag is 'false' call onPlayerEnterCuboid
					// if a player is not inside a Cuboid but this inCuboid flag is 'true' call onPlayerLeaveCuboid
					if (playerInsideCuboid(playerlocation, nextCuboid)) {
						if (!(inCuboid.get(movingPlayer))) {
							onPlayerEnterCuboid(movingPlayer, nextCuboid);
							inCuboid.put(movingPlayer, true);
						}
					} else {
						if (inCuboid.get(movingPlayer)) {
							onPlayerLeaveCuboid(movingPlayer, nextCuboid);
							inCuboid.put(movingPlayer, false);
						}
					}
				
					// initialize distance to the nearest Cuboid
					distLeft.put(
							movingPlayer,
							utils.getDist(nextCuboid.getRelativeCenter(playerlocation), playerlocation));

					// if the player moves towards an edge of the cuboid, the distance hat to be decreased by 2.
					// this is because of some issue with diagonal movement. i don't know exactly why.
					if (nextCuboid.edgeOrientation(playerlocation)) {
						if (distLeft.get(movingPlayer) > 2) {
							distLeft.put(movingPlayer, (distLeft.get(movingPlayer) - 2));
						}
					}

					dist.put(movingPlayer, distLeft.get(movingPlayer));
				
					// ----------------------------------------------------------------//

				}
			}
			if (SEdata.getDebugees(movingPlayer)) utils.SEmessage(movingPlayer, dist.get(movingPlayer)+"/"+distLeft.get(movingPlayer)); // debug
		}
	}

	// is called if a player moves
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		// if (getDebugees(player)) ScriptedEvents.SEmessage(player, "move!");

		if (!(lastLocation.containsKey(player))) {
			lastLocation.put(player, player.getLocation());
			onNewPos(player);
		} else { // check if the player has actually moved a block and call
					// onNewPos
			if (lastLocation.get(player).getBlock() != player.getLocation()
					.getBlock()) {
				lastLocation.put(player, player.getLocation());
				onNewPos(player);
			}
		}

	}
	
	// is called if a player is teleported
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		resetDist(event.getPlayer());
	}

	// is called if a player uses a portal
	public void onPlayerPortal(PlayerPortalEvent event) {
		resetDist(event.getPlayer());
	}
}
