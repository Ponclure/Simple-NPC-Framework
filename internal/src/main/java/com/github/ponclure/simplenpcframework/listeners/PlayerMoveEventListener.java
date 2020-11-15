package com.github.ponclure.simplenpcframework.listeners;

import com.github.ponclure.simplenpcframework.internal.NPCManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveEventListener extends HandleMoveBase implements Listener {

	public PlayerMoveEventListener(final NPCManager npcManager) {
		super(npcManager);
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Location from = event.getFrom();
		final Location to = event.getTo();
		// Only check movement when the player moves from one block to another. The event is called often
		// as it is also called when the pitch or yaw change. This is worth it from a performance view.
		if (to == null || from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
			handleMove(event.getPlayer());
		}
	}

}
