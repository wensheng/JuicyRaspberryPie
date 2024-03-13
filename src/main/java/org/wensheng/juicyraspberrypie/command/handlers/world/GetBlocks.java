package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.codehaus.plexus.util.StringUtils;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.util.List;

public class GetBlocks implements Handler {

	@Override
	public String handle(final Instruction instruction) {
		final Location pos1 = instruction.nextLocation();
		final Location pos2 = instruction.nextLocation();
		final List<Material> list = getLocationsBetween(pos1, pos2).stream().map(loc -> loc.getBlock().getType()).toList();
		return StringUtils.join(list.iterator(), ",");
	}
}
