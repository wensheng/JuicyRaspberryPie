package org.wensheng.juicyraspberrypie.command;

import java.io.IOException;

public interface HandlerVoid extends Handler {
    @Override
    default String handle(final Instruction instruction) throws IOException {
        handleVoid(instruction);
        return "OK";
    }

    void handleVoid(final Instruction instruction) throws IOException;
}
