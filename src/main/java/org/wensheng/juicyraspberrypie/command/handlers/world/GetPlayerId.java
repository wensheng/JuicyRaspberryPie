package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetPlayerId implements Handler {
	@Override
	public String handle(final Instruction instruction) {
		final Player p = instruction.nextNamedPlayer();
		if (p != null) {
			return p.getUniqueId().toString();
		} else {
			return "Fail";
		}
	}
}
