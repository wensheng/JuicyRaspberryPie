package org.wensheng.juicyraspberrypie.command.handlers.events.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.Optional;

/**
 * Get one chat event from the queue.
 */
public class Posts implements Handler {
	/**
	 * Create a new Posts event handler.
	 */
	public Posts() {
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final EventQueue<? extends Event> eventQueue = sessionAttachment.getEventQueue(this).orElseThrow();
		final StringBuilder stringBuilder = new StringBuilder();
		while (!eventQueue.isQueueEmpty()) {
			final AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) eventQueue.pollEvent();
			final Player player = event.getPlayer();
			stringBuilder.append(player.getName()).append(',').append(player.getUniqueId()).append(',').append(event.getMessage());
			if (!eventQueue.isQueueEmpty()) {
				stringBuilder.append('|');
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public @NotNull Optional<EventQueue<? extends Event>> createEventQueue() {
		return Optional.of(new ChatEventQueue());
	}

	/**
	 * Event queue for chat events.
	 */
	private static class ChatEventQueue extends EventQueue<AsyncPlayerChatEvent> {
		/** Constructor. */
		public ChatEventQueue() {
			super();
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
	}
}
