package org.wensheng.juicyraspberrypie.command.handlers.events.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.LocationRenderer;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.EnumSet;
import java.util.Set;

/**
 * Get one block hit event from the queue.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Hits extends EventQueue<PlayerInteractEvent> {
	/**
	 * The set of tools that can detect block breaks.
	 */
	private static final Set<Material> BLOCK_BREAK_DETECTION_TOOLS = EnumSet.of(
			Material.DIAMOND_SWORD,
			Material.GOLDEN_SWORD,
			Material.IRON_SWORD,
			Material.STONE_SWORD,
			Material.WOODEN_SWORD);

	/**
	 * Create a new Hits event handler.
	 *
	 * @param plugin The plugin to associate with this handler.
	 */
	public Hits(final Plugin plugin) {
		super(plugin);
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final StringBuilder stringBuilder = new StringBuilder();
		while (isQueueEmpty()) {
			final PlayerInteractEvent event = pollEvent();
			final Block block = event.getClickedBlock();
			if (block != null) {
				final Location loc = block.getLocation();
				stringBuilder.append(LocationRenderer.getBlockLocation(loc)).append(',').append(event.getBlockFace().name()).append(',').append(event.getPlayer().getUniqueId());
			} else {
				stringBuilder.append("0,0,0,Fail,0");
			}
			if (!isQueueEmpty()) {
				stringBuilder.append('|');
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Handle the PlayerInteractEvent.
	 *
	 * @param event The PlayerInteractEvent to handle.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final ItemStack currentTool = event.getItem();
		if (currentTool == null || !BLOCK_BREAK_DETECTION_TOOLS.contains(currentTool.getType())) {
			return;
		}
		queueEvent(event);
	}

	@Override
	public void start() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}
}
