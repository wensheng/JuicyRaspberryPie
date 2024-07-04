package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A handler for a command.
 */
public interface Handler {
	/**
	 * Handle the instruction for a command and return the result.
	 * If an exception is thrown, the result will be "Fail: " followed by the exception message.
	 *
	 * @param instruction the instruction
	 * @return the result
	 */
	@SuppressWarnings({"PMD.AvoidPrintStackTrace", "PMD.AvoidCatchingGenericException"})
	default String get(final Instruction instruction) {
		try {
			return handle(instruction);
		} catch (final Exception e) {
			e.printStackTrace();
			return "Fail: " + e.getMessage();
		}
	}

	/**
	 * Handle the instruction for a command and return the result.
	 *
	 * @param instruction the instruction
	 * @return the result
	 */
	String handle(Instruction instruction);

	/**
	 * Get the locations between the given locations.
	 *
	 * @param loc1 the first location
	 * @param loc2 the second location
	 * @return the locations
	 */
	default List<Location> getLocationsBetween(final Location loc1, final Location loc2) {
		final List<Location> locations = new ArrayList<>();
		final World world = loc1.getWorld();
		final int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		final int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		final int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		final int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		final int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		final int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					locations.add(new Location(world, x, y, z));
				}
			}
		}
		return locations;
	}
}
