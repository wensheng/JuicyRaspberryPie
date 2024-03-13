package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetBlock implements Handler {

	@Override
	public String handle(final Instruction instruction) {
		return instruction.nextLocation().getBlock().getType().name();
	}
}
