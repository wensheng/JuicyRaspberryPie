package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Checks if a block is passable at a given location.
 */
public class IsBlockPassable implements Handler {
	/**
	 * Default IsBlockPassable constructor.
	 */
	public IsBlockPassable() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		return String.valueOf(loc.getBlock().isPassable());
	}
}
