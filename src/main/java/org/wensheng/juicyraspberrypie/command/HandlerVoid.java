package org.wensheng.juicyraspberrypie.command;

import org.jetbrains.annotations.NotNull;

/**
 * A handler for a command that does not return a result.
 */
public interface HandlerVoid extends Handler {
	@Override
	default String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		handleVoid(sessionAttachment, instruction);
		return "OK";
	}

	/**
	 * Handle the instruction.
	 *
	 * @param sessionAttachment the session attachment
	 * @param instruction       the instruction
	 */
	void handleVoid(@NotNull SessionAttachment sessionAttachment, @NotNull Instruction instruction);
}
