package org.wensheng.juicyraspberrypie.command;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

public class Registry {
    private final HashMap<String, Handler> handlers;

    public Registry(final Plugin plugin) {
        handlers = new HashMap<>();
    }

    @NotNull
    public Collection<Handler> getHandlers() {
        return handlers.values();
    }

    public void register(final String command, final Handler handler) {
        handlers.put(command, handler);
    }

    public Handler getHandler(final String command) {
        return handlers.get(command);
    }
}
