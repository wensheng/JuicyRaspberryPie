package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.block.Block;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

/**
 * Get the type and data of a block at a given location.
 */
public class GetBlockWithData implements Handler {
	/**
	 * Default GetBlockWithData constructor.
	 */
	public GetBlockWithData() {
	}

	@Override
	public String handle(final Instruction instruction) {
		final Block block = instruction.nextLocation().getBlock();
		return block.getType().name() + "," + block.getBlockData();
	}
}
