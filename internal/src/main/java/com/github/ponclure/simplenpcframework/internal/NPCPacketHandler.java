/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.internal;

import com.github.ponclure.simplenpcframework.api.state.NPCAnimation;
import com.github.ponclure.simplenpcframework.api.state.NPCSlot;
import org.bukkit.entity.Player;

/**
 * @author Ponclure
 */
interface NPCPacketHandler {

	void createPackets();

	void createPackets(Player player);

	void sendShowPackets(Player player);

	void sendHidePackets(Player player);

	void sendMetadataPacket(Player player);

	void sendEquipmentPacket(Player player, NPCSlot slot, boolean auto);

	void sendAnimationPacket(Player player, NPCAnimation animation);

	default void sendEquipmentPackets(final Player player) {
		for (final NPCSlot slot : NPCSlot.values()) {
			sendEquipmentPacket(player, slot, true);
		}
	}
}
