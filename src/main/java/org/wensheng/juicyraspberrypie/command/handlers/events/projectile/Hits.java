package org.wensheng.juicyraspberrypie.command.handlers.events.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.LocationRenderer;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.Objects;
import java.util.Optional;

/**
 * Get one projectile hit event from the queue.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Hits implements Handler {
	/**
	 * Create a new Hits event handler.
	 */
	public Hits() {
		super();
	}

	@Override
	@SuppressWarnings("PMD.CloseResource") // Closing is done by {@link EventQueue#close()} through {@link SessionAttachment#close()}.
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final EventQueue<? extends Event> eventQueue = (EventQueue<? extends Event>) sessionAttachment.getContext(this).orElseThrow();
		final StringBuilder stringBuilder = new StringBuilder();
		while (!eventQueue.isQueueEmpty()) {
			final ProjectileHitEvent event = (ProjectileHitEvent) eventQueue.pollEvent();
			final Arrow arrow = (Arrow) event.getEntity();
			final Player player = (Player) arrow.getShooter();
			if (player != null) {
				final Block block = arrow.getLocation().getBlock();
				final Location loc = block.getLocation();
				stringBuilder.append(LocationRenderer.getBlockLocation(loc)).append(',').append(player.getUniqueId()).append(',');
				final Entity hitEntity = event.getHitEntity();
				if (hitEntity != null) {
					stringBuilder.append(hitEntity.getUniqueId());
				}
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
	public @NotNull Optional<Object> createContext(@NotNull final JavaPlugin plugin, @NotNull final SessionAttachment sessionAttachment) {
		return Optional.of(new HitEventQueue(Objects.requireNonNull(plugin)));
	}

	/**
	 * Event queue for projectile hit events.
	 */
	private static class HitEventQueue extends EventQueue<ProjectileHitEvent> {
		/** Constructor. */
		public HitEventQueue(@NotNull final JavaPlugin plugin) {
			super(Objects.requireNonNull(plugin));
		}

		/**
		 * Handle the ProjectileHitEvent.
		 *
		 * @param event The ProjectileHitEvent to handle.
		 */
		@EventHandler
		public void onProjectileHit(final ProjectileHitEvent event) {
			queueEvent(event);
		}
	}
}
