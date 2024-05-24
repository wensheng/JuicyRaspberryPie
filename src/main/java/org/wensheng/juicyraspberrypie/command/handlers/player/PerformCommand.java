package org.wensheng.juicyraspberrypie.command.handlers.player;

import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Make the player associated with the current session perform the given command.
 */
public class PerformCommand implements Handler {
	/**
	 * The session attachment associated with this handler.
	 */
	private final SessionAttachment attachment;

	/**
	 * Create a new PerformCommand event handler.
	 *
	 * @param attachment The session attachment to associate with this handler.
	 */
	public PerformCommand(final SessionAttachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public String handle(final Instruction instruction) {
		return String.valueOf(attachment.getPlayer().performCommand(instruction.next()));
	}
}
