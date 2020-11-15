package com.github.ponclure.simplenpcframework.listeners;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import com.github.ponclure.simplenpcframework.api.events.NPCInteractEvent;
import com.github.ponclure.simplenpcframework.internal.NPCBase;
import com.github.ponclure.simplenpcframework.utility.Reflection;
import com.github.ponclure.simplenpcframework.utility.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Ponclure
 */
public class PacketListener {

	// Classes:
	private final Class<?> packetPlayInUseEntityClazz = Reflection.getMinecraftClass("PacketPlayInUseEntity");

	// Fields:
	private final Reflection.FieldAccessor<Integer> entityIdField = Reflection.getField(packetPlayInUseEntityClazz, "a", int.class);
	private final Reflection.FieldAccessor<?> actionField = Reflection.getField(packetPlayInUseEntityClazz, "action", Object.class);

	// Prevent players from clicking at very high speeds.
	private final Set<UUID> delay = new HashSet<>();

	private final SimpleNPCFramework instance;
	private Plugin plugin;

	public PacketListener(final SimpleNPCFramework instance) {
		this.instance = instance;
	}

	public void start(final SimpleNPCFramework instance) {
		this.plugin = instance.getPlugin();

		new TinyProtocol(instance) {

			@Override
			public Object onPacketInAsync(final Player player, final Object packet) {
				return handleInteractPacket(player, packet) ? super.onPacketInAsync(player, packet) : null;
			}
		};
	}

	private boolean handleInteractPacket(final Player player, final Object packet) {
		if (!packetPlayInUseEntityClazz.isInstance(packet) || player == null) {
			return true; // We aren't handling the packet.
		}

		NPCBase npc = null;
		final int packetEntityId = entityIdField.get(packet);

		// Not using streams here is an intentional choice.
		// Packet listeners is one of the few places where it is important to write optimized code.
		// Lambdas (And the stream api) create a massive amount of objects, especially if it isn't a static lambda.
		// So, we're avoiding them here.
		// ~ Kneesnap, 9 / 20 / 2019.

		for (final NPCBase testNPC : instance.getNpcManager().getAllNPCs()) {
			if (testNPC.isCreated() && testNPC.isShown(player) && testNPC.getEntityId() == packetEntityId) {
				npc = testNPC;
				break;
			}
		}

		if (npc == null) {
			// Default player, not doing magic with the packet.
			return true;
		}

		if (delay.contains(player.getUniqueId())) { // There is an active delay.
			return false;
		}

		final NPCInteractEvent.ClickType clickType = actionField.get(packet).toString().equals("ATTACK") ? NPCInteractEvent.ClickType.LEFT_CLICK : NPCInteractEvent.ClickType.RIGHT_CLICK;

		delay.add(player.getUniqueId());
		Bukkit.getScheduler().runTask(plugin, new TaskCallNpcInteractEvent(new NPCInteractEvent(player, clickType, npc), this));
		return false;
	}

	// This would be a non-static lambda, and its usage matters, so we'll make it a full class.
	private static final class TaskCallNpcInteractEvent implements Runnable {

		private final NPCInteractEvent eventToCall;
		private final PacketListener listener;

		private static final Location playerLocation = new Location(null, 0, 0, 0);

		TaskCallNpcInteractEvent(final NPCInteractEvent eventToCall, final PacketListener listener) {
			this.eventToCall = eventToCall;
			this.listener = listener;
		}

		@Override
		public void run() {
			final Player player = eventToCall.getWhoClicked();
			this.listener.delay.remove(player.getUniqueId()); // Remove the NPC from the interact cooldown.
			if (!player.getWorld().equals(eventToCall.getNPC().getWorld())) {
				return; // If the NPC and player are not in the same world, abort!
			}

			final double distance = player.getLocation(playerLocation).distanceSquared(eventToCall.getNPC().getLocation());
			if (distance <= 64) // Only handle the interaction if the player is within interaction range. This way, hacked clients can't interact with NPCs that they shouldn't be able to interact with.
			{
				Bukkit.getPluginManager().callEvent(this.eventToCall);
			}
		}
	}
}
