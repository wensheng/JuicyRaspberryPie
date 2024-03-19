package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

/**
 * Set the pitch of an entity.
 */
public class SetPitch implements HandlerVoid {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityProvider entityProvider;

	/**
	 * Create a new SetPitch event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public SetPitch(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(instruction);
		final float pitch = Float.parseFloat(instruction.next());
		final Location entityLoc = entity.getLocation();
		entityLoc.setPitch(pitch);
		entity.teleport(entityLoc);
	}
}
