package me.nopants.ScriptedEvents;

import org.bukkit.Location;

public class SEcuboid {
	private String world;  
	private Location cuboidCorner1;
	private Location cuboidCorner2;
	private String name;
	//private SEutils utils = plugin.utils;

	// public SEOrientation Orientation = new SEOrientation(null, null, null);

	// constructor
	SEcuboid(String newWorld, String newName, Location newCuboidCorner1, Location newCuboidCorner2) {
		this.setCorner(1, newCuboidCorner1);
		this.setCorner(2, newCuboidCorner2);
		this.name = newName;
		this.world = newWorld;
	}

	/*public SEcuboid(String ID) {
		SEyamlparser parser = new SEyamlparser();
		String input = ScriptedEvents.readCuboid(ID);
		this.cuboidCorner1 = parser.stringToCuboid(input).getCorner(1);
		this.cuboidCorner2 = parser.stringToCuboid(input).getCorner(2);
		this.name = parser.stringToCuboid(input).getName();
		this.world = parser.stringToCuboid(input).getWorld();
	}*/

	// overrides the equals-method. returns true if two cuboids equal another
	public boolean equals(SEcuboid cuboid){
		boolean result = (  (this.getWorld().equalsIgnoreCase(cuboid.getWorld()))
				&& (this.getCorner(1).getBlockX()==cuboid.getCorner(1).getBlockX())
				&& (this.getCorner(1).getBlockY()==cuboid.getCorner(1).getBlockY())
				&& (this.getCorner(1).getBlockZ()==cuboid.getCorner(1).getBlockZ())
				&& (this.getCorner(2).getBlockX()==cuboid.getCorner(1).getBlockX())
				&& (this.getCorner(2).getBlockY()==cuboid.getCorner(1).getBlockY())
				&& (this.getCorner(2).getBlockZ()==cuboid.getCorner(1).getBlockZ())
				&& (this.getName().equalsIgnoreCase(cuboid.getName()))); 
		return result;
	}
	
	// returns a text-version of this cuboid
	public String toString() { 
		//SEutils parser = new SEutils();		
		return ("world:"+world+",name:"+name+",vertex1:"+cuboidCorner1.getBlockX()+";"+cuboidCorner1.getBlockY()+";"+cuboidCorner1.getBlockZ()+",vertex2:"+cuboidCorner2.getBlockX()+";"+cuboidCorner2.getBlockY()+";"+cuboidCorner2.getBlockZ());
	}

	// sets the cuboids name
	public void setName(String newName){
		this.name = newName;
	}
	
	// returns the cuboids name
	public String getName(){
		return this.name;
	}

	// sets the corner with 'cornerID' of the cuboid
	public void setCorner(int cornerID, Location newCoordinates) {
		if (cornerID == 1) {
			cuboidCorner1 = newCoordinates;
		} else {
			cuboidCorner2 = newCoordinates;
		}
	}

	// returns the corner with 'cornerID'
	public Location getCorner(int cornerID) {
		if (cornerID == 1) {
			return cuboidCorner1;
		} else {
			return cuboidCorner2;
		}
	}

	// returns ID of the the corner with the smaller getBlockX()
	public int getSmallerXID() {
		if (cuboidCorner1.getBlockX() <= cuboidCorner2.getBlockX()) {
			return 1;
		} else {
			return 2;
		}
	}

	// returns the ID of the corner with the smaller getBlockY()
	public int getSmallerYID() {
		if (cuboidCorner1.getBlockY() <= cuboidCorner2.getBlockY()) {
			return 1;
		} else {
			return 2;
		}
	}

	// returns the ID of the corner with the bigger getBlockZ()
	public int getSmallerZID() {
		if (cuboidCorner1.getBlockZ() <= cuboidCorner2.getBlockZ()) {
			return 1;
		} else {
			return 2;
		}
	}

	// returns the ID of the corner with the bigger getBlockX()
	public int getBiggerXID() {
		if (cuboidCorner1.getBlockX() >= cuboidCorner2.getBlockX()) {
			return 1;
		} else {
			return 2;
		}
	}

	// returns the ID of the corner with the bigger getBlockY()
	public int getBiggerYID() {
		if (cuboidCorner1.getBlockY() >= cuboidCorner2.getBlockY()) {
			return 1;
		} else {
			return 2;
		}
	}

