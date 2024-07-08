package org.wensheng.juicyraspberrypie.command;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
	 * Return a context object that is attached to the current session (and available to the Handler via
	 * {@link SessionAttachment#getContext(Handler)}) if the handler needs session-scoped information.
	 */
	default @NotNull Optional<Object> createContext(@NotNull final JavaPlugin plugin, @NotNull final SessionAttachment sessionAttachment) {
		return Optional.empty();
	}
}
