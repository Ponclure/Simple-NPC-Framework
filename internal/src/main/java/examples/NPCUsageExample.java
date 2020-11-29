package examples;

import com.github.ponclure.simplenpcframework.SimpleNPCFramework;
import com.github.ponclure.simplenpcframework.api.NPC;
import com.github.ponclure.simplenpcframework.api.events.NPCInteractEvent;
import com.github.ponclure.simplenpcframework.api.skin.AsyncSkinFetcher;
import com.github.ponclure.simplenpcframework.api.state.NPCSlot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class NPCUsageExample implements Listener {

	/*
	 * An example of how this NPC library can be powerful in many ways. This class
	 * displays how you can create an NPC at the player's location (with a skin and
	 * armor) if they right click at a diamond block. We then will store the placer
	 * of the NPC and the NPC ID into a Map, and then if a Player interacts with an
	 * NPC, we will know who originally spawned the NPC.
	 */

	/*
	 * Define necessary variables such as a reference to the API to use it
	 * correctly.
	 */
	private final SimpleNPCFramework framework;
	private final Map<String, UUID> placed;

	/*
	 * Create a constructor which accepts an instance of your plugin main class and
	 * framework. Also known as dependency injection. We also register the event
	 * here.
	 */
	public NPCUsageExample(final Plugin p, final SimpleNPCFramework framework) {
		this.framework = framework;
		this.placed = new HashMap<>();
		p.getServer().getPluginManager().registerEvents(this, p);
	}

	/*
	 * Now to the juicy part. We let the method accept a player, and we will use the
	 * provided AsyncSkinFetcher class to fetch the skin asynchronously for the NPC.
	 * We first set the location of the NPC to be the player's location. Then we get
	 * the contents of the player's inventory and set the armor of the NPC with the
	 * armor that the player has. Finally, we create the NPC, display it to that
	 * specific player, and then use the ID to be stored in our map and sent to the
	 * player as a chat message.
	 */
	public void setNPCAtPlayer(final Player player) {
		AsyncSkinFetcher.fetchSkinFromUuidAsync(player.getUniqueId(), skin -> {
			final PlayerInventory inv = player.getInventory();
			final NPC npc = framework.createNPC(Collections.singletonList(player.getName()));
			npc.setLocation(player.getLocation());
			npc.setItem(NPCSlot.BOOTS, inv.getBoots());
			npc.setItem(NPCSlot.LEGGINGS, inv.getLeggings());
			npc.setItem(NPCSlot.CHESTPLATE, inv.getChestplate());
			npc.setItem(NPCSlot.HELMET, inv.getHelmet());
			npc.create();
			npc.show(player);
			final String id = npc.getId();
			placed.put(id, player.getUniqueId());
			player.sendMessage(ChatColor.GOLD + "The new NPC created has id: " + id);
		});

	}

	/*
	 * We use the PlayerInteractEvent to check if the player interacted with a
	 * block. We check if the player is opped first, and then we get the type of the
	 * block and see if it's a Diamond Block. Then, we call the function we have
	 * created above using the player in the event.
	 */
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer(); // Pfft imagine checking for permissions xd
		if (!player.isOp()) {
			return;
		}

		if (event.getClickedBlock().getType() != Material.DIAMOND_BLOCK) {
			return;
		}

		setNPCAtPlayer(player);
	}

	/*
	 * We can use the NPCInteractEvent to check if any player interacted with the
	 * NPC's we have created. Then we check if the ID is stored in our map. If it is,
	 * we can print out which player created the NPC the first place.
	 */
	@EventHandler
	public void onNPCInteract(final NPCInteractEvent event) {
		final NPC clicked = event.getNPC();
		if (!placed.containsKey(clicked.getId())) {
			return;
		}

		event.getWhoClicked().sendMessage(Bukkit.getPlayer(placed.get(clicked.getId())).getName() + ChatColor.GOLD + " created this NPC!");
	}
}
