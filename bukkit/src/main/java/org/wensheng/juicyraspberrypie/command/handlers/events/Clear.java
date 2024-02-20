package org.wensheng.juicyraspberrypie.command.handlers.events;

import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.Registry;

public class Clear implements HandlerVoid {
    private final Registry registry;

    public Clear(final Registry registry) {
        this.registry = registry;
    }

    @Override
    public void handleVoid(final Instruction instruction) {
        registry.getHandlers().stream()
                .filter(handler -> handler instanceof EventQueue<?>)
                .map(handler -> (EventQueue<?>) handler)
                .forEach(EventQueue::clearQueue);
    }
}
