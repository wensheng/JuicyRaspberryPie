package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

/**
 * Get the names and UUIDs of all online players.
 */
public class GetPlayerIds implements Handler {
	/**
	 * The server to get the players from.
	 */
	private final Server server;

	/**
	 * Constructor for GetPlayerIds.
	 *
	 * @param server The server to get the players from.
	 */
	public GetPlayerIds(final Server server) {
		this.server = server;
	}

	@Override
	public String handle(final Instruction instruction) {
		final StringBuilder bdr = new StringBuilder();
		for (final Player p : server.getOnlinePlayers()) {
			bdr.append(p.getName()).append(',').append(p.getUniqueId()).append('|');
		}
		if (!bdr.isEmpty()) {
			bdr.deleteCharAt(bdr.length() - 1);
		}
		return bdr.toString();
	}
}
