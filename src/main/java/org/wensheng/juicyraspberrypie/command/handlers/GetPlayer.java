package org.wensheng.juicyraspberrypie.command.handlers;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Get the player associated with the current session.
 */
public class GetPlayer implements Handler {
	/**
	 * The session attachment associated with this handler.
	 */
	private final SessionAttachment attachment;

	/**
	 * Create a new GetPlayer event handler.
	 *
	 * @param attachment The session attachment to associate with this handler.
	 */
	public GetPlayer(final SessionAttachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public String handle(final Instruction instruction) {
		final Player player = attachment.getPlayer();
		return player == null ? "(none)" : player.getName();
	}
}
