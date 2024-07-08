package org.wensheng.juicyraspberrypie.command.handlers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Get the player associated with the current session.
 */
public class GetPlayer implements Handler {
	/**
	 * Create a new GetPlayer event handler.
	 */
	public GetPlayer() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Player player = sessionAttachment.getPlayer();
		return player == null ? "(none)" : player.getName();
	}
}
