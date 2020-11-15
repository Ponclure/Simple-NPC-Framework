package com.github.ponclure.simplenpcframework.nms.v1_16_R3.packets;

import com.github.ponclure.simplenpcframework.api.state.NPCState;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;

import java.util.Collection;

public class PacketPlayOutEntityMetadataWrapper {

	public PacketPlayOutEntityMetadata create(final Collection<NPCState> activateStates, final int entityId) {
		final DataWatcher dataWatcher = new DataWatcher(null);
		dataWatcher.register(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

		final byte masked = NPCState.getMasked(activateStates);
		// TODO: Find out why NPCState#CROUCHED doesn't work.
		dataWatcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), masked);

//        for (Player online : Bukkit.getOnlinePlayers()) {
//            DataWatcher watcher = ((CraftPlayer) online).getHandle().getDataWatcher();
//            try {
//                Field entriesField = watcher.getClass().getDeclaredField("entries");
//                entriesField.setAccessible(true);
//
//                Int2ObjectOpenHashMap<DataWatcher.Item<?>> entries = (Int2ObjectOpenHashMap<DataWatcher.Item<?>>) entriesField.get(watcher);
//                entries.forEach((integer, item) -> {
//                    if (item.b() instanceof Boolean || item.b() instanceof Byte)
//                        online.sendMessage(integer + ": " + item.b() + " type = " + item.b().getClass().toString());
//                });
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }

		return new PacketPlayOutEntityMetadata(entityId, dataWatcher, true);
	}
}
