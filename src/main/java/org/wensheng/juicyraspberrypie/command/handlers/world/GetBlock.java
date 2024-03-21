package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

/**
 * Get the type of block at a given location.
 */
public class GetBlock implements Handler {
	/**
	 * Default GetBlock constructor.
	 */
	public GetBlock() {
	}

	@Override
	public String handle(final Instruction instruction) {
		return instruction.nextLocation().getBlock().getType().name();
	}
}