	// returns the ID of the corner with the bigger getBlockZ()
	public int getBiggerZID() {
		if (cuboidCorner1.getBlockZ() >= cuboidCorner2.getBlockZ()) {
			return 1;
		} else {
			return 2;
		}
	}

	// sets the world the cuboid is in
	public void setWorld(String newWorld) {
		this.world = newWorld;
	}
	
	// returns the world the cuboid is in
	public String getWorld() {
		return this.world;
	}

	public int biggerInt(int var1, int var2) {
		if (var1 >= var2) {
			return var1;
		} else {
			return var2;
		}
	}
	
	// returns the smaler integer
	public int smalerInt(int var1, int var2) {
		if (var1 <= var2) {
			return var1;
		} else {
			return var2;
		}
	}
	
	// returns the lenght of the cuboid
	public int getLength() {
		return (biggerInt(cuboidCorner1.getBlockX(), cuboidCorner2.getBlockX()) - (smalerInt(
				cuboidCorner1.getBlockX(), cuboidCorner2.getBlockX()))) + 1;
	}

	// returns the height of the cuboid
	public int getHeight() {
		return (biggerInt(cuboidCorner1.getBlockY(), cuboidCorner2.getBlockY()) - (smalerInt(
				cuboidCorner1.getBlockY(), cuboidCorner2.getBlockY()))) + 1;
	}

	// returns the width of the cuboid
	public int getWidth() {
		return (biggerInt(cuboidCorner1.getBlockZ(), cuboidCorner2.getBlockZ()) - (smalerInt(
				cuboidCorner1.getBlockZ(), cuboidCorner2.getBlockZ()))) + 1;
	}

	// returns true if a location is orientated diagonal to this cuboid  
	public boolean edgeOrientation(Location playerLocation) {
		SEorientation Orientation = new SEorientation(null, null, null);
		Orientation = getPlayerCuboidOrientation(playerLocation);
		SEorientation.XOrientation Xcentral = SEorientation.XOrientation.central;
		SEorientation.YOrientation Ycentral = SEorientation.YOrientation.central;
		SEorientation.ZOrientation Zcentral = SEorientation.ZOrientation.central;
		SEorientation.XOrientation x = Orientation.xorientation;
		SEorientation.YOrientation y = Orientation.yorientation;
		SEorientation.ZOrientation z = Orientation.zorientation;

		
		if ((((x == Xcentral && y != Ycentral) && z != Zcentral) ||
			 ((x != Xcentral && y == Ycentral) && z != Zcentral)) ||
			 ((x != Xcentral && y != Ycentral) && z == Zcentral)) {
			return true;
		} else
			return false;
	}

	// returns the side of a cuboid a player is standing in front of
	public SEorientation getPlayerCuboidOrientation(Location playerLocation) {
		SEorientation Orientation = new SEorientation(null, null, null);
		int playerX = playerLocation.getBlockX();
		int playerY = playerLocation.getBlockY();
		int playerZ = playerLocation.getBlockZ();

		// ============================================================================//
		// X-Orientation //
		// ==============//
		// x=left?
		if (playerX <= smalerInt(cuboidCorner1.getBlockX(),
				cuboidCorner2.getBlockX())) {
			Orientation.xorientation = SEorientation.XOrientation.right;
		}
		// x=right?
		if (playerX >= biggerInt(cuboidCorner1.getBlockX(),
				cuboidCorner2.getBlockX())) {
			Orientation.xorientation = SEorientation.XOrientation.left;
		}
		// x=central?
		if ((playerX <= biggerInt(cuboidCorner1.getBlockX(),
				cuboidCorner2.getBlockX()))
				&& (playerX >= smalerInt(cuboidCorner1.getBlockX(),
						cuboidCorner2.getBlockX()))) {
			Orientation.xorientation = SEorientation.XOrientation.central;
		}
		// ============================================================================//
		// Y-Orientation //
		// ==============//
		// y=front?
		if (playerY <= smalerInt(cuboidCorner1.getBlockY(),
				cuboidCorner2.getBlockY())) {
			Orientation.yorientation = SEorientation.YOrientation.down;
		}
		// y=back?
		if (playerY >= biggerInt(cuboidCorner1.getBlockY(),
				cuboidCorner2.getBlockY())) {
			Orientation.yorientation = SEorientation.YOrientation.up;
		}
		// y=central?
		if ((playerY <= biggerInt(cuboidCorner1.getBlockY(),
				cuboidCorner2.getBlockY()))
				&& (playerY >= smalerInt(cuboidCorner1.getBlockY(),
						cuboidCorner2.getBlockY()))) {
			Orientation.yorientation = SEorientation.YOrientation.central;
		}
		// ============================================================================//
		// Z-Orientation //
		// ==============//
		// z=down?
		if (playerZ <= smalerInt(cuboidCorner1.getBlockZ(),
				cuboidCorner2.getBlockZ())) {
			Orientation.zorientation = SEorientation.ZOrientation.front;
		}
		// z=up?
		if (playerZ >= biggerInt(cuboidCorner1.getBlockZ(),
				cuboidCorner2.getBlockZ())) {
			Orientation.zorientation = SEorientation.ZOrientation.back;
		}
		// z=central?
		if ((playerZ <= biggerInt(cuboidCorner1.getBlockZ(),
				cuboidCorner2.getBlockZ()))
				&& (playerZ >= smalerInt(cuboidCorner1.getBlockZ(),
						cuboidCorner2.getBlockZ()))) {
			Orientation.zorientation = SEorientation.ZOrientation.central;
		}
		// ============================================================================//

		return Orientation;
	}

