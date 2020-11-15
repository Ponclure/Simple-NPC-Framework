/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.api.events;

import com.github.ponclure.simplenpcframework.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Ponclure
 */
public class NPCHideEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private boolean cancelled = false;

	private final NPC npc;
	private final Player player;
	private final boolean automatic;

	public NPCHideEvent(final NPC npc, final Player player, final boolean automatic) {
		this.npc = npc;
		this.player = player;
		this.automatic = automatic;
	}

	public NPC getNPC() {
		return npc;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * @return Value on whether the hiding was triggered automatically.
	 */
	public boolean isAutomatic() {
		return automatic;
	}

	@Override
	public void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}

