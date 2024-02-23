package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

public class SetDirection implements HandlerVoid {
    private final EntityProvider entityProvider;

    public SetDirection(final EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) {
        final Entity entity = entityProvider.getEntity(instruction);
        final Location loc = instruction.nextLocation();
        final Location entityLoc = entity.getLocation();
        final Location target = entityLoc.setDirection(loc.toVector());
        entity.teleport(target);
        if (entity instanceof final Mob mob) {
            mob.setBodyYaw(target.getYaw());
        }
    }
}
