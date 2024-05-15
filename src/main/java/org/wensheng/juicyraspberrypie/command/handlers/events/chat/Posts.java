package org.wensheng.juicyraspberrypie.command.handlers.events.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

/**
 * Get one chat event from the queue.
 */
public class Posts extends EventQueue<AsyncPlayerChatEvent> {
	/**
	 * Create a new Posts event handler.
	 *
	 * @param plugin The plugin to associate with this handler.
	 */
	public Posts(final Plugin plugin) {
		super(plugin);
	}

	@Override
	public String handle(final Instruction instruction) {
		final StringBuilder stringBuilder = new StringBuilder();
		while (isQueueEmpty()) {
			final AsyncPlayerChatEvent event = pollEvent();
			final Player player = event.getPlayer();
			stringBuilder.append(player.getName()).append(',').append(player.getUniqueId()).append(',').append(event.getMessage());
			if (!isQueueEmpty()) {
				stringBuilder.append('|');
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Handle the AsyncPlayerChatEvent.
	 *
	 * @param event The AsyncPlayerChatEvent to handle.
	 */
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
