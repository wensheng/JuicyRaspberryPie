package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class SpawnEntity implements Handler {
	@Override
	public String handle(final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		EntityType entityType;
		try {
			entityType = EntityType.valueOf(instruction.next().toUpperCase());
		} catch (Exception exc) {
			entityType = EntityType.valueOf("COW");
		}
		final Entity entity = loc.getWorld().spawnEntity(loc, entityType);
		return entity.getUniqueId().toString();
	}
}
