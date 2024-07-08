package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.ControllableEntity;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

/**
 * Enable control of an entity.
 */
public class DisableControl implements HandlerVoid {
	/**
	 * The plugin associated with this handler.
	 */
	private final Plugin plugin;

	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityByUUIDProvider entityProvider;

	/**
	 * Create a new EnableControl event handler.
	 *
	 * @param plugin         The plugin to associate with this handler.
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public DisableControl(final Plugin plugin, final EntityByUUIDProvider entityProvider) {
		this.plugin = plugin;
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final ControllableEntity entity = new ControllableEntity(plugin, entityProvider.getEntity(sessionAttachment, instruction));
		entity.disableControl();
	}
}
