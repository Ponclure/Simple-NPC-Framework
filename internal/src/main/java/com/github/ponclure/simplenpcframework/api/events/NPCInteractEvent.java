/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.api.events;

import com.github.ponclure.simplenpcframework.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Ponclure
 */
public class NPCInteractEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private final Player player;
	private final ClickType clickType;
	private final NPC npc;

	public NPCInteractEvent(final Player player, final ClickType clickType, final NPC npc) {
		this.player = player;
		this.clickType = clickType;
		this.npc = npc;
	}

	public Player getWhoClicked() {
		return this.player;
	}

	public ClickType getClickType() {
		return this.clickType;
	}

	public NPC getNPC() {
		return this.npc;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public enum ClickType {
		LEFT_CLICK,
		RIGHT_CLICK
	}
}
