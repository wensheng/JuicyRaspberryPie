package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An instruction for a command.
 */
public class Instruction implements Iterator<String> {
	/**
	 * The arguments.
	 */
	private final String[] args;

	/**
	 * The location parser.
	 */
	private final LocationParser locationParser;

	/**
	 * The courser.
	 */
	private int courser;

	/**
	 * Create a new instruction.
	 *
	 * @param args           the arguments
	 * @param locationParser the location parser
	 */
	public Instruction(final String[] args, final LocationParser locationParser) {
		this.args = Arrays.copyOf(args, args.length);
		this.locationParser = locationParser;
		this.courser = 0;
	}

	/**
	 * Check if there are more arguments.
	 *
	 * @return true if there are more arguments
	 */
	@Override
	public boolean hasNext() {
		return hasNext(1);
	}

	/**
	 * Check if there are n more arguments.
	 *
	 * @param amount the number of arguments
	 * @return true if there are n more arguments
	 */
	public boolean hasNext(final int amount) {
		return courser + amount <= args.length;
	}

	/**
	 * Get the next argument.
	 *
	 * @return the next argument
	 */
	@Override
	public String next() {
		return hasNext() ? args[courser++] : null;
	}

	/**
	 * Peek at the next argument.
	 *
	 * @return the next argument
	 */
	public String peek() {
		return hasNext() ? args[courser] : null;
	}

	/**
	 * Get the next location from the instruction.
	 *
	 * @return the location
	 */
	public Location nextLocation() {
		return locationParser.parse(next(), next(), next());
	}

	/**
	 * Get the locations between the next two locations from the instruction.
	 *
	 * @return the locations
	 */
	public List<Location> nextLocationsBetween() {
		final Location loc1 = nextLocation();
		final Location loc2 = nextLocation();
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

	/**
	 * Get the next block location from the instruction.
	 *
	 * @return the location
	 */
	public Location nextBlockLocation() {
		return nextLocation().getBlock().getLocation();
	}

	/**
	 * Get the next player from the instruction.
	 *
	 * @return the player
	 */
	public Player nextNamedPlayer() {
		final String name = next();
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if (name.equalsIgnoreCase(p.getName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Get all (remaining) arguments as a single String.
	 *
	 * @return all arguments joined together
	 */
	public String allArguments() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false)
				.map(arg -> arg == null ? "" : arg)
				.collect(Collectors.joining(","));
	}
}
