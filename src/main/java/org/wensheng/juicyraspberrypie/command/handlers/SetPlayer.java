package org.wensheng.juicyraspberrypie.command.handlers;

import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Set the player associated with the current session.
 */
public class SetPlayer implements Handler {
	/**
	 * Create a new SetPlayer event handler.
	 */
	public SetPlayer() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		return String.valueOf(sessionAttachment.setPlayerAndOrigin(instruction.next()));
	}
}
