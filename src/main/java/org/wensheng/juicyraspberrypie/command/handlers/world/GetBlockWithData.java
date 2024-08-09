package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

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
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Block block = instruction.nextLocation().getBlock();
		return block.getType().name() + "," + getBlockDataStripped(block);
	}

	private static @NotNull String getBlockDataStripped(final Block block) {
		final String data = block.getBlockData().getAsString();
		final int start = data.indexOf('[');
		final int end = data.indexOf(']');
		if (start == -1 || end == -1) {
			return "";
		}
		return data.substring(start + 1, end);
	}
}
