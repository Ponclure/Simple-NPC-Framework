package com.github.ponclure.simplenpcframework;

import com.github.ponclure.simplenpcframework.api.NPC;
import com.github.ponclure.simplenpcframework.api.utilities.Logger;
import com.github.ponclure.simplenpcframework.internal.NPCManager;
import com.github.ponclure.simplenpcframework.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SimpleNPCFramework {

	private final NPCManager npcManager = new NPCManager();
	private final JavaPlugin plugin;
	private final Logger logger;
	private final Class<?> npcClass;

	private double autoHideDistance = 50.0;

	private SimpleNPCFramework(final JavaPlugin plugin, final SimpleNPCFrameworkOptions.MovementHandling moveHandling) {
		this.plugin = plugin;
		this.logger = new Logger("SimpleNPCFramework");

		final String versionName = Bukkit.getServer().getClass().getName().split("\\.")[3];

		try {
			npcClass = Class.forName("com.github.ponclure.simplenpcframework.nms." + versionName + ".NPC_" + versionName);
		} catch (final ClassNotFoundException exception) {
			throw new RuntimeException("Failed to initiate. Your server's version (" + versionName + ") is not supported", exception);
		}

		final PluginManager pluginManager = plugin.getServer().getPluginManager();

		pluginManager.registerEvents(new PlayerListener(this), plugin);
		pluginManager.registerEvents(new ChunkListener(this), plugin);

		if (moveHandling.usePme) {
			pluginManager.registerEvents(new PlayerMoveEventListener(this.npcManager), plugin);
		} else {
			pluginManager.registerEvents(new PeriodicMoveListener(this, moveHandling.updateInterval), plugin);
		}

		// Boot the according packet listener.
		new PacketListener(this).start(this);

		logger.info("Enabled for Minecraft " + versionName);
	}

	public SimpleNPCFramework(final JavaPlugin plugin) {
		this(plugin, SimpleNPCFrameworkOptions.MovementHandling.playerMoveEvent());
	}

	public SimpleNPCFramework(final JavaPlugin plugin, final SimpleNPCFrameworkOptions options) {
		this(plugin, options.moveHandling);
	}

	/**
	 * @return The JavaPlugin instance.
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * @return The NPCManager instance.
	 */
	public NPCManager getNpcManager() {
		return npcManager;
	}

	/**
	 * Set a new value for the auto-hide distance. A recommended value (and default)
	 * is 50 blocks.
	 *
	 * @param autoHideDistance The new value.
	 */
	public void setAutoHideDistance(final double autoHideDistance) {
		this.autoHideDistance = autoHideDistance;
	}

	/**
	 * @return The auto-hide distance.
	 */
	public double getAutoHideDistance() {
		return autoHideDistance;
	}

	/**
	 * @return The logger SimpleNPCFramework uses.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Create a new non-player character (NPC).
	 *
	 * @param text The text you want to sendShowPackets above the NPC (null = no
	 *             text).
	 * @return The NPC object you may use to sendShowPackets it to players.
	 */
	public NPC createNPC(final List<String> text) {
		try {
			return (NPC) npcClass.getConstructor(SimpleNPCFramework.class, List.class).newInstance(this, text);
		} catch (final ReflectiveOperationException exception) {
			logger.warning("Failed to create NPC. Please report the following stacktrace message", exception);
		}

		return null;
	}

	/**
	 * Create a new non-player character (NPC).
	 *
	 * @return The NPC object you may use to sendShowPackets it to players.
	 */
	public NPC createNPC() {
		return createNPC(null);
	}
}
