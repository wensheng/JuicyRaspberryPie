package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.NoSuchElementException;

/**
 * Provides entities for use in command handlers by player name.
 */
public class EntityByPlayerNameProvider implements EntityProvider {
	/**
	 * The session attachment associated with this handler.
	 */
	private final SessionAttachment attachment;

	/**
	 * Create a new entity provider by player name.
	 *
	 * @param attachment The session attachment to associate with this handler.
	 */
	public EntityByPlayerNameProvider(final SessionAttachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public Player getEntity(final Instruction instruction) {
		final Player player;
		if (instruction.peek() == null) {
			instruction.next();
			player = attachment.getPlayer();
		} else {
			player = instruction.nextNamedPlayer();
		}
		if (player == null) {
			throw new NoSuchElementException("No player found");
		}
		return player;
	}
}
