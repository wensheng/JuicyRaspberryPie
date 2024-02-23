package org.wensheng.juicyraspberrypie.command.handlers;

import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

public class SetPlayer implements Handler {
    private final SessionAttachment attachment;

    public SetPlayer(final SessionAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String handle(final Instruction instruction) {
        return String.valueOf(attachment.setPlayerAndOrigin(instruction.next()));
    }
}
