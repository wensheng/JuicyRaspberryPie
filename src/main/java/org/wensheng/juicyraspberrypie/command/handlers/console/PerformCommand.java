package org.wensheng.juicyraspberrypie.command.handlers.console;

import org.bukkit.Bukkit;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

/**
 * Execute the given command on the console.
 */
public class PerformCommand implements Handler {
	/**
	 * Create a new PerformCommand event handler.
	 */
	public PerformCommand() {
	}

	@Override
	public String handle(final Instruction instruction) {
		return String.valueOf(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), instruction.next()));
	}
}
