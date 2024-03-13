package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class IsBlockPassable implements Handler {
	@Override
	public String handle(final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		return String.valueOf(loc.getBlock().isPassable());
	}
}
