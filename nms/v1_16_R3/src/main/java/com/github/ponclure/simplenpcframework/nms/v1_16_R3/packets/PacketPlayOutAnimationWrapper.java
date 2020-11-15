package com.github.ponclure.simplenpcframework.nms.v1_16_R3.packets;

import com.github.ponclure.simplenpcframework.api.state.NPCAnimation;
import com.github.ponclure.simplenpcframework.utility.Reflection;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;

public class PacketPlayOutAnimationWrapper {

	public PacketPlayOutAnimation create(final NPCAnimation npcAnimation, final int entityId) {
		final PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation();

		Reflection.getField(packetPlayOutAnimation.getClass(), "a", int.class).set(packetPlayOutAnimation, entityId);
		Reflection.getField(packetPlayOutAnimation.getClass(), "b", int.class).set(packetPlayOutAnimation, npcAnimation.getId());

		return packetPlayOutAnimation;
	}

}
