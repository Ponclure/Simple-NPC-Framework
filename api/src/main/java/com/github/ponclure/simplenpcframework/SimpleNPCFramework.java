package com.github.ponclure.simplenpcframework;

import com.github.ponclure.simplenpcframework.listeners.ChunkListener;
import com.github.ponclure.simplenpcframework.listeners.PacketListener;
import com.github.ponclure.simplenpcframework.listeners.PeriodicMoveListener;
import com.github.ponclure.simplenpcframework.listeners.PlayerListener;
import com.github.ponclure.simplenpcframework.listeners.PlayerMoveEventListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ponclure.simplenpcframework.api.NPC;
import com.github.ponclure.simplenpcframework.api.utilities.Logger;

import java.util.List;

public final class SimpleNPCFramework {

	private final JavaPlugin plugin;
	private final Logger logger;
	private final Class<?> npcClass;

	private double autoHideDistance = 50.0;

	private SimpleNPCFramework(JavaPlugin plugin, SimpleNPCFrameworkOptions.MovementHandling moveHandling) {
		this.plugin = plugin;
		this.logger = new Logger("SimpleNPCFramework");

		String versionName = plugin.getServer().getClass().getName().split("\\.")[3];

		Class<?> npcClass = null;

		try {
			npcClass = Class.forName("com.github.ponclure.simplenpcframework.nms." + versionName + ".NPC_" + versionName);
		} catch (ClassNotFoundException exception) {
			// Version not supported, error below.
		}

		this.npcClass = npcClass;

		if (npcClass == null) {
			logger.severe("Failed to initiate. Your server's version (" + versionName + ") is not supported");
			return;
		}

		PluginManager pluginManager = plugin.getServer().getPluginManager();

		pluginManager.registerEvents(new PlayerListener(this), plugin);
		pluginManager.registerEvents(new ChunkListener(this), plugin);

		if (moveHandling.usePme) {
			pluginManager.registerEvents(new PlayerMoveEventListener(), plugin);
		} else {
			pluginManager.registerEvents(new PeriodicMoveListener(this, moveHandling.updateInterval), plugin);
		}

		// Boot the according packet listener.
		new PacketListener().start(this);

		logger.info("Enabled for Minecraft " + versionName);
	}

	public SimpleNPCFramework(JavaPlugin plugin) {
		this(plugin, SimpleNPCFrameworkOptions.MovementHandling.playerMoveEvent());
	}

	public SimpleNPCFramework(JavaPlugin plugin, SimpleNPCFrameworkOptions options) {
		this(plugin, options.moveHandling);
	}

	/**
	 * @return The JavaPlugin instance.
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Set a new value for the auto-hide distance. A recommended value (and default)
	 * is 50 blocks.
	 *
	 * @param autoHideDistance The new value.
	 */
	public void setAutoHideDistance(double autoHideDistance) {
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
	public NPC createNPC(List<String> text) {
		try {
			return (NPC) npcClass.getConstructors()[0].newInstance(this, text);
		} catch (Exception exception) {
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
