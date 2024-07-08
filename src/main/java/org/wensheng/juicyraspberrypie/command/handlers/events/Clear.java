package org.wensheng.juicyraspberrypie.command.handlers.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.Registry;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Clear all event queues.
 */
public class Clear implements HandlerVoid {
	/**
	 * The registry associated with this handler.
	 */
	private final Registry registry;

	/**
	 * Create a new Clear event handler.
	 *
	 * @param registry The registry to associate with this handler.
	 */
	public Clear(final Registry registry) {
		this.registry = registry;
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		clearEventQueues(sessionAttachment);
	}

	private void clearEventQueues(@NotNull final SessionAttachment sessionAttachment) {
		registry.getHandlers().forEach(handler -> clearEventQueue(sessionAttachment, handler));
	}

	private void clearEventQueue(@NotNull final SessionAttachment sessionAttachment, @NotNull final Handler handler) {
		sessionAttachment.getContext(handler).ifPresent(context -> {
			if (context instanceof EventQueue<? extends Event> eventQueue) {
				eventQueue.clearQueue();
			}
		});
	}
}
