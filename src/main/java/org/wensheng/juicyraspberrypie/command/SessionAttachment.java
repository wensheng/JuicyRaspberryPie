package org.wensheng.juicyraspberrypie.command;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class SessionAttachment {

	private final Plugin plugin;

	private World world;

	private Player player;

	public SessionAttachment(final Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean setPlayerAndOrigin() {
		final Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		if (!players.isEmpty()) {
			player = players.iterator().next();
			world = player.getWorld();
			return true;
		} else {
			player = null;
		}
		world = plugin.getServer().getWorlds().get(0);
		return false;
	}

	public boolean setPlayerAndOrigin(final String playerName) {
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
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
