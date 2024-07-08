package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

/**
 * Set the rotation of an entity.
 */
public class SetRotation implements HandlerVoid {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityProvider entityProvider;

	/**
	 * Create a new SetRotation event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public SetRotation(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(sessionAttachment, instruction);
		final float yaw = Float.parseFloat(instruction.next());
		final Location entityLoc = entity.getLocation();
		entityLoc.setYaw(yaw);
		entity.teleport(entityLoc);
		if (entity instanceof final Mob mob) {
			mob.setBodyYaw(entityLoc.getYaw());
		}
	}
}
