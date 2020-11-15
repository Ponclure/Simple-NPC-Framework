package com.github.ponclure.simplenpcframework.listeners;

import com.github.ponclure.simplenpcframework.internal.NPCBase;
import com.github.ponclure.simplenpcframework.internal.NPCManager;
import org.bukkit.entity.Player;

public class HandleMoveBase {

	protected final NPCManager npcManager;

	public HandleMoveBase(final NPCManager npcManager) {
		this.npcManager = npcManager;
	}

	void handleMove(final Player player) {
		for (final NPCBase npc : npcManager.getAllNPCs()) {
			if (!npc.getShown().contains(player.getUniqueId())) {
				continue; // NPC was never supposed to be shown to the player.
			}

			if (!npc.isShown(player) && npc.inRangeOf(player) && npc.inViewOf(player)) {
				// The player is in range and can see the NPC, auto-show it.
				npc.show(player, true);
			} else if (npc.isShown(player) && !npc.inRangeOf(player)) {
				// The player is not in range of the NPC anymore, auto-hide it.
				npc.hide(player, true);
			}
		}
	}

}
