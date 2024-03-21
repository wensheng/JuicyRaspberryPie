package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Switch;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.util.NoSuchElementException;

/**
 * Set a {@link Switch}able block to a given power state at a given location
 */
public class SetPowered implements HandlerVoid {
	/**
	 * Default SetPowered constructor.
	 */
	public SetPowered() {
	}

	@Override
	public void handleVoid(final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		final Block block = loc.getBlock();
		if (block.getBlockData() instanceof final Switch powerableSwitch) {
			powerableSwitch.setPowered(parsePoweredState(instruction.next(), powerableSwitch.isPowered()));
			block.setBlockData(powerableSwitch);
			block.getState().update();
			updateBlocksAround(block, powerableSwitch);
			return;
		}

		throw new NoSuchElementException("No powerable block at " + loc);
	}

	private boolean parsePoweredState(final String arg, final boolean powered) {
		return switch (arg) {
			case "PoweredState.ON" -> true;
			case "PoweredState.OFF" -> false;
			case "PoweredState.TOGGLE" -> !powered;
			default -> throw new IllegalArgumentException("Invalid state type: " + arg);
		};
	}

	private void updateBlocksAround(final Block block, final Switch powerableSwitch) {
		final BlockFace attachedTo = switch (powerableSwitch.getAttachedFace()) {
			case FLOOR -> BlockFace.DOWN;
			case CEILING -> BlockFace.UP;
			default -> powerableSwitch.getFacing().getOppositeFace();
		};
		final Block relative = block.getRelative(attachedTo);

		final BlockState relativeState = relative.getState();
		if (relativeState instanceof final Container container) {
			container.getInventory().clear();
		}
		relative.setType(Material.AIR, false);
		relativeState.update(true);
	}
}
