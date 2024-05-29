package org.wensheng.juicyraspberrypie.command.handlers.console;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Execute the given command on the console.
 */
public class PerformCommand implements Handler {
	/**
	 * The configured whitelist patterns for commands.
	 * Anchored Java regular expressions; at least one must match for a command to be allowed.
	 */
	@NotNull
	private final List<Pattern> whitelistPatterns;

	/**
	 * Create a new PerformCommand event handler.
	 *
	 * @param whitelistPattern The whitelist patterns to associate with this handler.
	 */
	public PerformCommand(@NotNull final List<String> whitelistPattern) {
		this.whitelistPatterns = Objects.requireNonNull(whitelistPattern).stream().map(Pattern::compile).toList();
	}

	@Override
	public String handle(@NotNull final Instruction instruction) {
		final String command = instruction.next();
		if (whitelistPatterns.stream().noneMatch(pattern -> pattern.matcher(command).matches())) {
			return "Fail: Command rejected: " + command;
		}

		return String.valueOf(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
	}
}
