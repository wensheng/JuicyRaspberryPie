package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

/**
 * Sets a sign at the specified location with specified data.
 * <br><br>
 * in 1.14
 * ACACIA BIRCH OAK DARK_OAK JUNGLE SPRUCE -LEGACY- +_SIGN +_WALL_SIGN
 * no ACACIA_WALL_SIGN
 * in 1.13
 * SIGN WALL_SIGN, LEGACY
 * note in 1.14.4 LEGACY is deprecated
 */
public class SetSign implements HandlerVoid {
	/**
	 * Default SetSign constructor.
	 */
	public SetSign() {
	}

	@Override
	@SuppressWarnings("PMD.PrematureDeclaration")
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		Material material = Material.matchMaterial(instruction.next());
		if (material == null) {
			material = Material.BIRCH_SIGN;
		}
		if (!material.toString().contains("_SIGN")) {
			throw new IllegalArgumentException("material must be sign");
		}

		final Block thisBlock = loc.getBlock();
		thisBlock.setType(material);

		int facing = instruction.hasNext() ? Integer.parseInt(instruction.next()) : 0;
		if (facing >= 4 || facing < 0) {
			facing = 0;
		}
		setSignFace(facing, thisBlock);

		final Sign sign = (Sign) thisBlock.getState();
		int line = 0;
		while (instruction.hasNext() && line < 4) {
			sign.setLine(line++, instruction.next());
		}
		sign.update();
	}

	private static void setSignFace(final int facing, final Block thisBlock) {
		final BlockFace blockFace = BlockFace.values()[facing];
		final BlockData blockData = thisBlock.getBlockData();
		if (blockData instanceof final org.bukkit.block.data.type.WallSign signData) {
			signData.setFacing(blockFace);
			thisBlock.setBlockData(signData);
		} else if (blockData instanceof final org.bukkit.block.data.type.Sign signData) {
			signData.setRotation(blockFace);
			thisBlock.setBlockData(signData);
		}
	}
}
