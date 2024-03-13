package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetPlayerIds implements Handler {
	private final Server server;

	public GetPlayerIds(final Server server) {
		this.server = server;
	}

	@Override
	public String handle(final Instruction instruction) {
		final StringBuilder bdr = new StringBuilder();
		for (final Player p : server.getOnlinePlayers()) {
			bdr.append(p.getName());
			bdr.append(",");
			bdr.append(p.getUniqueId());
			bdr.append("|");
		}
		if (!bdr.isEmpty()) {
			bdr.deleteCharAt(bdr.length() - 1);
		}
		return bdr.toString();
	}
}
