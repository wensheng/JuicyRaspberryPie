package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

public class SetRotation implements HandlerVoid {
    private final EntityProvider entityProvider;

    public SetRotation(final EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) {
        final Entity entity = entityProvider.getEntity(instruction);
        final float yaw = Float.parseFloat(instruction.next());
        final Location entityLoc = entity.getLocation();
        entityLoc.setYaw(yaw);
        entity.teleport(entityLoc);
        if (entity instanceof final Mob mob) {
            mob.setBodyYaw(entityLoc.getYaw());
        }
    }
}
