package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.util.Collection;

public class GetNearbyEntities implements Handler {
	@Override
	public String handle(final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		final double nearby_distance = instruction.hasNext() ? Double.parseDouble(instruction.next()) : 10.0;
		final Collection<Entity> nearbyEntities = loc.getNearbyEntities(nearby_distance, 5.0, nearby_distance);
		final StringBuilder sb = new StringBuilder();
		for (final Entity e : nearbyEntities) {
			sb.append(e.getName()).append(",").append(e.getUniqueId()).append("|");
		}
		if (sb.length() > 1) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}
