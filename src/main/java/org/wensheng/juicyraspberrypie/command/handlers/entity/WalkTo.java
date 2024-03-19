package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

/**
 * Let an entity walk to a specific location.
 */
public class WalkTo implements HandlerVoid {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityByUUIDProvider entityProvider;

	/**
	 * Create a new WalkTo event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public WalkTo(final EntityByUUIDProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(instruction);
		if (entity instanceof final Mob mob) {
			mob.getPathfinder().moveTo(instruction.nextLocation());
		}
	}
}
