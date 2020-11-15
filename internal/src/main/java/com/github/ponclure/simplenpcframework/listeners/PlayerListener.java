package com.github.ponclure.simplenpcframework.listeners;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import com.github.ponclure.simplenpcframework.internal.NPCBase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Ponclure
 */
public class PlayerListener extends HandleMoveBase implements Listener {

	private final SimpleNPCFramework instance;

	public PlayerListener(final SimpleNPCFramework instance) {
		super(instance.getNpcManager());
		this.instance = instance;
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		for (final NPCBase npc : super.npcManager.getAllNPCs()) {
			npc.onLogout(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {
		// Need to auto hide the NPCs from the player, or else the system will think they can see the NPC on respawn.
		final Player player = event.getEntity();
		for (final NPCBase npc : super.npcManager.getAllNPCs()) {
			if (npc.isShown(player) && npc.getWorld().equals(player.getWorld())) {
				npc.hide(player, true);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		// If the player dies in the server spawn world, the world change event isn't called (nor is the PlayerTeleportEvent).
		final Player player = event.getPlayer();
		final Location respawn = event.getRespawnLocation();
		if (respawn.getWorld() != null && respawn.getWorld().equals(player.getWorld())) {
			// Waiting until the player is moved to the new location or else it'll mess things up.
			// I.e. if the player is at great distance from the NPC spawning, they won't be able to see it.
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!player.isOnline()) {
						this.cancel();
						return;
					}
					if (player.getLocation().equals(respawn)) {
						handleMove(player);
						this.cancel();
					}
				}
			}.runTaskTimer(instance.getPlugin(), 0L, 1L);
		}
	}

	@EventHandler
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		final World from = event.getFrom();

		// The PlayerTeleportEvent is called, and will handle visibility in the new world.
		for (final NPCBase npc : super.npcManager.getAllNPCs()) {
			if (npc.isShown(player) && npc.getWorld().equals(from)) {
				npc.hide(player, true);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		handleMove(event.getPlayer());
	}
}
