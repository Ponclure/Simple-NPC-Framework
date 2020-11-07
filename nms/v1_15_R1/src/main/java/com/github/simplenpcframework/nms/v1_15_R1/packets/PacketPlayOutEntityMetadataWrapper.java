package com.github.simplenpcframework.nms.v1_15_R1.packets;

import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;

import java.util.Collection;

import com.github.simplenpcframework.api.state.NPCState;

public class PacketPlayOutEntityMetadataWrapper {

    public PacketPlayOutEntityMetadata create(Collection<NPCState> activateStates, int entityId) {
        DataWatcher dataWatcher = new DataWatcher(null);
        dataWatcher.register(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

        byte masked = NPCState.getMasked(activateStates);
        dataWatcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), masked);

        return new PacketPlayOutEntityMetadata(entityId, dataWatcher, true);
    }
}
