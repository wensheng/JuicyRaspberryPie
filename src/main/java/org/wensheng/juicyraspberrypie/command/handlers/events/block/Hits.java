package org.wensheng.juicyraspberrypie.command.handlers.events.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.LocationRenderer;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Get one block hit event from the queue.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Hits implements Handler {
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
	 */
	public Hits() {
		super();
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final EventQueue<? extends Event> eventQueue = sessionAttachment.getEventQueue(this).orElseThrow();
		final StringBuilder stringBuilder = new StringBuilder();
		while (!eventQueue.isQueueEmpty()) {
			final PlayerInteractEvent event = (PlayerInteractEvent) eventQueue.pollEvent();
			final Block block = event.getClickedBlock();
			if (block != null) {
				final Location loc = block.getLocation();
				stringBuilder.append(LocationRenderer.getBlockLocation(loc)).append(',').append(event.getBlockFace().name()).append(',').append(event.getPlayer().getUniqueId());
			} else {
				stringBuilder.append("0,0,0,Fail,0");
			}
			if (!eventQueue.isQueueEmpty()) {
				stringBuilder.append('|');
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public @NotNull Optional<EventQueue<? extends Event>> createEventQueue() {
		return Optional.of(new HitEventQueue());
	}

	/**
	 * Event queue for block hit events.
	 */
	private static class HitEventQueue extends EventQueue<PlayerInteractEvent> {
		/** Constructor. */
		public HitEventQueue() {
			super();
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
		}
}
