package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to hold the player, world, and contexts for commands.
 */
public class SessionAttachment implements AutoCloseable {
	/** The logger. */
	@NotNull
	private final Logger logger;

	/**
	 * The server to get the player from.
	 */
	@NotNull
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
	 * The context objects submitted by command handlers.
	 */
	@NotNull
	private final Map<@NotNull Handler, @NotNull Object> contexts = new HashMap<>();

	/**
	 * Create a new session attachment.
	 *
	 * @param server the server to get the player from
	 */
	public SessionAttachment(@NotNull final Logger logger, @NotNull final Server server) {
		this.logger = Objects.requireNonNull(logger);
		this.server = Objects.requireNonNull(server);
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

	/**
	 * Set a context for the given handler.
	 * @param handler The handler who the context belongs to.
	 * @param context The context object to associate with the handler.
	 */
	public void setContext(@NotNull final Handler handler, @NotNull final Object context) {
		if (contexts.put(handler, context) != null) {
			throw new IllegalStateException("Handler " + handler + " already has a context.");
		}
	}

	/**
	 * Get the context for the given handler.
	 */
	public @NotNull Optional<Object> getContext(@NotNull final Handler handler) {
		return Optional.ofNullable(contexts.get(handler));
	}

	@Override
	@SuppressWarnings("PMD.AvoidCatchingGenericException")
	public void close() {
		contexts.values().stream()
				.filter(context -> context instanceof AutoCloseable)
				.forEach(context -> {
					try {
						((AutoCloseable) context).close();
					} catch (final Exception e) {
						logger.log(Level.WARNING, "Failed to close context: " + e.getMessage(), e);
					}
				});
	}
}
