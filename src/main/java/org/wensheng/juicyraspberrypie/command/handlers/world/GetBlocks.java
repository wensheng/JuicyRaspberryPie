package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Material;
import org.codehaus.plexus.util.StringUtils;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.util.List;

/**
 * Get the blocks between two locations.
 */
public class GetBlocks implements Handler {
	/**
	 * Default GetBlocks constructor.
	 */
	public GetBlocks() {
	}

	@Override
	public String handle(final Instruction instruction) {
		final List<Material> list = instruction.nextLocationsBetween().stream().map(loc -> loc.getBlock().getType()).toList();
		return StringUtils.join(list.iterator(), ",");
	}
}
