package org.wensheng.juicyraspberrypie.command.handlers.player;

import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Make the player associated with the current session perform the given command.
 */
public class PerformCommand implements Handler {
	/**
	 * Create a new PerformCommand event handler.
	 */
	public PerformCommand() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		return String.valueOf(sessionAttachment.getPlayer().performCommand(instruction.allArguments()));
	}
}
