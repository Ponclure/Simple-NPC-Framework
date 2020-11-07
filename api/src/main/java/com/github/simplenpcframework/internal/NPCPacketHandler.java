/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.simplenpcframework.internal;

import org.bukkit.entity.Player;

import com.github.simplenpcframework.api.state.NPCAnimation;
import com.github.simplenpcframework.api.state.NPCSlot;

/**
 * @author Jitse Boonstra
 */
interface NPCPacketHandler {

    void createPackets();

    void createPackets(Player player);

    void sendShowPackets(Player player);

    void sendHidePackets(Player player);

    void sendMetadataPacket(Player player);

    void sendEquipmentPacket(Player player, NPCSlot slot, boolean auto);

    void sendAnimationPacket(Player player, NPCAnimation animation);

    default void sendEquipmentPackets(Player player) {
        for (NPCSlot slot : NPCSlot.values())
            sendEquipmentPacket(player, slot, true);
    }
}
