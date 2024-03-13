package org.wensheng.juicyraspberrypie.command.handlers.chat;

import org.bukkit.Server;
import org.codehaus.plexus.util.StringUtils;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class Post implements HandlerVoid {
	private final Server server;

	public Post(final Server server) {
		this.server = server;
	}

	@Override
	public void handleVoid(final Instruction instruction) {
		final String message = StringUtils.join(instruction, ",");
		server.broadcastMessage(message);
	}
}
