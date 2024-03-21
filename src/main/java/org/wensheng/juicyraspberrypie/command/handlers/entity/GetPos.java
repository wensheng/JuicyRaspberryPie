package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

/**
 * Get the position of an entity.
 */
public class GetPos implements Handler {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityProvider entityProvider;

	/**
	 * Create a new GetPos event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public GetPos(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public String handle(final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(instruction);
		return getLocation(entity.getLocation());
	}
}