	// returns the distance from the center to the next border in dimension X
	public int getRadiusX() {
		if (this.getLength() % 2 == 0) {
			return (this.getLength() / 2);
		} else if (this.getLength() > 1) {
			return ((this.getLength() - 1) / 2);
		} else
			return 1;
	}

	// returns the distance from the center to the next border in dimension Y
	public int getRadiusY() {
		if (this.getHeight() % 2 == 0) {
			return (this.getHeight() / 2);
		} else if (this.getHeight() > 1) {
			return ((this.getHeight() - 1) / 2);
		} else
			return 1;
	}

	// returns the distance from the center to the next border in dimension Z
	public int getRadiusZ() {
		if (this.getWidth() % 2 == 0) {
			return (this.getWidth() / 2);
		} else if (this.getWidth() > 1) {
			return ((this.getWidth() - 1) / 2);
		} else
			return 1;
	}

	// returns the centerX of the cuboid as integer
	public int getCenterX(Location playerLocation) {
		SEcuboid tempCuboid = new SEcuboid(null,null,null,null); 
			tempCuboid = this.makeUnevenCuboid(playerLocation);
		return (tempCuboid.getCorner(getSmallerXID()).getBlockX() + tempCuboid
				.getRadiusX());
	}

	// returns the centerY of the cuboid as integer
	public int getCenterY(Location playerLocation) {
		SEcuboid tempCuboid = new SEcuboid(null,null,null,null); 
		tempCuboid = this.makeUnevenCuboid(playerLocation);
		return (tempCuboid.getCorner(getSmallerYID()).getBlockY() + tempCuboid
				.getRadiusY());
	}

	// returns the centerX of the cuboid as integer (has to have even length)
	public int getCenterZ(Location playerLocation) {
		SEcuboid tempCuboid = new SEcuboid(null,null,null,null); 
		tempCuboid = this.makeUnevenCuboid(playerLocation);
		return (tempCuboid.getCorner(getSmallerZID()).getBlockZ() + tempCuboid
				.getRadiusZ());
	}

