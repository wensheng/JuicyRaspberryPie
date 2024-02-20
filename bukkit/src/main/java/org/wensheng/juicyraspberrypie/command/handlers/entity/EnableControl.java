package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.ControllableEntity;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

import java.io.IOException;

public class EnableControl implements HandlerVoid {
    private final Plugin plugin;

    private final EntityByUUIDProvider entityProvider;

    public EnableControl(final Plugin plugin, final EntityByUUIDProvider entityProvider) {
        this.plugin = plugin;
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) throws IOException {
        final ControllableEntity entity = new ControllableEntity(plugin, entityProvider.getEntity(instruction));
        entity.enableControl();
    }
}
