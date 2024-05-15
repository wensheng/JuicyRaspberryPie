package org.wensheng.juicyraspberrypie.command.handlers.events.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

/**
 * Get one projectile hit event from the queue.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Hits extends EventQueue<ProjectileHitEvent> {
	/**
	 * Create a new Hits event handler.
	 *
	 * @param plugin The plugin to associate with this handler.
	 */
	public Hits(final Plugin plugin) {
		super(plugin);
	}

	@Override
	public String handle(final Instruction instruction) {
		final StringBuilder stringBuilder = new StringBuilder();
		while (isQueueEmpty()) {
			final ProjectileHitEvent event = pollEvent();
			final Arrow arrow = (Arrow) event.getEntity();
			final Player player = (Player) arrow.getShooter();
			if (player != null) {
				final Block block = arrow.getLocation().getBlock();
				final Location loc = block.getLocation();
				stringBuilder.append(getBlockLocation(loc)).append(',').append(player.getUniqueId()).append(',');
				final Entity hitEntity = event.getHitEntity();
				if (hitEntity != null) {
					stringBuilder.append(hitEntity.getUniqueId());
				}
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
	 * Handle the ProjectileHitEvent.
	 *
	 * @param event The ProjectileHitEvent to handle.
	 */
	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
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
