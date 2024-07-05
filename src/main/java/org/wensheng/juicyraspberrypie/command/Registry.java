package org.wensheng.juicyraspberrypie.command;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry of command handlers.
 */
public class Registry {
	/**
	 * The handlers.
	 */
	private final Map<String, Handler> handlers;

	/**
	 * Create a new registry.
	 */
	public Registry() {
		handlers = new HashMap<>();
	}

	/**
	 * Get all the handlers.
	 *
	 * @return the handlers
	 */
	public @NotNull Collection<Handler> getHandlers() {
		return handlers.values();
	}

	/**
	 * Register a handler for a command.
	 *
	 * @param command the command
	 * @param handler the handler
	 */
	public void register(final String command, final Handler handler) {
		handlers.put(command, handler);
	}

	/**
	 * Get the handler for the given command.
	 *
	 * @param command the command
	 * @return the handler
	 */
	public Handler getHandler(final String command) {
		return handlers.get(command);
	}

	/**
	 * Create all contexts for the passed session.
	 */
	public void createContexts(@NotNull final JavaPlugin plugin, @NotNull final SessionAttachment sessionAttachment) {
		getHandlers().forEach(handler -> createContext(plugin, sessionAttachment, handler));
	}

	private void createContext(@NotNull final JavaPlugin plugin, @NotNull final SessionAttachment sessionAttachment, @NotNull final Handler handler) {
		handler.createContext(plugin, sessionAttachment).ifPresent(context -> {
			sessionAttachment.setContext(handler, context);
		});
	}
}
