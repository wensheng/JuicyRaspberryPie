package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

import java.io.IOException;

public class Remove implements HandlerVoid {
    private final EntityByUUIDProvider entityProvider;

    public Remove(final EntityByUUIDProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) throws IOException {
        final Entity entity = entityProvider.getEntity(instruction);
        if (!(entity instanceof Player)) {
            entity.remove();
        }
    }
}
