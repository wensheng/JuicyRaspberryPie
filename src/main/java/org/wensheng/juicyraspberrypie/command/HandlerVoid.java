package org.wensheng.juicyraspberrypie.command;

/**
 * A handler for a command that does not return a result.
 */
public interface HandlerVoid extends Handler {
	@Override
	default String handle(final Instruction instruction) {
		handleVoid(instruction);
		return "OK";
	}

	/**
	 * Handle the instruction.
	 *
	 * @param instruction the instruction
	 */
	void handleVoid(Instruction instruction);
}
