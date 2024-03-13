package org.wensheng.juicyraspberrypie.command.handlers.events.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

public class Posts extends EventQueue<AsyncPlayerChatEvent> {
	public Posts(final Plugin plugin) {
		super(plugin);
	}

	@Override
	public String handle(final Instruction instruction) {
		final StringBuilder b = new StringBuilder();
		AsyncPlayerChatEvent event;
		while ((event = pollEvent()) != null) {
			final Player p = event.getPlayer();
			b.append(p.getName());
			b.append(",");
			b.append(p.getUniqueId());
			b.append(",");
			b.append(event.getMessage());
			if (!isQueueEmpty()) {
				b.append("|");
			}
		}
		return b.toString();
	}

	@EventHandler
	public void onChatPosted(final AsyncPlayerChatEvent event) {
		queueEvent(event);
	}

	@Override
	public void start() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}
}