	// returns an uneven cuboid relative to the playerLocation
	public SEcuboid makeUnevenCuboid(Location playerLocation) {
		SEcuboid tempCuboid = new SEcuboid(null,null,null,null);
		tempCuboid = this;
		Location tempLocation = playerLocation;
		SEorientation Orientation = getPlayerCuboidOrientation(tempLocation);

		if ((this.getLength() % 2) == 0) { // if the lenght is not odd, the
											// cuboid has to get cut relative to
											// the orientation
			if (Orientation.xorientation == SEorientation.XOrientation.left) {
				tempCuboid.getCorner(getSmallerXID()).setX(
						this.getCorner(getSmallerXID()).getX() + 1); // weil man
																	// rechts
																	// vom
																	// Cuboid
																	// steht,
																	// muss der
																	// kleine
																	// x-wert um
																	// 1
																	// vergrößert
																	// werden
			}
			if (Orientation.xorientation == SEorientation.XOrientation.right) {
				tempCuboid.getCorner(getBiggerXID()).setX(
						this.getCorner(getBiggerXID()).getX() - 1); // große
																	// x-wert um
																	// 1
																	// verkleinern
			}
		}
		if ((this.getHeight() % 2) == 0) { // if the width is not odd, the cuboid
											// has to get cut relative to the
											// orientation
			if (Orientation.yorientation == SEorientation.YOrientation.up) {
				tempCuboid.getCorner(getSmallerYID()).setY(
						this.getCorner(getSmallerYID()).getY() + 1); // kleinen
																	// y-wert um
																	// 1
																	// vergrößern
			}
			if (Orientation.yorientation == SEorientation.YOrientation.down) {
				tempCuboid.getCorner(getBiggerYID()).setY(
						this.getCorner(getBiggerYID()).getY() - 1); // große
																	// y-wert um
																	// 1
																	// verkleinern
			}
		}

		if ((this.getWidth() % 2) == 0) { // if the width is not odd, the
											// cuboid has to get cut relative to
											// the orientation
			if (Orientation.zorientation == SEorientation.ZOrientation.back) {
				tempCuboid.getCorner(getSmallerZID()).setZ(
						this.getCorner(getSmallerZID()).getZ() + 1); // kleinen
																	// z-wert um
																	// 1
																	// vergrößern
			}
			if (Orientation.zorientation == SEorientation.ZOrientation.front) {
				tempCuboid.getCorner(getBiggerZID()).setZ(
						this.getCorner(getBiggerZID()).getZ() - 1); // große
																	// z-wert um
																	// 1
																	// verkleinern
			}
		}

		return tempCuboid;
	}

