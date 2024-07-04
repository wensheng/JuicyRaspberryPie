package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.Locale;

/**
 * Spawns an entity at a given location.
 */
public class SpawnEntity implements Handler {
	/**
	 * Default SpawnEntity constructor.
	 */
	public SpawnEntity() {
	}

	@Override
	@SuppressWarnings("PMD.AvoidCatchingGenericException")
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		EntityType entityType;
		try {
			entityType = EntityType.valueOf(instruction.next().toUpperCase(Locale.ROOT));
		} catch (Exception exc) {
			entityType = EntityType.valueOf("COW");
		}
		final Entity entity = loc.getWorld().spawnEntity(loc, entityType);
		return entity.getUniqueId().toString();
	}
}
