package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;

/**
 * A parser for locations.
 */
public class LocationParser {
	/**
	 * The session attachment.
	 */
	private final SessionAttachment attachment;

	/**
	 * Create a new location parser.
	 *
	 * @param attachment the session attachment
	 */
	public LocationParser(final SessionAttachment attachment) {
		this.attachment = attachment;
	}

	/**
	 * Parse a location from the given coordinates.
	 *
	 * @param stringX the x coordinate
	 * @param stringY the y coordinate
	 * @param stringZ the z coordinate
	 * @return the location
	 */
	public Location parse(final String stringX, final String stringY, final String stringZ) {
		final double doubleX = Double.parseDouble(stringX);
		final double doubleY = Double.parseDouble(stringY);
		final double doubleZ = Double.parseDouble(stringZ);
		return parse(doubleX, doubleY, doubleZ);
	}

	/**
	 * Parse a location from the given coordinates.
	 *
	 * @param doubleX the x coordinate
	 * @param doubleY the y coordinate
	 * @param doubleZ the z coordinate
	 * @return the location
	 */
	public Location parse(final double doubleX, final double doubleY, final double doubleZ) {
		return new Location(attachment.getWorld(), doubleX, doubleY, doubleZ);
	}
}
