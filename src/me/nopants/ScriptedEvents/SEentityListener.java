package me.nopants.ScriptedEvents;

import java.util.ArrayList;
import java.util.Map;

import me.nopants.ScriptedEvents.type.SEentitySet;
import me.nopants.ScriptedEvents.type.entities.SEtrigger;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SEentityListener extends EntityListener{

	private ScriptedEvents plugin;
	public ArrayList<String> lastDamagePlayer = new ArrayList<String>();
	public ArrayList<String> lastDamageType = new ArrayList<String>();
	public String beforedamage = "";
	
	public SEentityListener(ScriptedEvents scriptedEvents) {
		this.plugin = scriptedEvents;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof HumanEntity){
			HumanEntity human = (HumanEntity)event.getEntity();
			if(human instanceof Player){
				Player player = (Player) human;
				String damageType = null;
				
				if (player.getLastDamageCause()!=null)
					damageType = player.getLastDamageCause().getCause().toString();
				else
					damageType = DamageCause.CUSTOM.toString();
				
				if (lastDamagePlayer.contains(player.getName()) && player.getLastDamageCause()!=null) {
					damageType = lastDamageType.get(lastDamagePlayer.indexOf(player.getName()));
				}
				
				//---[ onDeath]-----------------------------------------------------------//
				// get the triggers matching to the event onDeath
				Map<String, SEtrigger> triggerList = plugin.triggerManager.getRelevantTriggers(new SEentitySet(SEtrigger.triggerEvent.onDeath));
				// release the triggers
				plugin.triggerManager.releaseTriggerList(triggerList, new SEentitySet(event, damageType));
				//----------------------------------------------------------------------------//
			}
		}
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			//player.sendMessage(event.getType().toString());
			lastDamageDone(player, event);
		}
	}

	public void lastDamageDone(Player player, EntityDamageEvent event) {
		String lastdamage = event.getCause().name();
		if (event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent mobevent = (EntityDamageByProjectileEvent) event;
			Entity attacker = mobevent.getDamager();
			if (attacker instanceof Ghast) {
				lastdamage = "GHAST";
			}
			else if (attacker instanceof Monster) {
				lastdamage = "SKELETON";
			}
			else if (attacker instanceof Player) {
				Player pvper = (Player) attacker;
				String usingitem = pvper.getItemInHand().getType().name();
				if (usingitem == "AIR") {
					usingitem = "FISTS";
				}
				lastdamage = "PVP";
			}
		} // Projectile
		
		else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) event;
			Entity attacker = mobevent.getDamager();

			if (attacker.toString().toLowerCase().matches("craftslime")) {
				lastdamage = "SLIME";
			}
			else if (attacker instanceof Wolf) {
				lastdamage = "WOLF";
			}
			else if (attacker instanceof Monster) {
				Monster mob = (Monster) attacker;

				if (mob instanceof PigZombie) {
					lastdamage = "PIGZOMBIE";
				}
				else if (mob instanceof Zombie) {
					lastdamage = "ZOMBIE";
				}
				else if (mob instanceof Creeper) {
					lastdamage = "CREEPER";
				}
				else if (mob instanceof Spider) {
					lastdamage = "SPIDER";
				}
				else if (mob instanceof Skeleton) {
					lastdamage = "SKELETON";
				}
				else if (mob instanceof Ghast) {
					lastdamage = "GHAST";
				}
				else if (mob instanceof Slime) {
					lastdamage = "SLIME";
				}
			}
			else if (attacker instanceof Player) {
				Player pvper = (Player) attacker;
				String usingitem = pvper.getItemInHand().getType().name();
				if (usingitem == "AIR") {
					usingitem = "FISTS";
				}
				lastdamage = "PVP";
			}
		} // Close Combat

		if ((beforedamage.equals("GHAST") && lastdamage.equals("BLOCK_EXPLOSION")) ||(beforedamage.equals("GHAST") && lastdamage.equals("GHAST"))) {
			lastdamage = "GHAST";
		}

		if (!lastDamagePlayer.contains(player.getName())) {
			lastDamagePlayer.add(player.getName());
			lastDamageType.add(event.getCause().name());
		} else {
			lastDamageType.set(lastDamagePlayer.indexOf(player.getName()), lastdamage);
		}

		beforedamage = lastdamage;
	}
	
}
