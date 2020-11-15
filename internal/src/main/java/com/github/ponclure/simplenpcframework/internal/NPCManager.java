/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.internal;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ponclure
 */
public final class NPCManager {

	private final Set<NPCBase> NPCS = new HashSet<>();

	public Set<NPCBase> getAllNPCs() {
		return NPCS;
	}

	public Set<NPCBase> getShownToPlayer(final Player player) {
		final Set<NPCBase> set = Collections.emptySet();
		for (final NPCBase npc : getAllNPCs()) {
			if (npc.getShown().contains(player.getUniqueId())) {
				set.add(npc);
			}
		}
		return set;
	}

	public void add(final NPCBase npc) {
		NPCS.add(npc);
	}

	public void remove(final NPCBase npc) {
		NPCS.remove(npc);
	}
}
