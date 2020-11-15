package com.github.ponclure.simplenpcframework.nms.v1_15_R1.packets;

import com.github.ponclure.simplenpcframework.api.state.NPCState;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;

import java.util.Collection;

public class PacketPlayOutEntityMetadataWrapper {

	public PacketPlayOutEntityMetadata create(final Collection<NPCState> activateStates, final int entityId) {
		final DataWatcher dataWatcher = new DataWatcher(null);
		dataWatcher.register(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

		final byte masked = NPCState.getMasked(activateStates);
		dataWatcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), masked);

		return new PacketPlayOutEntityMetadata(entityId, dataWatcher, true);
	}
}
