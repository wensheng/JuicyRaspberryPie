package org.wensheng.juicyraspberrypie.command;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.Optional;

/**
 * A handler for a command.
 */
public interface Handler {
	/**
	 * Handle the instruction for a command and return the result.
	 * If an exception is thrown, the result will be "Fail: " followed by the exception message.
	 *
	 * @param instruction the instruction
	 * @return the result
	 */
	@SuppressWarnings({"PMD.AvoidPrintStackTrace", "PMD.AvoidCatchingGenericException"})
	default String get(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		try {
			return handle(sessionAttachment, instruction);
		} catch (final Exception e) {
			e.printStackTrace();
			return "Fail: " + e.getMessage();
		}
	}

	/**
	 * Handle the instruction for a command and return the result.
	 *
	 * @param sessionAttachment the session attachment
	 * @param instruction       the instruction
	 * @return the result
	 */
	String handle(@NotNull SessionAttachment sessionAttachment, @NotNull Instruction instruction);

	/**
	 * Return an event queue that is attached to the current session (and available to the Handler via
	 * {@link SessionAttachment#getEventQueue(Handler)}) if the handler listens for events.
	 */
	default @NotNull Optional<EventQueue<? extends Event>> createEventQueue() {
		return Optional.empty();
	}
}
