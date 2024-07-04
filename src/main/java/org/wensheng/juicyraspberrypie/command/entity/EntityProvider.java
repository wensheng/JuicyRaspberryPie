package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Provides entities for use in command handlers.
 */
public interface EntityProvider {
	/**
	 * Get the entity associated with the given instruction.
	 *
	 * @param sessionAttachment The session attachment through which the entity may be gotten.
	 * @param instruction       The instruction to get the entity for.
	 * @return The entity associated with the given instruction.
	 */
	Entity getEntity(@NotNull SessionAttachment sessionAttachment, @NotNull Instruction instruction);
}
