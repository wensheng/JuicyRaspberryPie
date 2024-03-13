package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class SpawnParticle implements HandlerVoid {
	@Override
	public void handleVoid(final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		Particle particle;
		try {
			particle = Particle.valueOf(instruction.next().toUpperCase());
		} catch (Exception exc) {
			particle = Particle.valueOf("EXPLOSION_NORMAL");
		}
		final int count;
		if (instruction.hasNext()) {
			count = Integer.parseInt(instruction.next());
		} else {
			count = 10;
		}
		final double speed;
		if (instruction.hasNext()) {
			speed = Double.parseDouble(instruction.next());
		} else {
			speed = 1.0;
		}
		loc.getWorld().spawnParticle(particle, loc, count, 0, 0, 0, speed);
	}
}
