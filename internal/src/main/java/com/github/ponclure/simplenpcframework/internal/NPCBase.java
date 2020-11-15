/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.internal;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import com.github.ponclure.simplenpcframework.api.NPC;
import com.github.ponclure.simplenpcframework.api.events.NPCHideEvent;
import com.github.ponclure.simplenpcframework.api.events.NPCShowEvent;
import com.github.ponclure.simplenpcframework.api.skin.Skin;
import com.github.ponclure.simplenpcframework.api.state.NPCAnimation;
import com.github.ponclure.simplenpcframework.api.state.NPCSlot;
import com.github.ponclure.simplenpcframework.api.state.NPCState;
import com.github.ponclure.simplenpcframework.hologram.Hologram;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class NPCBase implements NPC, NPCPacketHandler {

	protected final int entityId;
	protected final Set<UUID> hasTeamRegistered = new HashSet<>();
	protected final Set<NPCState> activeStates = EnumSet.noneOf(NPCState.class);

	private final Set<UUID> shown = new HashSet<>();
	private final Set<UUID> autoHidden = new HashSet<>();

	protected double cosFOV = Math.cos(Math.toRadians(60));
	// 12/4/20, JMB: Changed the UUID in order to enable LabyMod Emotes:
	// This gives a format similar to: 528086a2-4f5f-2ec2-0000-000000000000
	protected final UUID uuid = new UUID(ThreadLocalRandom.current().nextLong(), 0);
	protected final String name = uuid.toString().replace("-", "").substring(0, 10);
	protected final GameProfile gameProfile = new GameProfile(uuid, name);
	protected boolean created = false;

	protected final SimpleNPCFramework instance;
	protected List<String> text;
	protected Location location;
	protected Skin skin;

	// protected Hologram hologram;

	protected final Map<NPCSlot, ItemStack> items = new EnumMap<>(NPCSlot.class);

	// Storage for per-player text;
	protected final Map<UUID, List<String>> uniqueText = new HashMap<>();
	protected final Map<UUID, Hologram> textDisplayHolograms = new HashMap<>();

	public NPCBase(final SimpleNPCFramework instance, final List<String> text) {
		this.instance = instance;
		this.text = text == null ? Collections.emptyList() : text;
		this.entityId = Integer.MAX_VALUE - instance.getNpcManager().getAllNPCs().size();

		instance.getNpcManager().add(this);
	}

	public SimpleNPCFramework getInstance() {
		return instance;
	}

	@Override
	public Hologram getPlayerHologram(final Player player) {
		Validate.notNull(player, "Player cannot be null.");
		return textDisplayHolograms.getOrDefault(player.getUniqueId(), null);
	}

	@Override
	public NPC removePlayerLines(final Player targetPlayer) {
		Validate.notNull(targetPlayer, "Player cannot be null.");
		setPlayerLines(null, targetPlayer);
		return this;
	}

	@Override
	public NPC removePlayerLines(final Player targetPlayer, final boolean update) {
		Validate.notNull(targetPlayer, "Player cannot be null.");
		setPlayerLines(null, targetPlayer, update);
		return this;
	}

	@Override
	public NPC setPlayerLines(final List<String> uniqueLines, final Player targetPlayer) {
		Validate.notNull(targetPlayer, "Player cannot be null.");

		if (uniqueLines == null) {
			uniqueText.remove(targetPlayer.getUniqueId());
		} else {
			uniqueText.put(targetPlayer.getUniqueId(), uniqueLines);
		}
		return this;
	}

	@Override
	public NPC setPlayerLines(List<String> uniqueLines, final Player targetPlayer, final boolean update) {
		Validate.notNull(targetPlayer, "Player cannot be null.");

		final List<String> originalLines = getPlayerLines(targetPlayer);
		setPlayerLines(uniqueLines, targetPlayer);

		if (update) {
			uniqueLines = getPlayerLines(targetPlayer); // retrieve the player lines from this function, incase it's been removed.

			if (originalLines.size() != uniqueLines.size()) { // recreate the entire hologram
				final Hologram originalhologram = getPlayerHologram(targetPlayer);
				originalhologram.hide(targetPlayer); // essentially destroy the hologram
				textDisplayHolograms.remove(targetPlayer.getUniqueId()); // remove the old obj
			}

			if (isShown(targetPlayer)) { // only show hologram if the player is in range
				final Hologram hologram = getPlayerHologram(targetPlayer);
				final List<Object> updatePackets = hologram.getUpdatePackets(getPlayerLines(targetPlayer));
				hologram.update(targetPlayer, updatePackets);
			}
		}
		return this;
	}

	@Override
	public List<String> getPlayerLines(final Player targetPlayer) {
		Validate.notNull(targetPlayer, "Player cannot be null.");
		return uniqueText.getOrDefault(targetPlayer.getUniqueId(), text);
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public NPC setSkin(final Skin skin) {
		this.skin = skin;

		gameProfile.getProperties().get("textures").clear();
		if (skin != null) {
			gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
		}

		return this;
	}

	@Override
	public void destroy() {
		instance.getNpcManager().remove(this);

		// Destroy NPC for every player that is still seeing it.
		for (final UUID uuid : shown) {
			if (autoHidden.contains(uuid)) {
				continue;
			}
			final Player plyr = Bukkit.getPlayer(uuid); // destroy the per player holograms
			if (plyr != null) {
				getPlayerHologram(plyr).hide(plyr);
				hide(plyr, true);
			}
		}
	}

	public void disableFOV() {
		this.cosFOV = 0;
	}

	public void setFOV(final double fov) {
		this.cosFOV = Math.cos(Math.toRadians(fov));
	}

	public Set<UUID> getShown() {
		return shown;
	}

	public Set<UUID> getAutoHidden() {
		return autoHidden;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public World getWorld() {
		return location != null ? location.getWorld() : null;
	}

	public int getEntityId() {
		return entityId;
	}

	@Override
	public boolean isShown(final Player player) {
		Objects.requireNonNull(player, "Player object cannot be null");
		return shown.contains(player.getUniqueId()) && !autoHidden.contains(player.getUniqueId());
	}

	@Override
	public NPC setLocation(final Location location) {
		this.location = location;
		return this;
	}

	@Override
	public NPC create() {
		createPackets();
		this.created = true;
		return this;
	}

	@Override
	public boolean isCreated() {
		return created;
	}

	public void onLogout(final Player player) {
		getAutoHidden().remove(player.getUniqueId());
		getShown().remove(player.getUniqueId()); // Don't need to use NPC#hide since the entity is not registered in the
		// NMS server.
		hasTeamRegistered.remove(player.getUniqueId());
	}

	public boolean inRangeOf(final Player player) {
		if (player == null) {
			return false;
		}
		if (!player.getWorld().equals(location.getWorld())) {
			// No need to continue our checks, they are in different worlds.
			return false;
		}

		// If Bukkit doesn't track the NPC entity anymore, bypass the hiding distance
		// variable.
		// This will cause issues otherwise (e.g. custom skin disappearing).
		final double hideDistance = instance.getAutoHideDistance();
		final double distanceSquared = player.getLocation().distanceSquared(location);
		final double bukkitRange = Bukkit.getViewDistance() << 4;

		return distanceSquared <= hideDistance * hideDistance && distanceSquared <= bukkitRange * bukkitRange;
	}

	public boolean inViewOf(final Player player) {
		final Vector dir = location.toVector().subtract(player.getEyeLocation().toVector()).normalize();
		return dir.dot(player.getEyeLocation().getDirection()) >= cosFOV;
	}

	@Override
	public void show(final Player player) {
		show(player, false);
	}

	public void show(final Player player, final boolean auto) {
		final NPCShowEvent event = new NPCShowEvent(this, player, auto);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		if (isShown(player)) {
			throw new IllegalArgumentException("NPC is already shown to player");
		}

		if (auto) {
			sendShowPackets(player);
			sendMetadataPacket(player);
			sendEquipmentPackets(player);

			// NPC is auto-shown now, we can remove the UUID from the set.
			autoHidden.remove(player.getUniqueId());
		} else {
			// Adding the UUID to the set.
			shown.add(player.getUniqueId());

			if (inRangeOf(player) && inViewOf(player)) {
				// The player can see the NPC and is in range, send the packets.
				sendShowPackets(player);
				sendMetadataPacket(player);
				sendEquipmentPackets(player);
			} else {
				// We'll wait until we can show the NPC to the player via auto-show.
				autoHidden.add(player.getUniqueId());
			}
		}
	}

	@Override
	public void hide(final Player player) {
		hide(player, false);
	}

	public void hide(final Player player, final boolean auto) {
		final NPCHideEvent event = new NPCHideEvent(this, player, auto);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		if (!shown.contains(player.getUniqueId())) {
			throw new IllegalArgumentException("NPC cannot be hidden from player before calling NPC#show first");
		}

		if (auto) {
			if (autoHidden.contains(player.getUniqueId())) {
				throw new IllegalStateException("NPC cannot be auto-hidden twice");
			}

			sendHidePackets(player);

			// NPC is auto-hidden now, we will add the UUID to the set.
			autoHidden.add(player.getUniqueId());
		} else {
			// Removing the UUID from the set.
			shown.remove(player.getUniqueId());

			if (inRangeOf(player)) {
				// The player is in range of the NPC, send the packets.
				sendHidePackets(player);
			} else {
				// We don't have to send any packets, just don't let it auto-show again by
				// removing the UUID from the set.
				autoHidden.remove(player.getUniqueId());
			}
		}
	}

	@Override
	public boolean getState(final NPCState state) {
		return activeStates.contains(state);
	}

	@Override
	public NPC toggleState(final NPCState state) {
		if (activeStates.contains(state)) {
			activeStates.remove(state);
		} else {
			activeStates.add(state);
		}

		// Send a new metadata packet to all players that can see the NPC.
		for (final UUID shownUuid : shown) {
			final Player player = Bukkit.getPlayer(shownUuid);
			if (player != null && isShown(player)) {
				sendMetadataPacket(player);
			}
		}
		return this;
	}

	@Override
	public void playAnimation(final NPCAnimation animation) {
		for (final UUID shownUuid : shown) {
			final Player player = Bukkit.getPlayer(shownUuid);
			if (player != null && isShown(player)) {
				sendAnimationPacket(player, animation);
			}
		}
	}

	@Override
	public ItemStack getItem(final NPCSlot slot) {
		Objects.requireNonNull(slot, "Slot cannot be null");

		return items.get(slot);
	}

	@Override
	public NPC setItem(final NPCSlot slot, final ItemStack item) {
		Objects.requireNonNull(slot, "Slot cannot be null");

		items.put(slot, item);

		for (final UUID shownUuid : shown) {
			final Player player = Bukkit.getPlayer(shownUuid);
			if (player != null && isShown(player)) {
				sendEquipmentPacket(player, slot, false);
			}
		}
		return this;
	}

	@Override
	public NPC setText(final List<String> text) {
		uniqueText.clear();

		for (final UUID shownUuid : shown) {
			final Player player = Bukkit.getPlayer(shownUuid);
			if (player != null && isShown(player)) {
				final Hologram originalHologram = getPlayerHologram(player);
				originalHologram.hide(player); // essentially destroy the hologram
				textDisplayHolograms.remove(player.getUniqueId()); // remove the old obj
				final Hologram hologram = getPlayerHologram(player); // let it regenerate
				final List<Object> updatePackets = hologram.getUpdatePackets(getPlayerLines(player));
				hologram.update(player, updatePackets);
			}
		}

		this.text = text;
		return this;
	}

	@Override
	public List<String> getText() {
		return text;
	}
}
