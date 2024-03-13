package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

public class SetPitch implements HandlerVoid {
	private final EntityProvider entityProvider;

	public SetPitch(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public void handleVoid(final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(instruction);
		final float pitch = Float.parseFloat(instruction.next());
		final Location entityLoc = entity.getLocation();
		entityLoc.setPitch(pitch);
		entity.teleport(entityLoc);
	}
}
