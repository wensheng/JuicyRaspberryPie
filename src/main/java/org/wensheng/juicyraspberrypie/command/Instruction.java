package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;

import java.util.Arrays;
import java.util.Iterator;

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
		return StringUtils.join(this, ",");
	}
}
