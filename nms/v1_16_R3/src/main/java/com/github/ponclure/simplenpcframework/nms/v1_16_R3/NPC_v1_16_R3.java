package com.github.ponclure.simplenpcframework.nms.v1_16_R3;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import com.github.ponclure.simplenpcframework.api.skin.Skin;
import com.github.ponclure.simplenpcframework.api.state.NPCAnimation;
import com.github.ponclure.simplenpcframework.api.state.NPCSlot;
import com.github.ponclure.simplenpcframework.hologram.Hologram;
import com.github.ponclure.simplenpcframework.internal.MinecraftVersion;
import com.github.ponclure.simplenpcframework.internal.NPCBase;
import com.github.ponclure.simplenpcframework.nms.v1_16_R3.packets.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * @author Jitse Boonstra
 */
public class NPC_v1_16_R3 extends NPCBase {

	private PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn;
	private PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeamRegister;
	private PacketPlayOutPlayerInfo packetPlayOutPlayerInfoAdd, packetPlayOutPlayerInfoRemove;
	private PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation;
	private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;
	private PacketPlayOutAnimation packetPlayOutAnimation;

	public NPC_v1_16_R3(final SimpleNPCFramework instance, final List<String> lines) {
		super(instance, lines);
	}

	@Override
	public Hologram getPlayerHologram(final Player player) {
		Hologram holo = super.getPlayerHologram(player);
		if (holo == null) {
			holo = new Hologram(MinecraftVersion.v1_16_R3, location.clone().add(0, 0.5, 0), getPlayerLines(player));
		}
		super.textDisplayHolograms.put(player.getUniqueId(), holo);
		return holo;
	}

	@Override
	public void createPackets() {
		Bukkit.getOnlinePlayers().forEach(this::createPackets);
	}

	@Override
	public void createPackets(final Player player) {

		final PacketPlayOutPlayerInfoWrapper packetPlayOutPlayerInfoWrapper = new PacketPlayOutPlayerInfoWrapper();

		// Packets for spawning the NPC:
		this.packetPlayOutScoreboardTeamRegister = new PacketPlayOutScoreboardTeamWrapper().createRegisterTeam(name); // First packet to send.

		this.packetPlayOutPlayerInfoAdd = packetPlayOutPlayerInfoWrapper.create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, gameProfile, name); // Second packet to send.

		this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawnWrapper().create(uuid, location, entityId); // Third packet to send.

		this.packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotationWrapper().create(location, entityId); // Fourth packet to send.

		this.packetPlayOutPlayerInfoRemove = packetPlayOutPlayerInfoWrapper.create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, gameProfile, name); // Fifth packet to send (delayed).

		// Packet for destroying the NPC:
		this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityId); // First packet to send.
	}

	@Override
	public void sendShowPackets(final Player player) {
		final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		if (hasTeamRegistered.add(player.getUniqueId())) {
			playerConnection.sendPacket(packetPlayOutScoreboardTeamRegister);
		}
		playerConnection.sendPacket(packetPlayOutPlayerInfoAdd);
		playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
		playerConnection.sendPacket(packetPlayOutEntityHeadRotation);
		sendMetadataPacket(player);

		getPlayerHologram(player).show(player);

		// Removing the player info after 10 seconds.
		Bukkit.getScheduler().runTaskLater(instance.getPlugin(), () -> playerConnection.sendPacket(packetPlayOutPlayerInfoRemove), 200);
	}

	@Override
	public void sendHidePackets(final Player player) {
		final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		playerConnection.sendPacket(packetPlayOutEntityDestroy);
		playerConnection.sendPacket(packetPlayOutPlayerInfoRemove);

		getPlayerHologram(player).hide(player);
	}

	@Override
	public void sendMetadataPacket(final Player player) {
		final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadataWrapper().create(activeStates, entityId);

		playerConnection.sendPacket(packet);
	}

	@Override
	public void sendEquipmentPacket(final Player player, final NPCSlot slot, final boolean auto) {
		final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		final EnumItemSlot nmsSlot = slot.getNmsEnum(EnumItemSlot.class);
		final ItemStack item = getItem(slot);

		final Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pair = new Pair<>(nmsSlot, CraftItemStack.asNMSCopy(item));
		final PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityId, Collections.singletonList(pair));
		playerConnection.sendPacket(packet);
	}

	@Override
	public void sendAnimationPacket(final Player player, final NPCAnimation animation) {
		final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		final PacketPlayOutAnimation packet = new PacketPlayOutAnimationWrapper().create(animation, entityId);
		playerConnection.sendPacket(packet);
	}

	@Override
	public void updateSkin(final Skin skin) {
		final GameProfile newProfile = new GameProfile(uuid, name);
		newProfile.getProperties().get("textures").clear();
		newProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
		this.packetPlayOutPlayerInfoAdd = new PacketPlayOutPlayerInfoWrapper().create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, newProfile, name);
		for (final Player player : Bukkit.getOnlinePlayers()) {
			final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
			playerConnection.sendPacket(packetPlayOutPlayerInfoRemove);
			playerConnection.sendPacket(packetPlayOutEntityDestroy);
			playerConnection.sendPacket(packetPlayOutPlayerInfoAdd);
			playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
		}
	}
}
