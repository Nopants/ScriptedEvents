package me.nopants.ScriptedEvents.type;

public class SEorientation {
	public enum XOrientation {left, central, right};
	public enum YOrientation {down, central, up};
	public enum ZOrientation {front, central, back};
	public XOrientation xorientation;
	public YOrientation yorientation;
	public ZOrientation zorientation;
	
	// constructor
	public SEorientation(XOrientation newXorientation, YOrientation newYorientation, ZOrientation newZorientation) {
		this.xorientation = XOrientation.central;
		this.yorientation = YOrientation.central;
		this.zorientation = ZOrientation.central;
	}
	
	// returns a text-version of this orientation
	public String toString() { 
		return this.xorientation.toString() + ";" + this.yorientation.toString() + ";" + this.zorientation.toString();
	}
}
