package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.NoSuchElementException;

/**
 * Provides entities for use in command handlers by player name.
 */
public class EntityByPlayerNameProvider implements EntityProvider {
	/**
	 * Create a new entity provider by player name.
	 */
	public EntityByPlayerNameProvider() {
	}

	@Override
	public Player getEntity(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Player player;
		if (instruction.peek() == null) {
			instruction.next();
			player = sessionAttachment.getPlayer();
		} else {
			player = instruction.nextNamedPlayer();
		}
		if (player == null) {
			throw new NoSuchElementException("No player found");
		}
		return player;
	}
}
