package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.Collection;

/**
 * Get the entities near a given location.
 */
public class GetNearbyEntities implements Handler {
	/**
	 * Default GetNearbyEntities constructor.
	 */
	public GetNearbyEntities() {
	}

	@Override
	@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		final double nearbyDistance = instruction.hasNext() ? Double.parseDouble(instruction.next()) : 10.0;
		final Collection<Entity> nearbyEntities = loc.getNearbyEntities(nearbyDistance, 5.0, nearbyDistance);
		final StringBuilder stringBuilder = new StringBuilder();
		for (final Entity entity : nearbyEntities) {
			stringBuilder.append(entity.getName()).append(',').append(entity.getUniqueId()).append('|');
		}
		if (stringBuilder.length() > 1) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}
		return stringBuilder.toString();
	}
}
