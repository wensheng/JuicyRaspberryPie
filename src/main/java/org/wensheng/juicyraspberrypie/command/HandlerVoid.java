package org.wensheng.juicyraspberrypie.command;

public interface HandlerVoid extends Handler {
	@Override
	default String handle(final Instruction instruction) {
		handleVoid(instruction);
		return "OK";
	}

	void handleVoid(final Instruction instruction);
}
