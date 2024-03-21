package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * A class to hold the player and world for commands.
 */
public class SessionAttachment {
	/**
	 * The server to get the player from.
	 */
	private final Server server;

	/**
	 * The world to use for commands.
	 */
	private World world;

	/**
	 * The player to use for commands.
	 */
	private Player player;

	/**
	 * Create a new session attachment.
	 *
	 * @param server the server to get the player from
	 */
	public SessionAttachment(final Server server) {
		this.server = server;
	}

	/**
	 * Set the player and the world to the first online player and its world.
	 *
	 * @return true if a player was found, false otherwise
	 */
	@SuppressWarnings("PMD.LinguisticNaming")
	public boolean setPlayerAndOrigin() {
		final Collection<? extends Player> players = server.getOnlinePlayers();
		if (players.isEmpty()) {
			player = null;
		} else {
			player = players.iterator().next();
			world = player.getWorld();
			return true;
		}
		world = server.getWorlds().get(0);
		return false;
	}

	/**
	 * Set the player and the world to the given player name.
	 *
	 * @param playerName the name of the player
	 * @return true if the player was found, false otherwise
	 */
	@SuppressWarnings("PMD.LinguisticNaming")
	public boolean setPlayerAndOrigin(final String playerName) {
		for (final Player player : server.getOnlinePlayers()) {
			if (playerName.equalsIgnoreCase(player.getName())) {
				this.player = player;
				world = this.player.getWorld();
				return true;
			}
		}
		player = null;
		return false;
	}

	public World getWorld() {
		return world;
	}

	public Player getPlayer() {
		if (player != null && player.isOnline()) {
			return player;
		}
		setPlayerAndOrigin();
		return player;
	}
}
