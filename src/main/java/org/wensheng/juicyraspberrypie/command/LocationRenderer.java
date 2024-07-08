package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;

/**
 * A renderer for locations.
 */
public final class LocationRenderer {
	private LocationRenderer() {
	}

	/**
	 * Get the location as a string from the given location.
	 *
	 * @param loc the location
	 * @return the location as a string
	 */
	public static String getLocation(final Location loc) {
		return loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}

	/**
	 * Get the block location as a string from the given location.
	 *
	 * @param loc the location
	 * @return the block location as a string
	 */
	public static String getBlockLocation(final Location loc) {
		return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
}
