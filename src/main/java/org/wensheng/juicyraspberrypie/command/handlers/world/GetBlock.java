package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Get the type of block at a given location.
 */
public class GetBlock implements Handler {
	/**
	 * Default GetBlock constructor.
	 */
	public GetBlock() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		return instruction.nextLocation().getBlock().getType().name();
	}
}
