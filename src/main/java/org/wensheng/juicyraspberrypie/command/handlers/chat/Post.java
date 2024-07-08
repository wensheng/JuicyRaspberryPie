package org.wensheng.juicyraspberrypie.command.handlers.chat;

import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Post a message to the chat.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Post implements HandlerVoid {
	/**
	 * The server associated with this handler.
	 */
	private final Server server;

	/**
	 * Create a new Post event handler.
	 *
	 * @param server The server to associate with this handler.
	 */
	public Post(final Server server) {
		this.server = server;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final String message = instruction.allArguments();
		server.broadcastMessage(message);
	}
}
