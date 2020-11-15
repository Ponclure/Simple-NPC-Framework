/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.nms.v1_15_R1.packets;

import com.github.ponclure.simplenpcframework.utility.Reflection;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.Location;

/**
 * @author Jitse Boonstra
 */
public class PacketPlayOutEntityHeadRotationWrapper {

	public PacketPlayOutEntityHeadRotation create(final Location location, final int entityId) {
		final PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation();

		Reflection.getField(packetPlayOutEntityHeadRotation.getClass(), "a", int.class).
						set(packetPlayOutEntityHeadRotation, entityId);
		Reflection.getField(packetPlayOutEntityHeadRotation.getClass(), "b", byte.class).set(packetPlayOutEntityHeadRotation, (byte) ((int) location.getYaw() * 256.0F / 360.0F));

		return packetPlayOutEntityHeadRotation;
	}
}
