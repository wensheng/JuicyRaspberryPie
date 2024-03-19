package org.wensheng.juicyraspberrypie.command.handlers;

import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Set the player associated with the current session.
 */
public class SetPlayer implements Handler {
	/**
	 * The session attachment associated with this handler.
	 */
	private final SessionAttachment attachment;

	/**
	 * Create a new SetPlayer event handler.
	 *
	 * @param attachment The session attachment to associate with this handler.
	 */
	public SetPlayer(final SessionAttachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public String handle(final Instruction instruction) {
		return String.valueOf(attachment.setPlayerAndOrigin(instruction.next()));
	}
}
