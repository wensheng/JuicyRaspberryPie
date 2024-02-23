package org.wensheng.juicyraspberrypie.command.handlers;

import org.bukkit.entity.Player;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

public class GetPlayer implements Handler {
    private final SessionAttachment attachment;

    public GetPlayer(final SessionAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String handle(final Instruction instruction) {
        final Player p = attachment.getPlayer();
        return p == null ? "(none)" : p.getName();
    }
}
