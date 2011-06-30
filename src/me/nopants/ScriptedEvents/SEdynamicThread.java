package me.nopants.ScriptedEvents;

import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class SEdynamicThread extends Thread {
    private ScriptedEvents plugin;
    private int delay;
    private int offset;
    //private Map<Integer, String> actionList = new HashMap<Integer, String>();
    private SEscript script;
    private SEentitySet entitySet;
    
    protected static final Logger log = Logger.getLogger("Minecraft");

    // the constructor gets all the needed information passed
    public SEdynamicThread(int newOffset, SEscript newScript, SEentitySet newEntitySet, int newDelay, ScriptedEvents newPlugin) {
        this.plugin = newPlugin;
        this.delay = newDelay;
        this.offset = newOffset;
        this.script = newScript;
        //this.actionList = newActionList;
        this.entitySet = newEntitySet;
    }

    // the method run() is called if the thread is started
    public void run() {
    	try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
		}
		if (script!=null)
			Bukkit.getServer().getScheduler().callSyncMethod(plugin, plugin.triggerManager.executeScript(script, entitySet, offset));
    }
}