	// returns center of the cuboid as a location, relative to the
	// playerlocation
	public Location getRelativeCenter(Location playerLocation) {
		SEcuboid tempCuboid = new SEcuboid(null, null, null, null);  
		Location tempLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
		tempCuboid = this.makeUnevenCuboid(tempLocation);
		Location result = tempLocation;

		// result.setX(tempLocation.getX()+5); // debug

		// ----------------------------------------
		// get relativeCenter for every Orientation
		// 1 Quader-Mitte *irrelevant
		// 8 Eckpunkte *markiert
		// 6 Flächen-Mitten *fertig
		// 12 Kanten-Mitten *markiert
		// 1+8+6+12 = 3*3*3 = 27
		// ----------------------------------------
		SEorientation.XOrientation right = SEorientation.XOrientation.right;
		SEorientation.XOrientation Xcentral = SEorientation.XOrientation.central;
		SEorientation.XOrientation left = SEorientation.XOrientation.left;

		SEorientation.YOrientation down = SEorientation.YOrientation.down;
		SEorientation.YOrientation Ycentral = SEorientation.YOrientation.central;
		SEorientation.YOrientation up = SEorientation.YOrientation.up;

		SEorientation.ZOrientation front = SEorientation.ZOrientation.front;
		SEorientation.ZOrientation Zcentral = SEorientation.ZOrientation.central;
		SEorientation.ZOrientation back = SEorientation.ZOrientation.back;

		SEorientation.XOrientation tempX = tempCuboid
				.getPlayerCuboidOrientation(playerLocation).xorientation;
		SEorientation.YOrientation tempY = tempCuboid
				.getPlayerCuboidOrientation(playerLocation).yorientation;
		SEorientation.ZOrientation tempZ = tempCuboid
				.getPlayerCuboidOrientation(playerLocation).zorientation;

		// -------------------------------------------------------

		if ((tempX == right && tempY == down) && tempZ == front) {
			// Eckpunkt #1
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ());
		}
		if ((tempX == Xcentral && tempY == down) && tempZ == front) {
			// Kantenmitte #1
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID())
					.getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID())
					.getBlockZ());
		}
		if ((tempX == left && tempY == down) && tempZ == front) {
			// Eckpunkt #2
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ());
		}
		if ((tempX == right && tempY == Ycentral) && tempZ == front) {
			// Kantenmitte #2
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID())
					.getBlockX());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID())
					.getBlockZ());
		}
		if ((tempX == Xcentral && tempY == Ycentral) && tempZ == front) {
			// Flächenmitte #1
			result.setZ(tempCuboid.getCenterZ(tempLocation)
					- tempCuboid.getRadiusZ());
		}
		if (tempX == left && tempY == Ycentral && tempZ == front) {
			// Kantenmitte #3 (y vom spieler, größtes x, kleinstes z vom block)
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID())
					.getBlockX());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID())
					.getBlockZ());
		}
		if (tempX == right && tempY == up && tempZ == front) {
			// Eckpunkt #3
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ());
		}
		if (tempX == Xcentral && tempY == up && tempZ == front) {
			// Kantenmitte #4
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID())
					.getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID())
					.getBlockZ());

		}
		if (tempX == left && tempY == up && tempZ == front) {
			// Eckpunkt #4
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getSmallerZID()).getBlockZ());
		}

		// -------------------------------------------------------

		if (tempX == right && tempY == down && tempZ == Zcentral) {
			// Kantenmitte #5
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID())
					.getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID())
					.getBlockY());
		}
		if (tempX == Xcentral && tempY == down && tempZ == Zcentral) {
			// Flächenmitte #2
			result.setY(tempCuboid.getCenterY(tempLocation)
					- tempCuboid.getRadiusY());
		}
		if (tempX == left && tempY == down && tempZ == Zcentral) {
			// Kantenmitte #6
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID())
					.getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID())
					.getBlockY());
		}
		if (tempX == right && tempY == Ycentral && tempZ == Zcentral) {
			// Flächenmitte #3
			result.setX(tempCuboid.getCenterX(tempLocation)
					- tempCuboid.getRadiusX());
		}
		/*
		 * if (tempX == Xcentral && tempY == Ycentral && tempZ == Zcentral){
		 * 
		 * }
		 */
		if (tempX == left && tempY == Ycentral && tempZ == Zcentral) {
			// Flächenmitte #4
			result.setX(tempCuboid.getCenterX(tempLocation)
					+ tempCuboid.getRadiusX());
		}
		if (tempX == right && tempY == up && tempZ == Zcentral) {
			// Kantenmitte #7
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID())
					.getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID())
					.getBlockY());
		}
		if (tempX == Xcentral && tempY == up && tempZ == Zcentral) {
			// Flächenmitte #5
			result.setY(tempCuboid.getCenterY(tempLocation)
					+ tempCuboid.getRadiusY());
		}
		if (tempX == left && tempY == up && tempZ == Zcentral) {
			// Kantenmitte #8
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID())
					.getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID())
					.getBlockY());
		}

		// -------------------------------------------------------

		if (tempX == right && tempY == down && tempZ == back) {
			// Eckpunkt #5
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ());
		}
		if (tempX == Xcentral && tempY == down && tempZ == back) {
			// Kantenmitte #9
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID())
					.getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID())
					.getBlockZ());
		}
		if (tempX == left && tempY == down && tempZ == back) {
			// Eckpunkt #6
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getSmallerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ());
		}
		if (tempX == right && tempY == Ycentral && tempZ == back) {
			// Kantenmitte #10
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID())
					.getBlockX());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID())
					.getBlockZ());
		}
		if (tempX == Xcentral && tempY == Ycentral && tempZ == back) {
			// Flächenmitte #6
			result.setZ(tempCuboid.getCenterZ(tempLocation)
					+ tempCuboid.getRadiusZ());
		}
		if (tempX == left && tempY == Ycentral && tempZ == back) {
			// Kantenmitte #11
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID())
					.getBlockX());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID())
					.getBlockZ());
		}
		if (tempX == right && tempY == up && tempZ == back) {
			// Eckpunkt #7
			result.setX(tempCuboid.getCorner(tempCuboid.getSmallerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ());
		}
		if (tempX == Xcentral && tempY == up && tempZ == back) {
			// Kantenmitte #12
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID())
					.getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID())
					.getBlockZ());
		}
		if (tempX == left && tempY == up && tempZ == back) {
			// Eckpunkt #8
			result.setX(tempCuboid.getCorner(tempCuboid.getBiggerXID()).getBlockX());
			result.setY(tempCuboid.getCorner(tempCuboid.getBiggerYID()).getBlockY());
			result.setZ(tempCuboid.getCorner(tempCuboid.getBiggerZID()).getBlockZ());
		}

		// -------------------------------------------------------

		return result; // static 5
	}
}
