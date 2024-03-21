package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Instruction;

/**
 * Provides entities for use in command handlers.
 */
public interface EntityProvider {
	/**
	 * Get the entity associated with the given instruction.
	 *
	 * @param instruction The instruction to get the entity for.
	 * @return The entity associated with the given instruction.
	 */
	Entity getEntity(Instruction instruction);
}
