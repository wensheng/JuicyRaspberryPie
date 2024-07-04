package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

/**
 * Remove an entity from the world.
 */
public class Remove implements HandlerVoid {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityByUUIDProvider entityProvider;

	/**
	 * Create a new Remove event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public Remove(final EntityByUUIDProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(sessionAttachment, instruction);
		if (!(entity instanceof Player)) {
			entity.remove();
		}
	}
}
