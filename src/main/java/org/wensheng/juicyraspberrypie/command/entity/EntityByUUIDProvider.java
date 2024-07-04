package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Provides entities for use in command handlers by UUID.
 */
public class EntityByUUIDProvider implements EntityProvider {
	/**
	 * The server to get entities from.
	 */
	private final Server server;

	/**
	 * Create a new entity provider by UUID.
	 *
	 * @param server The server to get entities from.
	 */
	public EntityByUUIDProvider(final Server server) {
		this.server = server;
	}

	@Override
	public Entity getEntity(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final UUID entityUuid = UUID.fromString(instruction.next());
		final Entity entity = server.getEntity(entityUuid);
		if (entity == null) {
			throw new NoSuchElementException("No entity found for UUID " + entityUuid);
		}
		return entity;
	}
}
