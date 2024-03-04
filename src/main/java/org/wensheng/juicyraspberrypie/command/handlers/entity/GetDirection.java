package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

public class GetDirection implements Handler {
    private final EntityProvider entityProvider;

    public GetDirection(final EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public String handle(final Instruction instruction) {
        final Entity entity = entityProvider.getEntity(instruction);
        return entity.getLocation().getDirection().toString();
    }
}
