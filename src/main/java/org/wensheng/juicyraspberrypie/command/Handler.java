package org.wensheng.juicyraspberrypie.command;

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
	default String get(final Instruction instruction) {
		try {
			return handle(instruction);
		} catch (final Exception e) {
			e.printStackTrace();
			return "Fail: " + e.getMessage();
		}
	}

	/**
	 * Handle the instruction for a command and return the result.
	 *
	 * @param instruction the instruction
	 * @return the result
	 */
	String handle(Instruction instruction);
}
