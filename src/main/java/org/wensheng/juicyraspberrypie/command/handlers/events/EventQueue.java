package org.wensheng.juicyraspberrypie.command.handlers.events;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Handler;

import java.util.ArrayDeque;

public abstract class EventQueue<T extends Event> implements Handler, Listener {
    private final ArrayDeque<T> eventQueue = new ArrayDeque<>();

    protected final Plugin plugin;

    public EventQueue(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void queueEvent(final T event) {
        eventQueue.add(event);
    }

    public T pollEvent() {
        return eventQueue.poll();
    }

    public boolean isQueueEmpty() {
        return eventQueue.isEmpty();
    }

    public void clearQueue() {
        eventQueue.clear();
    }

    public abstract void start();

    public abstract void stop();
}
