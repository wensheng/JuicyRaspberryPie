package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

/**
 * Set the tile of an entity.
 */
public class SetTile implements HandlerVoid {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityProvider entityProvider;

	/**
	 * Create a new SetTile event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public SetTile(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(sessionAttachment, instruction);
		final Location loc = instruction.nextBlockLocation();
		final Location entityLoc = entity.getLocation();
		loc.setPitch(entityLoc.getPitch());
		loc.setYaw(entityLoc.getYaw());
		entity.teleport(loc);
	}
}
