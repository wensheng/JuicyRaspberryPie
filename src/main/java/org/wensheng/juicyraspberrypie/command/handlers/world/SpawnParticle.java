package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.Locale;

/**
 * Spawn a particle at a location.
 */
public class SpawnParticle implements HandlerVoid {

	/**
	 * Default SpawnParticle constructor.
	 */
	public SpawnParticle() {
	}

	@Override
	@SuppressWarnings("PMD.AvoidCatchingGenericException")
	public void handleVoid(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Location loc = instruction.nextLocation();
		Particle particle;
		try {
			particle = Particle.valueOf(instruction.next().toUpperCase(Locale.ROOT));
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
