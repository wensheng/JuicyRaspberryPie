package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

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
	public String handle(final Instruction instruction) {
		final Player player = instruction.nextNamedPlayer();
		if (player != null) {
			return player.getUniqueId().toString();
		} else {
			return "Fail";
		}
	}
}
