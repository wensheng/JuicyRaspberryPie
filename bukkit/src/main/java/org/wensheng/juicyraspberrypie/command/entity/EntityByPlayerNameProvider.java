package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.util.NoSuchElementException;

public class EntityByPlayerNameProvider implements EntityProvider {

    private final SessionAttachment attachment;

    public EntityByPlayerNameProvider(final SessionAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public Player getEntity(final Instruction instruction) {
        final Player player;
        if ("".equals(instruction.peek())) {
            instruction.next();
            player = attachment.getPlayer();
        } else {
            player = instruction.nextNamedPlayer();
        }
        if (player == null) {
            throw new NoSuchElementException("No player found");
        }
        return player;
    }
}
