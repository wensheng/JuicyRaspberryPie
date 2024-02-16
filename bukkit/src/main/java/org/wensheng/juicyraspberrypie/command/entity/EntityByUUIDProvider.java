package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.io.IOException;
import java.util.UUID;

public class EntityByUUIDProvider implements EntityProvider {
    private final Server server;

    public EntityByUUIDProvider(final Server server) {
        this.server = server;
    }

    @Override
    public Entity getEntity(final Instruction instruction) throws IOException {
        final Entity entity = server.getEntity(UUID.fromString(instruction.next()));
        if (entity == null) {
            throw new IOException("No entity found");
        }
        return entity;
    }
}
