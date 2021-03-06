/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.nms.v1_16_R3.packets;

import com.github.ponclure.simplenpcframework.utility.Reflection;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;

import java.util.Collections;
import java.util.List;

/**
 * @author Jitse Boonstra
 */
public class PacketPlayOutPlayerInfoWrapper {

	private final Class<?> packetPlayOutPlayerInfoClazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
	private final Class<?> playerInfoDataClazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
	private final Reflection.ConstructorInvoker playerInfoDataConstructor = Reflection.getConstructor(playerInfoDataClazz, packetPlayOutPlayerInfoClazz, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class);

	public PacketPlayOutPlayerInfo create(final PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, final GameProfile gameProfile, final String name) {
		final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
		Reflection.getField(packetPlayOutPlayerInfo.getClass(), "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class).set(packetPlayOutPlayerInfo, action);

		final Object playerInfoData = playerInfoDataConstructor.invoke(packetPlayOutPlayerInfo, gameProfile, 1, EnumGamemode.NOT_SET, IChatBaseComponent.ChatSerializer.b("{\"text\":\"[NPC] " + name + "\",\"color\":\"dark_gray\"}"));

		@SuppressWarnings("rawtypes") final Reflection.FieldAccessor<List> fieldAccessor = Reflection.getField(packetPlayOutPlayerInfo.getClass(), "b", List.class);
		fieldAccessor.set(packetPlayOutPlayerInfo, Collections.singletonList(playerInfoData));

		return packetPlayOutPlayerInfo;
	}
}
