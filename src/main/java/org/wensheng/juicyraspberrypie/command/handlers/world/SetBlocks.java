package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.List;

/**
 * Set blocks between two locations
 */
public class SetBlocks extends SetBlock implements HandlerVoid {
	/**
	 * Default SetBlocks constructor.
	 */
	public SetBlocks() {
		super();
	}

	@Override
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final List<Location> locations = instruction.nextLocationsBetween();
		final Material mat = Material.matchMaterial(instruction.next());
		final Material material = mat == null ? Material.valueOf("SANDSTONE") : mat;
		final int facing = instruction.hasNext() ? Integer.parseInt(instruction.next()) : 0;
		final BlockFace blockFace = BlockFace.values()[facing];

		locations.forEach(loc -> updateBlock(loc, material, blockFace));
	}
}
