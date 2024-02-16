package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

import java.io.IOException;

public class GetTile implements Handler {
    private final EntityProvider entityProvider;

    public GetTile(final EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public String handle(final Instruction instruction) throws IOException {
        final Entity entity = entityProvider.getEntity(instruction);
        return getBlockLocation(entity.getLocation());
    }
}
