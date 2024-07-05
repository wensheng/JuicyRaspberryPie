package org.wensheng.juicyraspberrypie.command.handlers.events.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.Objects;
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
	@SuppressWarnings("PMD.CloseResource") // Closing is done by {@link EventQueue#close()} through {@link SessionAttachment#close()}.
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final EventQueue<? extends Event> eventQueue = (EventQueue<? extends Event>) sessionAttachment.getContext(this).orElseThrow();
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
	public @NotNull Optional<Object> createContext(@NotNull final JavaPlugin plugin, @NotNull final SessionAttachment sessionAttachment) {
		return Optional.of(new ChatEventQueue(Objects.requireNonNull(plugin)));
	}

	/**
	 * Event queue for chat events.
	 */
	private static class ChatEventQueue extends EventQueue<AsyncPlayerChatEvent> {
		/** Constructor. */
		public ChatEventQueue(@NotNull final JavaPlugin plugin) {
			super(Objects.requireNonNull(plugin));
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
