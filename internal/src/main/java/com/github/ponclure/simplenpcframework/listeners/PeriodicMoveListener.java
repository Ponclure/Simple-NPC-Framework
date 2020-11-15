package com.github.ponclure.simplenpcframework.listeners;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PeriodicMoveListener extends HandleMoveBase implements Listener {

	private final SimpleNPCFramework instance;
	private final long updateInterval;

	private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

	public PeriodicMoveListener(final SimpleNPCFramework instance, final long updateInterval) {
		super(instance.getNpcManager());
		this.instance = instance;
		this.updateInterval = updateInterval;
	}

	private void startTask(final UUID uuid) {
		// purposefully using UUIDs and not holding player references
		tasks.put(uuid, Bukkit.getScheduler().runTaskTimer(instance.getPlugin(), () -> {
			final Player player = Bukkit.getPlayer(uuid);
			if (player != null) { // safety check
				handleMove(player);
			}
		}, 1L, updateInterval));
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		startTask(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final BukkitTask task = tasks.remove(event.getPlayer().getUniqueId());
		if (task != null) {
			task.cancel();
		}
	}

}
