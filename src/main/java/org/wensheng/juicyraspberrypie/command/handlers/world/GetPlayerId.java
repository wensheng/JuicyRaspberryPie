package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Get the UUID of a given player.
 */
public class GetPlayerId implements Handler {
	/**
	 * Default GetPlayerId constructor.
	 */
	public GetPlayerId() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Player player = instruction.nextNamedPlayer();
		if (player != null) {
			return player.getUniqueId().toString();
		} else {
			return "Fail";
		}
	}
}
