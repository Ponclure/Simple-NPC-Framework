/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.nms.v1_16_R3.packets;

import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;

import com.github.ponclure.simplenpcframework.utility.Reflection;

import java.util.UUID;

/**
 * @author Jitse Boonstra
 */
public class PacketPlayOutNamedEntitySpawnWrapper {

    public PacketPlayOutNamedEntitySpawn create(UUID uuid, Location location, int entityId) {
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "a", int.class)
                .set(packetPlayOutNamedEntitySpawn, entityId);
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "b", UUID.class)
                .set(packetPlayOutNamedEntitySpawn, uuid);
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "c", double.class)
                .set(packetPlayOutNamedEntitySpawn, location.getX());
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "d", double.class)
                .set(packetPlayOutNamedEntitySpawn, location.getY());
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "e", double.class)
                .set(packetPlayOutNamedEntitySpawn, location.getZ());
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "f", byte.class)
                .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "g", byte.class)
                .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));

        return packetPlayOutNamedEntitySpawn;
    }
}
